package com.clubfactory.platform.upgrade;

import com.alibaba.druid.pool.DruidDataSource;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.clubfactory.platform.scheduler.common.utils.AESUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

/**
 * @author xiejiajun
 */
@Slf4j
public class DataSourceEncrypt {


    @Parameter(names = {"--jdbcUrl", "-l"})
    private String url;
    @Parameter(names = {"--user", "-u"})
    private String user;
    @Parameter(names = {"--password", "-p"}, password = true)
    private String pass;


    private DruidDataSource dataSource;
    private JdbcTemplate jdbcTemplate;

    private AESUtils aesUtils = new AESUtils();


    public void init() {
        if (StringUtils.isBlank(url) || StringUtils.isBlank(user)) {
            url = "jdbc:mysql://localhost:3306/scheduler";
            user = "root";
            pass = "root";
        }
        if (this.dataSource == null) {
            this.dataSource = new DruidDataSource();
            this.dataSource.setUrl(url);
            this.dataSource.setUsername(user);
            this.dataSource.setPassword(pass);
            this.dataSource.setMaxPoolPreparedStatementPerConnectionSize(20);
            this.dataSource.setInitialSize(1);
            this.dataSource.setMinIdle(1);
            this.dataSource.setMaxActive(50);
            this.dataSource.setMaxWait(600000);
            this.jdbcTemplate = new JdbcTemplate(dataSource);
        }
    }

    /**
     * 执行SQL
     *
     * @param sql
     * @param obj
     */
    public void executeSql(String sql, Object... obj) {
        ensureNotNull();
        log.info("exec sql: {}", sql);
        jdbcTemplate.update(sql, obj);
    }

    public <T> List<T> findAll(String sql, Class<T> elementType, Object... obj) {
        ensureNotNull();
        log.info("exec select statement: {}", sql);
        try {
            return jdbcTemplate.query(sql, new BeanPropertyRowMapper(elementType), obj);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }

    }


    private void ensureNotNull() {
        if (this.jdbcTemplate == null) {
            log.info("jdbc is not init, skip run sql ...");
            throw new RuntimeException("jdbc is not init, skip run sql ...");
        }
    }

    /**
     * 释放连接池
     */
    public void destroy() {
        if (this.dataSource != null) {
            log.info("closing jdbc datasource pool...");
            this.dataSource.close();
        }
    }

    @Data
    static class DSEntity {
        private Long id;
        private String dsPassword;
    }


    public static void main(String[] args) throws Exception {
        DataSourceEncrypt encryptor = new DataSourceEncrypt();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> encryptor.destroy()));
        JCommander.newBuilder()
                .addObject(encryptor)
                .build()
                .parse(args);
        encryptor.init();
        String selectSql = "select id, ds_password from sc_collect_db";
        List<DSEntity> dsEntities = encryptor.findAll(selectSql, DSEntity.class);
        String updateSql = "update sc_collect_db set encrypt_pwd = ? , pwd_key = ? where id = ?";
        for (DSEntity dsEntity : dsEntities){
            String pwdKey = encryptor.aesUtils.generateKey();
            String encryptPwd = dsEntity.getDsPassword() == null ?
                    null : encryptor.aesUtils.encrypt(dsEntity.getDsPassword(), pwdKey);
            encryptor.executeSql(updateSql,
                    encryptPwd,
                    pwdKey,
                    dsEntity.getId());
        }
    }
}
