package com.bigdata.platform.scheduler.server;

import com.bigdata.platform.scheduler.server.logserver.LoggerServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.bigdata.platform.scheduler.core.enums.ServerType;
import com.bigdata.platform.scheduler.core.utils.SpringBean;
import com.bigdata.platform.scheduler.server.leader.LeaderServer;
import com.bigdata.platform.scheduler.server.worker.WorkerServer;

@SpringBootApplication(scanBasePackages = "com.bigdata.platform.scheduler")
@EnableTransactionManagement
//@ImportResource({"classpath:spring/*.xml"})
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableScheduling
public class Application {


	public static void main(String[] args) throws Exception {
		SpringApplication app = new SpringApplication(Application.class);

		ServerType serverType = ServerType.MASTER_SERVER;
		if (args.length > 0){
			serverType = ServerType.keyOf(args[0]);
		}
		System.setProperty("LOG_NAME",serverType.getLogName());
		if (serverType != ServerType.MASTER_SERVER) {
			app.setWebApplicationType(WebApplicationType.NONE);
		}
		app.run(args);

		switch (serverType){
			case LOGGER_SERVER:
				LoggerServer.main(args);
				break;
			case WORKER_SERVER:
				WorkerServer workerServer = SpringBean.getBean(WorkerServer.class);
				workerServer.run(args);
				break;
			case MASTER_SERVER:
			default:
				LeaderServer masterServer = SpringBean.getBean(LeaderServer.class);
				masterServer.run();
				break;
		}
	}

}
