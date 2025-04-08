package ${packagePath}.test.controller;


import com.alibaba.fastjson.JSON;
import com.${corpName}.common.bean.BaseResult;
import com.${corpName}.common.util.BeanUtil;
import ${packagePath}.biz.vo.${simpleClassName}VO;
import ${packagePath}.test.BaseControllerTest;
import org.junit.Assert;
import org.junit.Test;

import java.math.*;
import java.util.*;

public class ${simpleClassName}ControllerTest extends BaseControllerTest {

    String url_save = "${urlPath}/save";

    String url_list = "${urlPath}/list";

    String url_count = "${urlPath}/count";

    String url_edit = "${urlPath}/edit";

    String url_remove = "${urlPath}/remove";

    String url_get = "${urlPath}/get";

    static String pkId = "wxgm330832516524231A81E84C883832";

    @Test
    public void testSave()
    {
        ${simpleClassName}VO vo = generateVO();
        String paramStr = JSON.toJSONString(vo);
        //打印出参数列表，写接口RAP用
        System.err.println(paramStr);
        BaseResult baseResult = postJSON(url_save, paramStr,headMap);
        //打印出返回值，写接口RAP用
        System.err.println(JSON.toJSONString(baseResult));
        Assert.assertEquals(baseResult.getMsg(), 1, baseResult.getCode());
    }

    @Test
    public void testEdit()
    {
        ${simpleClassName}VO vo = generateVO();
        Map<String,Object> otherParam = new HashMap<String,Object>();
        vo.setOtherParam(otherParam);
    [#list fieldList as field]
       [#if field.fieldType=='String']
        otherParam.put("${field.fieldDb}","字符串");
       [/#if]
       [#if field.fieldType=='Long']
        otherParam.put("${field.fieldDb}",1);
       [/#if]
       [#if field.fieldType=='Integer']
        otherParam.put("${field.fieldDb}",1);
       [/#if]
       [#if field.fieldType=='Byte']
        otherParam.put("${field.fieldDb}",1);
       [/#if]
       [#if field.fieldType=='Enum']
        otherParam.put("${field.fieldDb}",1);
       [/#if]
       [#if field.fieldType=='Date']
        otherParam.put("${field.fieldDb}",new Date());
       [/#if]
       [#if field.fieldType=='BigDecimal']
        otherParam.put("${field.fieldDb}","1.22");
       [/#if]
    [/#list]
        String paramStr = JSON.toJSONString(vo);
        //打印出参数列表，写接口RAP用
        System.err.println(paramStr);
        BaseResult baseResult = postJSON(url_edit,paramStr,headMap);
        System.err.println(JSON.toJSONString(baseResult));
        Assert.assertEquals(baseResult.getMsg(), 1, baseResult.getCode());
    }

    @Test
    public void testList()
    {
        ${simpleClassName}VO vo = generateVO();
        vo.setPageIndex(1);
        vo.setPageSize(15);
        //塞入自定义查询对象
        Map<String,Object> paramMap = new HashMap<>();
        BeanUtil.copyBean2Map(paramMap,vo);
        System.err.println(JSON.toJSONString(paramMap));
        BaseResult baseResult = get(url_list,paramMap,headMap);
        System.err.println(JSON.toJSONString(baseResult));
        Assert.assertEquals(baseResult.getMsg(), 1, baseResult.getCode());
    }

    @Test
    public void testCount()
    {
        ${simpleClassName}VO vo = generateVO();
        //塞入自定义查询对象
        Map<String,Object> paramMap = new HashMap<>();
        BeanUtil.copyBean2Map(paramMap,vo);
        System.err.println(JSON.toJSONString(paramMap));
        BaseResult baseResult = get(url_count,paramMap,headMap);
        System.err.println(JSON.toJSONString(baseResult));
        Assert.assertEquals(baseResult.getMsg(), 1, baseResult.getCode());
    }

    @Test
    public void testRemove()
    {
        ${simpleClassName}VO vo = new ${simpleClassName}VO();
        vo.setPkId(pkId);
        String paramStr = JSON.toJSONString(vo);
        //打印出参数列表，写接口RAP用
        System.err.println(paramStr);
        BaseResult baseResult = postJSON(url_remove,paramStr,headMap);
        System.err.println(JSON.toJSONString(baseResult));
        Assert.assertEquals(baseResult.getMsg(), 1, baseResult.getCode());
    }


    @Test
    public void testGet()
    {
        ${simpleClassName}VO vo = new ${simpleClassName}VO();
        vo.setPkId(pkId);
        Map<String,Object> paramMap = new HashMap<>();
        BeanUtil.copyBean2Map(paramMap,vo);
        System.err.println(JSON.toJSONString(paramMap));
        BaseResult baseResult = get(url_get,paramMap,headMap);
        System.err.println(JSON.toJSONString(baseResult));
        Assert.assertEquals(baseResult.getMsg(), 1, baseResult.getCode());
    }

    @Test
    public void testListByIds()
    {
        ${simpleClassName}VO vo = new ${simpleClassName}VO();
        List<String> ids = new ArrayList<String>();
        ids.add(pkId);
        vo.setIds(ids);
        Map<String,Object> paramMap = new HashMap<>();
        BeanUtil.copyBean2Map(paramMap,vo);
        System.err.println(JSON.toJSONString(paramMap));
        BaseResult baseResult = get(url_list,paramMap,headMap);
        System.err.println(JSON.toJSONString(baseResult));
        Assert.assertEquals(baseResult.getMsg(), 1, baseResult.getCode());
    }

    public static ${simpleClassName}VO generateVO(){
        ${simpleClassName}VO vo = new ${simpleClassName}VO();
        vo.setPkId(pkId);
    [#list fieldList as field]
       [#if field.fieldType=='String']
        vo.set${field.fieldUpcase}("字符串");
       [/#if]
       [#if field.fieldType=='Long']
        vo.set${field.fieldUpcase}(1L);
       [/#if]
       [#if field.fieldType=='Integer']
        vo.set${field.fieldUpcase}(1);
       [/#if]
        [#if field.fieldType=='Enum']
        vo.set${field.fieldUpcase}(${field.fieldTypeOri});
       [/#if]
        [#if field.fieldType=='Date']
        vo.set${field.fieldUpcase}(new Date());
       [/#if]
       [#if field.fieldType=='BigDecimal']
        BigDecimal b = new BigDecimal("1.22");
        vo.set${field.fieldUpcase}(b);
       [/#if]
    [/#list]
        return vo;
    }
}
