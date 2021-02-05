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
@Slf4j
public class PolymerizePay {
    ReqInfo reqInfo;
    EncAndDnc encAndDnc;
    Map map;
    String transNo=TestConfig.dateString();
    //渠道订单号
    String channelOrderNo=null;
    //交易订单号
    String tradeNo=null;


    //类运行之前执行
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

//走宝付渠道，宝财通版本，收单后，分账
    @Test(description = "聚合支付支付宝主扫",groups = "alipay")
    public void aliPay(){

        /*由接口可以知道，map的值几乎是固定的，唯一的变化就是会随着method的不同，进行变化，
        那么对于数据库来说，只需要存储变化的内容即可，所以，再新增一张表，将method，以及signContent进行入库；
        */
        //支付请求，替换requestDate与transNo,并且加密
        reqInfo = TestConfig.session.selectOne("com.hsq.selReqInfo","聚合支付支付宝主扫");
        log.info("请求明文："+reqInfo.toString());
        JSONObject jsonObject= JSONObject.parseObject(reqInfo.getSignContent());
        jsonObject.put("requestDate", TestConfig.dateString());
        jsonObject.put("transNo",transNo);
        encAndDnc=new EncAndDnc();
        //加密后的字符串
        String signContent= encAndDnc.encMessage(jsonObject.toString());
        log.info("加密后的字符串："+signContent);
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

        Assert.assertEquals(jsonObject2.get("respMsg"),"系统繁忙，请稍后再试");
        log.info("响应明文结果"+jsonObject2.toString());
      //  System.out.println("响应明文结果"+encAndDnc.dencMessage(result));

    }

    @Test(description = "修改宝付慧收钱订单状态",dependsOnMethods = "aliPay",groups = "alipay")
    public void changeTrans(){
        try {
            //修改宝付订单号；
           int i1= DatabaseUtil.getSqlSessionBf().update("updateBfTrans",channelOrderNo);
            //修改慧收钱订单号；
            log.info("宝付修改数据"+i1);
            int i2=   DatabaseUtil.getSqlSessionHsqTrader().update("updateTransBase",tradeNo);
            log.info("交易修改数据1"+i2);
            int i3=  DatabaseUtil.getSqlSessionHsqTrader().update("updateTransChannel",tradeNo);
            log.info("交易修改数据2"+i3);
            int i4= DatabaseUtil.getSqlSessionHsqGateway().update("updateGateway",transNo);
            log.info("gateway修改数据"+i4);
            int sum=i1+i2+i3+i4;
            Assert.assertEquals(sum,4);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //修改订单成功后宝付通知商户
    @Test(description = "宝付订单查询通知商户",dependsOnGroups = "alipay")
    public void selBfTrans()throws Exception{
        String businessNo=DatabaseUtil.getSqlSessionBf().selectOne("selBfTansNo",channelOrderNo);
        log.info("businessNO："+businessNo);
        String cookie=DatabaseUtil.getSqlSession1().selectOne("selCookie");
      JSONObject jsonObject=  TestConfig.selTrans(cookie,businessNo);
        Assert.assertEquals(jsonObject.get("errorMsg"),"成功查询1条订单信息:");
        log.info("通知商户成功");

    }



   // @Test(description = "聚合分账",dependsOnMethods = "selBfTrans")
    @Test(description = "聚合分账")
    public void payShare(){
        reqInfo = TestConfig.session.selectOne("com.hsq.selReqInfo","聚合分账");
        JSONObject jsonObject= JSONObject.parseObject(reqInfo.getSignContent());
        jsonObject.put("transNo", TestConfig.dateString());
        jsonObject.put("origTransNo",transNo);
        jsonObject.put("requestDate",TestConfig.dateString());
        log.info("聚合分账请求明文："+jsonObject.toString());
        encAndDnc=new EncAndDnc();
        //加密后的字符串
         String signContent= encAndDnc.encMessage(jsonObject.toString());
        map.put("method",reqInfo.getMethod());
        map.put("signContent",signContent);
        String result=  TestConfig.HttpSend(reqInfo.getUrl(),map);
        JSONObject jsonObject2= JSONObject.parseObject(result);
        Assert.assertEquals(jsonObject2.get("success"),true);
        //响应明文：
        String result2=encAndDnc.dencMessage(result);
        JSONObject jsonObject3= JSONObject.parseObject(result2);
        Assert.assertEquals(jsonObject3.get("respMsg"),"交易成功");
        log.info("响应密文："+jsonObject2.toString());
        log.info("响应明文结果"+encAndDnc.dencMessage(result));

    }




    @Test(description = "聚合支付订单查询")
    public void payQqury(){
        reqInfo = TestConfig.session.selectOne("com.hsq.selReqInfo","聚合支付订单查询");
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

        /*由接口可以知道，map的值几乎是固定的，唯一的变化就是会随着method的不同，进行变化，
        那么对于数据库来说，只需要存储变化的内容即可，所以，再新增一张表，将method，以及signContent进行入库；
        */
    //支付请求，替换requestDate与transNo,并且加密
    reqInfo = TestConfig.session.selectOne("com.hsq.selReqInfo","聚合支付支付宝主扫");

    JSONObject jsonObject= JSONObject.parseObject(reqInfo.getSignContent());
    jsonObject.put("requestDate", TestConfig.dateString());
    jsonObject.put("transNo",TestConfig.dateString());
    //总收单金额：2000
    jsonObject.put("totalOrderAmt","2000");
    //营销户出1000
    jsonObject.put("unionInfo","1000");
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

    Assert.assertEquals(jsonObject2.get("respMsg"),"系统繁忙，请稍后再试");
    log.info("响应明文结果"+jsonObject2.toString());
    //  System.out.println("响应明文结果"+encAndDnc.dencMessage(result));

}



}
