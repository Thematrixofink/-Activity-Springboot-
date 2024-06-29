package cn.huanzi.qch.springbootactiviti7.activiti.pojo;

import lombok.Data;


@Data
public class OrderVo {
    /**
     * 餐馆名称
     */
    private String restaurantName;

    /**
     * 订单详情
     */
    private String orderDetails;

    /**
     数量
     */
    private String quantity;

    /**
     * 数量
     */
    private String address;

    /**
     * 用户名
     */
    private String username;

    /**
     * 订单ID
     */
    private String orderId;

    /**
     * 订单状态
     */
    private String status;

    /**
     * 消息
     */
    private String message;

    /**
     * 订单是否被批准
     */
    private boolean approve;


    private boolean auditFlag;
}
