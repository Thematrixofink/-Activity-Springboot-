package cn.huanzi.qch.springbootactiviti7.activiti.controller;

import cn.hutool.core.util.IdUtil;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
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



    @GetMapping("/order")
    public String userOrder() {
        return "waimai/orderIndex";
    }

    @GetMapping("/rest")
    public String userViewOrders() {
        return "waimai/rest-orders";
    }

    @GetMapping("/rider")
    public String restaurantViewOrders() {
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

        Map<String, Object> variables = new HashMap<>();
        variables.put("restaurantName", restaurantName);
        variables.put("orderDetails", orderDetails);
        variables.put("quantity", quantity);
        variables.put("address", address);
        variables.put("username", username);
        variables.put("taskId", IdUtil.simpleUUID());
        variables.put("status", "商家正在备餐中~~~");
        runtimeService.startProcessInstanceByKey("orderProcess", variables);
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
     * @param taskId
     * @param rider
     * @param model
     * @return
     */
    @PostMapping("/assign-rider")
    public ModelAndView assignRider(@RequestParam String taskId, @RequestParam String rider, Model model) {
        List<ProcessInstance> processInstances = runtimeService.createProcessInstanceQuery()
                .processDefinitionKey("orderProcess") // 根据流程定义key过滤，如果有的话
                .variableValueEquals("taskId", taskId)
                .list();
        //根据taskId来查询的话，只会有一个订单
        ProcessInstance processInstance = processInstances.get(0);
        runtimeService.setVariable(processInstance.getProcessInstanceId(),"rider",rider);
        runtimeService.setVariable(processInstance.getProcessInstanceId(),"status","骑手正在配送中~~~");
        ModelAndView modelAndView = new ModelAndView("waimai/restaurant-orders");
        modelAndView.addObject("message","为订单:"+taskId+"分配骑手: " + rider);
        return modelAndView;
    }

    /**
     * 确认订单
     * @param taskId
     * @return
     */
    @PostMapping("/ensure")
    public ModelAndView ensureOrder(@RequestParam String taskId){

        //根据订单的id(taskId)查找到activiti的任务Id
        Task task = taskService.createTaskQuery()
                .processVariableValueEquals("taskId", taskId)
                .singleResult();

        if (task == null) {
            // 处理任务不存在的情况
            ModelAndView modelAndView = new ModelAndView("waimai/user-orders");
            modelAndView.addObject("message", "任务不存在!");
            return modelAndView;
        }
        //完成任务
        taskService.complete(task.getId());

        // 更新流程变量
        String processInstanceId = task.getProcessInstanceId();
        runtimeService.setVariable(processInstanceId, "status", "已完成~~~");
        ModelAndView modelAndView = new ModelAndView("waimai/user-orders");
        modelAndView.addObject("message", "确认订单成功!");
        return modelAndView;
    }

}
