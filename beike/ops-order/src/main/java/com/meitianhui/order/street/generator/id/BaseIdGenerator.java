package com.meitianhui.order.street.generator.id;

import cn.hutool.core.util.RandomUtil;
import com.meitianhui.order.street.generator.IdGenerator;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * <pre> 主键生成器抽象类 </pre>
 *
 * <p>用来生成唯一的编号</p>
 *
 * @author tortoise
 * @since 2019/3/26 20:34
 */
public abstract class BaseIdGenerator implements IdGenerator {

    /**
     * 可重入锁，初始化idQueues的时候防止重复初始化
     */
    protected Lock lock = new ReentrantLock();
    /**
     * 业务标志队列Map集合
     */
    private volatile Map<String, Queue<Long>> idQueues = new ConcurrentHashMap<>();

    /**
     * 生成主键
     *
     * @param bizTag 业务标志
     * @return 长整形的编号
     */
    @Override
    public long generateId(String bizTag) {
        Queue<Long> queue = getQueue(bizTag);
        Long pollId = queue.poll();
        if (null == pollId) {
            lock.lock();
            try {
                pollId = queue.poll();
                while (null == pollId) {
                    addQueueData(bizTag, queue);
                    pollId = queue.poll();
                }
            } finally {
                lock.unlock();
            }
        }
        return pollId;
    }

    /**
     * 生成编号，前面1位为随机数，中间10位全局增长，后面1位为随机数
     *
     * @param bizTag 业务标志
     * @return 字符串类型的编号
     */
    @Override
    public String generateNo(String bizTag) {
        long id = generateId(bizTag);
        int prefix = RandomUtil.randomInt(0, 9);
        int suffix = RandomUtil.randomInt(0, 9);
        return prefix + String.format("%010d", id) + suffix;
    }

    /**
     * 获取队列大小
     *
     * @param bizTag 业务标志
     * @return 队列大小
     */
    public int getQueueSize(String bizTag) {
        return getQueue(bizTag).size();
    }

    /**
     * 添加队列数据，必须保证线程安全
     *
     * @param bizTag 业务标志
     * @param queue  队列
     */
    public abstract void addQueueData(String bizTag, Queue<Long> queue);

    /**
     * 获取业务标志当前队列，如果队列不存在则采用可重入锁初始化队列，线程安全
     *
     * @param bizTag 业务标志
     * @return 队列
     */
    private Queue<Long> getQueue(String bizTag) {
        Queue<Long> idQueue = idQueues.get(bizTag);
        if (null == idQueue) {
            lock.lock();
            try {
                idQueue = idQueues.get(bizTag);
                if (idQueue == null) {
                    idQueue = initQueue(bizTag);
                }
            } finally {
                lock.unlock();
            }
        }
        return idQueue;
    }

    /**
     * 初始化队列
     *
     * @param bizTag 业务标志
     * @return Queue<Long>初始化的队列
     */
    private Queue<Long> initQueue(String bizTag) {
        Queue<Long> idQueue = new ConcurrentLinkedQueue<>();
        addQueueData(bizTag, idQueue);
        idQueues.put(bizTag, idQueue);
        return idQueue;
    }

}
