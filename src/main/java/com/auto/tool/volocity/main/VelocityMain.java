package com.auto.tool.volocity.main;

import com.auto.tool.volocity.utils.MybatisGeneratorUtil;
import com.auto.tool.volocity.utils.PropertiesFileUtil;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2018/1/26.
 */
public class VelocityMain {

    // 根据命名规范，只修改此常量值即可
   /* private static String MODULE = "upms";
    private static String DATABASE = "zheng";
    private static String TABLE_PREFIX = "upms_log";
    private static String PACKAGE_NAME = "com.willow.upms"; */
     private static String MODULE = PropertiesFileUtil.getInstance("application").get("project.module");
    private static String DATABASE = PropertiesFileUtil.getInstance("application").get("mysql.database");
    private static String TABLE_PREFIX = PropertiesFileUtil.getInstance("application").get("mysql.data.tablename");
    private static String PACKAGE_NAME = PropertiesFileUtil.getInstance("application").get("project.package.name");
    private static String JDBC_DRIVER = PropertiesFileUtil.getInstance("application").get("jdbc.driver");
    private static String JDBC_URL = PropertiesFileUtil.getInstance("application").get("jdbc.url");
    private static String JDBC_USERNAME = PropertiesFileUtil.getInstance("application").get("jdbc.username");
    private static String JDBC_PASSWORD = PropertiesFileUtil.getInstance("application").get("jdbc.password");
    // 需要insert后返回主键的表配置，key:表名,value:主键名
    private static Map<String, String> LAST_INSERT_ID_TABLES = new HashMap<String, String>();
    static {
        LAST_INSERT_ID_TABLES.put("upms_user", "user_id");
    }

    /**
     * 自动代码生成
     * @param args
     */
    public static void main(String[] args) throws Exception {
       MybatisGeneratorUtil.generator(JDBC_DRIVER, JDBC_URL, JDBC_USERNAME, JDBC_PASSWORD, MODULE, DATABASE, TABLE_PREFIX, PACKAGE_NAME, LAST_INSERT_ID_TABLES);
    }

   public static void create() throws Exception {
        MybatisGeneratorUtil.generator(JDBC_DRIVER, JDBC_URL, JDBC_USERNAME, JDBC_PASSWORD, MODULE, DATABASE, TABLE_PREFIX, PACKAGE_NAME, LAST_INSERT_ID_TABLES);
   }
    public static void create(String tableName) throws Exception {
        if(StringUtils.isNotEmpty(tableName))
        TABLE_PREFIX=tableName;
        MybatisGeneratorUtil.generator(JDBC_DRIVER, JDBC_URL, JDBC_USERNAME, JDBC_PASSWORD, MODULE, DATABASE, TABLE_PREFIX, PACKAGE_NAME, LAST_INSERT_ID_TABLES);
    }
}
