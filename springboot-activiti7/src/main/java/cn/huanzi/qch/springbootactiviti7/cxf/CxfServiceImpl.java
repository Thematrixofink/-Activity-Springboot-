package cn.huanzi.qch.springbootactiviti7.cxf;

import org.springframework.stereotype.Component;

import javax.jws.WebService;

@Component
@WebService(targetNamespace = "http://cxf.springbootactiviti7.qch.huanzi.cn/",endpointInterface = "cn.huanzi.qch.springbootactiviti7.cxf.CxfService")
public class CxfServiceImpl implements CxfService{
    @Override
    public String func(String param) {
        System.out.println("收到：" + param);
        return "收到：" + param;
    }
}
