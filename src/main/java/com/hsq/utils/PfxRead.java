package com.hsq.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * <p>
 * 读取私钥
 * </p>
 *
 * @author huaheshang
 * @version 1.0
 * @class_name PfxRead
 * @date 2019/5/28
 * @time 3:39 PM
 */
@Slf4j
public class PfxRead {
    /**
     * 读取私钥内容
     * @param in 输入流
     * @return 返回字节数组
     */
    public static byte[] readStream(InputStream in) {
        ByteArrayOutputStream bos = null;
        try {

            bos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = -1;
            while ((len = in.read(buffer)) != -1) {
                bos.write(buffer, 0, len);
            }
        } catch (Exception e) {
          //  log.error("读取私钥异常:", e);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (bos != null) {
                    bos.close();
                }
            } catch (Exception e) {
             //   log.error("读取私钥异常:", e);

            }
        }
        byte[] t=bos.toByteArray();

        String s= Base64.encodeBase64String(t);
        byte[]d= Base64.decodeBase64(s);
      //  log.info("bsse:"+s);
        return t;
    }
}
