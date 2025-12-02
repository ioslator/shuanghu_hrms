package utils;

import java.io.*;
import java.sql.*;
import java.util.Properties;

public class JdbcUtil {

    private static String driver;
    private static String url;
    private static String username;
    private static String password;

    private static Properties pro = new Properties();

    // 静态代码块：读取配置文件 + 加载驱动（关键修改在这！）
    static {
        try {
            // 方案1（推荐！最稳）：优先用类加载器找（支持 Eclipse + 打 war 包）
            InputStream is = JdbcUtil.class.getClassLoader().getResourceAsStream("jdbc.properties");

            if (is == null) {
                // 方案2（兜底）：如果你非要放 F 盘，也可以继续用绝对路径（保留你的原逻辑）
                System.out.println("类加载器没找到 jdbc.properties，尝试绝对路径...");
                is = new FileInputStream("F:\\Eclipse\\shuanghu_hrms\\jdbc.properties");
            }

            pro.load(is);

            // ✅ 正确代码：加上 jdbc. 前缀
            driver = pro.getProperty("jdbc.driver");
            url = pro.getProperty("jdbc.url");
            username = pro.getProperty("jdbc.username");
            password = pro.getProperty("jdbc.password");

// 建议增加空值检查，方便排查
            if (driver == null) {
                throw new RuntimeException("在 jdbc.properties 中未找到 'jdbc.driver' 配置，请检查键名是否匹配！");
            }

            Class.forName(driver);
            System.out.println("数据库驱动加载成功！URL = " + url);

        } catch (IOException | ClassNotFoundException e) {
            System.err.println("加载 jdbc.properties 失败或驱动加载失败！");
            e.printStackTrace();
        }
    }

    // 获取连接（保持你原样）
    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            System.err.println("数据库连接失败！");
            e.printStackTrace();
            return null;
        }
    }

    // 关闭资源（你写得完美，保留）
    public static void close(ResultSet rs, Statement st, Connection con) {
        try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
        try { if (st != null) st.close(); } catch (SQLException e) { e.printStackTrace(); }
        try { if (con != null) con.close(); } catch (SQLException e) { e.printStackTrace(); }
    }

    public static void close(Statement st, Connection con) {
        close(null, st, con);
    }

    // 你原来的 executeQuery 和 executeUpdate（一个字不改！）
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