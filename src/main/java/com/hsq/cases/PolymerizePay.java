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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

//聚合交易
/*
* 主扫
* 修改交易
* 查询
* 分账
* 退款
*微信公众号下单
* 微信小程序下单
* 对于聚合交易，只能做到下单成功
* */
@Slf4j
public class PolymerizePay {
    ReqInfo reqInfo;
    EncAndDnc encAndDnc;
    Map map=TestConfig.getMap();
    String transNo=TestConfig.dateString();
    //渠道订单号
    String channelOrderNo=null;
    //交易订单号
    String tradeNo=null;


    //类运行之前执行
    @BeforeClass
    public void BeforePay(){
       log.info("聚合交易开始执行");

    }

//走宝付渠道，宝财通版本，收单后，分账
    @Test(description = "聚合支付支付宝主扫",groups = "alipay")
    public void aliPay(){
        //支付请求，替换requestDate与transNo,并且加密
        reqInfo = TestConfig.sessionLocalhost.selectOne("com.hsq.selReqInfo","聚合支付支付宝主扫");
        log.info("请求明文："+reqInfo.toString());
        JSONObject jsonObject= JSONObject.parseObject(reqInfo.getSignContent());
        jsonObject.put("requestDate", TestConfig.dateString());
        jsonObject.put("transNo",transNo);
        encAndDnc=new EncAndDnc();
        //加密后的字符串
        String signContent= encAndDnc.encMessage(jsonObject.toString());
        //请求中的请求体替换为加密后的字符串
        reqInfo.setSignContent(signContent);
        map.put("method",reqInfo.getMethod());
        map.put("signContent",signContent);
     String result=  TestConfig.HttpSend(reqInfo.getUrl(),map);
     JSONObject jsonObject1= JSONObject.parseObject(result);
        log.info("响应密文："+jsonObject1.toString());
        //获取响应信息，并转换为json
        JSONObject jsonObject2= JSONObject.parseObject(encAndDnc.dencMessage(result));
        channelOrderNo= (String) jsonObject2.get("channelOrderNo");
        tradeNo= (String) jsonObject2.get("tradeNo");

        Assert.assertEquals(jsonObject2.get("respMsg"),"交易处理中，请稍后查询");
        log.info("响应明文结果"+jsonObject2.toString());
      //  System.out.println("响应明文结果"+encAndDnc.dencMessage(result));

    }

    //聚合2.0-所有
    @Test(description = "聚合2.0All")
    public void  DYNAMIC_ALL(){
        //支付请求，替换requestDate与transNo,并且加密
        reqInfo = TestConfig.sessionLocalhost.selectOne("com.hsq.selReqInfo","聚合支付支付宝主扫");
        log.info("请求明文："+reqInfo.toString());
        JSONObject jsonObject= JSONObject.parseObject(reqInfo.getSignContent());
        jsonObject.put("requestDate", TestConfig.dateString());
        jsonObject.put("transNo",transNo);
        //payType不一样
        jsonObject.put("payType","DYNAMIC_ALL");
        encAndDnc=new EncAndDnc();
        //加密后的字符串
        String signContent= encAndDnc.encMessage(jsonObject.toString());
        //请求中的请求体替换为加密后的字符串
        reqInfo.setSignContent(signContent);
        map.put("method",reqInfo.getMethod());
        map.put("signContent",signContent);
        String result=  TestConfig.HttpSend(reqInfo.getUrl(),map);
        JSONObject jsonObject1= JSONObject.parseObject(result);
        log.info("响应密文："+jsonObject1.toString());
        //获取响应信息，并转换为json
        JSONObject jsonObject2= JSONObject.parseObject(encAndDnc.dencMessage(result));
        channelOrderNo= (String) jsonObject2.get("channelOrderNo");
        tradeNo= (String) jsonObject2.get("tradeNo");

        Assert.assertEquals(jsonObject2.get("respMsg"),"等待用户扫码");
        log.info("响应明文结果"+jsonObject2.toString());
        //  System.out.println("响应明文结果"+encAndDnc.dencMessage(result));

    }



    @Test(description = "聚合支付订单查询")
    public void payQqury(){
        reqInfo = TestConfig.sessionLocalhost.selectOne("com.hsq.selReqInfo","聚合支付订单查询");
        encAndDnc=new EncAndDnc();
        //加密后的字符串
        String signContent= encAndDnc.encMessage(reqInfo.getSignContent());
        map.put("method",reqInfo.getMethod());
        map.put("signContent",signContent);
        String result=  TestConfig.HttpSend(reqInfo.getUrl(),map);
        JSONObject jsonObject= JSONObject.parseObject(result);
        Assert.assertEquals(jsonObject.get("success"),true);
        log.info("响应密文："+jsonObject.toString());
        log.info("响应明文结果"+encAndDnc.dencMessage(result));

    }

//聚合支付含营销户
@Test(description = "聚合支付支付宝主扫含营销户")
public void aliPayMarket(){
    reqInfo = TestConfig.sessionLocalhost.selectOne("com.hsq.selReqInfo","聚合支付支付宝主扫");
    JSONObject jsonObject= JSONObject.parseObject(reqInfo.getSignContent());
    jsonObject.put("requestDate", TestConfig.dateString());
    jsonObject.put("transNo",TestConfig.dateString());
    //总收单金额：2000
    jsonObject.put("totalOrderAmt","2000");
    //营销户出1000
    jsonObject.put("unionInfo","1000,883007563082");
    encAndDnc=new EncAndDnc();
    //加密后的字符串
    String signContent= encAndDnc.encMessage(jsonObject.toString());
    log.info("请求明文："+jsonObject.toString());
    reqInfo.setSignContent(signContent);
    map.put("method",reqInfo.getMethod());
    map.put("signContent",signContent);
    String result=  TestConfig.HttpSend(reqInfo.getUrl(),map);
    JSONObject jsonObject1= JSONObject.parseObject(result);

    log.info("响应密文："+jsonObject1.toString());
    //获取响应信息，并转换为json
    JSONObject jsonObject2= JSONObject.parseObject(encAndDnc.dencMessage(result));
    channelOrderNo= (String) jsonObject2.get("channelOrderNo");
    tradeNo= (String) jsonObject2.get("tradeNo");

    Assert.assertEquals(jsonObject2.get("respMsg"),"交易处理中，请稍后查询");
    log.info("响应明文结果"+jsonObject2.toString());
    //  System.out.println("响应明文结果"+encAndDnc.dencMessage(result));

}
//聚合2.0下单





//退款

    //微信公众号
    @Test(description = "微信公众号下单")
    public void WeChatJS(){
        reqInfo = TestConfig.sessionLocalhost.selectOne("com.hsq.selReqInfo","微信公众号");
        JSONObject jsonObject= JSONObject.parseObject(reqInfo.getSignContent());
        jsonObject.put("requestDate", TestConfig.dateString());
        jsonObject.put("transNo",TestConfig.dateString());
        //支付类型为微信公众号
        jsonObject.put("payType","WECHAT_JSAPI");
        encAndDnc=new EncAndDnc();
        //加密后的字符串
        String signContent= encAndDnc.encMessage(jsonObject.toString());
        log.info("请求明文："+jsonObject.toString());
        reqInfo.setSignContent(signContent);
        map.put("method",reqInfo.getMethod());
        map.put("signContent",signContent);
        String result=  TestConfig.HttpSend(reqInfo.getUrl(),map);
        //获取响应信息，解密后，转换为json
        JSONObject jsonObject1= JSONObject.parseObject(encAndDnc.dencMessage(result));
        Assert.assertEquals(jsonObject1.get("respMsg"),"交易处理中，请稍后查询");
        log.info("响应明文结果"+jsonObject1.toString());

    }



    //微信小程序
//微信公众号
    @Test(description = "微信小程序下单")
    public void WeChatApp(){
        reqInfo = TestConfig.sessionLocalhost.selectOne("com.hsq.selReqInfo","微信公众号");
        JSONObject jsonObject= JSONObject.parseObject(reqInfo.getSignContent());
        jsonObject.put("requestDate", TestConfig.dateString());
        jsonObject.put("transNo",TestConfig.dateString());
        //支付类型为微信公众号
        jsonObject.put("payType","WECHAT_APPLET");
        encAndDnc=new EncAndDnc();
        //加密后的字符串
        String signContent= encAndDnc.encMessage(jsonObject.toString());
        log.info("请求明文："+jsonObject.toString());
        reqInfo.setSignContent(signContent);
        map.put("method",reqInfo.getMethod());
        map.put("signContent",signContent);
        String result=  TestConfig.HttpSend(reqInfo.getUrl(),map);
        //获取响应信息，解密后，转换为json
        JSONObject jsonObject1= JSONObject.parseObject(encAndDnc.dencMessage(result));
        Assert.assertEquals(jsonObject1.get("respMsg"),"交易处理中，请稍后查询");
        log.info("响应明文结果"+jsonObject1.toString());

    }

}
