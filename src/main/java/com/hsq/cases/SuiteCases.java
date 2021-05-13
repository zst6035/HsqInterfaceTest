package com.hsq.cases;

import com.hsq.utils.DatabaseUtil;
import com.hsq.utils.TestConfig;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.testng.Reporter;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

public class SuiteCases {
    @BeforeSuite
    public void beforeSuite()throws Exception{
        //声明session ,httpclient,cookie,ip地址等
        TestConfig.defaultHttpClient= HttpClientBuilder.create().build();
//        TestConfig.store=new BasicCookieStore();
        TestConfig.sessionLocalhost= DatabaseUtil.getSqlSession1();
        String cookie_value= TestConfig.sessionLocalhost.selectOne("selvalue","cookie");
      //  System.out.println(cookie_value);
        Reporter.log(cookie_value);
        BasicClientCookie basicClientCookie=new BasicClientCookie("EHR_COOKIES20190505ERWFSDF",cookie_value);
        TestConfig.store.addCookie(basicClientCookie);
        TestConfig.url= TestConfig.sessionLocalhost.selectOne("selvalue","ip_test");
        basicClientCookie.setDomain(TestConfig.url);
        //basicClientCookie.setDomain("tehr.mandao.com");
        basicClientCookie.setPath("/");
   //     TestConfig.defaultHttpClient.setCookieStore(TestConfig.store);
        TestConfig.userId=Integer.parseInt(TestConfig.sessionLocalhost.selectOne("selvalue","userId").toString()) ;
    }

    @AfterSuite
    public void afterSuite()throws Exception{
        //关闭连接
        TestConfig.sessionLocalhost.close();
    }
}
