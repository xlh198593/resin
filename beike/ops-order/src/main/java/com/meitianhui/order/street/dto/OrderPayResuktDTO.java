package com.meitianhui.order.street.dto;

import com.meitianhui.order.street.entity.BkcqLoadCode;
import com.meitianhui.order.street.entity.BkcqLocalOrder;
import lombok.*;

import java.io.Serializable;
import java.util.List;

/**
 * <pre> 街市订单支付结果传输对象 </pre>
 *
 * @author tortoise
 * @since 2019/3/27 15:05
 */
@Data
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderPayResuktDTO implements Serializable {

    /**
     * 订单
     */
    private BkcqLocalOrder bkcqLocalOrder;

    /**
     * 核销码
     */
    private List<BkcqLoadCode> codes;

    /**
     * 加密核销码
     */
    private String codeStr;

}
