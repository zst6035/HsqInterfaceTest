package com.hsq.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.codec.binary.Base64;

import java.io.InputStream;

public class EncAndDnc {
    //加密
    public   String  encMessage(String message){
        String signContent;
        InputStream in = getClass().getClassLoader().getResourceAsStream(TestConfig.merchant.getPrivateKeyPath());

        //读取私钥证书
        byte[] pfx = PfxRead.readStream(in);
        //加密主题报文，加密还需要私钥密码
        signContent = RsaUtil.encryptByPriPfxStream(Base64.encodeBase64String(message.getBytes()), pfx, TestConfig.merchant.getPrivateKeyPwd());
       // System.out.println("密文："+signContent);
        return signContent;
    }


    //解密

    public String dencMessage(String result ){
        JSONObject jsonObject = JSON.parseObject(result);
     //   System.out.println("密文结果"+jsonObject.toString());
     String result2=  new String(Base64.decodeBase64(
                    //解密响应报文，解密需要平台公钥,解密响应报文(平台分配)
                    RsaUtil.decryptByPubCerText((String) jsonObject.get("result"), TestConfig.merchant.getRsaKey())));
       return result2;
    }
}
