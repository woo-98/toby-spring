package com.springbook;

import static org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType.HSQL;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.mail.MailSender;
import org.springframework.oxm.Unmarshaller;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.springbook.user.dao.UserDao;
import com.springbook.user.dao.UserDaoJdbc;
import com.springbook.user.service.DummyMailSender;
import com.springbook.user.service.UserService;
import com.springbook.user.service.UserServiceImpl;
import com.springbook.user.service.UserServiceTest.TestUserService;
import com.springbook.user.sqlservice.OxmSqlService;
import com.springbook.user.sqlservice.SqlRegistry;
import com.springbook.user.sqlservice.SqlService;
import com.springbook.user.sqlservice.updatable.EmbeddedDbSqlRegistry;

import com.mysql.jdbc.Driver;

@Configuration
@EnableTransactionManagement
public class TestApplicationContext {
    /**
     * DB연결과 트랜잭션
     */

    @Bean
    public DataSource dataSource() {
        SimpleDriverDataSource ds = new SimpleDriverDataSource();
        ds.setDriverClass(Driver.class);
        ds.setUrl("jdbc:mysql://localhost/springbook?characterEncoding=UTF-8");
        ds.setUsername("root");
        ds.setPassword("park0473");
        return ds;
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        DataSourceTransactionManager tm = new DataSourceTransactionManager();
        tm.setDataSource(dataSource());
        return tm;
    }

    /**
     * 애플리케이션 로직 & 테스트용 빈
     */

    @Autowired SqlService sqlService;

    @Bean
    public UserDao userDao() {
        UserDaoJdbc dao = new UserDaoJdbc();
        dao.setDataSource(dataSource());
        dao.setSqlService(this.sqlService);
        return dao;
    }

    @Bean
    public UserService userService() {
        UserServiceImpl service = new UserServiceImpl();
        service.setUserDao(userDao());
        service.setMailSender(mailSender());
        return service;
    }

    @Bean
    public UserService testUserService() {
        TestUserService testService = new TestUserService();
        testService.setUserDao(userDao());
        testService.setMailSender(mailSender());
        return testService;
    }

    @Bean
    public MailSender mailSender() {
        return new DummyMailSender();
    }

    /**
     * SQL서비스
     */

    @Bean
    public SqlService sqlService() {
        OxmSqlService sqlService = new OxmSqlService();
        sqlService.setUnmarshaller(unmarshaller());
        sqlService.setSqlRegistry(sqlRegistry());
        return sqlService;
    }

    @Bean
    public SqlRegistry sqlRegistry() {
        EmbeddedDbSqlRegistry sqlRegistry = new EmbeddedDbSqlRegistry();
        sqlRegistry.setDataSource(embeddedDatabase());
        return sqlRegistry;
    }

    @Bean
    public Unmarshaller unmarshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setContextPath("com.springbook.user.sqlservice.jaxb");
        return marshaller;
    }

    @Bean
    public DataSource embeddedDatabase() {
        return new EmbeddedDatabaseBuilder()
                .setName("embeddedDatabase")
                .setType(HSQL)
                .addScript("classpath:com.springbook/user/sqlservice/updatable/sqlRegistrySchema.sql")
                .build();
    }
}