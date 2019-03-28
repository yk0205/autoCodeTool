package com.auto.tool.volocity.common;

import com.auto.tool.volocity.utils.StringUtil;
import org.apache.commons.lang.StringUtils;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;
import org.mybatis.generator.config.CommentGeneratorConfiguration;
import org.mybatis.generator.config.Context;

import java.sql.Types;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * 自定义插件
 *
 * @author CZH
 */
public class VelocityPlugin extends PluginAdapter {
    private Set<String> mappers = new HashSet<String>();
    // 注释生成器
    private CommentGeneratorConfiguration commentCfg;

    @Override
    public void setContext(Context context) {
        super.setContext(context);
        // 设置默认的注释生成器
        /*commentCfg = new CommentGeneratorConfiguration();
        commentCfg.setConfigurationType(GenCommentGenerator.class.getCanonicalName());
        context.setCommentGeneratorConfiguration(commentCfg);*/
        // 支持oracle获取注释#114
        context.getJdbcConnectionConfiguration().addProperty("remarksReporting", "true");
    }

    @Override
    public void setProperties(Properties properties) {
        super.setProperties(properties);
        String mappers = this.properties.getProperty("mappers");
        if (StringUtils.isNotEmpty(mappers)) {
            for (String mapper : mappers.split(",")) {
                this.mappers.add(mapper);
            }
        }
    }

    public void initialized(IntrospectedTable introspectedTable) {
        // 数据库表名
        String tableName = introspectedTable.getFullyQualifiedTableNameAtRuntime();
        // 主键
        String model = StringUtil.lineToHump(tableName);
        introspectedTable.setInsertSelectiveStatementId("save" + model);   //修改sqlId名称
        introspectedTable.setUpdateByPrimaryKeySelectiveStatementId("update" + model);
    }

    public boolean validate(List<String> warnings) {
        return true;
    }

    /**
     * 生成的Mapper接口
     *
     * @param interfaze
     * @param topLevelClass
     * @param introspectedTable
     * @return
     */
    @Override
    public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        // import接口
      /*  for (String mapper : mappers) {
            interfaze.addImportedType(new FullyQualifiedJavaType(mapper));
            interfaze.addSuperInterface(new FullyQualifiedJavaType(mapper));
        }*/
        return true;
    }

    @Override
    public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {
        XmlElement rootElement = document.getRootElement();
        // 数据库表名
        String tableName = introspectedTable.getFullyQualifiedTableNameAtRuntime();
        // 主键
        IntrospectedColumn pkColumn = introspectedTable.getPrimaryKeyColumns().get(0);


        // 公共字段
        XmlElement columnSql = new XmlElement("sql");
        columnSql.addAttribute(new Attribute("id", "sql_columns"));
        StringBuilder columnStr = new StringBuilder();
        // 添加公共where
        XmlElement whereSql = new XmlElement("sql");
        whereSql.addAttribute(new Attribute("id", "sql_where"));
        XmlElement where = new XmlElement("where");
        StringBuilder whereStr = new StringBuilder();
        //拼装更新字段
        XmlElement updateSql = new XmlElement("sql");
        updateSql.addAttribute(new Attribute("id", "sql_update"));

        // 新增数据
        XmlElement save = new XmlElement("insert");
        save.addAttribute(new Attribute("id", "save"));
        save.addAttribute(new Attribute("keyProperty", pkColumn.getJavaProperty()));
        save.addAttribute(new Attribute("useGeneratedKeys", "true"));
        StringBuilder saveStr = new StringBuilder("insert into ").append(tableName).append("(");
        // 要插入的字段(排除自增主键)
        StringBuilder saveColumn = new StringBuilder();
        // 要保存的值
        StringBuilder saveValue = new StringBuilder();

        // 批量保存
        XmlElement batchSave = new XmlElement("insert");
        batchSave.addAttribute(new Attribute("id", "batchSave"));
        StringBuilder btcSaveStr = new StringBuilder("insert into ").append(tableName).append("(");

        // 更新数据
        XmlElement update = new XmlElement("update");
        update.addAttribute(new Attribute("id", "update"));
        StringBuilder updateStr = new StringBuilder("update ").append(tableName).append(" set ").
                append(pkColumn.getActualColumnName()).append(" = #{").append(pkColumn.getJavaProperty()).append("}");
        update.addElement(new TextElement(updateStr.toString()));

        // 批量更新
        XmlElement batchUpdate = new XmlElement("update");
        batchUpdate.addAttribute(new Attribute("id", "batchUpdate"));
        XmlElement foreachUpdate = new XmlElement("foreach");
        foreachUpdate.addAttribute(new Attribute("collection", "list"));
        foreachUpdate.addAttribute(new Attribute("item", "item"));
        foreachUpdate.addAttribute(new Attribute("index", "index"));
        foreachUpdate.addAttribute(new Attribute("open", ""));
        foreachUpdate.addAttribute(new Attribute("close", ""));
        foreachUpdate.addAttribute(new Attribute("separator", ";"));
        batchUpdate.addElement(foreachUpdate);
        StringBuilder btcUpdateStr = new StringBuilder("update ").append(tableName).append(" set ").
                append(pkColumn.getActualColumnName()).append(" = #{").append(pkColumn.getJavaProperty()).append("}");

        foreachUpdate.addElement(new TextElement(btcUpdateStr.toString()));

        // 数据库字段名
        String columnName = null;
        // java字段名
        String javaProperty = null;
        for (IntrospectedColumn introspectedColumn : introspectedTable.getAllColumns()) {
            columnName = MyBatis3FormattingUtilities.getEscapedColumnName(introspectedColumn);
            javaProperty = introspectedColumn.getJavaProperty();
            // 拼装字段
            columnStr.append(columnName).append(",");

            // 拼装公共条件
            XmlElement isNotNullElement = new XmlElement("if");
            whereStr.setLength(0);
            if (introspectedColumn.getJdbcTypeName().equals("VARCHAR")) {
                whereStr.append("null != ").append(javaProperty).append(" and '' != ").append(javaProperty);
            } else {
                whereStr.append("null != ").append(javaProperty);
            }
            isNotNullElement.addAttribute(new Attribute("test", whereStr.toString()));

            whereStr.setLength(0);    /*  #{logId,jdbcType=INTEGER}*/
            whereStr.append(" AND  "+columnName).append(" = #{").append(javaProperty).append(",jdbcType=" + introspectedColumn.getJdbcTypeName() + "}");
            isNotNullElement.addElement(new TextElement(whereStr.toString()));

            where.addElement(isNotNullElement);
            updateSql.addElement(isNotNullElement);

            //保存SQL
            if (!introspectedColumn.isAutoIncrement()) {
                saveColumn.append(",").append(columnName);
                if (Types.TIMESTAMP == introspectedColumn.getJdbcType()) {
                    saveValue.append(", now()");
                } else {
                    saveValue.append(", #{").append(javaProperty).append("}");
                }
            }

        }
        String columns = columnStr.substring(0, columnStr.length() - 1);

        columnStr = new StringBuilder("select ").append(columns).append(" from ").append(tableName);
        columnSql.addElement(new TextElement(columns));
        rootElement.addElement(columnSql);
         //格式xml前面空格
        whereSql.addElement(new TextElement(StringUtil.getFormattedContent(where, 1)));
        rootElement.addElement(whereSql);

        // TODO rootElement.addElement(updateSql);

        saveStr.append(saveColumn.substring(1)).append(") values(").append(saveValue.substring(1)).append(")");
        save.addElement(new TextElement(saveStr.toString()));

        btcSaveStr.append(saveColumn.substring(1)).append(") values");
        batchSave.addElement(new TextElement(btcSaveStr.toString()));
        btcSaveStr.setLength(0);
        XmlElement foreach = new XmlElement("foreach");
        foreach.addAttribute(new Attribute("collection", "list"));
        foreach.addAttribute(new Attribute("item", "item"));
        foreach.addAttribute(new Attribute("index", "index"));
        foreach.addAttribute(new Attribute("separator", ","));
        foreach.addElement(new TextElement("(" + saveValue.toString().substring(1) + ")"));
        batchSave.addElement(foreach);

        XmlElement include = new XmlElement("include");
        include.addAttribute(new Attribute("refid", "sql_update"));
        columnStr = new StringBuilder(" where ").append(pkColumn.getActualColumnName()).append(" = #{").append(pkColumn.getJavaProperty()).append("}");

        update.addElement(include);
        update.addElement(new TextElement(columnStr.toString()));

        foreachUpdate.addElement(include);
        foreachUpdate.addElement(new TextElement(columnStr.toString()));
        String parameterType = introspectedTable.getBaseRecordType();
        rootElement.addElement(selectById(pkColumn, tableName));
        rootElement.addElement(selectXml("selectOne", tableName, parameterType));
        rootElement.addElement(selectXml("selectList", tableName, parameterType));
        rootElement.addElement(selectPage(tableName, parameterType));
        //  rootElement.addElement(save);   //TODO  增加
        //  rootElement.addElement(batchSave); //TODO 批量增加
        // rootElement.addElement(update);       //TODO 修改
        //   rootElement.addElement(batchUpdate);         TODO 批量增加
        rootElement.addElement(btcDels(tableName, pkColumn, "delArray", "array"));  //批量删除
        //  rootElement.addElement(btcDels(tableName, pkColumn, "delList", "list"));//批量删除

         //添加空包
        int len = rootElement.getElements().size();
        for (int i = 0; i < len; i++) {
            rootElement.addElement(i * 2 , new TextElement(""));
        }
        return super.sqlMapDocumentGenerated(document, introspectedTable);
    }

    private XmlElement select(String id, String tableName) {
        XmlElement select = new XmlElement("select");
        select.addAttribute(new Attribute("id", id));
        select.addAttribute(new Attribute("resultMap", "BaseResultMap"));

        select.addElement(new TextElement("select "));
        XmlElement include = new XmlElement("include");
        include.addAttribute(new Attribute("refid", "sql_columns"));
        select.addElement(include);
        select.addElement(new TextElement(" from " + tableName));
        return select;
    }

    private XmlElement selectById(IntrospectedColumn pkColumn, String tableName) {
        XmlElement select = select("selectById", tableName);
        StringBuilder sb = new StringBuilder(" where ");
        sb.append(pkColumn.getActualColumnName());
        sb.append(" = ");
        sb.append(MyBatis3FormattingUtilities.getParameterClause(pkColumn));
        select.addElement(new TextElement(sb.toString()));
        return select;
    }

    private XmlElement selectPage(String tableName, String parameterType) {
        XmlElement select = selectXml("selectPage", tableName, parameterType);
        select.addElement(new TextElement(" limit #{page.startRow}, #{page.pageSize}"));
        return select;
    }

    private XmlElement selectXml(String id, String tableName, String parameterType) {
        XmlElement select = select(id, tableName);
        XmlElement include = new XmlElement("include");
        select.addAttribute(new Attribute("parameterType", parameterType));
        include.addAttribute(new Attribute("refid", "sql_where"));
        select.addElement(include);
        return select;
    }

    private XmlElement btcDels(String tableName, IntrospectedColumn pkColumn, String method, String type) {
        XmlElement delete = new XmlElement("delete");
        delete.addAttribute(new Attribute("id", method));
        delete.addElement(new TextElement("delete from " + tableName + " where " + pkColumn.getActualColumnName() + " in"));
        XmlElement foreach = new XmlElement("foreach");
        foreach.addAttribute(new Attribute("collection", type));
        foreach.addAttribute(new Attribute("item", "item"));
        foreach.addAttribute(new Attribute("index", "index"));
        foreach.addAttribute(new Attribute("open", "("));
        foreach.addAttribute(new Attribute("separator", ","));
        foreach.addAttribute(new Attribute("close", ")"));
        foreach.addElement(new TextElement("#{item}"));
        delete.addElement(foreach);
        return delete;
    }

    //以下设置为false,取消生成默认增删查改xml
    @Override
    public boolean sqlMapDeleteByPrimaryKeyElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        return true;
    }

    @Override
    public boolean sqlMapInsertElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean sqlMapSelectAllElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean sqlMapSelectByPrimaryKeyElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean sqlMapUpdateByPrimaryKeyWithoutBLOBsElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        return false;
    }


    public boolean sqlMapBaseColumnListElementGenerated(XmlElement element,
                                                        IntrospectedTable introspectedTable) {
        return false;
    }

    public boolean sqlMapBlobColumnListElementGenerated(XmlElement element,
                                                        IntrospectedTable introspectedTable) {
        return false;
    }

    public boolean sqlMapResultMapWithBLOBsElementGenerated(XmlElement element,
                                                            IntrospectedTable introspectedTable) {
        return false;
    }

    /**
     * updateByPrimaryKeyWithBLOBs
     *
     * @param element
     * @param introspectedTable
     * @return
     */
    public boolean sqlMapUpdateByPrimaryKeyWithBLOBsElementGenerated(
            XmlElement element, IntrospectedTable introspectedTable) {
        return false;
    }
}

	    			