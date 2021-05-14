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
import java.util.Map;
//老接口，进件门店+宝财通商户号

@Slf4j
public class MerchantRegister {

    EncAndDnc encAndDnc=new EncAndDnc();
    String name=TestConfig.getChineseName(1);
    Map map=TestConfig.getMap();
    String uniqueCode=null;
    String outMerchantNo= TestConfig.dateString();


    @BeforeClass
    public void BeforePay(){
        log.info("before 测试");
    }


    @Test(description = "宝财通-消费户个人")
    public void merchantRegister() throws Exception{
        ReqInfo reqInfo=DatabaseUtil.getSqlSession1().selectOne("com.hsq.selReqInfo","商户进件-个人");
        JSONObject jsonObject= JSONObject.parseObject(reqInfo.getSignContent());
        jsonObject.put("bankCardNo","621082"+TestConfig.getRandom4(9)+"2");
        jsonObject.put("outMerchantNo", outMerchantNo);
        jsonObject.put("name",name);
        jsonObject.put("merchantName","个人消费户"+name);
        jsonObject.put("merchantShortName","个人消费户"+name);
        jsonObject.put("phone","131"+TestConfig.getRandom4(8));
        jsonObject.put("servicePhone","131"+TestConfig.getRandom4(8));
        log.info("请求体"+jsonObject.toString());
        //加密后的字符串
        String signContent= encAndDnc.encMessage(jsonObject.toString());
        reqInfo.setSignContent(signContent);
        //此处需要注意是代理商商户号，不是商户号
        map.put("agentMerchantNo","883007563082");
        map.put("method",reqInfo.getMethod());
        map.put("signContent",signContent);
        log.info("完整请求体"+map.toString());
        String result=  TestConfig.HttpSend(reqInfo.getUrl(),map);
        //返回数据转换为json
        // JSONObject jsonObject1=JSONObject.parseObject(result);
        //请求结果进行解密
        EncAndDnc e1=new EncAndDnc();
        String res=e1.dencMessage(result);
        //再将请求结果转换为json格式
        JSONObject jsonObject2= JSONObject.parseObject(res);
        log.info("响应明文结果"+res);
        //也就类似包含
       Assert.assertNotNull(jsonObject2.get("applyStatus"),"AUDIT");
        uniqueCode= (String) jsonObject2.get("uniqueCode");
    }



    //确认绑卡
    @Test(description = "开户确认",dependsOnMethods = "merchantRegister")
    public void merchantRegisterConfirm()throws Exception{
      ReqInfo  reqInfo = DatabaseUtil.getSqlSession1().selectOne("com.hsq.selReqInfo","个人进件-开户确认");
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

    @Test(description = "宝财通-非消费户个人")
    public void merchantRegisterCommon() throws Exception{
        ReqInfo reqInfo= DatabaseUtil.getSqlSession1().selectOne("com.hsq.selReqInfo","商户进件-个人");
        JSONObject jsonObject= JSONObject.parseObject(reqInfo.getSignContent());
        jsonObject.put("bankCardNo","621082"+TestConfig.getRandom4(9)+"2");
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
        //此处需要注意是代理商商户号，不是商户号
        map.put("agentMerchantNo","883007563082");
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
    public void merchantRegisterQuery()throws Exception{
       ReqInfo reqInfo = DatabaseUtil.getSqlSession1().selectOne("com.hsq.selReqInfo","进件结果-查询");
        JSONObject jsonObject= JSONObject.parseObject(reqInfo.getSignContent());

        jsonObject.put("outMerchantNo",outMerchantNo);


        log.info(jsonObject.toString());
        //加密后的字符串
        String signContent= encAndDnc.encMessage(jsonObject.toString());

        reqInfo.setSignContent(signContent);
        map.put("method",reqInfo.getMethod());
        map.put("signContent",signContent);
        //此处需要注意是代理商商户号，不是商户号
        map.put("agentMerchantNo","883007563082");
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
    @Test(description = "宝财通-企业")
    public void merchantRegisterCompany() throws Exception{
        ReqInfo reqInfo= DatabaseUtil.getSqlSession1().selectOne("com.hsq.selReqInfo","企业商户进件");
        JSONObject jsonObject= JSONObject.parseObject(reqInfo.getSignContent());
        jsonObject.put("bankCardNo","621082"+TestConfig.getRandom4(10));
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
        //此处需要注意是代理商商户号，不是商户号
        map.put("agentMerchantNo","883007563082");
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
    @Test(description = "宝财通-个体工商户")
    public void merchantRegisterPersonCompany()throws Exception{
        ReqInfo reqInfo=  DatabaseUtil.getSqlSession1().selectOne("com.hsq.selReqInfo","商户进件-个体工商户");
        JSONObject jsonObject= JSONObject.parseObject(reqInfo.getSignContent());
        jsonObject.put("bankCardNo","621082"+TestConfig.getRandom4(10));
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
