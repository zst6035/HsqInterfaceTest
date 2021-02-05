package com.hsq.tests;

import org.testng.annotations.Test;

public class test {
    @Test
    public void test01() throws InterruptedException {
        Thread.sleep(10);
        System.out.println(1);
    }


    @Test
    public void test02()  {
        System.out.println(3);
    }
}
