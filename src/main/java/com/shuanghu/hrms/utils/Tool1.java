package com.shuanghu.hrms.utils;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class Tool1 {


    //1.编码设置方法
    public void setUtf8_Text(HttpServletRequest request, HttpServletResponse response)
            throws UnsupportedEncodingException {
        request.setCharacterEncoding("UTF-8");   //【新增注释】设置 request 编码格式，避免接收中文出现乱码
        response.setCharacterEncoding("UTF-8");  //【新增注释】设置 response 输出编码
        response.setContentType("text/html;Charset=UTF-8"); //【新增注释】指定响应内容类型及编码
    }

    public void setUtf8_Html(HttpServletRequest request, HttpServletResponse response)
            throws UnsupportedEncodingException {
        request.setCharacterEncoding("UTF-8");   //【新增注释】同上，用于 HTML 响应
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;Charset=UTF-8");
    }


    //2. 请求参数与实体对象转换方法
    public Object requestToModel(HttpServletRequest request, Object object) {
        // 使用object反射接收外部對象。通过反射将 HTTP 请求参数（包括普通字段和文件）映射到实体对象。
        String savePath = "F:\\upLoadFile"; //【新增注释】文件上传存储路径（需确保服务器有写权限）
        Class<?> T0 = object.getClass(); // 返回任意对象实例的类名
        Field[] fields = T0.getDeclaredFields(); //【新增注释】取出实体类全部属性，用于匹配 request 参数
        //用分部part办法取前端数据过来，方便以后区分请求是否带文件图片等
        Collection<Part> parts = null;
        try {
            parts = request.getParts(); //【新增注释】一次性获取所有表单字段（包括文件与普通文本）
        } catch (IOException e2) {
            // TODO Auto-generated catch block
            e2.printStackTrace();
        } catch (ServletException e2) {
            // TODO Auto-generated catch block
            e2.printStackTrace();
        }
        // 把前端请求数据每一项当成一个独立部分存放到一个集合parts中
        for (Part part : parts) // 遍历前端每个部分
        {
            if (part.getContentType() == null) // 普通文本数字，否则就是各种文件
            {// 普通一般数据类型
                String[] valueS = request.getParameterValues(part.getName());
                //【新增注释】一次获取同名字段的所有值（例如多选框可能返回多个值）

                if (valueS.length == 1) // 表示是单值，如姓名
                { // 遍历实例学生成员变量，把对应值存放进去
                    for (Field field : fields)
                    {
                        if (field.getName().equals(part.getName()))
                        {
                            field.setAccessible(true); // 外部强制去访问类成员变量
                            try
                            {
                                Object value = valueS[0]; //【新增注释】表单字段值默认是字符串
                                // 类型转换：int类型
                                if (field.getType() == int.class)
                                {
                                    value = Integer.parseInt(value.toString()); //【新增注释】将字符串转 int
                                }
                                // 类型转换：double类型
                                else if (field.getType() == double.class)
                                {
                                    value = Double.parseDouble(value.toString()); //【新增注释】将字符串转 double
                                }
                                else if (field.getType() == java.sql.Date.class && value != null && !value.toString().isEmpty())
                                {
                                    //【新增注释】前端如 2000-01-01 转成 java.sql.Date
                                    value = java.sql.Date.valueOf(value.toString());
                                }
                                field.set(object, value); // 赋值转换后的值
                            }
                            catch (IllegalArgumentException | IllegalAccessException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                    }
                }

                else // 多值，如爱好要单独累加后再处理
                {
                    for (Field field : fields)
                    {
                        if (field.getName().equals(part.getName()))
                        {
                            field.setAccessible(true); // 外部强制去访问类成员变量
                            // 把爱好多值累加
                            String t = "";
                            // 先把多值如多个爱好值累加成一个串值
                            for (int i = 0; i < valueS.length; i++)
                                t = t + valueS[i] + ",";
                            // 去掉右边多余逗号
                            t = t.substring(0, t.length() - 1);
                            try {
                                field.set(object, t); //【新增注释】将以逗号分隔的字符串填入实体
                            } catch (IllegalArgumentException | IllegalAccessException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }

            else
            {
                //String header = part.getHeader("Content-Disposition");
                // 获取文件名字,但它是一个完整路径，我们不需要前面部分
                // System.out.println(header);
                String filename = part.getSubmittedFileName();// 这个是有效文件名
                System.out.println("测试文件名字,准备取出文件有效名称");
                System.out.println(filename);// 如果文件名前带路径，要把最后有效文件取名出来，不同系统这里有可能不同
                // 防止文件上传后重名，加日期数字前缀
                long temp = new java.util.Date().getTime();
                filename = String.valueOf(temp) + "." + filename; // 把日间戳转换为串累加到文件名前
                System.out.println(savePath + "\\" + filename);
                try {
                    part.write(savePath + "\\" + filename);  //【新增注释】将上传的文件保存到硬盘
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                } // 把文件内容写到指定位置
                // 下面把文件名和路径存放到数据库命令串中.本处只考虑了一次带一个文件
                for (Field field : fields) {
                    if (field.getName().equals(part.getName())) {
                        field.setAccessible(true); // 外部强制去访问类成员变量
                        try {
                            field.set(object, filename); //【新增注释】将文件名存入实体字段
                        } catch (IllegalArgumentException | IllegalAccessException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        return object; //【新增注释】返回填充完字段的实体对象
    }


    //3. 实体对象与 SQL 转换方法
    public String modelToInsertString(Object object, String tableName) {
        //根据实体对象字段生成 SQL 插入语句（insert into ...），拼接字段名和对应值。
        Class<?> T0 = object.getClass();
        java.lang.reflect.Field[] fields = T0.getDeclaredFields();
        String inserStr = "insert into " + tableName + "(";
        String colString = "";
        String valueString = "";
        for (Field field : fields)
        {
            colString = colString + field.getName() + ",";
            //【新增注释】将字段名累加：col1,col2,col3,...

            try {
                field.setAccessible(true);
                valueString = valueString + "'" + field.get(object) + "',";
                //【新增注释】将字段值以字符串形式拼接到 value 中
            } catch (IllegalArgumentException | IllegalAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } // 根据当前列名去实例中去值

        }
        colString = colString.substring(0, colString.length() - 1); // 去掉右边多余逗号
        valueString = valueString.substring(0, valueString.length() - 1);
        inserStr = inserStr + colString + ")values(" + valueString + ")";

        return inserStr; //【新增注释】返回完整 insert SQL 字符串
    }

    public void modelToDb(Object object, String tableName) {
        // 把模型数据插入到数据库中
        // 1. 根据对象和表名生成insert语句
        String insertSql = modelToInsertString(object, tableName);

        // 2. 执行SQL语句完成插入操作
        int rowsAffected = excuteNoQuery(insertSql); //【新增注释】执行 INSERT，返回影响行数

        // 3. 可根据需要添加操作结果的日志输出
        if (rowsAffected > 0) {
            System.out.println("数据插入成功，影响行数：" + rowsAffected); //【新增注释】插入成功日志
        } else {
            System.out.println("数据插入失败"); //【新增注释】插入失败提示
        }
    }


    //4. JSON 处理方法
    public JSONObject modelToJson(Object object) {
        //通过反射将实体对象转换为JSONObject
        //使用LinkedHashMap保证字段顺序与实体类一致，便于前端解析时保持字段顺序。
        Class<?> T0 = object.getClass(); // 返回任意对象实例的类名
        java.lang.reflect.Field[] fields = T0.getDeclaredFields();
        JSONObject jsonObject = new JSONObject(new LinkedHashMap<String, Object>());// 防止json內部值錯位混亂

        for (Field field : fields)
        {
            field.setAccessible(true); //【新增注释】允许反射访问 private 字段
            try {
                jsonObject.put(field.getName(), field.get(object));
                //【新增注释】将对象字段名与字段值写入 JSONObject
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return jsonObject; //【新增注释】返回 JSON 对象
    }

    @SuppressWarnings("null")
    public JSONArray modelToJsonArray(Object object) {
        //将单个实体对象转换为包含该对象的JSONArray（存在空指针风险，jsonArray未初始化）。
        Class<?> T0 = object.getClass(); // 返回任意对象实例的类名
        java.lang.reflect.Field[] fields = T0.getDeclaredFields();
        JSONArray jsonArray=null;   //【新增注释】此处未 new JSONArray()，会导致 NullPointerException
        //【新增注释】但保持你的原逻辑，不修改，仅提示风险

        JSONObject jsonObject = new JSONObject(new LinkedHashMap<String, Object>());// 防止json內部值錯位混亂

        for (Field field : fields) {
            field.setAccessible(true);
            try {
                jsonObject.put(field.getName(), field.get(object)); //【新增注释】实体字段写入 JSON
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        jsonArray.add(jsonObject); //【新增注释】此处会抛异常，因为 jsonArray = null

        return jsonArray; //【新增注释】返回 JSON 数组（当前代码实际会报 NullPointerException）
    }

    public void reponseJson(HttpServletResponse response, JSONObject josn) {
        //将JSONObject数据通过响应流返回给前端，自动完成 JSON 字符串转换并关闭流资源。
        PrintWriter out = null;
        try {
            out = response.getWriter();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        out.write(JSON.toJSONString(josn));
        out.flush();
        out.close();
    }

    public void reponseJsonArray(HttpServletResponse response, JSONObject josn) {
        //与reponseJson实现一致（参数名可能有误，应为JSONArray），用于将 JSON 数组返回给前端。
        PrintWriter out = null;
        try {
            out = response.getWriter();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        out.write(JSON.toJSONString(josn));
        out.flush();
        out.close();
    }

    // 新增：List<String>转JSON数组（用于响应前端下拉框数据）
    public JSONArray listToJsonArray(List<String> list) {
        //将字符串列表转换为JSONArray，适用于前端下拉框等需要数组数据的场景。
        JSONArray jsonArray = new JSONArray();
        for (String str : list) {
            jsonArray.add(str);
        }
        return jsonArray;
    }


    //5. 数据库操作方法
    public static int excuteNoQuery(String sqlString) {//数据查询叫query,数据增删改叫noquery
        //执行增删改类型的 SQL 语句（INSERT/UPDATE/DELETE），
        //硬编码数据库连接参数（驱动、URL、用户名、密码），返回受影响的行数，执行后关闭数据库连接和语句对象。
        String driver1 = "com.mysql.jdbc.Driver";// 定义驱动串，它是一个类
        String dburl = "jdbc:mysql://localhost:3306/shuanghu";// 如果是使用mysql8.0以上的版本，新版数据库的时区设置与本地时区不同，可能造成乱码或日期时间问题，可以在数据库名字后加上
        // “?serverTimezone=UTC”
        String username1 = "root";
        String password1 = "123456"; // 以上是定义数据库驱动时的参数，数据库类型，用户，密码等
        int re=0;
        java.sql.Connection conn1 = null; // 连接对象，把JAVA与数据库连接起来
        try {
            Class.forName(driver1);//根据类的字符串名字来实例化一个对象。
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } // 加载数据库驱动程序，实例化
        try { //得到一个数据库连接实例对象conn1
            conn1 =DriverManager.getConnection(dburl, username1, password1);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //创建一个数据库的连接
        java.sql.Statement pst1 = null;//建立一个执行命令的对象
        try {
            pst1 = conn1.createStatement();//实例化命令对象
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } // 实例化一个命令对象statement,用于接收和执行sql命令
        try {
            System.out.println("准备执行插入数据：");
            re=pst1.executeUpdate(sqlString);//这种修改类的操作返回的是数据库表的操作成功的行数
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }// 执行命令，update管insert,update,delete
        try {
            pst1.close();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            conn1.close();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }// 最后关闭st和con
        return re;
    }

    // 新增：执行查询SQL，返回ResultSet（查询专用）
    public ResultSet excuteQuery(String sqlString) {
        //执行查询类型的 SQL 语句（SELECT），
        //返回ResultSet结果集，连接参数硬编码，不立即关闭资源（避免结果集失效，需后续手动关闭）。
        String driver1 = "com.mysql.jdbc.Driver";
        String dburl = "jdbc:mysql://localhost:3306/shuanghu?serverTimezone=UTC"; // 补全时区参数
        String username1 = "root";
        String password1 = "123456";
        java.sql.Connection conn1 = null;
        java.sql.Statement pst1 = null;
        ResultSet rs = null;

        try {
            Class.forName(driver1);
            conn1 = DriverManager.getConnection(dburl, username1, password1);
            pst1 = conn1.createStatement();
            rs = pst1.executeQuery(sqlString); // 执行查询，返回结果集
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }

        // 注意：此处不关闭连接和Statement，后续处理完ResultSet后再关闭（避免ResultSet失效）
        return rs;
    }


    // 新增：ResultSet转实体类列表（支持单行/多行结果）
    public <T> List<T> resultSetToModelList(ResultSet rs, Class<T> clazz) {
        //通过反射将ResultSet结果集转换为指定类型的实体类列表，
        //自动处理字段类型映射（如日期类型），处理完成后关闭结果集、语句和连接资源。
        List<T> modelList = new ArrayList<>();
        try {
            ResultSetMetaData metaData = rs.getMetaData(); // 获取结果集列信息
            int columnCount = metaData.getColumnCount(); // 列数

            while (rs.next()) { // 遍历每一行数据
                T model = clazz.getConstructor().newInstance(); // 调用无参构造创建实体对象
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i); // 列名（需与实体类属性名一致）
                    Object columnValue = rs.getObject(i); // 列值

                    // 反射设置实体类属性值
                    Field field = clazz.getDeclaredField(columnName);
                    field.setAccessible(true);
                    // 处理日期类型转换（java.sql.Date -> java.util.Date，根据实体类属性调整）
                    if (field.getType() == java.sql.Date.class && columnValue != null) {
                        field.set(model, (java.sql.Date) columnValue);
                    } else {
                        field.set(model, columnValue);
                    }
                }
                modelList.add(model);
            }

            // 关闭资源（ResultSet、Statement、Connection）
            java.sql.Statement stmt = rs.getStatement();
            java.sql.Connection conn = stmt.getConnection();
            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return modelList;
    }


    // 新增：查询指定表的指定列所有值（用于前端下拉框加载）
    public List<String> getTableColumnValues(String tableName, String columnName) {
        //查询指定表中某一列的所有去重值（DISTINCT），返回字符串列表，常用于前端下拉框加载数据（如省份、类别等选项）。
        List<String> values = new ArrayList<>();
        String sql = "SELECT DISTINCT " + columnName + " FROM " + tableName; // DISTINCT去重
        ResultSet rs = excuteQuery(sql);
        try {
            while (rs.next()) {
                values.add(rs.getString(columnName)); // 提取列值并添加到列表
            }
            // 关闭资源
            java.sql.Statement stmt = rs.getStatement();
            java.sql.Connection conn = stmt.getConnection();
            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return values;
    }


    /**
     * 生成更新SQL语句（根据实体对象和主键字段）
     * @param object 实体对象（含修改后的数据）
     * @param tableName 表名
     * @param primaryKey 主键字段名（如"id"）
     * @return UPDATE语句
     */
    public String modelToUpdateString(Object object, String tableName, String primaryKey) {
        Class<?> clazz = object.getClass();
        Field[] fields = clazz.getDeclaredFields();
        StringBuilder setClause = new StringBuilder();
        Object primaryValue = null;

        try {
            for (Field field : fields) {
                field.setAccessible(true);
                String fieldName = field.getName();
                if (fieldName.equals(primaryKey)) {
                    primaryValue = field.get(object); // 记录主键值
                    continue;
                }
                setClause.append(fieldName).append("='").append(field.get(object)).append("',");
            }
            // 移除最后一个逗号
            setClause.setLength(setClause.length() - 1);
            return "UPDATE " + tableName + " SET " + setClause + " WHERE " + primaryKey + "='" + primaryValue + "'";
        } catch (Exception e) {
            throw new RuntimeException("生成更新SQL失败", e);
        }
    }

    /**
     * 执行单条件查询（返回实体列表）
     * @param sql 带占位符的查询SQL（如"SELECT * FROM user WHERE name=?"）
     * @param param 条件参数
     * @param clazz 实体类类型
     * @return 实体列表
     */
    public <T> List<T> queryBySingleCondition(String sql, Object param, Class<T> clazz) {
        ResultSet rs = JdbcUtil.executeQuery(sql, new Object[]{param});
        return resultSetToModelList(rs, clazz); // 复用现有转换方法
    }

    /**
     * 执行多条件查询
     * @param sql 带多个占位符的SQL（如"SELECT * FROM book WHERE press=? AND price>?"）
     * @param params 条件参数数组
     * @param clazz 实体类类型
     * @return 实体列表
     */
    public <T> List<T> queryByMultiConditions(String sql, Object[] params, Class<T> clazz) {
        ResultSet rs = JdbcUtil.executeQuery(sql, params);
        return resultSetToModelList(rs, clazz);
    }

    /**
     * 删除数据（根据主键）
     * @param tableName 表名
     * @param primaryKey 主键字段名
     * @param primaryValue 主键值
     * @return 影响行数
     */
    public int deleteByPrimaryKey(String tableName, String primaryKey, Object primaryValue) {
        String sql = "DELETE FROM " + tableName + " WHERE " + primaryKey + "=?";
        return JdbcUtil.executeUpdate(sql, new Object[]{primaryValue});
    }
}