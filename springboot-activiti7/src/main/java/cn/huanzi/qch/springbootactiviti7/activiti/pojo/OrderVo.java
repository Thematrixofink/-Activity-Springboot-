package cn.huanzi.qch.springbootactiviti7.activiti.pojo;

import lombok.Data;


@Data
public class OrderVo {

    String restaurantName;
    String orderDetails;
    String quantity;
    String address;
    String username;

//    //请假流程发起
//    private String businessKey;
//    private String username;
//    private String dish;
//    private String count;
//    private String store;

    //完成任务
    private String taskId;
    private boolean auditFlag;
    private String message;
}
