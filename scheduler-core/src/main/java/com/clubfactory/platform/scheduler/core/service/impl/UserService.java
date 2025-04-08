package com.clubfactory.platform.scheduler.core.service.impl;

import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import com.google.common.collect.Maps;
import org.springframework.stereotype.Service;

import com.clubfactory.platform.common.util.Assert;
import com.clubfactory.platform.scheduler.core.vo.UserVO;
import com.clubfactory.platform.scheduler.dal.dao.UserMapper;
import com.clubfactory.platform.scheduler.dal.po.User;

@Service
public class UserService extends BaseNewService<UserVO,User> {

    @Resource
    UserMapper userMapper;
    
    Map<Long, String> userMap = Maps.newConcurrentMap();
	Map<Long, String> userAliasMap = Maps.newConcurrentMap();

    @PostConstruct
    public void init(){
        setBaseMapper(userMapper);
    }

    public String getUserName(Long id) {
    	Assert.notNull(id);
    	
    	if (userMap.get(id) != null) {
    		return userMap.get(id);
    	}
    	
    	UserVO userVO = getUserInfoById(id);
    	if (userVO == null) {
    		return null;
    	}
    	String name = userVO.getName();
    	
    	userMap.put(id, name);
    	return name;
    }


	/**
	 * 根据用户名获取用户Alias
	 * @param id
	 * @return
	 */
	public String getUserAlias(Long id) {
		Assert.notNull(id);

		if (userAliasMap.get(id) != null) {
			return userAliasMap.get(id);
		}

		UserVO userVO = getUserInfoById(id);
		if (userVO == null) {
			return null;
		}
		String alias = userVO.getAlias();

		userAliasMap.put(id, alias);
		return alias;
	}

	/**
	 * 根据用户id获取用户信息
	 * @param id
	 * @return
	 */
	public UserVO getUserInfoById(Long id){
		User user = new User();
		user.setId(id);
		user.setIsDeleted(false);
		return this.get(user);
	}
}
