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
//老接口，进件门店+宝财通商户号

@Slf4j
public class MerchantRegister {
    ReqInfo reqInfo;
    EncAndDnc encAndDnc=new EncAndDnc();
    String name=TestConfig.getChineseName(1);
    Map map;
    String uniqueCode=null;
    String outMerchantNo= TestConfig.dateString();


    @BeforeClass
    public void BeforePay(){
        map=new HashMap();
        /*由接口可以知道，map的值几乎是固定的，唯一的变化就是会随着method的不同，进行变化，
        那么对于数据库来说，只需要存储变化的内容即可，所以，再新增一张表，将method，以及signContent进行入库；
        */
        //这些针对所有请求都是一样的，不变化的；
        map.put("agentMerchantNo",TestConfig.merchant.getMerchantNo());
        map.put("version","5.0.0");
        map.put("format","json");
        map.put("signType","CFCA");
        map.put("sign",null);
    }


    @Test(description = "商户进件-消费户个人")
    public void merchantRegister(){
        reqInfo = TestConfig.session.selectOne("com.hsq.selReqInfo","商户进件-个人");
        JSONObject jsonObject= JSONObject.parseObject(reqInfo.getSignContent());
      jsonObject.put("bankCardNo","622848"+TestConfig.getRandom4(13));
        jsonObject.put("outMerchantNo", outMerchantNo);
        jsonObject.put("name",name);
        jsonObject.put("merchantName","个人消费户"+name);
        jsonObject.put("merchantShortName","个人消费户"+name);
        jsonObject.put("phone","131"+TestConfig.getRandom4(8));
        jsonObject.put("servicePhone","131"+TestConfig.getRandom4(8));


        log.info(jsonObject.toString());
        //加密后的字符串
        String signContent= encAndDnc.encMessage(jsonObject.toString());

        reqInfo.setSignContent(signContent);
        map.put("method",reqInfo.getMethod());
        map.put("signContent",signContent);
        log.info(map.toString());
        String result=  TestConfig.HttpSend(reqInfo.getUrl(),map);
        //返回数据转换为json
        // JSONObject jsonObject1=JSONObject.parseObject(result);
        //请求结果进行解密
        String res=encAndDnc.dencMessage(result);
        //再将请求结果转换为json格式
        JSONObject jsonObject2= JSONObject.parseObject(res);
        log.info("响应明文结果"+res);
        //也就类似包含
       Assert.assertNotNull(jsonObject2.get("uniqueCode"),"请求失败");
        uniqueCode= (String) jsonObject2.get("uniqueCode");



    }
    //确认绑卡
    @Test(description = "开户确认",dependsOnMethods = "merchantRegister")
    public void merchantRegisterConfirm(){
        reqInfo = TestConfig.session.selectOne("com.hsq.selReqInfo","个人进件-开户确认");
        JSONObject jsonObject= JSONObject.parseObject(reqInfo.getSignContent());

        jsonObject.put("uniqueCode",uniqueCode);
        jsonObject.put("outMerchantNo",outMerchantNo);


        log.info(jsonObject.toString());
        //加密后的字符串
        String signContent= encAndDnc.encMessage(jsonObject.toString());

        reqInfo.setSignContent(signContent);
        map.put("method",reqInfo.getMethod());
        map.put("signContent",signContent);
        log.info(map.toString());
        String result=  TestConfig.HttpSend(reqInfo.getUrl(),map);
        //返回数据转换为json
        // JSONObject jsonObject1=JSONObject.parseObject(result);
        //请求结果进行解密
        String res=encAndDnc.dencMessage(result);
        //再将请求结果转换为json格式
        JSONObject jsonObject2= JSONObject.parseObject(res);
        log.info("响应明文结果"+res);
        Assert.assertEquals(jsonObject2.get("applyStatus"),"AUDIT_PASS");

    }

    @Test(description = "商户进件-非消费户个人")
    public void merchantRegisterCommon(){
        reqInfo = TestConfig.session.selectOne("com.hsq.selReqInfo","商户进件-个人");
        JSONObject jsonObject= JSONObject.parseObject(reqInfo.getSignContent());
        jsonObject.put("bankCardNo","622848"+TestConfig.getRandom4(13));
        jsonObject.put("outMerchantNo", TestConfig.dateString());
        jsonObject.put("name",name);
        jsonObject.put("merchantName","个人普通户"+name);
        jsonObject.put("merchantShortName","个人普通户"+name);
        jsonObject.put("phone","131"+TestConfig.getRandom4(8));
        jsonObject.put("servicePhone","131"+TestConfig.getRandom4(8));
        //非钱包用户：2，钱包用户：1
        jsonObject.put("userType","2");


        log.info(jsonObject.toString());
        //加密后的字符串
        String signContent= encAndDnc.encMessage(jsonObject.toString());

        reqInfo.setSignContent(signContent);
        map.put("method",reqInfo.getMethod());
        map.put("signContent",signContent);
        log.info(map.toString());
        String result=  TestConfig.HttpSend(reqInfo.getUrl(),map);
        //返回数据转换为json
        // JSONObject jsonObject1=JSONObject.parseObject(result);
        //请求结果进行解密
        String res=encAndDnc.dencMessage(result);
        //再将请求结果转换为json格式
        JSONObject jsonObject2= JSONObject.parseObject(res);
        log.info("响应明文结果"+res);
        //也就类似包含
       Assert.assertEquals(jsonObject2.get("applyStatus"),"AUDIT_PASS");

    }


    //结果查询
    @Test(description = "消费户进件结果查询",dependsOnMethods = "merchantRegister")
    public void merchantRegisterQuery(){
        reqInfo = TestConfig.session.selectOne("com.hsq.selReqInfo","进件结果-查询");
        JSONObject jsonObject= JSONObject.parseObject(reqInfo.getSignContent());

        jsonObject.put("outMerchantNo",outMerchantNo);


        log.info(jsonObject.toString());
        //加密后的字符串
        String signContent= encAndDnc.encMessage(jsonObject.toString());

        reqInfo.setSignContent(signContent);
        map.put("method",reqInfo.getMethod());
        map.put("signContent",signContent);
        log.info(map.toString());
        String result=  TestConfig.HttpSend(reqInfo.getUrl(),map);
        //返回数据转换为json
        // JSONObject jsonObject1=JSONObject.parseObject(result);
        //请求结果进行解密
        String res=encAndDnc.dencMessage(result);
        //再将请求结果转换为json格式
        JSONObject jsonObject2= JSONObject.parseObject(res);
        log.info("响应明文结果"+res);
        Assert.assertEquals(jsonObject2.get("applyStatus"),"AUDIT_PASS");

    }
    //企业商户进件
    @Test(description = "商户进件-企业")
    public void merchantRegisterCompany(){
        reqInfo = TestConfig.session.selectOne("com.hsq.selReqInfo","企业商户进件");
        JSONObject jsonObject= JSONObject.parseObject(reqInfo.getSignContent());
        jsonObject.put("bankCardNo","622848"+TestConfig.getRandom4(13));
        jsonObject.put("outMerchantNo", TestConfig.dateString());
        jsonObject.put("contactName",name);
        jsonObject.put("contactPhone","131"+TestConfig.getRandom4(8));
        jsonObject.put("legalPhone","131"+TestConfig.getRandom4(8));
        jsonObject.put("servicePhone","131"+TestConfig.getRandom4(8));
        jsonObject.put("email",TestConfig.getEmail(5,10));
        //非钱包用户：2，钱包用户：1



        log.info(jsonObject.toString());
        //加密后的字符串
        String signContent= encAndDnc.encMessage(jsonObject.toString());

        reqInfo.setSignContent(signContent);
        map.put("method",reqInfo.getMethod());
        map.put("signContent",signContent);
        log.info(map.toString());
        String result=  TestConfig.HttpSend(reqInfo.getUrl(),map);
        //返回数据转换为json
        // JSONObject jsonObject1=JSONObject.parseObject(result);
        //请求结果进行解密
        String res=encAndDnc.dencMessage(result);
        //再将请求结果转换为json格式
        JSONObject jsonObject2= JSONObject.parseObject(res);
        log.info("响应明文结果"+res);
        //也就类似包含
        Assert.assertEquals(jsonObject2.get("applyStatus"),"AUDIT_PASS");

    }
    //个体工商户
    @Test(description = "商户进件-个体工商户")
    public void merchantRegisterPersonCompany(){
        reqInfo = TestConfig.session.selectOne("com.hsq.selReqInfo","企业商户进件");
        JSONObject jsonObject= JSONObject.parseObject(reqInfo.getSignContent());
        jsonObject.put("bankCardNo","622848"+TestConfig.getRandom4(13));
        jsonObject.put("outMerchantNo", TestConfig.dateString());
        jsonObject.put("contactName",name);
        jsonObject.put("contactPhone","131"+TestConfig.getRandom4(8));
        jsonObject.put("legalPhone","131"+TestConfig.getRandom4(8));
        jsonObject.put("servicePhone","131"+TestConfig.getRandom4(8));
        jsonObject.put("email",TestConfig.getEmail(5,10));
        //1:个体工商户
        jsonObject.put("selfEmployed","1");


        log.info(jsonObject.toString());
        //加密后的字符串
        String signContent= encAndDnc.encMessage(jsonObject.toString());

        reqInfo.setSignContent(signContent);
        map.put("method",reqInfo.getMethod());
        map.put("signContent",signContent);
        log.info(map.toString());
        String result=  TestConfig.HttpSend(reqInfo.getUrl(),map);
        //返回数据转换为json
        // JSONObject jsonObject1=JSONObject.parseObject(result);
        //请求结果进行解密
        String res=encAndDnc.dencMessage(result);
        //再将请求结果转换为json格式
        JSONObject jsonObject2= JSONObject.parseObject(res);
        log.info("响应明文结果"+res);
        //也就类似包含
        Assert.assertEquals(jsonObject2.get("applyStatus"),"AUDIT_PASS");

    }





}
