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
/*
* 协议支付绑卡--建设银行
* 协议支付确认绑卡
* 协议支付直接支付
* 协议支付分账
* 协议支付退款（对已分账的进行归集）
* 协议支付含营销户
*
*
* */
@Slf4j
public class AgreePay {
    ReqInfo reqInfo;
    EncAndDnc encAndDnc=new EncAndDnc();
    String uniqueCode=null;
    String agreePayNo=null;
    Map map=TestConfig.getMap();
    String transNo=TestConfig.dateString();
    String transNo2=TestConfig.dateString();

    //协议支付
    @BeforeClass
    public void BeforePay(){
       log.info("测试之前");
    }

    @Test(description = "协议支付预绑卡",groups = "bind" ,testName = "协议支付预绑卡")
    public void AgreePayPreBind(){
        reqInfo = TestConfig.sessionLocalhost.selectOne("com.hsq.selReqInfo","协议支付预绑卡");
        JSONObject jsonObject= JSONObject.parseObject(reqInfo.getSignContent());
        jsonObject.put("requestDate", TestConfig.dateString());
        jsonObject.put("transNo",TestConfig.dateString());
        jsonObject.put("accNo","621082"+TestConfig.getRandom4(9)+"2");
        //加密后的字符串
        String signContent= encAndDnc.encMessage(jsonObject.toString());
        //   log.info("加密后的字符串："+signContent);
        reqInfo.setSignContent(signContent);
        map.put("method",reqInfo.getMethod());
        map.put("signContent",signContent);
        String result=  TestConfig.HttpSend(reqInfo.getUrl(),map);
        JSONObject jsonObject1= JSONObject.parseObject(result);

        Assert.assertEquals(jsonObject1.get("success"),true);

        //获取唯一编码：
        String res=encAndDnc.dencMessage(result);
        JSONObject jsonObject2= JSONObject.parseObject(res);
        uniqueCode= (String) jsonObject2.get("uniqueCode");
        log.info("唯一编码是"+uniqueCode);
        log.info("响应明文结果"+res);


    }

    @Test(description = "协议支付确认绑卡",dependsOnMethods = "AgreePayPreBind",groups = "bind",testName ="协议支付确认绑卡" )
    public void AgreePayConfigBind(){
        reqInfo = TestConfig.sessionLocalhost.selectOne("com.hsq.selReqInfo","协议支付确认绑卡");
        JSONObject jsonObject= JSONObject.parseObject(reqInfo.getSignContent());
        jsonObject.put("requestDate", TestConfig.dateString());
        jsonObject.put("uniqueCode",uniqueCode);

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
        log.info("响应明文结果"+res);
        Assert.assertEquals(jsonObject2.get("respMsg"),"交易成功");

    }


   // @Test(description = "协议支付直接支付" ,dependsOnGroups = "bind",testName = "协议支付直接支付")
    @Test(description = "协议支付直接支付" ,testName = "协议支付直接支付")
    public void AgreeDirectPay(){
        reqInfo = TestConfig.sessionLocalhost.selectOne("com.hsq.selReqInfo","协议支付直接支付");
        JSONObject jsonObject= JSONObject.parseObject(reqInfo.getSignContent());
        jsonObject.put("transNo",transNo);
        jsonObject.put("requestDate", TestConfig.dateString());
     //  jsonObject.put("agreePayNo", agreePayNo);
        //协议支付可以直接成功的交易
        jsonObject.put("agreePayNo","1632340150846163");


        //加密后的字符串
        String signContent= encAndDnc.encMessage(jsonObject.toString());

        reqInfo.setSignContent(signContent);
        map.put("method",reqInfo.getMethod());
        map.put("signContent",signContent);
        log.info("请求明文"+map.toString());
        String result=  TestConfig.HttpSend(reqInfo.getUrl(),map);
        JSONObject jsonObject1= JSONObject.parseObject(result);
        log.info("响应明文结果"+encAndDnc.dencMessage(result));
        Assert.assertEquals(jsonObject1.get("success"),true);

    }


    @Test(description = "协议支付订单查询",testName = "协议支付订单查询")
    public void AgreePayQuery(){
        reqInfo = TestConfig.sessionLocalhost.selectOne("com.hsq.selReqInfo","协议支付订单查询");
        JSONObject jsonObject= JSONObject.parseObject(reqInfo.getSignContent());
        jsonObject.put("transNo",transNo);
        jsonObject.put("requestDate", TestConfig.dateString());
        //加密后的字符串
        String signContent= encAndDnc.encMessage(jsonObject.toString());

        reqInfo.setSignContent(signContent);
        map.put("method",reqInfo.getMethod());
        map.put("signContent",signContent);
        log.info("请求明文"+jsonObject.toString());
        //获取请求结果，密文string
        String result=  TestConfig.HttpSend(reqInfo.getUrl(),map);
        JSONObject jsonObject1= JSONObject.parseObject(result);
        log.info("响应明文结果"+encAndDnc.dencMessage(result));
        Assert.assertEquals(jsonObject1.get("success"),true);
    }



    //分账，只能向非消费户分账，消费户是不支持分账的
   @Test(description = "协议支付分账" ,dependsOnMethods ="AgreeDirectPay",testName ="协议支付分账" )
  //  @Test(description = "协议支付分账" , testName ="协议支付分账" )
    public void AgreeShare(){
        reqInfo = TestConfig.sessionLocalhost.selectOne("com.hsq.selReqInfo","协议支付分账");
        JSONObject jsonObject= JSONObject.parseObject(reqInfo.getSignContent());
        jsonObject.put("transNo",TestConfig.dateString());
        jsonObject.put("requestDate", TestConfig.dateString());
        //向指定账户转账
       // jsonObject.put("origTransNo","20210517163230");
        jsonObject.put("origTransNo",transNo);

        //加密后的字符串
        String signContent= encAndDnc.encMessage(jsonObject.toString());

        reqInfo.setSignContent(signContent);
        map.put("method",reqInfo.getMethod());
        map.put("signContent",signContent);
        log.info("请求明文"+jsonObject.toString());
        //获取请求结果，密文string
        String result=  TestConfig.HttpSend(reqInfo.getUrl(),map);
        JSONObject jsonObject1= JSONObject.parseObject(result);
        log.info("响应明文结果"+encAndDnc.dencMessage(result));
        Assert.assertEquals(jsonObject1.get("success"),true);
    }

    //含已分账的归集回来
     @Test(description = "协议支付退款" ,dependsOnMethods = "AgreeDirectPay")
    public void AgreePayRefund(){
        reqInfo = TestConfig.sessionLocalhost.selectOne("com.hsq.selReqInfo","协议支付退款");
        JSONObject jsonObject= JSONObject.parseObject(reqInfo.getSignContent());
        jsonObject.put("transNo",TestConfig.dateString());
        jsonObject.put("requestDate", TestConfig.dateString());
        jsonObject.put("origTransNo",transNo);

        //加密后的字符串
        String signContent= encAndDnc.encMessage(jsonObject.toString());

        reqInfo.setSignContent(signContent);
        map.put("method",reqInfo.getMethod());
        map.put("signContent",signContent);
        log.info("请求明文"+map.toString());
        String result=  TestConfig.HttpSend(reqInfo.getUrl(),map);
        JSONObject jsonObject1= JSONObject.parseObject(result);
        log.info("响应明文结果"+encAndDnc.dencMessage(result));
        Assert.assertEquals(jsonObject1.get("success"),true);
    }



    //1+1
    @Test(description = "协议支付含营销户" ,dependsOnGroups = "bind")
    public void AgreeDirectPayIncludeMarketing(){
        reqInfo = TestConfig.sessionLocalhost.selectOne("com.hsq.selReqInfo","协议支付含营销户");
        JSONObject jsonObject= JSONObject.parseObject(reqInfo.getSignContent());
        jsonObject.put("transNo",transNo2);
        jsonObject.put("requestDate", TestConfig.dateString());
        jsonObject.put("agreePayNo", agreePayNo);

        //加密后的字符串
        String signContent= encAndDnc.encMessage(jsonObject.toString());

        reqInfo.setSignContent(signContent);
        map.put("method",reqInfo.getMethod());
        map.put("signContent",signContent);
        log.info("请求明文"+map.toString());
        String result=  TestConfig.HttpSend(reqInfo.getUrl(),map);
        JSONObject jsonObject1= JSONObject.parseObject(result);
        log.info("响应明文结果"+encAndDnc.dencMessage(result));
        Assert.assertEquals(jsonObject1.get("success"),true);
    }
    //协议支付含营销户退款

    @Test(description = "协议支付含营销户退款" ,dependsOnMethods = "AgreeDirectPay")
    public void AgreePayRefundMarketing(){
        reqInfo = TestConfig.sessionLocalhost.selectOne("com.hsq.selReqInfo","协议支付含营销户退款");
        JSONObject jsonObject= JSONObject.parseObject(reqInfo.getSignContent());
        jsonObject.put("transNo",TestConfig.dateString());
        jsonObject.put("requestDate", TestConfig.dateString());
        jsonObject.put("origTransNo",transNo2);

        //加密后的字符串
        String signContent= encAndDnc.encMessage(jsonObject.toString());

        reqInfo.setSignContent(signContent);
        map.put("method",reqInfo.getMethod());
        map.put("signContent",signContent);
        log.info("请求明文"+map.toString());
        String result=  TestConfig.HttpSend(reqInfo.getUrl(),map);
        JSONObject jsonObject1= JSONObject.parseObject(result);
        log.info("响应明文结果"+encAndDnc.dencMessage(result));
        Assert.assertEquals(jsonObject1.get("success"),true);
    }

}
