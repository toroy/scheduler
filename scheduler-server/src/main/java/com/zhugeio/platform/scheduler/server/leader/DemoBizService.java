package com.zhugeio.platform.scheduler.server.leader;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zhugeio.platform.scheduler.common.exception.BizException;
import com.zhugeio.platform.scheduler.core.service.impl.DemoService;
import com.zhugeio.platform.scheduler.core.vo.DemoVO;
import com.zhugeio.platform.scheduler.dal.po.Demo;

@Service
public class DemoBizService {

	@Resource
	DemoService demoService;
	
	public Boolean get() {
		Demo demo = new Demo();
		//demo.setIsDeleted(true);
		DemoVO demoVO = demoService.get(demo);
		return demoVO.getIsDeleted();
	}
	
    @Transactional
	public void test() {
    	Demo demo = new Demo();
    	demo.setName("test");
    	demoService.save(demo);
    	
    	if (true) {
    		throw new BizException("test");
    	}
    	
    	
    	demoService.save(demo);
	}
}
