package com.hsq.cases;

import com.alibaba.fastjson.JSONObject;
import com.hsq.model.ReqInfo;
import com.hsq.utils.EncAndDnc;
import com.hsq.utils.TestConfig;
import lombok.extern.slf4j.Slf4j;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;
/*
* 账户产品：
* 提现
* 订单查询
* 账户余额查询
* 账户明细查询
* 余额支付
* 个人充值
* */

@Slf4j
public class Balance {

    //基础参数
    ReqInfo reqInfo;
    EncAndDnc encAndDnc=new EncAndDnc();
    Map map=TestConfig.getMap();
    String transNo=TestConfig.transNo();

    @BeforeClass
    public void BeforePay(){

        /*由接口可以知道，map的值几乎是固定的，唯一的变化就是会随着method的不同，进行变化，
        那么对于数据库来说，只需要存储变化的内容即可，所以，再新增一张表，将method，以及signContent进行入库；
        */
        //这些针对所有请求都是一样的，不变化的；所以可以直接生成一个map,放在TestConfig中
        log.info("测试前准备");
    }


    @Test(description = "门店提现",testName = "门店提现")
    public void withDraw(){
        reqInfo = TestConfig.sessionLocalhost.selectOne("com.hsq.selReqInfo","门店提现");
        JSONObject jsonObject= JSONObject.parseObject(reqInfo.getSignContent());
        jsonObject.put("requestDate", TestConfig.dateString());
        jsonObject.put("transNo",transNo);
        //二级户配置的银行卡号尾号是7，模拟处理中的状态;
        jsonObject.put("subMerchantNo","2000401508492982");


        //加密后的字符串
        String signContent= encAndDnc.encMessage(jsonObject.toString());
         log.info("请求明文"+jsonObject.toString());
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
        log.info("门店提现响应明文结果"+res);
        Assert.assertEquals(jsonObject2.get("respMsg"),"订单处理中，请稍后查询");



    }
    @Test(description = "门店提现订单查询")
    public void withDrawQuery(){
        reqInfo = TestConfig.sessionLocalhost.selectOne("com.hsq.selReqInfo","门店提现订单查询");
        JSONObject jsonObject= JSONObject.parseObject(reqInfo.getSignContent());
        jsonObject.put("transNo",transNo);

        log.info("请求明文"+jsonObject.toString());
        //加密后的字符串
        String signContent= encAndDnc.encMessage(jsonObject.toString());
        //   log.info("加密后的字符串："+signContent);
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
        log.info("门店提现订单查询响应明文结果"+res);
        Assert.assertEquals(jsonObject2.get("respMsg"),"交易处理中，请稍后查询");



    }


    @Test(description = "门店余额查询")
    public void accountBalance(){
        reqInfo = TestConfig.sessionLocalhost.selectOne("com.hsq.selReqInfo","门店余额查询");
        JSONObject jsonObject= JSONObject.parseObject(reqInfo.getSignContent());

        encAndDnc=new EncAndDnc();
        //加密后的字符串
        String signContent= encAndDnc.encMessage(jsonObject.toString());
        //   log.info("加密后的字符串："+signContent);
        log.info("请求明文"+jsonObject.toString());
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
        log.info("门店余额查询响应明文结果"+res);
        Assert.assertEquals(jsonObject2.get("respMsg"),"成功");


    }


    @Test(description = "门店账户明细查询")
    public void accountDetail(){
        reqInfo = TestConfig.sessionLocalhost.selectOne("com.hsq.selReqInfo","门店账户明细查询");
        JSONObject jsonObject= JSONObject.parseObject(reqInfo.getSignContent());

        encAndDnc=new EncAndDnc();
        //加密后的字符串
        String signContent= encAndDnc.encMessage(jsonObject.toString());
        //   log.info("加密后的字符串："+signContent);
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
        log.info("门店账户明细查询响应明文结果"+res);
        Assert.assertEquals(jsonObject2.get("respMsg"),"查询成功");


    }




//发起充值后，渠道是先调用协议支付，再调用分账接口
    //二级户：2000400008322968  只能是无附件的，充值和余额支付
    @Test(description = "门店充值")
    public void personRecharge(){
        reqInfo = TestConfig.sessionLocalhost.selectOne("com.hsq.selReqInfo","门店充值");
        JSONObject jsonObject= JSONObject.parseObject(reqInfo.getSignContent());

        jsonObject.put("requestDate", TestConfig.dateString());
        jsonObject.put("transNo",TestConfig.transNo());
        jsonObject.put("subMerchantNo","2000400008322968");
        encAndDnc=new EncAndDnc();
        //加密后的字符串
        log.info("请求明文"+jsonObject.toString());
        String signContent= encAndDnc.encMessage(jsonObject.toString());
        //   log.info("加密后的字符串："+signContent);
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
        log.info("响应明文结果"+res);
        Assert.assertEquals(jsonObject2.get("respMsg"),"交易处理中，请稍后查询");

    }

    @Test(description = "门店余额支付",dependsOnMethods = "personRecharge")
    public void balancePay(){
        reqInfo = TestConfig.sessionLocalhost.selectOne("com.hsq.selReqInfo","门店余额支付");
        JSONObject jsonObject= JSONObject.parseObject(reqInfo.getSignContent());

        jsonObject.put("requestDate", TestConfig.dateString());
        jsonObject.put("transNo",TestConfig.transNo());
        jsonObject.put("subMerchantNo","2000400008322968");
        log.info("请求明文"+jsonObject.toString());
        encAndDnc=new EncAndDnc();
        //加密后的字符串
        String signContent= encAndDnc.encMessage(jsonObject.toString());
        //   log.info("加密后的字符串："+signContent);
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
        log.info("响应明文结果"+jsonObject2.toString());
        Assert.assertEquals(jsonObject2.get("respMsg"),"交易成功");

    }

    @Test(description = "一级商户提现")
    public void merchantWithDraw(){
        reqInfo = TestConfig.sessionLocalhost.selectOne("com.hsq.selReqInfo","商户提现");
        JSONObject jsonObject= JSONObject.parseObject(reqInfo.getSignContent());
        jsonObject.put("transNo",TestConfig.transNo());
        log.info("请求明文"+jsonObject.toString());
        encAndDnc=new EncAndDnc();
        //加密后的字符串
        String signContent= encAndDnc.encMessage(jsonObject.toString());
        //   log.info("加密后的字符串："+signContent);
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
        log.info("响应明文结果"+jsonObject2.toString());
        Assert.assertEquals(jsonObject2.get("respMsg"),"订单处理中，请稍后查询");

    }

}
