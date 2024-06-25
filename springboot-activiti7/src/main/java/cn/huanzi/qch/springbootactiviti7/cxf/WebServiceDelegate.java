package cn.huanzi.qch.springbootactiviti7.cxf;

import lombok.SneakyThrows;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.JavaDelegate;
import org.activiti.engine.delegate.VariableScope;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;

public class WebServiceDelegate implements JavaDelegate {

    private Expression wsdl;

    private Expression param;

    public Expression getWsdl() {
        return wsdl;
    }

    public void setWsdl(Expression wsdl) {
        this.wsdl = wsdl;
    }

    public Expression getParam() {
        return param;
    }

    public void setParam(Expression param) {
        this.param = param;
    }

    @Override
    public void execute(DelegateExecution delegateExecution) {

        try {
            JaxWsDynamicClientFactory dcf = JaxWsDynamicClientFactory.newInstance();
            // 使用wsdl路径创建Client
            Client client = dcf.createClient((String) wsdl.getValue(null));
            // 使用配置的值创建参数对象
            Object[] vars = new Object[] { (String)param.getValue(null) };
            // 调用
            Object[] object = client
                    .invoke("func", vars);
            String result = (String) object[0];
            System.out.println("在JavaDelegate中调用Web Service后，结果: "
                    + result);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @SneakyThrows
    public static void main(String[] args) {
        JaxWsDynamicClientFactory dcf = JaxWsDynamicClientFactory.newInstance();
        // 使用wsdl路径创建Client
        Client client = dcf.createClient("http://localhost:10010/services/myWebService?wsdl");
        // 使用配置的值创建参数对象
        Object[] vars = new Object[] { "你好啊" };
        // 调用
        Object[] object = client.invoke("func", vars);
        String result = (String) object[0];
        System.out.println("在JavaDelegate中调用Web Service后，结果: "
                + result);
    }
}
