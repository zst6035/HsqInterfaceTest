package com.hsq.cases;

import com.hsq.utils.DatabaseUtil;
import com.hsq.utils.TestConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.testng.Reporter;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;



@Slf4j
public class SuiteCases {
    @BeforeSuite
    public void beforeSuite()throws Exception{
        log.info("开始执行测试");
    }

    @AfterSuite
    public void afterSuite()throws Exception{
        //关闭连接
        TestConfig.sessionLocalhost.close();
    }
}
