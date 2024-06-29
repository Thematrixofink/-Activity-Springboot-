package cn.huanzi.qch.springbootactiviti7.activiti.controller;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/thymeleaf")
public class OrderThymeleafController {

    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskService taskService;

    @Autowired
    private HistoryService historyService;


    @GetMapping("/order")
    public String userOrder() {
        return "waimai/orderIndex";
    }

    @GetMapping("/user")
    public String userViewOrders() {
        return "waimai/user-orders";
    }

    @GetMapping("/rest")
    public String restViewOrders() {
        return "waimai/rest-orders";
    }

    @GetMapping("/rider")
    public String riderViewOrders() {
        return "waimai/rider-orders";
    }

    /**
     * 用户订餐
     * @param restaurantName
     * @param orderDetails
     * @param quantity
     * @param address
     * @param username
     * @return
     */
    @PostMapping("/start")
    public ModelAndView startOrder(@RequestParam String restaurantName,
                                   @RequestParam String orderDetails,
                                   @RequestParam String quantity,
                                   @RequestParam String address,
                                   @RequestParam String username) {

        String orderId = IdUtil.simpleUUID();
        Map<String, Object> variables = new HashMap<>();
        variables.put("restaurantName", restaurantName);
        variables.put("orderDetails", orderDetails);
        variables.put("quantity", quantity);
        variables.put("address", address);
        variables.put("username", username);
        variables.put("orderId",orderId);
        variables.put("status","下单成功，等待商家确认~~~");
        ProcessInstance orderProcess = runtimeService.startProcessInstanceByKey("orderProcess", variables);
        String id = orderProcess.getProcessInstanceId();

        //完成用户填写订单任务
        Task task = completeTask(orderId);
        if (ObjectUtil.isNull(task)) {
            ModelAndView modelAndView = new ModelAndView("waimai/user-orders");
            modelAndView.addObject("message", "订单不存在!");
            return modelAndView;
        }

        ModelAndView modelAndView = new ModelAndView("waimai/orderIndex");
        modelAndView.addObject("message","下单成功");
        return modelAndView;
    }

    /**
     * 查询用户的所有订单
     * @param username
     * @return
     */
    @GetMapping("/user-orders")
    public ModelAndView getUserOrders(@RequestParam String username) {

        List<ProcessInstance> processInstances = runtimeService.createProcessInstanceQuery()
                .processDefinitionKey("orderProcess") // 根据流程定义key过滤，如果有的话
                .variableValueEquals("username", username)
                .list();

        List<Map<String, Object>> result = processInstances.stream().map(new Function<ProcessInstance, Map<String, Object>>() {
            @Override
            public Map<String, Object> apply(ProcessInstance processInstance) {
                return runtimeService.getVariables(processInstance.getProcessInstanceId());
            }
        }).collect(Collectors.toList());


        ModelAndView modelAndView = new ModelAndView("waimai/user-orders");
        modelAndView.addObject("orders", result);
        return modelAndView;
    }

    /**
     * 商家确认订单
     * @param orderId
     * @return
     */
    @PostMapping("/approve")
    public ModelAndView approveOrder(@RequestParam String orderId) {
        //批准订单
        List<ProcessInstance> processInstances = runtimeService.createProcessInstanceQuery()
                .processDefinitionKey("orderProcess") // 根据流程定义key过滤，如果有的话
                .variableValueEquals("orderId", orderId)
                .list();

        //根据orderId来查询的话，只会有一个订单
        ProcessInstance processInstance = processInstances.get(0);
        runtimeService.setVariable(processInstance.getProcessInstanceId(),"approve",true);
        runtimeService.removeVariable(processInstance.getProcessInstanceId(),"status");
        runtimeService.setVariable(processInstance.getProcessInstanceId(),"status","等待商家召唤骑手~~~");

        List<Map<String, Object>> result = processInstances.stream().map(new Function<ProcessInstance, Map<String, Object>>() {
            @Override
            public Map<String, Object> apply(ProcessInstance processInstance) {
                return runtimeService.getVariables(processInstance.getProcessInstanceId());
            }
        }).collect(Collectors.toList());

        //完成批准任务
        Task task1 = completeTask(orderId);
        if (ObjectUtil.isNull(task1)) {
            ModelAndView modelAndView = new ModelAndView("waimai/user-orders");
            modelAndView.addObject("message", "订单不存在!");
            return modelAndView;
        }

        ModelAndView modelAndView = new ModelAndView("waimai/user-orders");
        modelAndView.addObject("orders",result);
        modelAndView.addObject("message", "确认订单成功!");
        return modelAndView;
    }

    @PostMapping("/reject")
    public ModelAndView rejectOrder(@RequestParam String orderId) {
        //批准订单
        List<ProcessInstance> processInstances = runtimeService.createProcessInstanceQuery()
                .processDefinitionKey("orderProcess") // 根据流程定义key过滤，如果有的话
                .variableValueEquals("orderId", orderId)
                .list();
        //根据taskId来查询的话，只会有一个订单
        ProcessInstance processInstance = processInstances.get(0);
        runtimeService.setVariable(processInstance.getProcessInstanceId(),"approve",false);
        runtimeService.setVariable(processInstance.getProcessInstanceId(),"status","商家拒绝了你的订单!");
        //完成批准任务
        Task task = completeTask(orderId);
        if (ObjectUtil.isNull(task)) {
            ModelAndView modelAndView = new ModelAndView("waimai/user-orders");
            modelAndView.addObject("message", "订单不存在!");
            return modelAndView;
        }

        ModelAndView modelAndView = new ModelAndView("waimai/user-orders");
        modelAndView.addObject("message", "确认订单成功!");
        return modelAndView;
    }




    /**
     * 餐馆查询其所有的订单
     * @param restaurantName
     * @return
     */
    @GetMapping("/restaurant-orders")
    public ModelAndView getRestaurantOrders(@RequestParam String restaurantName) {

        List<ProcessInstance> processInstances = runtimeService.createProcessInstanceQuery()
                .processDefinitionKey("orderProcess") // 根据流程定义key过滤，如果有的话
                .variableValueEquals("restaurantName", restaurantName)
                .list();

        List<Map<String, Object>> result = processInstances.stream().map(new Function<ProcessInstance, Map<String, Object>>() {
            @Override
            public Map<String, Object> apply(ProcessInstance processInstance) {
                return runtimeService.getVariables(processInstance.getProcessInstanceId());
            }
        }).collect(Collectors.toList());

        ModelAndView modelAndView = new ModelAndView("waimai/rest-orders");
        modelAndView.addObject("orders", result);
        return modelAndView;
    }


    /**
     * 骑手查看自己的所有的订单
     * @param rider
     * @return
     */
    @GetMapping("/rider-orders")
    public ModelAndView getRiderOrders(@RequestParam String rider) {
        List<ProcessInstance> processInstances = runtimeService.createProcessInstanceQuery()
                .processDefinitionKey("orderProcess") // 根据流程定义key过滤，如果有的话
                .variableValueEquals("rider", rider)
                .list();

        List<Map<String, Object>> result = processInstances.stream().map(new Function<ProcessInstance, Map<String, Object>>() {
            @Override
            public Map<String, Object> apply(ProcessInstance processInstance) {
                return runtimeService.getVariables(processInstance.getProcessInstanceId());
            }
        }).collect(Collectors.toList());

        ModelAndView modelAndView = new ModelAndView("waimai/rider-orders");
        modelAndView.addObject("orders", result);
        return modelAndView;
    }

    /**
     * 为订单分配骑手
     * @param orderId
     * @param rider
     * @param model
     * @return
     */
    @PostMapping("/assign-rider")
    public ModelAndView assignRider(@RequestParam String orderId, @RequestParam String rider) {
        List<ProcessInstance> processInstances = runtimeService.createProcessInstanceQuery()
                .processDefinitionKey("orderProcess") // 根据流程定义key过滤，如果有的话
                .variableValueEquals("orderId", orderId)
                .list();
        //根据taskId来查询的话，只会有一个订单
        ProcessInstance processInstance = processInstances.get(0);
        runtimeService.setVariable(processInstance.getProcessInstanceId(),"rider",rider);
        runtimeService.setVariable(processInstance.getProcessInstanceId(),"status","骑手正在配送中~~~");

        Task task = completeTask(orderId);
        if(ObjectUtil.isNull(task)){
            ModelAndView modelAndView = new ModelAndView("waimai/user-orders");
            modelAndView.addObject("message", "订单不存在!");
            return modelAndView;
        }

        //获取最新的信息
        List<ProcessInstance> newProcess = runtimeService.createProcessInstanceQuery()
                .processDefinitionKey("orderProcess") // 根据流程定义key过滤，如果有的话
                .variableValueEquals("orderId", orderId)
                .list();
        List<Map<String, Object>> result = newProcess.stream().map(new Function<ProcessInstance, Map<String, Object>>() {
            @Override
            public Map<String, Object> apply(ProcessInstance processInstance) {
                return runtimeService.getVariables(processInstance.getProcessInstanceId());
            }
        }).collect(Collectors.toList());


        ModelAndView modelAndView = new ModelAndView("waimai/rest-orders");
        modelAndView.addObject("orders",result);
        modelAndView.addObject("message","为订单:"+orderId+"分配骑手: " + rider);
        return modelAndView;
    }

    /**
     * 确认订单
     * @param orderId
     * @return
     */
    @PostMapping("/ensure")
    public ModelAndView ensureOrder(@RequestParam String orderId){
        Task task = taskService.createTaskQuery()
                .processVariableValueEquals("orderId", orderId)
                .singleResult();
        // 更新流程变量
        String processInstanceId = task.getProcessInstanceId();
        runtimeService.setVariable(processInstanceId, "status", "已完成~~~");
        ModelAndView modelAndView = new ModelAndView("waimai/user-orders");
        modelAndView.addObject("message", "确认订单成功!");

        taskService.complete(task.getId());

        return modelAndView;
    }

    /**
     * 根据订单ID，完成task
     * @param orderId
     * @return
     */
    private Task completeTask(String orderId){
        //根据订单的id(taskId)查找到activiti的任务Id
        Task task = taskService.createTaskQuery()
                .processVariableValueEquals("orderId", orderId)
                .singleResult();
        if (task == null) {
            // 处理任务不存在的情况
            return null;
        }
        taskService.complete(task.getId());
        return task;
    }

}
