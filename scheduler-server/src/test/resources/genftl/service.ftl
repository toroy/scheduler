package ${packagePath}.core.service.impl;

import ${packagePath}.core.vo.${simpleClassName}VO;
import ${packagePath}.dal.dao.${simpleClassName}Mapper;
import ${packagePath}.dal.po.${simpleClassName};
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Service
public class ${simpleClassName}Service extends BaseNewService<${simpleClassName}VO,${simpleClassName}> {

    @Resource
    ${simpleClassName}Mapper ${propertyName}Mapper;

    @PostConstruct
    public void init(){
        setBaseMapper(${propertyName}Mapper);
    }

}
