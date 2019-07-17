package com.meitianhui.order.street.generator.id.worker.entity;

import lombok.*;

import java.io.Serializable;
import java.util.Date;

/**
 * <pre> 数据库编号生成器实体 </pre>
 *
 * @author tortoise
 * @since 2019/3/27 10:05
 */
@Data
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OdBizId implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 业务标志
     */
    private String bizTag;

    /**
     * 最大编号
     */
    private Long maxId;

    /**
     * 步长
     */
    private Integer step;

    /**
     * 描述
     */
    private String desc;

    /**
     * 最后修改时间
     */
    private Date updateTime;

}
