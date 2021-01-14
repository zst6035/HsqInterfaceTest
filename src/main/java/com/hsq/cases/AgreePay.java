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

@Slf4j
public class AgreePay {
    ReqInfo reqInfo;
    EncAndDnc encAndDnc;
    String uniqueCode=null;
    String agreePayNo=null;
    Map map;
    String transNo=TestConfig.dateString();

    //协议支付
    @BeforeClass
    public void BeforePay(){
        map=new HashMap();
        /*由接口可以知道，map的值几乎是固定的，唯一的变化就是会随着method的不同，进行变化，
        那么对于数据库来说，只需要存储变化的内容即可，所以，再新增一张表，将method，以及signContent进行入库；
        */
        //这些针对所有请求都是一样的，不变化的；
        map.put("merchantNo",TestConfig.merchant.getMerchantNo());
        map.put("version","1.0.0");
        map.put("format","json");
        map.put("signType","CFCA");
        map.put("sign",null);
    }

    @Test(description = "协议支付预绑卡",groups = "bind" ,testName = "协议支付预绑卡")
    public void AgreePayPreBind(){
        reqInfo = TestConfig.session.selectOne("com.hsq.selReqInfo","协议支付预绑卡");
        JSONObject jsonObject= JSONObject.parseObject(reqInfo.getSignContent());
        jsonObject.put("requestDate", TestConfig.dateString());
        jsonObject.put("transNo",TestConfig.dateString());

        encAndDnc=new EncAndDnc();
        //加密后的字符串
        String signContent= encAndDnc.encMessage(jsonObject.toString());
        //   log.info("加密后的字符串："+signContent);
        reqInfo.setSignContent(signContent);
        map.put("method",reqInfo.getMethod());
        map.put("signContent",signContent);
        String result=  TestConfig.HttpSend(reqInfo.getUrl(),map);
        JSONObject jsonObject1= JSONObject.parseObject(result);

        Assert.assertEquals(jsonObject1.get("success"),true);
        log.info("响应密文："+jsonObject1.toString());
        //获取唯一编码：
        String res=encAndDnc.dencMessage(result);
        JSONObject jsonObject2= JSONObject.parseObject(res);
        uniqueCode= (String) jsonObject2.get("uniqueCode");
        log.info("唯一编码是"+uniqueCode);
        log.info("响应明文结果"+res);


    }

    @Test(description = "协议支付确认绑卡",dependsOnMethods = "AgreePayPreBind",groups = "bind",testName ="协议支付确认绑卡" )
    public void AgreePayConfigBind(){
        reqInfo = TestConfig.session.selectOne("com.hsq.selReqInfo","协议支付确认绑卡");
        JSONObject jsonObject= JSONObject.parseObject(reqInfo.getSignContent());
        jsonObject.put("requestDate", TestConfig.dateString());
        jsonObject.put("uniqueCode",uniqueCode);


        encAndDnc=new EncAndDnc();
        //加密后的字符串
        String signContent= encAndDnc.encMessage(jsonObject.toString());
        // log.info("加密后的字符串："+signContent);
        reqInfo.setSignContent(signContent);
        map.put("method",reqInfo.getMethod());
        map.put("signContent",signContent);
        log.info("协议支付确认绑卡请求报文"+map.toString());
        String result=  TestConfig.HttpSend(reqInfo.getUrl(),map);
        JSONObject jsonObject1= JSONObject.parseObject(result);
        String res=encAndDnc.dencMessage(result);
        JSONObject jsonObject2= JSONObject.parseObject(res);
        agreePayNo= (String) jsonObject2.get("agreePayNo");
        log.info("agreePayNo"+agreePayNo);
        Assert.assertEquals(jsonObject2.get("respMsg"),"交易成功");
        log.info("响应密文："+jsonObject1.toString());
        log.info("响应明文结果"+res);


    }


    @Test(description = "协议支付直接支付" ,dependsOnGroups = "bind",testName = "协议支付直接支付")
    public void AgreeDirectPay(){
        reqInfo = TestConfig.session.selectOne("com.hsq.selReqInfo","协议支付直接支付");
        JSONObject jsonObject= JSONObject.parseObject(reqInfo.getSignContent());
        jsonObject.put("transNo",transNo);
        jsonObject.put("requestDate", TestConfig.dateString());
        jsonObject.put("agreePayNo", agreePayNo);
        encAndDnc=new EncAndDnc();
        //加密后的字符串
        String signContent= encAndDnc.encMessage(jsonObject.toString());

        reqInfo.setSignContent(signContent);
        map.put("method",reqInfo.getMethod());
        map.put("signContent",signContent);
        log.info("请求明文"+map.toString());
        String result=  TestConfig.HttpSend(reqInfo.getUrl(),map);
        JSONObject jsonObject1= JSONObject.parseObject(result);
        Assert.assertEquals(jsonObject1.get("success"),true);
        log.info("响应密文："+jsonObject1.toString());
        log.info("响应明文结果"+encAndDnc.dencMessage(result));
        //  System.out.println("响应明文结果"+encAndDnc.dencMessage(result));

    }
    @Test(description = "协议支付含营销户" ,dependsOnGroups = "bind")
    public void AgreeDirectPayIncludeMarketing(){
        reqInfo = TestConfig.session.selectOne("com.hsq.selReqInfo","协议支付含营销户");
        JSONObject jsonObject= JSONObject.parseObject(reqInfo.getSignContent());
        jsonObject.put("transNo",TestConfig.dateString());
        jsonObject.put("requestDate", TestConfig.dateString());
        jsonObject.put("agreePayNo", agreePayNo);
        encAndDnc=new EncAndDnc();
        //加密后的字符串
        String signContent= encAndDnc.encMessage(jsonObject.toString());

        reqInfo.setSignContent(signContent);
        map.put("method",reqInfo.getMethod());
        map.put("signContent",signContent);
        log.info("请求明文"+map.toString());
        String result=  TestConfig.HttpSend(reqInfo.getUrl(),map);
        JSONObject jsonObject1= JSONObject.parseObject(result);
        Assert.assertEquals(jsonObject1.get("success"),true);
        log.info("响应密文："+jsonObject1.toString());
        log.info("响应明文结果"+encAndDnc.dencMessage(result));
        //  System.out.println("响应明文结果"+encAndDnc.dencMessage(result));

    }

    @Test(description = "协议支付订单查询",testName = "协议支付订单查询")
    public void AgreePayQuery(){
        reqInfo = TestConfig.session.selectOne("com.hsq.selReqInfo","协议支付订单查询");
        JSONObject jsonObject= JSONObject.parseObject(reqInfo.getSignContent());
        jsonObject.put("transNo",transNo);
        jsonObject.put("requestDate", TestConfig.dateString());

        encAndDnc=new EncAndDnc();
        //加密后的字符串
        String signContent= encAndDnc.encMessage(jsonObject.toString());

        reqInfo.setSignContent(signContent);
        map.put("method",reqInfo.getMethod());
        map.put("signContent",signContent);
        log.info("请求明文"+jsonObject.toString());
        //获取请求结果，密文string
        String result=  TestConfig.HttpSend(reqInfo.getUrl(),map);
        JSONObject jsonObject1= JSONObject.parseObject(result);
        Assert.assertEquals(jsonObject1.get("success"),true);
        log.info("响应密文："+jsonObject1.toString());
        log.info("响应明文结果"+encAndDnc.dencMessage(result));


    }

    @Test(description = "协议支付分账" ,dependsOnMethods ="AgreeDirectPay",testName ="协议支付分账" )
    public void AgreeShare(){
        reqInfo = TestConfig.session.selectOne("com.hsq.selReqInfo","协议支付分账");
        JSONObject jsonObject= JSONObject.parseObject(reqInfo.getSignContent());
        jsonObject.put("transNo",TestConfig.dateString());
        jsonObject.put("requestDate", TestConfig.dateString());
        jsonObject.put("origTransNo",transNo);
        encAndDnc=new EncAndDnc();
        //加密后的字符串
        String signContent= encAndDnc.encMessage(jsonObject.toString());

        reqInfo.setSignContent(signContent);
        map.put("method",reqInfo.getMethod());
        map.put("signContent",signContent);
        log.info("请求明文"+jsonObject.toString());
        //获取请求结果，密文string
        String result=  TestConfig.HttpSend(reqInfo.getUrl(),map);
        JSONObject jsonObject1= JSONObject.parseObject(result);
        Assert.assertEquals(jsonObject1.get("success"),true);
        log.info("响应密文："+jsonObject1.toString());
        log.info("响应明文结果"+encAndDnc.dencMessage(result));


    }
}
