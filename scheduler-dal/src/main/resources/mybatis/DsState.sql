CREATE TABLE `sc_ds_state` (
	`id` BIGINT NOT NULL AUTO_INCREMENT,
	`ds_id` BIGINT NOT NULL COMMENT '数据源ID',
	`db_host` varchar(256) NOT NULL COMMENT '数据源Host',
	`db_port` varchar(50) NOT NULL DEFAULT '' COMMENT '数据源端口',
	`db_user` varchar(256) NOT NULL DEFAULT '' COMMENT '数据源用户',
	`db_type` varchar(256) NOT NULL DEFAULT '' COMMENT '数据源类型',
	`worker_ip` varchar(512) NOT NULL DEFAULT '' COMMENT '调度机IP',
	conn_success Boolean  COMMENT '1:成功，0：失败',
	failed_reason text  COMMENT '失败原因',
    create_time timestamp null COMMENT '创建时间',
	PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='数据源状态';