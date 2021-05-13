package com.hsq.cases;

import com.alibaba.fastjson.JSONObject;
import com.hsq.model.ReqInfo;
import com.hsq.utils.EncAndDnc;
import com.hsq.utils.TestConfig;
import lombok.extern.slf4j.Slf4j;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;
//代理商进件一级商户报备商户，开会+报备smid
//此处平台商作为代理商使用
/*
* 企业报备
* 个人报备
* 个体工商户报备
* 报备查询
* */

@Slf4j
public class AgentRegister {
    ReqInfo reqInfo;
    EncAndDnc encAndDnc=new EncAndDnc();
    String name=TestConfig.getChineseName(1);
    Map map;
   String transNo=TestConfig.dateString();

    @BeforeClass
    public void BeforePay(){
        map=new HashMap();

        map.put("agentMerchantNo",TestConfig.merchant.getMerchantNo());
        map.put("version","5.0.0");
        map.put("format","json");
        map.put("signType","CFCA");
        map.put("sign",null);
    }


    @Test(description = "代理商报备企业平台商")
    public void  AgentCompanyRegister(){
        reqInfo = TestConfig.sessionLocalhost.selectOne("com.hsq.selReqInfo","代理商报备企业商户");
        JSONObject jsonObject= JSONObject.parseObject(reqInfo.getSignContent());
        jsonObject.put("bankCardNo","622848"+TestConfig.getRandom4(13));
        jsonObject.put("transNo",transNo);
        jsonObject.put("contactName",name);
        jsonObject.put("contactPhone","131"+TestConfig.getRandom4(8));
        jsonObject.put("servicePhone","131"+TestConfig.getRandom4(8));
        log.info("请求明文："+jsonObject.toString());
        //加密后的字符串
        String signContent= encAndDnc.encMessage(jsonObject.toString());
        reqInfo.setSignContent(signContent);
        map.put("method",reqInfo.getMethod());
        map.put("signContent",signContent);
        String result=  TestConfig.HttpSend(reqInfo.getUrl(),map);

        String res=encAndDnc.dencMessage(result);
        //再将请求结果转换为json格式
        JSONObject jsonObject2= JSONObject.parseObject(res);
        Assert.assertEquals(jsonObject2.get("respMsg"),"受理成功");
        log.info("代理商报备企业平台商响应明文结果"+res);
    }


    @Test(description = "代理商报备个体工商户")
    public void  AgentLegalRegister(){
        reqInfo = TestConfig.sessionLocalhost.selectOne("com.hsq.selReqInfo","代理商报备个体工商户");
        JSONObject jsonObject= JSONObject.parseObject(reqInfo.getSignContent());
        jsonObject.put("bankCardNo","622848"+TestConfig.getRandom4(13));
        jsonObject.put("transNo",TestConfig.dateString());
        jsonObject.put("contactName",name);
        jsonObject.put("contactPhone","131"+TestConfig.getRandom4(8));
        jsonObject.put("servicePhone","131"+TestConfig.getRandom4(8));
        log.info("请求明文："+jsonObject.toString());
        //加密后的字符串
        String signContent= encAndDnc.encMessage(jsonObject.toString());
        reqInfo.setSignContent(signContent);
        map.put("method",reqInfo.getMethod());
        map.put("signContent",signContent);
        String result=  TestConfig.HttpSend(reqInfo.getUrl(),map);
        //返回数据转换为json
        // JSONObject jsonObject1=JSONObject.parseObject(result);
        //请求结果进行解密
        String res=encAndDnc.dencMessage(result);
        //再将请求结果转换为json格式
        JSONObject jsonObject2= JSONObject.parseObject(res);
        Assert.assertEquals(jsonObject2.get("respMsg"),"受理成功");
        log.info("代理商报备个体工商户响应明文结果"+res);
    }


    @Test(description = "代理商报备自然人商户")
    public void AgentPersonalRegister(){
        reqInfo = TestConfig.sessionLocalhost.selectOne("com.hsq.selReqInfo","代理商报备自然人商户");
        JSONObject jsonObject= JSONObject.parseObject(reqInfo.getSignContent());
        //修改一些常用变量
        jsonObject.put("bankCardNo","622848"+TestConfig.getRandom4(13));
        jsonObject.put("transNo",TestConfig.dateString());
        jsonObject.put("name",name);
        jsonObject.put("bankPhone","131"+TestConfig.getRandom4(8));
        jsonObject.put("phone","131"+TestConfig.getRandom4(8));
        log.info("请求明文："+jsonObject.toString());
        //加密后的字符串
        String signContent= encAndDnc.encMessage(jsonObject.toString());
        reqInfo.setSignContent(signContent);
        map.put("method",reqInfo.getMethod());
        map.put("signContent",signContent);
        String result=  TestConfig.HttpSend(reqInfo.getUrl(),map);
        String res=encAndDnc.dencMessage(result);
        //再将请求结果转换为json格式
        JSONObject jsonObject2= JSONObject.parseObject(res);
        Assert.assertEquals(jsonObject2.get("respMsg"),"受理成功");
        log.info("代理商报备自然人商户响应明文结果"+res);
    }

//报备结果查询
@Test(description = "代理商报备结果查询",dependsOnMethods = "AgentCompanyRegister")
public void  AgentRegisterResult(){
    reqInfo = TestConfig.sessionLocalhost.selectOne("com.hsq.selReqInfo","代理商报备结果查询");
    JSONObject jsonObject= JSONObject.parseObject(reqInfo.getSignContent());
    jsonObject.put("transNo",transNo);
    log.info("请求明文："+jsonObject.toString());
    //加密后的字符串
    String signContent= encAndDnc.encMessage(jsonObject.toString());
    reqInfo.setSignContent(signContent);
    map.put("method",reqInfo.getMethod());
    map.put("signContent",signContent);
    String result=  TestConfig.HttpSend(reqInfo.getUrl(),map);
    String res=encAndDnc.dencMessage(result);
    //再将请求结果转换为json格式
    JSONObject jsonObject2= JSONObject.parseObject(res);
    Assert.assertEquals(jsonObject2.get("respMsg"),"受理成功");
    log.info("报备结果响应明文结果"+res);
}



    @Test(description = "代理商报备结果查询2")
    public void  AgentRegisterResult2(){
        reqInfo = TestConfig.sessionLocalhost.selectOne("com.hsq.selReqInfo","代理商报备结果查询");
        JSONObject jsonObject= JSONObject.parseObject(reqInfo.getSignContent());
        //对一个已经报备好的进行查询
        jsonObject.put("transNo","20210513153051");
        log.info("请求明文："+jsonObject.toString());
        //加密后的字符串
        String signContent= encAndDnc.encMessage(jsonObject.toString());
        reqInfo.setSignContent(signContent);
        map.put("method",reqInfo.getMethod());
        map.put("signContent",signContent);
        String result=  TestConfig.HttpSend(reqInfo.getUrl(),map);
        String res=encAndDnc.dencMessage(result);
        //再将请求结果转换为json格式
        JSONObject jsonObject2= JSONObject.parseObject(res);
        Assert.assertEquals(jsonObject2.get("respMsg"),"成功");
        log.info("报备结果响应明文结果2"+res);
    }
}
