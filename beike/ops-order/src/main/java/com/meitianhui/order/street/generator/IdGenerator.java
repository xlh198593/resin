package com.meitianhui.order.street.generator;

/**
 * <pre> 编号生成器 </pre>
 *
 * @author tortoise
 * @since 2019/3/26 20:34
 */
public interface IdGenerator {

    /**
     * 生成主键
     *
     * @param bizTag 业务标志
     * @return 长整形的编号
     */
    long generateId(String bizTag);

    /**
     * 生成编号，前面1位为随机数，中间10位全局增长，后面1位为随机数
     *
     * @param bizTag 业务标志
     * @return 字符串类型的编号
     */
    String generateNo(String bizTag);


}
