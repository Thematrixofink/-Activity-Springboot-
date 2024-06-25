package cn.huanzi.qch.springbootactiviti7;

import org.activiti.engine.*;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentQuery;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class  SpringbootActiviti7ApplicationTests {

    @Autowired
    private RepositoryService repositoryService;


    /**
     * 流程部署
     */
    @Test
    public void test() {

        //查询已部署的流程
        DeploymentQuery query = repositoryService.createDeploymentQuery();
        List<Deployment> list = query.deploymentKey("流程关键字").list();
        //List<Deployment> list = query.deploymentId("c5b139e7-231b-11ef-bb03-8c17594e7add").list();
        System.out.println(list.size());
        repositoryService.createDeployment()
                .addClasspathResource("bpm/askForLeaveBpm.bpmn")
                .name("请假流程")
                .key("ASK_FOR_LEAVE_ACT")
                .deploy();

        System.out.println("流程部署成功！");
    }


    @Test
    public void testOrder() {

        //查询已部署的流程
        DeploymentQuery query = repositoryService.createDeploymentQuery();
        //查询部署的所有的
        List<Deployment> list1 = query.list();
        //根据key来查询部署的所有的
        List<Deployment> list2 = query.deploymentKey("流程关键字").list();
        //根据ID查询部署的所有的
        List<Deployment> list3 = query.deploymentId("c5b139e7-231b-11ef-bb03-8c17594e7add").list();
        //获取流程定义的信息
        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery();
        List<ProcessDefinition> list = processDefinitionQuery.list();
        //从list里面各个对象可以查询到一些信息

        //部署流程
        repositoryService.createDeployment()
                .addClasspathResource("bpm/waimai/order-process.bpmn20.xml")
                .name("下单流程")
                .key("ORDER_ACT")
                .deploy();
        System.out.println("流程部署成功！");
    }
}
