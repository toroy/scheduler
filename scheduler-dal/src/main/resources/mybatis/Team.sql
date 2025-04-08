CREATE TABLE sc_team
(
    id bigint PRIMARY KEY NOT NULL AUTO_INCREMENT COMMENT '自增id',
    depart_id INT COMMENT '',
    depart_name VARCHAR(256) COMMENT '部门名称',
    create_time timestamp null COMMENT '创建时间',
    update_time timestamp null COMMENT '修改时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 AUTO_INCREMENT=1;


