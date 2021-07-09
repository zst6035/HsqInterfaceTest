package com.hsq.cases;

import com.alibaba.fastjson.JSONObject;
import com.hsq.model.ReqInfo;
import com.hsq.utils.DatabaseUtil;
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

    String name=TestConfig.getChineseName(1);
    Map map;
   String transNo=TestConfig.transNo();

    @BeforeClass
    public void BeforePay(){
        map=new HashMap();

        map.put("agentMerchantNo",TestConfig.merchant.getMerchantNo());
        map.put("version","5.0.0");
        map.put("format","json");
        map.put("signType","CFCA");
        map.put("sign",null);
    }


   @Test(description = "代理商报备企业平台商" )
   // @Test(description = "代理商报备企业平台商" ,enabled = false)
    public void  AgentCompanyRegister()throws Exception{
       ReqInfo reqInfo = DatabaseUtil.getSqlSession1().selectOne("com.hsq.selReqInfo","代理商报备企业商户");
        JSONObject jsonObject= JSONObject.parseObject(reqInfo.getSignContent());
        jsonObject.put("bankCardNo","622848"+TestConfig.getRandom4(13));
        jsonObject.put("transNo",transNo);
        jsonObject.put("contactName",name);
        /*法人证件类型：1大陆身份证 2护照 3 港澳台 4台湾居民 5其他*/
        jsonObject.put("legalIdcardType","3");
        jsonObject.put("contactPhone","131"+TestConfig.getRandom4(8));
        jsonObject.put("servicePhone","131"+TestConfig.getRandom4(8));
        log.info("请求明文："+jsonObject.toString());
        //加密后的字符串
        EncAndDnc e1=new EncAndDnc();
        String signContent=e1.encMessage(jsonObject.toString());
        reqInfo.setSignContent(signContent);
        map.put("method",reqInfo.getMethod());
        map.put("signContent",signContent);
        String result=  TestConfig.HttpSend(reqInfo.getUrl(),map);

        String res=e1.dencMessage(result);
        //再将请求结果转换为json格式
        JSONObject jsonObject2= JSONObject.parseObject(res);
        Assert.assertEquals(jsonObject2.get("respMsg"),"受理成功");
        log.info("代理商报备企业平台商响应明文结果"+res);
    }


    @Test(description = "代理商报备个体工商户")
    public void  AgentLegalRegister()throws Exception{
       ReqInfo reqInfo = DatabaseUtil.getSqlSession1().selectOne("com.hsq.selReqInfo","代理商报备个体工商户");
        JSONObject jsonObject= JSONObject.parseObject(reqInfo.getSignContent());
        //jsonObject.put("bankCardNo","622848"+TestConfig.getRandom4(13));
        jsonObject.put("transNo",TestConfig.transNo());
        jsonObject.put("contactName",name);
        jsonObject.put("contactPhone","131"+TestConfig.getRandom4(8));
        jsonObject.put("servicePhone","131"+TestConfig.getRandom4(8));
        log.info("请求明文："+jsonObject.toString());
        //加密后的字符串
        EncAndDnc e2=new EncAndDnc();
        String signContent= e2.encMessage(jsonObject.toString());
        reqInfo.setSignContent(signContent);
        map.put("method",reqInfo.getMethod());
        map.put("signContent",signContent);
        String result=  TestConfig.HttpSend(reqInfo.getUrl(),map);
        //返回数据转换为json
        // JSONObject jsonObject1=JSONObject.parseObject(result);
        //请求结果进行解密
        String res=e2.dencMessage(result);
        //再将请求结果转换为json格式
        JSONObject jsonObject2= JSONObject.parseObject(res);
        Assert.assertEquals(jsonObject2.get("respMsg"),"受理成功");
        log.info("代理商报备个体工商户响应明文结果"+res);
    }


    @Test(description = "代理商报备自然人商户")
    public void AgentPersonalRegister()throws Exception{
      ReqInfo  reqInfo = DatabaseUtil.getSqlSession1().selectOne("com.hsq.selReqInfo","代理商报备自然人商户");
        JSONObject jsonObject= JSONObject.parseObject(reqInfo.getSignContent());
        //修改一些常用变量,银行卡号需要是真实的
        jsonObject.put("bankCardNo","6214850210371263");
        jsonObject.put("transNo",TestConfig.transNo());
        jsonObject.put("name","张守婷");
        jsonObject.put("merchantName","张守婷");
        jsonObject.put("bankPhone","131"+TestConfig.getRandom4(8));
        jsonObject.put("phone","131"+TestConfig.getRandom4(8));
        log.info("请求明文："+jsonObject.toString());
        //加密后的字符串
        EncAndDnc e3=new EncAndDnc();
        String signContent= e3.encMessage(jsonObject.toString());
        reqInfo.setSignContent(signContent);
        map.put("method",reqInfo.getMethod());
        map.put("signContent",signContent);
        String result=  TestConfig.HttpSend(reqInfo.getUrl(),map);
        String res=e3.dencMessage(result);
        //再将请求结果转换为json格式
        JSONObject jsonObject2= JSONObject.parseObject(res);
        log.info("代理商报备自然人商户响应明文结果"+res);
        Assert.assertEquals(jsonObject2.get("respMsg"),"受理成功");

    }

//报备结果查询
@Test(description = "代理商报备结果查询",dependsOnMethods = "AgentCompanyRegister")
public void  AgentRegisterResult()throws Exception{
    ReqInfo reqInfo = DatabaseUtil.getSqlSession1().selectOne("com.hsq.selReqInfo","代理商报备结果查询");
    JSONObject jsonObject= JSONObject.parseObject(reqInfo.getSignContent());
    jsonObject.put("transNo",transNo);
    log.info("请求明文："+jsonObject.toString());
    //加密后的字符串
    EncAndDnc e4=new EncAndDnc();
    String signContent= e4.encMessage(jsonObject.toString());
    reqInfo.setSignContent(signContent);
    map.put("method",reqInfo.getMethod());
    map.put("signContent",signContent);
    String result=  TestConfig.HttpSend(reqInfo.getUrl(),map);
    String res=e4.dencMessage(result);
    //再将请求结果转换为json格式
    JSONObject jsonObject2= JSONObject.parseObject(res);
    Assert.assertEquals(jsonObject2.get("respMsg"),"受理成功");
    log.info("报备结果响应明文结果"+res);
}



    @Test(description = "代理商报备结果查询2")
    public void  AgentRegisterResult2()throws Exception{
     ReqInfo   reqInfo = DatabaseUtil.getSqlSession1().selectOne("com.hsq.selReqInfo","代理商报备结果查询");
        JSONObject jsonObject= JSONObject.parseObject(reqInfo.getSignContent());
        //对一个已经报备好的进行查询
        jsonObject.put("transNo","20210513153051");
        log.info("请求明文："+jsonObject.toString());
        //加密后的字符串
        EncAndDnc e5=new EncAndDnc();
        String signContent= e5.encMessage(jsonObject.toString());
        reqInfo.setSignContent(signContent);
        map.put("method",reqInfo.getMethod());
        map.put("signContent",signContent);
        String result=  TestConfig.HttpSend(reqInfo.getUrl(),map);
        String res=e5.dencMessage(result);
        //再将请求结果转换为json格式
        JSONObject jsonObject2= JSONObject.parseObject(res);
        Assert.assertEquals(jsonObject2.get("respMsg"),"成功");
        log.info("报备结果响应明文结果2"+res);
    }
}
