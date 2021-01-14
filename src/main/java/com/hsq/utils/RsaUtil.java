package com.hsq.utils;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.Enumeration;


/**
 * <p>
 * 加解密算法
 * </p>
 *
 * @author huaheshang
 * @version 1.0
 * @class_name RsaUtil
 * @date 2018/11/16
 * @time 10:24 AM
 */

@Slf4j
public class RsaUtil {

    /**
     * 编码
     */
    public static final String CHARSET_NAME = "UTF-8";

    /**
     * base  64加密
     *
     * @param str 待加密字符串
     * @return 返回加密后的字符串
     */
    public static String base64Encode(String str) {
        String message = null;
        try {
            message = new BASE64Encoder().encodeBuffer(str.getBytes(CHARSET_NAME));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return message;
    }

    /**
     * base  64解密
     *
     * @param str 待解密字符串
     * @return 返回机密后的字符串
     */
    public static String base64Decode(String str) {
        String message = null;
        try {
            message = new String(new BASE64Decoder().decodeBuffer(str), CHARSET_NAME);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return message;
    }

    /**
     * 加密
     *
     * @param message    待加密消息
     * @param pfxPath    私钥路径
     * @param priKeyPass 私钥密码
     * @return 返回加密结果
     */
    public static String encryptByPriPfxFile(String message, String pfxPath, String priKeyPass) {
        PrivateKey privateKey = RsaUtil.getPrivateKeyFromFile(pfxPath, priKeyPass);
        return encryptByPrivateKey(message, privateKey);
    }

    /**
     * 解密
     *
     * @param message    待机密内容
     * @param pubCerPath 公钥所在路径
     * @return 返回解密结果
     */
    public static String decryptByPubCerFile(String message, String pubCerPath) {
        PublicKey publicKey = RsaUtil.getPublicKeyFromFile(pubCerPath);
        return publicKey == null ? null : RsaUtil.decryptByPublicKey(message, publicKey);
    }





    public static String encryptByPrivateKey(String src, PrivateKey privateKey) {
        byte[] destBytes = rsaByPrivateKey(src.getBytes(), privateKey, 1);
        return destBytes == null ? null : (RsaUtil.byte2Hex(destBytes));
    }


    /**
     * 解密
     *
     * @param src        密文
     * @param pubKeyText 公钥字符串
     * @return 返回解密结果
     */
    public static String decryptByPubCerText(String src, String pubKeyText) {
        PublicKey publicKey = RsaUtil.getPublicKeyByText(pubKeyText);
        return publicKey == null ? null : decryptByPublicKey(src, publicKey);
    }

    /**
     * 加密
     *
     * @param src        待加密报文
     * @param pfxBytes   私钥
     * @param priKeyPass 私钥密码
     * @return 返回加密结果
     */
    public static String encryptByPriPfxStream(String src, byte[] pfxBytes, String priKeyPass) {
        PrivateKey privateKey = RsaUtil.getPrivateKeyByStream(pfxBytes, priKeyPass);
        return privateKey == null ? null : encryptByPrivateKey(src, privateKey);
    }

    public static byte[] rsaByPrivateKey(byte[] srcData, PrivateKey privateKey, int mode) {
        try {
            Cipher e = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            e.init(mode, privateKey);
            int blockSize = mode == 1 ? 117 : 128;
            byte[] decryptData = null;

            for (int i = 0; i < srcData.length; i += blockSize) {
                byte[] doFinal = e.doFinal(ArrayUtils.subarray(srcData, i, i + blockSize));
                decryptData = ArrayUtils.addAll(decryptData, doFinal);
            }

            return decryptData;
        } catch (NoSuchAlgorithmException var8) {
            log.error("私钥算法-不存在的解密算法:", var8);
        } catch (NoSuchPaddingException var9) {
            log.error("私钥算法-无效的补位算法:", var9);
        } catch (IllegalBlockSizeException var10) {
            log.error("私钥算法-无效的块大小:", var10);
        } catch (BadPaddingException var11) {
            log.error("私钥算法-补位算法异常:", var11);
        } catch (InvalidKeyException var12) {
            log.error("私钥算法-无效的私钥:", var12);
        }

        return null;
    }

    public static PrivateKey getPrivateKeyFromFile(String pfxPath, String priKeyPass) {
        FileInputStream priKeyStream = null;

        try {
            priKeyStream = new FileInputStream(pfxPath);
            byte[] e = new byte[priKeyStream.available()];
            priKeyStream.read(e);
            PrivateKey var4 = getPrivateKeyByStream(e, priKeyPass);
            return var4;
        } catch (Exception var14) {
            log.error("解析文件，读取私钥失败:", var14);
        } finally {
            if (priKeyStream != null) {
                try {
                    priKeyStream.close();
                } catch (Exception var13) {
                    ;
                }
            }

        }

        return null;
    }


    public static PrivateKey getPrivateKeyByStream(byte[] pfxBytes, String priKeyPass) {
        try {
            KeyStore e = KeyStore.getInstance("PKCS12");
            char[] charPriKeyPass = priKeyPass.toCharArray();
            e.load(new ByteArrayInputStream(pfxBytes), charPriKeyPass);
            PrivateKey privateKey = null;
            Enumeration aliasEnum = e.aliases();
            String keyAlias;

            while (aliasEnum.hasMoreElements()) {
                keyAlias = (String) aliasEnum.nextElement();
                privateKey = (PrivateKey) e.getKey(keyAlias, charPriKeyPass);
                if (privateKey != null) {
                    break;
                }
            }

            return privateKey;
        } catch (IOException var7) {
            log.error("解析文件，读取私钥失败:", var7);
        } catch (KeyStoreException var8) {
            log.error("私钥存储异常:", var8);
        } catch (NoSuchAlgorithmException var9) {
            log.error("不存在的解密算法:", var9);
        } catch (CertificateException var10) {
            log.error("证书异常:", var10);
        } catch (UnrecoverableKeyException var11) {
            log.error("不可恢复的秘钥异常", var11);
        }
        return null;
    }



    //获取公钥

    public static PublicKey getPublicKeyFromFile(String pubCerPath) {
        FileInputStream pubKeyStream = null;

        try {
            pubKeyStream = new FileInputStream(pubCerPath);
            byte[] e = new byte[pubKeyStream.available()];
            pubKeyStream.read(e);
            PublicKey var3 = getPublicKeyByText(new String(e));
            return var3;
        } catch (FileNotFoundException var15) {
            log.error("公钥文件不存在:", var15);
        } catch (IOException var16) {
            log.error("公钥文件读取失败:", var16);
        } finally {
            if (pubKeyStream != null) {
                try {
                    pubKeyStream.close();
                } catch (Exception var14) {
                    ;
                }
            }

        }
        return null;
    }

    //

    public static PublicKey getPublicKeyByText(String pubKeyText) {
        try {
            CertificateFactory e = CertificateFactory.getInstance("X509");
            BufferedReader br = new BufferedReader(new StringReader(pubKeyText));
            String line = null;
            StringBuilder keyBuffer = new StringBuilder();

            while ((line = br.readLine()) != null) {
                if (!line.startsWith("-")) {
                    keyBuffer.append(line);
                }
            }

            Certificate certificate = e.generateCertificate(new ByteArrayInputStream(
                    new BASE64Decoder().decodeBuffer(keyBuffer.toString())));
            return certificate.getPublicKey();
        } catch (Exception var6) {
            log.error("解析公钥内容失败:", var6);
            return null;
        }
    }

    //公钥解密
    public static String decryptByPublicKey(String src, PublicKey publicKey) {
        try {
            byte[] e = rsaByPublicKey(RsaUtil.hex2Bytes(src), publicKey, 2);
            return e == null ? null : new String(e, CHARSET_NAME);
        } catch (UnsupportedEncodingException var3) {
            log.error("解密内容不是正确的UTF8格式:", var3);
            return null;
        }
    }


    public static byte[] rsaByPublicKey(byte[] srcData, PublicKey publicKey, int mode) {
        try {
            Cipher e = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            e.init(mode, publicKey);
            int blockSize = mode == 1 ? 117 : 128;
            byte[] encryptedData = null;

            for (int i = 0; i < srcData.length; i += blockSize) {
                byte[] doFinal = e.doFinal(ArrayUtils.subarray(srcData, i, i + blockSize));
                encryptedData = ArrayUtils.addAll(encryptedData, doFinal);
            }

            return encryptedData;
        } catch (NoSuchAlgorithmException var8) {
            log.error("公钥算法-不存在的解密算法:", var8);
        } catch (NoSuchPaddingException var9) {
            log.error("公钥算法-无效的补位算法:", var9);
        } catch (IllegalBlockSizeException var10) {
            log.error("公钥算法-无效的块大小:", var10);
        } catch (BadPaddingException var11) {
            log.error("公钥算法-补位算法异常:", var11);
        } catch (InvalidKeyException var12) {
            log.error("公钥算法-无效的私钥:", var12);
        }

        return null;
    }

    public static byte[] hex2Bytes(String source) {
        byte[] sourceBytes = new byte[source.length() / 2];

        for (int i = 0; i < sourceBytes.length; ++i) {
            sourceBytes[i] = (byte) Integer.parseInt(source.substring(i * 2, i * 2 + 2), 16);
        }

        return sourceBytes;
    }

    public static String byte2Hex(byte[] srcBytes) {
        StringBuilder hexRetSB = new StringBuilder();
        byte[] arr$ = srcBytes;
        int len$ = srcBytes.length;

        for (int i$ = 0; i$ < len$; ++i$) {
            byte b = arr$[i$];
            String hexString = Integer.toHexString(255 & b);
            hexRetSB.append(hexString.length() == 1 ? Integer.valueOf(0) : "").append(hexString);
        }

        return hexRetSB.toString();
    }
}
