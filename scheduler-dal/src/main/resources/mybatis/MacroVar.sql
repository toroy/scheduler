CREATE TABLE `sc_macro_var` (
	`id` BIGINT NOT NULL AUTO_INCREMENT,
	`var_name` varchar(256)  NOT NULL COMMENT '宏变量名称',
	`var_expr` varchar(512) NOT NULL DEFAULT '' COMMENT '宏变量表达式/Text',
	`var_desc` varchar(1024) NOT NULL DEFAULT '' COMMENT '宏变量说明',
	create_time timestamp null COMMENT '创建时间',
    update_time timestamp null COMMENT '修改时间',
    is_mask Boolean null COMMENT '是否需要脱敏',
	PRIMARY KEY (`id`),
	UNIQUE KEY `var_key` (`var_name`) USING HASH
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='宏变量配置表';