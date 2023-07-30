package com.springbook.user.dao;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DaoFactory {
    @Bean
    public UserDaoJdbc userDao() {
        UserDaoJdbc dao = new UserDaoJdbc();
        return dao;
    }

    @Bean
    public ConnectionMaker connectionMaker() {
        ConnectionMaker connectionMaker = new DConnectionMaker();
        return connectionMaker;
    }
}