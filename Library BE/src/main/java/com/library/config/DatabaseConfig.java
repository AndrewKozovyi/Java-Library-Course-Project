package com.library.config;

import com.library.dao.BookDAO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DatabaseConfig {

    @Value("${DB_URL}")
    private String url;

    @Value("${DB_USER}")
    private String user;

    @Value("${DB_PASSWORD}")
    private String password;

    @Bean
    public BookDAO bookDAO() {
        return new BookDAO(url, user, password);
    }
}