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
public class Ebank {
    //基础参数
    ReqInfo reqInfo;
    EncAndDnc encAndDnc=new EncAndDnc();
    Map map;
    String transNo= TestConfig.dateString();

    @BeforeClass
    public void BeforePay(){
        map=new HashMap();
        /*由接口可以知道，map的值几乎是固定的，唯一的变化就是会随着method的不同，进行变化，
        那么对于数据库来说，只需要存储变化的内容即可，所以，再新增一张表，将method，以及signContent进行入库；
        */
        //这些针对所有请求都是一样的，不变化的；
        map.put("merchantNo", TestConfig.merchant.getMerchantNo());
        map.put("version","1.0.0");
        map.put("format","json");
        map.put("signType","CFCA");
        map.put("sign",null);
    }


    @Test(description = "网银支付预下单")
    public void eBank(){
        reqInfo = TestConfig.session.selectOne("com.hsq.selReqInfo","网银支付预下单");
        JSONObject jsonObject= JSONObject.parseObject(reqInfo.getSignContent());
        jsonObject.put("requestDate", TestConfig.dateString());
        jsonObject.put("transNo",transNo);

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
        log.info("响应明文结果"+res);
        Assert.assertEquals(jsonObject2.get("respMsg"),"交易处理中，请稍后查询");

    }


    @Test(description ="网银订单查询",dependsOnMethods = "eBank")
    public void eBankQuery(){
        reqInfo = TestConfig.session.selectOne("com.hsq.selReqInfo","网银订单查询");
        JSONObject jsonObject= JSONObject.parseObject(reqInfo.getSignContent());
        jsonObject.put("requestDate", TestConfig.dateString());
        jsonObject.put("transNo",transNo);

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
        log.info("响应明文结果"+res);
        Assert.assertEquals(jsonObject2.get("respMsg"),"交易处理中，请稍后查询");

    }
    @Test(description ="网银分账",dependsOnMethods = "eBank")
    public void eBankShare(){
        reqInfo = TestConfig.session.selectOne("com.hsq.selReqInfo","网银分账");
        JSONObject jsonObject= JSONObject.parseObject(reqInfo.getSignContent());
        jsonObject.put("requestDate", TestConfig.dateString());
        jsonObject.put("origTransNo",transNo);

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
        log.info("响应明文结果"+res);
        Assert.assertEquals(jsonObject2.get("respMsg"),"业务订单状态异常");
    }
}
