CREATE TABLE `sc_distribute_lock` (
	`id` BIGINT NOT NULL AUTO_INCREMENT,
	`resource_uniq_id` int NOT NULL COMMENT '锁定的资源',
	`remark` varchar(1024) NOT NULL DEFAULT "" COMMENT '描述',
	`expire_time` BIGINT NOT NULL COMMENT '锁自动释放时间，超过这个时间自动释放',
	PRIMARY KEY (`id`),
	UNIQUE KEY `uiq_idx_resource` (`resource_uniq_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='数据库分布式锁表';