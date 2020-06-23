package com.etoak.epidemic.factory;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.dbcp.DataSourceConnectionFactory;


import javax.sql.DataSource;
import javax.xml.crypto.Data;
import java.io.InputStream;
import java.sql.Connection;
import java.util.Properties;


public class CF {
    public static void main(String[] args) throws Exception {
/*        BasicDataSource ds = new BasicDataSource();
        ds.setDriverClassName("oracle.jdbc.driver.OracleDriver");
        *//*
         * oracle.jdbc.driver.OracleDriver
         *
         * *//*
        ds.setUrl("jdbc:oracle:thin:@localhost:1521:orcl");
        ds.setUsername("scott");
        ds.setPassword("etoak");
        Connection con = ds.getConnection();
        System.out.println(con);*/
        Connection con = CF.getConnection();
        System.out.println(con);
    }

    private static BasicDataSource ds = new BasicDataSource();

    private CF() {
    }

    static {
        try {
            InputStream is = CF.class.getClassLoader().getResourceAsStream("db.properties");
            Properties pro = new Properties();
            pro.load(is);
            is.close();

            ds.setDriverClassName(pro.getProperty("o.driver"));
            ds.setUrl(pro.getProperty("o.url"));
            ds.setUsername(pro.getProperty("o.user"));
            ds.setPassword(pro.getProperty("o.pwd"));

            ds.setMaxActive(Integer.parseInt(pro.getProperty("o.maxActive")));
            ds.setMaxIdle(Integer.parseInt(pro.getProperty("o.maxIdle")));
            ds.setMaxWait(Integer.parseInt(pro.getProperty("o.maxWait")));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static DataSource getDs() {
        return ds;
    }

    public static Connection getConnection() {
        try {
            return ds.getConnection();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


}












