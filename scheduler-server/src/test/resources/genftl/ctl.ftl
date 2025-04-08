package ${packagePath}.web.controller;

import com.${corpName}.common.bean.BaseResult;
import com.google.common.collect.Maps;
import ${packagePath}.service.service.${simpleClassName}Service;
import ${packagePath}.service.vo.${simpleClassName}VO;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping("${urlPath}")
public class ${simpleClassName}Controller {

    @Resource
    ${simpleClassName}Service ${propertyName}Service;

    @GetMapping("count")
    public BaseResult count(${simpleClassName}VO vo) {
        Map<String,Object> resultMap = Maps.newHashMap();
        resultMap.put("records",${propertyName}Service.count(vo));
        return new BaseResult(resultMap);
    }

    @GetMapping("list")
    public BaseResult list(${simpleClassName}VO vo) {
        Map<String,Object> resultMap = Maps.newHashMap();
        resultMap.put("rows",${propertyName}Service.list(vo));
        return new BaseResult(resultMap);
       //return new BaseResult(${propertyName}Service.pageVoList(vo));
    }

    @PostMapping("remove")
    public BaseResult removeArticle(@RequestBody ${simpleClassName}VO vo) {
        ${propertyName}Service.remove(vo);
        return new BaseResult();
    }


    @PostMapping("save")
    public BaseResult save(@RequestBody ${simpleClassName}VO vo) {
        Map<String,Object> resultMap = Maps.newHashMap();
        ${propertyName}Service.save(vo);
        resultMap.put("row",vo);
       // resultMap.put("pkId",vo.getPkId());
        return new BaseResult(resultMap);

    }

    @PostMapping("edit")
    public BaseResult edit(@RequestBody ${simpleClassName}VO vo) {
        Map<String,Object> resultMap = Maps.newHashMap();
        ${propertyName}Service.edit(vo);
        resultMap.put("row",vo);
        //resultMap.put("pkId",vo.getPkId());
        return new BaseResult(resultMap);
    }

    @GetMapping("get")
    public BaseResult get(${simpleClassName}VO vo) {
        Map<String,Object> resultMap = Maps.newHashMap();
        ${simpleClassName}VO result = ${propertyName}Service.get(vo);
        resultMap.put("row",result);
        return new BaseResult(resultMap);
    }
}
