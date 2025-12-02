package com.shuanghu.hrms.utils;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import java.sql.*;

@Component
@Lazy(false)  // 强制 Spring 启动时就创建
public class JdbcUtil {

    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Value("${spring.datasource.driver-class-name}")
    private String driver;

    private static JdbcUtil instance;

    @PostConstruct
    public void init() {
        instance = this;
        try {
            Class.forName(driver);
            System.out.println("【JdbcUtil】驱动加载成功，URL = " + url);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() {
        if (instance == null) {
            throw new IllegalStateException("JdbcUtil 未被 Spring 初始化！请检查是否加了 @Component");
        }
        try {
            return DriverManager.getConnection(instance.url, instance.username, instance.password);
        } catch (SQLException e) {
            System.err.println("数据库连接失败！URL=" + instance.url);
            e.printStackTrace();
            return null;
        }
    }

    public static void close(ResultSet rs, Statement st, Connection con) {
        try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
        try { if (st != null) st.close(); } catch (SQLException e) { e.printStackTrace(); }
        try { if (con != null) con.close(); } catch (SQLException e) { e.printStackTrace(); }
    }

    public static void close(Statement st, Connection con) {
        close(null, st, con);
    }

    public static ResultSet executeQuery(String sql, Object[] params) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            if (params != null) {
                for (int i = 0; i < params.length; i++) {
                    pstmt.setObject(i + 1, params[i]);
                }
            }
            rs = pstmt.executeQuery();
            return rs;
        } catch (SQLException e) {
            System.err.println("executeQuery 执行失败：" + sql);
            e.printStackTrace();
            close(rs, pstmt, conn);
            return null;
        }
    }

    public static int executeUpdate(String sql, Object[] params) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            if (params != null) {
                for (int i = 0; i < params.length; i++) {
                    pstmt.setObject(i + 1, params[i]);
                }
            }
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("executeUpdate 执行失败：" + sql);
            e.printStackTrace();
            return 0;
        } finally {
            close(pstmt, conn);
        }
    }

    public int excuteNoQuery(String sqlString) {
        return executeUpdate(sqlString, null);
    }
}