package com.clubfactory.platform.scheduler.server.provider;

import com.alibaba.dubbo.config.annotation.Service;
import com.clubfactory.platform.scheduler.client.HelloService;

@Service
public class HelloServiceImpl implements HelloService {

	@Override
	public String getHello() {
		return "hello world";
	}

}
