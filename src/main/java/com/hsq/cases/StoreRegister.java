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
//门店报备,新增门店，并报备smid
/*
* 企业报备
* 个人报备
* 个体工商户报备
* 报备查询
* */

@Slf4j
public class StoreRegister {

 //   EncAndDnc encAndDnc=new EncAndDnc();
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
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Test(description = "门店个体工商户报备")
    public void  legalRegister() throws  Exception {
      ReqInfo  reqInfo = DatabaseUtil.getSqlSession1().selectOne("com.hsq.selReqInfo","门店个体工商户报备");
      JSONObject jsonObject= JSONObject.parseObject(reqInfo.getSignContent());
        jsonObject.put("bankCardNo","622848"+TestConfig.getRandom4(13));
        jsonObject.put("transNo",TestConfig.transNo());
        jsonObject.put("contactName",TestConfig.getChineseName(2));
        jsonObject.put("selfEmployed","2");
        jsonObject.put("contactPhone","131"+TestConfig.getRandom4(8));
        jsonObject.put("contactPhone","131"+TestConfig.getRandom4(8));
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
        Thread.sleep(5);
        //返回数据转换为json
        // JSONObject jsonObject1=JSONObject.parseObject(result);
        //请求结果进行解密
        String res=e2.dencMessage(result);
        //再将请求结果转换为json格式
        JSONObject jsonObject2= JSONObject.parseObject(res);
        Assert.assertEquals(jsonObject2.get("respMsg"),"受理成功");
        log.info("响应明文结果"+res);
    }


    @Test(description = "门店企业报备")
    public void  CompanyRegister()throws Exception{
        ReqInfo  reqInfo = DatabaseUtil.getSqlSession1().selectOne("com.hsq.selReqInfo","门店企业报备");
        JSONObject jsonObject= JSONObject.parseObject(reqInfo.getSignContent());
        jsonObject.put("bankCardNo","622848"+TestConfig.getRandom4(13));
        jsonObject.put("transNo",transNo);
        jsonObject.put("contactName",TestConfig.getChineseName(2));
        jsonObject.put("legalName",TestConfig.getChineseName(2));
        jsonObject.put("contactPhone","131"+TestConfig.getRandom4(8));
        jsonObject.put("servicePhone","131"+TestConfig.getRandom4(8));
        log.info("门店企业报备请求明文："+jsonObject.toString());

        //加密后的字符串
        EncAndDnc e7=new EncAndDnc();
        String signContent= e7.encMessage(jsonObject.toString());
        reqInfo.setSignContent(signContent);
        map.put("method",reqInfo.getMethod());
        map.put("signContent",signContent);
        log.info("========={}",map);
        String result=  TestConfig.HttpSend(reqInfo.getUrl(),map);
        log.info("========={}",result);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //返回数据转换为json
        // JSONObject jsonObject1=JSONObject.parseObject(result);
        //请求结果进行解密
        String res=e7.dencMessage(result);
        //再将请求结果转换为json格式
        JSONObject jsonObject2= JSONObject.parseObject(res);
        log.info("门店企业报备响应明文结果"+res);
        Assert.assertEquals(jsonObject2.get("respMsg"),"受理成功");

    }


    @Test(description = "门店个人报备")
    public void personalRegister()throws Exception{
        ReqInfo  reqInfo = DatabaseUtil.getSqlSession1().selectOne("com.hsq.selReqInfo","门店个人报备");
        JSONObject jsonObject= JSONObject.parseObject(reqInfo.getSignContent());
        jsonObject.put("bankCardNo","622848"+TestConfig.getRandom4(13));
        jsonObject.put("transNo",TestConfig.transNo());
        jsonObject.put("name",TestConfig.getChineseName(2));
        jsonObject.put("bankPhone","131"+TestConfig.getRandom4(8));
        jsonObject.put("phone","131"+TestConfig.getRandom4(8));
        log.info("请求明文："+jsonObject.toString());
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //加密后的字符串
        //这里不重新定义一个，会拿不到数据；所以就重新定义一个，有很多地方都有这毛病
        EncAndDnc e4=new EncAndDnc();
        String signContent= e4.encMessage(jsonObject.toString());
        reqInfo.setSignContent(signContent);
        map.put("method",reqInfo.getMethod());
        map.put("signContent",signContent);
        String result=  TestConfig.HttpSend(reqInfo.getUrl(),map);

        log.info("========={}",result);

        String res=e4.dencMessage(result);
        //再将请求结果转换为json格式
        JSONObject jsonObject2= JSONObject.parseObject(res);
        log.info("响应明文结果"+res);
        Assert.assertEquals(jsonObject2.get("respMsg"),"受理成功");

    }

//报备结果查询
@Test(description = "门店报备结果查询",dependsOnMethods = "CompanyRegister")
public void  RegisterResult()throws Exception{
    ReqInfo  reqInfo = DatabaseUtil.getSqlSession1().selectOne("com.hsq.selReqInfo","门店报备结果查询");
    JSONObject jsonObject= JSONObject.parseObject(reqInfo.getSignContent());
    jsonObject.put("transNo",transNo);
    log.info("请求明文："+jsonObject.toString());
    //加密后的字符串
    EncAndDnc e4=new EncAndDnc();
    String signContent= e4.encMessage(jsonObject.toString());
    reqInfo .setSignContent(signContent);
    map.put("method",reqInfo.getMethod());
    map.put("signContent",signContent);
    String result=  TestConfig.HttpSend(reqInfo.getUrl(),map);

    String res=e4.dencMessage(result);
    //再将请求结果转换为json格式
    JSONObject jsonObject2= JSONObject.parseObject(res);
    Assert.assertEquals(jsonObject2.get("respMsg"),"受理成功");
    log.info("响应明文结果"+res);
}

    //门店特约商户配置
    @Test(description = "门店特约商户配置")
    public void  StoreWxConfig()throws Exception{
        ReqInfo reqInfo = DatabaseUtil.getSqlSession1().selectOne("com.hsq.selReqInfo","门店特约商户配置");
        JSONObject jsonObject= JSONObject.parseObject(reqInfo.getSignContent());
      EncAndDnc  encAndDnc=new EncAndDnc();
        //加密后的字符串
        String signContent= encAndDnc.encMessage(jsonObject.toString());

        reqInfo.setSignContent(signContent);
        map.put("method",reqInfo.getMethod());
        map.put("signContent",signContent);
        //此处是字段是叫代理商，不叫商户号；
        map.put("agentMerchantNo","883007563082");
        map.remove("merchant");
        log.info("请求明文："+map.toString());
        String result=  TestConfig.HttpSend(reqInfo.getUrl(),map);
        log.info("响应密文："+result);
        JSONObject jsonObject1=JSONObject.parseObject(result);
        Assert.assertEquals(jsonObject1.get("errorMsg"),"商户特约配置异常");
    }



}
