package cn.huanzi.qch.springbootactiviti7.cxf;

import javax.jws.WebParam;
import javax.jws.WebService;

@WebService
public interface CxfService {

    String func(@WebParam(name = "param") String param);
}
