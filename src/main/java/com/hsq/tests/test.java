package com.hsq.tests;

import org.testng.annotations.Test;

import java.util.Random;

public class test {
    @Test
    public void test01() throws InterruptedException {
        Thread.sleep(10);
        System.out.println(1);
        int i=new Random().nextInt(10)+1;
        System.out.println(i);
    }


    @Test
    public void test02()  {
        System.out.println(3);
    }
}
