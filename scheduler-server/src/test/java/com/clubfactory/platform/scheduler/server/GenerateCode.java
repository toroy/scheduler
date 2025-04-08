package com.clubfactory.platform.scheduler.server;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;


import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class GenerateCode {
	
    public static void main(String[] args) throws Exception {
        generateSingle("DqcTask","sc_dqc_task");
    }

    //公司名称
    private static String corpName = "clubfactory";

    //工程名字，例如最终会拼接ywwl-wechat-biz这样的路径
    private static String pre = "platform";
    
    private static String end = "scheduler";
    
    
    private static String projectName = end;
    
    private static String packageName = pre + "." + end;
    
    private static String packagePath = pre + "/" + end;

    private static Configuration cfg;


    private static String userDir;

    static{

        try
        {
            userDir = new File(".").getCanonicalPath().replace("\\","/");
//            userDir = System.getProperty("user.dir").replace("\\","/");

            /* 在整个应用的生命周期中，这个工作你应该只做一次。 */
            /* 创建和调整配置。 */
            cfg = new Configuration();
            cfg.setClassForTemplateLoading(GenerateCode.class,"/genftl");
            cfg.setObjectWrapper(new DefaultObjectWrapper());
            /* 在整个应用的生命周期中，这个工作你可以执行多次 */
            /* 获取或创建模板 */

            InputStream is = GenerateCode.class.getResourceAsStream("/freemarker.properties");
            cfg.setSettings(is);
            cfg.setEncoding(Locale.SIMPLIFIED_CHINESE, "UTF-8");
            is.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (TemplateException e)
        {
            e.printStackTrace();
        }
    }
    private static String baseProjectPath = userDir + "/" + projectName + "-";

    private static String mavenJavaPath =  "/src/main/java/com/" + corpName + "/" + packagePath + "/";


    public static void testGenerateAll()
    {
        String poBasePackagePath = baseProjectPath + "dal" + mavenJavaPath + "/dal/po";

        File file = new File(poBasePackagePath);

        if(!file.exists())
        {
            return;
        }
        Collection<File> fileNames = FileUtils.listFiles(file,new String[]{"java"}, false);
        if(CollectionUtils.isEmpty(fileNames))
        {
            return;
        }
        fileNames.stream().forEach(s -> {
            String fileName = s.getName().substring(0,s.getName().lastIndexOf(".java"));
            PoMetaInfo getClassInfo = getClassInfo(fileName);
            generateSingle(fileName,null);
        });
    }

    public static void generateSingle(String poClassName,String tableName)
    {
        PoMetaInfo getClassInfo = getClassInfo(poClassName);
        if(StringUtils.isNotBlank(tableName))
        {
            getClassInfo.setTableName(tableName);
        }
        try {
            renderFile(getClassInfo);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void renderFile(PoMetaInfo info)
            throws Exception {
        String entityName = info.getSimpleClassName();
        FileWriter out = null;
        /**
         * Controller
         */
        String javaFileName = entityName + "Controller.java";
        String javaFilePath = baseProjectPath + "web" + mavenJavaPath +  "/web/controller/" + javaFileName;
        Template ctlTemplate = cfg.getTemplate("ctl.ftl");
//        out = new FileWriter(javaFilePath);
//        ctlTemplate.process(info, out);

        /**
         * dao
         */
        javaFileName = entityName + "Mapper.java";
        javaFilePath = baseProjectPath + "dal" + mavenJavaPath +  "/dal/dao/" + javaFileName;
        ctlTemplate = cfg.getTemplate("dao.ftl");
        out = new FileWriter(javaFilePath);
        ctlTemplate.process(info, out);

        /**
         * xml  数据库xml
         */
        javaFileName = entityName + ".xml";
        javaFilePath = baseProjectPath + "dal/src/main/resources/mybatis/mapper/" + javaFileName;
        ctlTemplate = cfg.getTemplate("xml.ftl");
        out = new FileWriter(javaFilePath);
        ctlTemplate.process(info, out);

        /**
         * service
         */
        javaFileName = entityName + "Service.java";
        javaFilePath = baseProjectPath + "core" + mavenJavaPath  + "/core/service/impl/" + javaFileName;
        ctlTemplate = cfg.getTemplate("service.ftl");
        out = new FileWriter(javaFilePath);
        ctlTemplate.process(info, out);

        /**
         * vo
         */
        javaFileName = entityName + "VO.java";
        javaFilePath = baseProjectPath + "core" + mavenJavaPath + "/core/vo/" + javaFileName;
        ctlTemplate = cfg.getTemplate("vo.ftl");
        out = new FileWriter(javaFilePath);
        ctlTemplate.process(info, out);

        /**
         * 单元测试
         */
//        javaFileName = entityName + "ControllerTest.java";
//        javaFilePath = baseProjectPath + "web/src/test/java/com/" +  corpName + "/" + projectName + "/test/controller/" + javaFileName;
//        ctlTemplate = cfg.getTemplate("ctltest.ftl");
//        out = new FileWriter(javaFilePath);
//        ctlTemplate.process(info, out);

        /**
         * create建表语句
         */
        javaFileName = entityName + ".sql";
        javaFilePath = baseProjectPath + "dal/src/main/resources/mybatis/" + javaFileName;
        ctlTemplate = cfg.getTemplate("sql.ftl");
        out = new FileWriter(javaFilePath);
        ctlTemplate.process(info, out);

        /**
         * create建表语句，控制台输出后，复制，拷贝到测试数据库执行
         */
        ctlTemplate = cfg.getTemplate("sql.ftl");
        System.err.println("------------sql建表语句-------------");
        ctlTemplate.process(info, new OutputStreamWriter(System.err));


        out.flush();
        out.close();
    }

    public static PoMetaInfo getClassInfo(String poClassName) {
        PoMetaInfo info = new PoMetaInfo();
        info.setCorpName(corpName);
        info.setProjectName(projectName);
        info.setPropertyName(firstLetterChangeCase('l',poClassName));
        info.setSimpleClassName(poClassName);
        info.setTableName(camelToSplit(poClassName,null,true));
        info.setUrlPath(camelToSplit(poClassName,"/",false));
        info.setPackagePath("com." + corpName + "." + packageName);
        String className = "com." + corpName + "." + packageName + ".dal.po." + poClassName;
        Class<?> c = null;
        try {
            c = Class.forName(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        //获取字段名字和字段类型
        Map<String,FieldMeta> fieldMetaMap = getFieldList(c.getDeclaredFields());
        info.setFieldList(fieldMetaMap.values());

        //获取字段注释（中文解释）
        getFieldCommet(poClassName,fieldMetaMap);
        return info;
    }

    public static Map<String,FieldMeta> getFieldList(Field[] fields){
        Map<String,FieldMeta> fieldMetaMap = new HashMap<String,FieldMeta>();

        for (Field field : fields) {
            String fieldName = field.getName();
            Class<?> returnTypeClass = field.getType();
            if(fieldName!=null)
            {
                FieldMeta fieldMeta = new FieldMeta();
                fieldMetaMap.put(fieldName,fieldMeta);
                fieldMeta.setFieldName(fieldName);
                fieldMeta.setFieldUpcase(firstLetterChangeCase('u',fieldName));
                fieldMeta.setFieldDb(camelToSplit(fieldName,null,true));
                fieldMeta.setFieldTypeOri(returnTypeClass.getSimpleName());

                // 时间类型
                if (returnTypeClass.getName().equalsIgnoreCase("java.util.Date")) {
                    fieldMeta.setFieldType("Date");
                }
                else if (returnTypeClass.getSimpleName().equalsIgnoreCase("long")||returnTypeClass.getSimpleName().equalsIgnoreCase("Long")) {
                    fieldMeta.setFieldType("Long");

                }
                else if (returnTypeClass.getSimpleName().equalsIgnoreCase("int")||returnTypeClass.getSimpleName().equalsIgnoreCase("Integer")) {
                    fieldMeta.setFieldType("Integer");
                }
                // byte tinyint
                else if (returnTypeClass.getSimpleName().equalsIgnoreCase("byte")||returnTypeClass.getSimpleName().equalsIgnoreCase("Byte")) {
                    fieldMeta.setFieldType("Byte");
                }
                // byte tinyint
//                else if (EnumIntegerAble.class.isAssignableFrom(returnTypeClass)) {
//                    fieldMeta.setFieldType("Enum");
//                }
                // 小数点
                else if (returnTypeClass.getSimpleName().equalsIgnoreCase("BigDecimal")) {
                    fieldMeta.setFieldType("BigDecimal");
                }
                else
                {
                    fieldMeta.setFieldType("String");
                }
            }
        }
        return fieldMetaMap;
    }


    /**
     * Pattern.MULTILINE 让 ^ 匹配每行的行首，$ 匹配每行的行尾 没有Pattern.MULTILINE ^ 整个字符串的开始 $
     * 匹配整个字符串的行尾
     *
     * 所以除非要在行首尾，或者整个文本的收尾匹配字符，否则这两个操作符基本用不到 如果这两个操作符不用，那么Pattern.MULTILINE设置与否都没有意义
     *
     * @author Administrator
     *
     */
    public static Map<String,FieldMeta> getFieldCommet(String poClassName,Map<String,FieldMeta> fieldMetaMap) {

        String filePath = baseProjectPath + "dal" + mavenJavaPath + "/dal/po/" + poClassName + ".java";

        String fileStr = getFileString(filePath);

        // 找到所有注释开始的字段
        //  /**.*;
        Pattern pattern = Pattern.compile("\\/\\*\\*.*?\\;", Pattern.MULTILINE
                | Pattern.DOTALL);// 正则表达式

        Matcher matcher = pattern.matcher(fileStr);// 操作的字符串

        while (matcher.find()) {

            String findStr = matcher.group();

            // 有可能是类，也有可能是方法
            if (findStr.contains("{")) {
                // class的注释
                if (findStr.contains("class")) {

                    // 查找中文
                    Pattern descriptionPattern = Pattern.compile(
                            "[\\u4e00-\\u9fa5]+", Pattern.MULTILINE
                                    | Pattern.DOTALL);// 正则表达式
                    Matcher descriptionMatcher = descriptionPattern
                            .matcher(findStr);// 操作的字符串
                    String description = null;
                    if (descriptionMatcher.find()) {
                        description = descriptionMatcher.group();
//                        entityMap.put("description", description);
                    }
                }
            }
            //排除method
            else if (!findStr.contains("{")) {

                //查找 字段上  /**/注释的部分
                Pattern descriptionPattern = Pattern.compile(
                        "\\/\\*\\*.*?\\*\\/", Pattern.MULTILINE
                                | Pattern.DOTALL);// 正则表达式
                Matcher descriptionMatcher = descriptionPattern
                        .matcher(findStr);// 操作的字符串
                String fieldCommet = null;
                String name = null;
                if (descriptionMatcher.find()) {
                    fieldCommet = descriptionMatcher.group();
                }

                // 原始的郑则表达  "\\b\\w+(?=\\s*\\;)"  解释：\b匹配单词开始,\w+匹配字母，数字或下划线（也就是匹配变量名）
                // (?=\\s*\\;) 意思是后面跟着空白，换行，制表符等一个或多个（主要怕有些不规范的人变量名后面打几个空格）
                // 仍然匹配不了变量赋初始值的情况（即后面有“=”的情况）

                // js展示方式(?<=(private|public|protected)\s+)\w+\s+(\w+)
                Pattern namePattern = Pattern.compile("(?<=(private|public|protected)\\s+)\\w+\\s+(\\w+)",
                        Pattern.MULTILINE | Pattern.DOTALL);// 正则表达式

                Matcher nameMatcher = namePattern.matcher(findStr.substring(fieldCommet.length()));// 操作的字符串
                if (nameMatcher.find()) {
                    //第二个小括号匹配到
                    name = nameMatcher.group(2);
                    // System.out.println(description + ":" + name);
                }
                FieldMeta fieldMeta = fieldMetaMap.get(name);
                if(fieldMeta!=null)
                {
                    fieldMeta.setFieldCommet(getCommentContent(fieldCommet));
                }
            }
        }
        return fieldMetaMap;
    }

    public static String getFileString(String filePath) {
        BufferedReader reader = null;
        StringBuffer sb = new StringBuffer();
        try {
            reader = new BufferedReader(new InputStreamReader(
                    new FileInputStream(filePath)));
            int readLenth = 0;
            char[] bufferedByte = new char[1024 * 10];
            while ((readLenth = reader.read(bufferedByte)) > 0) {
                sb.append(bufferedByte, 0, readLenth);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String str = sb.toString();
        // System.out.println(str);
        return str;
    }

    public static final String UNDERLINE="_";

    // 驼峰命名转分隔符
    public static String camelToSplit(String str,String splitStr,boolean excludeFirst) {
        if (StringUtils.isBlank(str)){
            return "";
        }
        if(StringUtils.isBlank(splitStr)){
            splitStr = UNDERLINE;
        }
        int len = str.length();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            char c = str.charAt(i);
            if (Character.isUpperCase(c)){
                sb.append(splitStr);
                sb.append(Character.toLowerCase(c));
            }else{
                sb.append(c);
            }
        }
        String result = sb.toString();
        if(excludeFirst && StringUtils.startsWith(result,splitStr))
        {
            return result.substring(1);
        }
        return result;
    }


    /**
     * 将单词首字母大写
     *
     * @param input
     * @return String
     */
    public static String firstLetterChangeCase(char flag,String input)
    {
        String outPut = null;
        if(StringUtils.isNotBlank(input))
        {
            if(flag=='u')
            {
                outPut =  input.substring(0, 1).toUpperCase() + input.substring(1);
            }
            else
            {
                outPut =  input.substring(0, 1).toLowerCase() + input.substring(1);
            }

        }
        return outPut;
    }

    public static String getCommentContent(String input)
    {
        if(StringUtils.isBlank(input))
        {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c=='\r' || c=='\n'){
                continue;
            }
            if (c=='/' || c=='*'){
                continue;
            }
            sb.append(c);
        }
        return sb.toString().trim();
    }

    public static class PoMetaInfo {

        /**
         * PO类名字
         */
        private String simpleClassName;

        /**
         * PO类名字，首字母小写
         */
        private String propertyName;

        /**
         * 表名，命名规则是PO类名字驼峰改下划线
         */
        private String tableName;

        /**
         * controller url路径，命名规则是PO类名字驼峰改“/”
         */
        private String urlPath;

        /**
         * 类路径前缀 com.corpName.projectName
         */
        private String packagePath;

        private String corpName;

        private String projectName;

        private Collection<FieldMeta> fieldList;

        public String getSimpleClassName() {
            return simpleClassName;
        }

        public void setSimpleClassName(String simpleClassName) {
            this.simpleClassName = simpleClassName;
        }

        public String getPropertyName() {
            return propertyName;
        }

        public void setPropertyName(String propertyName) {
            this.propertyName = propertyName;
        }

        public String getTableName() {
            return tableName;
        }

        public void setTableName(String tableName) {
            this.tableName = tableName;
        }

        public String getUrlPath() {
            return urlPath;
        }

        public void setUrlPath(String urlPath) {
            this.urlPath = urlPath;
        }

        public String getPackagePath() {
            return packagePath;
        }

        public void setPackagePath(String packagePath) {
            this.packagePath = packagePath;
        }

        public String getCorpName() {
            return corpName;
        }

        public void setCorpName(String corpName) {
            this.corpName = corpName;
        }

        public String getProjectName() {
            return projectName;
        }

        public void setProjectName(String projectName) {
            this.projectName = projectName;
        }

        public Collection<FieldMeta> getFieldList() {
            return fieldList;
        }

        public void setFieldList(Collection<FieldMeta> fieldList) {
            this.fieldList = fieldList;
        }

    }

    public static class FieldMeta{

        /**
         * 字段名
         */
        private String fieldName;

        /**
         * 字段注释
         */
        private String fieldCommet;

        /**
         * 字段类型
         */
        private String fieldType;

        public String getFieldTypeOri() {
            return fieldTypeOri;
        }

        public void setFieldTypeOri(String fieldTypeOri) {
            this.fieldTypeOri = fieldTypeOri;
        }

        /**
         * 字段原始类型
         */
        private String fieldTypeOri;

        /**
         * 字段DB名字，命名规则是字段名字驼峰改下划线
         */
        private String fieldDb;

        /**
         * fieldName首字母变大写
         */
        private String fieldUpcase;

        public String getFieldName() {
            return fieldName;
        }

        public void setFieldName(String fieldName) {
            this.fieldName = fieldName;
        }

        public String getFieldCommet() {
            return fieldCommet;
        }

        public void setFieldCommet(String fieldCommet) {
            this.fieldCommet = fieldCommet;
        }

        public String getFieldType() {
            return fieldType;
        }

        public void setFieldType(String fieldType) {
            this.fieldType = fieldType;
        }

        public String getFieldDb() {
            return fieldDb;
        }

        public void setFieldDb(String fieldDb) {
            this.fieldDb = fieldDb;
        }

        public String getFieldUpcase() {
            return fieldUpcase;
        }

        public void setFieldUpcase(String fieldUpcase) {
            this.fieldUpcase = fieldUpcase;
        }
    }
}