package com.foxconn.iisd.rcadsvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.TimeZone;

@EnableJpaRepositories(entityManagerFactoryRef = "userEntityManagerFactory",
        transactionManagerRef = "userTransactionManager",
        basePackages = "com.foxconn.iisd.rcadsvc.repo")
@EntityScan("com.foxconn.iisd.rcadsvc.domain.*")
@SpringBootApplication
public class RcaDsvcApplication {

    @Autowired
    @Qualifier("mysqlJtl")
    private JdbcTemplate mysqlJtl;

    public static void main(String[] args) {
        SpringApplication.run(RcaDsvcApplication.class, args);
    }

    @PostConstruct
    public void createDBView() throws Exception {
        System.out.println("******* create DB view");

        mysqlJtl.execute(
                "CREATE OR REPLACE VIEW `product_line_station` AS " +
                        "SELECT distinct `risk_test_station_sn`.`product` AS `product`," +
                        "`risk_test_station_sn`.`line` AS `line`," +
                        "`risk_test_station_sn`.`station` AS `station` " +
                        "FROM `risk_test_station_sn` " +
                        " union " + 
                        "SELECT distinct `risk_test_station_sn_history`.`product` AS `product`," +
                        "`risk_test_station_sn_history`.`line` AS `line`," +
                        "`risk_test_station_sn_history`.`station` AS `station` " +
                        "FROM `risk_test_station_sn_history` ;" 
                        );
    }
    @PostConstruct
    public void init(){
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Taipei"));   // It will set Taipei timezone
        System.out.println("Spring boot application running in Taipei timezone :"+new Date());   // It will print Taipei timezone
    }
}

