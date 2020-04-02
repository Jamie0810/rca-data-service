package com.foxconn.iisd.rcadsvc.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {
    /**
     * The default datasource.
     */
    @Bean(name = "mysqlDS")
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource mysqlDataSource() {
        return DataSourceBuilder.create().build();
    }

    /**
     * The second datasource.
     */
    @Bean(name = "cockroachDS")
    @ConfigurationProperties(prefix = "cock.datasource")
    public DataSource cockroachDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "mysqlJtl")
    @Autowired
    public JdbcTemplate mysqlJdbcTemplate(@Qualifier("mysqlDS") DataSource dsMaster) {
        return new JdbcTemplate(dsMaster);
    }

    @Bean(name = "cockroachJtl")
    @Autowired
    public JdbcTemplate cockroachJdbcTemplate(@Qualifier("cockroachDS") DataSource dsMaster) {
        return new JdbcTemplate(dsMaster);
    }
}
