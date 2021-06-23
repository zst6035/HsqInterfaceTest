package com.hsq.tests;

import com.hsq.utils.TestConfig;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;



@Slf4j
public class SendMail {
    public static void main(String [] args)throws Exception {
       File file= TestConfig.orderByDate();
       String [] emailsTo={"shouting_zhang@baofu.com","1148744992@qq.com"};
       TestConfig.sendMail(emailsTo,file);
        log.info("邮件发送成功");
    }
}
