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
    private String taskId;

    /**
     * 订单状态
     */
    private String status;


    private boolean auditFlag;
    private String message;
}
