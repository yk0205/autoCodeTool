package com.auto.tool.common;

import org.mybatis.generator.api.CommentGenerator;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.XmlElement;

import java.util.Properties;

/**
 * 生成model中，字段增加注释
 * Created by ZhangShuzheng on 2017/1/11.
 */
public class GenCommentGenerator implements  CommentGenerator {
	public GenCommentGenerator() {
		super();
	}

	public void addConfigurationProperties(Properties properties) {

	}

	/**
	 * 字段注释
	 * @param field
	 * @param introspectedTable
	 * @param introspectedColumn
	 */
	public void addFieldComment(Field field, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn) {
		//super.addFieldComment(field, introspectedTable, introspectedColumn);
		if (introspectedColumn.getRemarks() != null && !"".equals(introspectedColumn.getRemarks())) {
			field.addJavaDocLine("/** " + introspectedColumn.getRemarks() + " */");
		}
	}

	public void addFieldComment(Field field, IntrospectedTable introspectedTable) {

	}

	public void addModelClassComment(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {

	}

	public void addClassComment(InnerClass innerClass, IntrospectedTable introspectedTable) {

	}

	public void addClassComment(InnerClass innerClass, IntrospectedTable introspectedTable, boolean markAsDoNotDelete) {

	}

	public void addEnumComment(InnerEnum innerEnum, IntrospectedTable introspectedTable) {

	}

	public void addGetterComment(Method method, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn) {

	}

	public void addSetterComment(Method method, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn) {

	}

	public void addGeneralMethodComment(Method method, IntrospectedTable introspectedTable) {

	}

	public void addJavaFileComment(CompilationUnit compilationUnit) {

	}

	public void addComment(XmlElement xmlElement) {

	}

	public void addRootComment(XmlElement rootElement) {

	}

}
