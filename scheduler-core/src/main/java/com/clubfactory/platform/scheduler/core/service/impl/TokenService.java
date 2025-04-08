package com.clubfactory.platform.scheduler.core.service.impl;

import com.clubfactory.platform.scheduler.core.vo.TokenVO;
import com.clubfactory.platform.scheduler.dal.dao.TokenMapper;
import com.clubfactory.platform.scheduler.dal.po.Token;
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
