package com.bittech.everything.core.dao;

import com.alibaba.druid.pool.DruidDataSource;
import com.bittech.everything.config.myEverythingConfig;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DataSourceFactory {

    /**
     * 数据源（单例）
     */
    private static volatile DruidDataSource dataSource;

    private DataSourceFactory() {

    }

    public static DataSource dataSource() {
        if (dataSource == null) {
            synchronized(DataSourceFactory.class) {
                if (dataSource == null) {
                    //实例化
                    dataSource = new DruidDataSource();
                    //JDBC  driver class
                    dataSource.setDriverClassName("org.h2.Driver");
                    //url, username, password
                    //采用的是H2的嵌入式数据库，数据库以本地文件的方式存储，只需要提供url接口
                    //JDBC规范中关于MySQL jdbc:mysql://ip:port/databaseName

                    //JDBC规范中关于H2 jdbc:h2:filepath ->存储到本地文件
                    //JDBC规范中关于H2 jdbc:h2:~/filepath ->存储到当前用户的home目录
                    //JDBC规范中关于H2 jdbc:h2://ip:port/databaseName ->存储到服务器
                    dataSource.setUrl("jdbc:h2:" +myEverythingConfig.getInstance().getH2IndexPath());

                    //Druid数据库连接池的可配置参数
                    //第一种
                    //dataSource.setValidationQuery("select now");
                    //第二种
                    dataSource.setTestWhileIdle(false);
                }
            }
        }
        return dataSource;
    }

    public static void initDatabase() {
        //1.获取数据源
        DataSource dataSource = DataSourceFactory.dataSource();
        //2.获取SQL语句
        //不采取读取绝对路径文件
        //读取classpath路径下的文件
        try (InputStream in = DataSourceFactory.class.getClassLoader().getResourceAsStream("my_everything.sql");) {
            if (in == null) {
                throw new RuntimeException("Not read init database script please check it");
            }
            StringBuilder sqlBuilder = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(in));) {
                String line = null;
                while ((line = reader.readLine()) != null) {
                    if (!line.startsWith("--")) {
                        sqlBuilder.append(line);
                    }
                }
            }
            //3.获取数据库连接和名称执行SQL
            String sql = sqlBuilder.toString();
            //JDBC
            //3.1获取数据库的连接
            Connection connection = dataSource.getConnection();
            //3.2创建命令
            PreparedStatement statement = connection.prepareStatement(sql);
            //3.3执行SQL语句
            statement.execute();
            connection.close();
            statement.close();
        } catch (IOException e) {

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}