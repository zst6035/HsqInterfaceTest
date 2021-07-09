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


    String name=TestConfig.getChineseName(1);
    Map map=TestConfig.getMap();
    String uniqueCode=null;
    String outMerchantNo= TestConfig.transNo();


    @BeforeClass
    public void BeforePay(){

        log.info("before 测试");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Test(description = "宝财通-消费户个人")
    public void merchantRegister() throws Exception{
        ReqInfo reqInfo=DatabaseUtil.getSqlSession1().selectOne("com.hsq.selReqInfo","商户进件-个人");
        JSONObject jsonObject= JSONObject.parseObject(reqInfo.getSignContent());
        jsonObject.put("bankCardNo","621082"+TestConfig.getRandom4(9)+"7");
        jsonObject.put("outMerchantNo", outMerchantNo);
        jsonObject.put("name",name);
        jsonObject.put("merchantName","个人消费户"+name);
        jsonObject.put("merchantShortName","个人消费户"+name);
        jsonObject.put("phone","131"+TestConfig.getRandom4(8));
        jsonObject.put("servicePhone","131"+TestConfig.getRandom4(8));
        log.info("请求体"+jsonObject.toString());
        //加密后的字符串
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        EncAndDnc e1=new EncAndDnc();
        String signContent= e1.encMessage(jsonObject.toString());
        reqInfo.setSignContent(signContent);
        //此处需要注意是代理商商户号，不是商户号
        map.put("agentMerchantNo","883007563082");
        map.put("method",reqInfo.getMethod());
        map.put("signContent",signContent);
        log.info("完整请求体"+map.toString());
        String result=  TestConfig.HttpSend(reqInfo.getUrl(),map);
        log.info("宝财通-消费户个人==============={}",result);
        //返回数据转换为json
        // JSONObject jsonObject1=JSONObject.parseObject(result);
        //请求结果进行解密
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
      EncAndDnc  e2=new EncAndDnc();

        log.info(jsonObject.toString());
        //加密后的字符串
        String signContent= e2.encMessage(jsonObject.toString());

        reqInfo.setSignContent(signContent);
        map.put("method",reqInfo.getMethod());
        map.put("signContent",signContent);
        log.info(map.toString());
        String result=  TestConfig.HttpSend(reqInfo.getUrl(),map);
        //返回数据转换为json
        // JSONObject jsonObject1=JSONObject.parseObject(result);
        //请求结果进行解密

        String res=e2.dencMessage(result);
        //再将请求结果转换为json格式
        JSONObject jsonObject2= JSONObject.parseObject(res);
        log.info("响应明文结果"+res);
        Assert.assertEquals(jsonObject2.get("applyStatus"),"AUDIT_PASS");

    }

    @Test(description = "宝财通-非消费户个人")
    public void merchantRegisterCommon() throws Exception{
        ReqInfo reqInfo= DatabaseUtil.getSqlSession1().selectOne("com.hsq.selReqInfo","商户进件-个人");
        JSONObject jsonObject= JSONObject.parseObject(reqInfo.getSignContent());
        jsonObject.put("bankCardNo","621082"+TestConfig.getRandom4(9)+"7");
        jsonObject.put("outMerchantNo", TestConfig.transNo());
        jsonObject.put("name",name);
        jsonObject.put("merchantName","个人普通户"+name);
        jsonObject.put("merchantShortName","个人普通户"+name);
        jsonObject.put("phone","131"+TestConfig.getRandom4(8));
        jsonObject.put("servicePhone","131"+TestConfig.getRandom4(8));
        //非钱包用户：2，钱包用户：1
        jsonObject.put("userType","2");
        log.info(jsonObject.toString());
         EncAndDnc e3=new EncAndDnc();
        //加密后的字符串
        String signContent= e3.encMessage(jsonObject.toString());

        reqInfo.setSignContent(signContent);
        map.put("method",reqInfo.getMethod());
        map.put("signContent",signContent);
        //此处需要注意是代理商商户号，不是商户号
        map.put("agentMerchantNo","883007563082");
        log.info("请求报文是：===="+map.toString());
        String result=  TestConfig.HttpSend(reqInfo.getUrl(),map);
        //返回数据转换为json
        // JSONObject jsonObject1=JSONObject.parseObject(result);
        //请求结果进行解密
        log.info("响应密文是：===="+result);
        String res=e3.dencMessage(result);
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

       EncAndDnc e4=new EncAndDnc();
        log.info(jsonObject.toString());
        //加密后的字符串
        String signContent= e4.encMessage(jsonObject.toString());

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

        String res=e4.dencMessage(result);
        //再将请求结果转换为json格式
        JSONObject jsonObject2= JSONObject.parseObject(res);
        log.info("响应明文结果"+res);
        Assert.assertEquals(jsonObject2.get("applyStatus"),"AUDIT_PASS");

    }
    //企业商户进件
    @Test(description = "宝财通-企业")
    public void merchantRegisterCompany() throws Exception{
        ReqInfo reqInfo= DatabaseUtil.getSqlSession1().selectOne("com.hsq.selReqInfo","宝财通企业商户进件");
        JSONObject jsonObject= JSONObject.parseObject(reqInfo.getSignContent());
        jsonObject.put("bankCardNo","621082"+TestConfig.getRandom4(10));
        jsonObject.put("outMerchantNo", TestConfig.transNo());
        jsonObject.put("contactName",name);
        jsonObject.put("contactPhone","131"+TestConfig.getRandom4(8));
        jsonObject.put("legalPhone","131"+TestConfig.getRandom4(8));
        jsonObject.put("servicePhone","131"+TestConfig.getRandom4(8));
        jsonObject.put("email",TestConfig.getEmail(5,10));
        //非钱包用户：2，钱包用户：1
        log.info(jsonObject.toString());
       EncAndDnc e5=new EncAndDnc();
        //加密后的字符串
        String signContent= e5.encMessage(jsonObject.toString());

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

        String res=e5.dencMessage(result);
        //再将请求结果转换为json格式
        JSONObject jsonObject2= JSONObject.parseObject(res);
        log.info("响应明文结果"+res);
        //也就类似包含
        Assert.assertEquals(jsonObject2.get("applyStatus"),"AUDIT_PASS");

    }
    //个体工商户
    @Test(description = "宝财通-个体工商户")
    public void merchantRegisterPersonCompany()throws Exception{
        ReqInfo reqInfo=  DatabaseUtil.getSqlSession1().selectOne("com.hsq.selReqInfo","宝财通企业商户进件");
        JSONObject jsonObject= JSONObject.parseObject(reqInfo.getSignContent());
        jsonObject.put("bankCardNo","621082"+TestConfig.getRandom4(10));
        jsonObject.put("outMerchantNo", TestConfig.transNo());
        jsonObject.put("contactName",name);
        jsonObject.put("contactPhone","131"+TestConfig.getRandom4(8));
        jsonObject.put("legalPhone","131"+TestConfig.getRandom4(8));
        jsonObject.put("servicePhone","131"+TestConfig.getRandom4(8));
        jsonObject.put("email",TestConfig.getEmail(5,10));
        //1:个体工商户
        jsonObject.put("selfEmployed","1");
        log.info(jsonObject.toString());
        //加密后的字符串
       EncAndDnc e6=new EncAndDnc();
        //EncAndDnc e6=new EncAndDnc();
        String signContent= e6.encMessage(jsonObject.toString());

        reqInfo.setSignContent(signContent);
        map.put("method",reqInfo.getMethod());
        map.put("signContent",signContent);
        log.info(map.toString());
        String result=  TestConfig.HttpSend(reqInfo.getUrl(),map);
        //返回数据转换为json
        // JSONObject jsonObject1=JSONObject.parseObject(result);
        //请求结果进行解密
        String res=e6.dencMessage(result);
        //再将请求结果转换为json格式
        JSONObject jsonObject2= JSONObject.parseObject(res);
        log.info("响应明文结果"+res);
        //也就类似包含
        Assert.assertEquals(jsonObject2.get("applyStatus"),"AUDIT_PASS");

    }





}
