package com.bigdata.platform.scheduler.core.service.impl;

import com.bigdata.platform.scheduler.core.vo.TokenVO;
import com.bigdata.platform.scheduler.dal.dao.TokenMapper;
import com.bigdata.platform.scheduler.dal.po.Token;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Service
public class TokenService extends BaseNewService<TokenVO,Token> {

    @Resource
    TokenMapper tokenMapper;

    @PostConstruct
    public void init(){
        setBaseMapper(tokenMapper);
    }

}
