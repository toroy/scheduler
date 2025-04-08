CREATE TABLE `sc_log_map` (
	`id` BIGINT NOT NULL AUTO_INCREMENT,
	`task_id` BIGINT NOT NULL COMMENT '实例ID',
	`log_name` varchar(512) NOT NULL DEFAULT '' COMMENT '日志名称',
	`log_path` varchar(1024) NOT NULL DEFAULT '' COMMENT '日志路径',
	`log_host` varchar(512) NOT NULL DEFAULT '' COMMENT '日志主机',
	create_time timestamp null COMMENT '创建时间',
    update_time timestamp null COMMENT '修改时间',
	PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='日志信息映射表';