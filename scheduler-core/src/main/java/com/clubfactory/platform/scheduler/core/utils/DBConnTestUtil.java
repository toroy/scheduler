package com.clubfactory.platform.scheduler.core.utils;

import com.clubfactory.platform.common.util.Assert;
import com.clubfactory.platform.scheduler.dal.enums.DbType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * @author xiejiajun
 */
@Slf4j
public class DBConnTestUtil {

    /**
     * 测试数据源是否能正常连接
     * @param
     * @return
     */
    public static boolean isConnSuccess(String dbUrl,
                                        String dbUser,
                                        String dbPassword ,
                                        DbType dbType,
                                        int timeoutSeconds) throws Exception{
        boolean isConnectSuccess = false;
        Connection conn = null;
        Assert.notNull(dbUrl,"JDBC URL");
        Assert.notNull(dbUser);
        String driverClass = dbType.getDriverClassName();
        try {
            // 新版本mysql 驱动自动加载，无需再次加载
            if (dbType != DbType.MYSQL) {
                Class.forName(driverClass);
            }
            DriverManager.setLoginTimeout(timeoutSeconds);
            Properties props = new Properties();
            props.put("user",dbUser);
            props.put("loginTimeout",String.valueOf(timeoutSeconds));
            if (StringUtils.isNotBlank(dbPassword)){
                props.put("password",dbPassword);
            }
            conn = DriverManager.getConnection(dbUrl,props);
        } catch (SQLException | ClassNotFoundException e) {
            if (log.isDebugEnabled()) {
                log.debug("try connect {} failed:{}", dbUrl, e.getMessage());
            }
            throw e;
        }finally {
            if (conn != null){
                isConnectSuccess = true;
                try {
                    conn.close();
                } catch (SQLException e) {
                    log.error(e.getMessage());
                }
            }
        }
        return isConnectSuccess;
    }
}
