package com.clubfactory.platform.scheduler.dal.config;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;

@Configuration
@MapperScan(basePackages = "com.clubfactory.platform.scheduler.dal.dao", sqlSessionFactoryRef = MybatisConfig.SQL_SESSION_FACTORY)
public class MybatisConfig implements TransactionManagementConfigurer {

    @Autowired
    private DataSource dataSource;

    @Value("${mybatis.config-location}")
    private String configLocation;

    @Value("${mybatis.mapper-locations}")
    private String mapperLocations;
    
    static final String SQL_SESSION_FACTORY = "sqlSessionFactory";

    @Override
    public PlatformTransactionManager annotationDrivenTransactionManager() {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean(name = SQL_SESSION_FACTORY)
    public SqlSessionFactory sqlSessionFactoryBean() throws Exception {
        SqlSessionFactoryBean factory  = new SqlSessionFactoryBean();
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        factory.setMapperLocations(resolver.getResources(mapperLocations));
        factory.setConfigLocation(resolver.getResource(configLocation));
        factory.setDataSource(dataSource);

        SqlSessionFactory sqlSessionFactory = factory.getObject();
//        TypeHandlerRegistry typeHandlerRegistry = sqlSessionFactory.getConfiguration().getTypeHandlerRegistry();
//
//        ResolverUtil<Class<?>> resolverUtil = new ResolverUtil<Class<?>>();
//
//        resolverUtil.find(new ResolverUtil.IsA(EnumIntegerAble.class), "com.clubfactory.platform.scheduler.dal.enums");
//        Set<Class<? extends Class<?>>> handlerSet = resolverUtil.getClasses();
//        for (Class<?> enumClass : handlerSet) {
//            typeHandlerRegistry.register(enumClass,IntegerAbleEnumHandler.class);
//        }
        return sqlSessionFactory;
    }

    @Bean
    public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

}