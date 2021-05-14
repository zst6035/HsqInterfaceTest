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
* 代付交易
* 代付交易查询
*
*
* */

@Slf4j
public class TransferPay {
    //基础参数
    ReqInfo reqInfo;
    EncAndDnc encAndDnc=new EncAndDnc();
    Map map=TestConfig.getMap();
    String transNo="161"+TestConfig.getRandom4(10);

    @BeforeClass
    public void BeforePay(){
        log.info("测试前准备");
    }

    @Test(description = "代付-工商银行")
    public void transFer(){
        reqInfo = TestConfig.sessionLocalhost.selectOne("com.hsq.selReqInfo","代付");
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
        log.info("响应明文"+jsonObject2.toString());
        Assert.assertEquals(jsonObject2.get("respMsg"),"订单处理中，请稍后查询");

        log.info("响应明文结果"+res);


    }
    //代付交易需要2分钟后，才能返回成功

    @Test(description = "代付查询")
    public void transFerQuery(){
        reqInfo = TestConfig.sessionLocalhost.selectOne("com.hsq.selReqInfo","代付查询");
        JSONObject jsonObject= JSONObject.parseObject(reqInfo.getSignContent());
        jsonObject.put("requestDate", TestConfig.dateString());
        jsonObject.put("transNo",transNo);

        //加密后的字符串
        String signContent= encAndDnc.encMessage(jsonObject.toString());

        reqInfo.setSignContent(signContent);
        map.put("method",reqInfo.getMethod());
        map.put("signContent",signContent);
        log.info("请求数据{}",map.toString());
        String result=  TestConfig.HttpSend(reqInfo.getUrl(),map);
        //响应进行解密
        String res=encAndDnc.dencMessage(result);
        log.info("响应明文结果"+res);
        JSONObject jsonObject2= JSONObject.parseObject(res);
        Assert.assertEquals(jsonObject2.get("respMsg"),"订单处理中，请稍后查询");

    }




}
