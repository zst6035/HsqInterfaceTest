package com.hsq.cases;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

public class Test {
    @org.testng.annotations.Test
    public void test01() throws Exception{
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet get=new HttpGet("https://test-api.huishouqian.com/oms/resource/html?url=agreement/acquiring/search");
        //添加cookie的方法
        get.addHeader("Cookie","SESSION=cd726ace-055d-421b-bb09-7d9907deb1a0; uid=1");
     HttpResponse response= httpClient.execute(get);
    HttpEntity entity= response.getEntity() ;
    String reuslt= EntityUtils.toString(entity,"utf-8");
        System.out.println(reuslt);
    }
}
