CREATE TABLE sc_task_monitor
(
     id bigint PRIMARY KEY NOT NULL AUTO_INCREMENT COMMENT '自增id',
    rm_port VARCHAR(256) COMMENT '',
    yarn_app_id VARCHAR(256) COMMENT '',
    rm_host VARCHAR(256) COMMENT '',
   task_id BIGINT COMMENT '',
    create_time timestamp null COMMENT '创建时间',
    update_time timestamp null COMMENT '修改时间',
    is_recovering Boolean COMMENT '是否正在恢复，1：是，0：不是'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 AUTO_INCREMENT=1;


