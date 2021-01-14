package com.hsq.utils;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.Reader;

//用于声明一个数据库连接
public class DatabaseUtil {

    public static SqlSession getSqlSession1() throws IOException {
        //获取配置的资源文件
        Reader reader = Resources.getResourceAsReader("databaseConfig.xml");
        //得到SqlSessionFactory，使用类加载器加载xml文件
        SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(reader);
        //得到sqlsession对象，这个对象就能执行配置文件中的sql语句啦,可以设置自动提交，就是下面的true,不自动提交，就是false
        SqlSession session = factory.openSession(true);

        return session;
    }

    public static SqlSession getSqlSessionBf() throws IOException {
        //获取配置的资源文件
        Reader reader = Resources.getResourceAsReader("databaseConfigBaofu.xml");
        //得到SqlSessionFactory，使用类加载器加载xml文件
        SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(reader);
        //得到sqlsession对象，这个对象就能执行配置文件中的sql语句啦,可以设置自动提交，就是下面的true,不自动提交，就是false
        SqlSession session = factory.openSession(true);

        return session;
    }

    public static SqlSession getSqlSessionHsqGateway() throws IOException {
        //获取配置的资源文件
        Reader reader = Resources.getResourceAsReader("databaseConfigHsqGateway.xml");
        //得到SqlSessionFactory，使用类加载器加载xml文件
        SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(reader);
        //得到sqlsession对象，这个对象就能执行配置文件中的sql语句啦,可以设置自动提交，就是下面的true,不自动提交，就是false
        SqlSession session = factory.openSession(true);

        return session;
    }

    public static SqlSession getSqlSessionHsqTrader() throws IOException {
        //获取配置的资源文件
        Reader reader = Resources.getResourceAsReader("databaseConfigHsqTrade.xml");
        //得到SqlSessionFactory，使用类加载器加载xml文件
        SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(reader);
        //得到sqlsession对象，这个对象就能执行配置文件中的sql语句啦,可以设置自动提交，就是下面的true,不自动提交，就是false
        SqlSession session = factory.openSession(true);

        return session;
    }
}
