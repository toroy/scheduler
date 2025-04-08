package com.clubfactory.platform.scheduler.dal.enums;

/**
 * @author xiejiajun
 */

public enum  DbType implements IEnum{

	MYSQL("mysql","com.mysql.jdbc.Driver", "mysql操作类"),
	POSTGRESQL("postgresql","org.postgresql.Driver", "pg操作类"),
	HIVE("hive2","org.apache.hive.jdbc.HiveDriver", "hive操作类"),
	REDSHIFT("redshift","com.amazon.redshift.jdbc.Driver","redshift操作类"),
	KAFKA("kafka",null,"kafka操作类"),
	HBASE("hbase",null,"hbase操作类"),
	MONGODB("mongodb", "com.ddtek.jdbc.mongodb.MongoDBDriver", "mongodb操作类"),
//	PHOENIX("phoenix", "org.apache.phoenix.jdbc.PhoenixDriver", "phoenix操作类"),
	UN_KNOW("unknow",null,"");
	
	private String dbName;
	
	private String driverClassName;
	
	private String desc;
	
	DbType(String dbName, String driverClassName, String desc) {
		this.dbName = dbName;
		this.driverClassName = driverClassName;
		this.desc = desc;
	}

	public String getDbName() {
		return dbName;
	}

	public String getDriverClassName() {
		return driverClassName;
	}

	@Override
	public String getDesc() {
		return desc;
	}
	
}
