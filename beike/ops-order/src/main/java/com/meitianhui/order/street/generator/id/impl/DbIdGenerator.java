package com.meitianhui.order.street.generator.id.impl;

import com.meitianhui.order.street.generator.id.BaseIdGenerator;
import com.meitianhui.order.street.generator.id.worker.entity.OdBizId;
import com.meitianhui.order.street.generator.id.worker.service.OdBizIdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Queue;

/**
 * <pre> 基于数据库的编号生成器 </pre>
 *
 * <p>用来生成唯一的编号</p>
 *
 * @author tortoise
 * @since 2019/3/26 20:34
 */
@Component
public class DbIdGenerator extends BaseIdGenerator {

    @Autowired
    private OdBizIdService odBizIdService;

    /**
     * 添加队列数据，线程安全
     *
     * @param bizTag 业务标志
     */
    @Override
    public void addQueueData(String bizTag, Queue<Long> queue) {
        lock.lock();
        try {
            OdBizId odBizId = odBizIdService.obtainOdBizId(bizTag);
            long startId = odBizId.getMaxId() - odBizId.getStep() + 1;
            long maxId = odBizId.getMaxId();
            for (long i = startId; i <= maxId; i++) {
                queue.offer(i);
            }
        } finally {
            lock.unlock();
        }
    }

}
