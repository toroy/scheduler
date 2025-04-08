CREATE TABLE `sc_sys_config` (
	`id` BIGINT NOT NULL AUTO_INCREMENT,
	`apply_host` varchar(256)  NOT NULL COMMENT '使用配置的机器',
	`param_key` varchar(512) NOT NULL DEFAULT '' COMMENT '配置key',
	`param_val` varchar(1024) NOT NULL DEFAULT '' COMMENT '配置值',
	`param_desc` varchar(1024) NOT NULL DEFAULT '' COMMENT '配置描述',
	config_type varchar(512) NOT NULL DEFAULT '' COMMENT '配置类型：MASTER,WORKER,LOGGER',
	create_time timestamp null COMMENT '创建时间',
    update_time timestamp null COMMENT '修改时间',
	PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统配置表';