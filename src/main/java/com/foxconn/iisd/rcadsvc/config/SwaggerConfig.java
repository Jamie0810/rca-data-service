package com.foxconn.iisd.rcadsvc.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
@Component("swaggerConfig")
public class SwaggerConfig {

    @Value("${swagger.host}")
    private String host;
    
    @Value("${swagger.port}")
    private String port;
    
    @Bean
    public Docket createRestApi() {
    	if(host==null || port ==null){
	        return new Docket(DocumentationType.SWAGGER_2)
	                .apiInfo(apiInfo())
	                .select()
	                .apis(RequestHandlerSelectors.basePackage("com.foxconn.iisd.rcadsvc"))
	                .paths(PathSelectors.any())
	                .build();
    	}else{
	        return new Docket(DocumentationType.SWAGGER_2)
	        		.host(host+":"+port)
	                .apiInfo(apiInfo())
	                .select()
	                .apis(RequestHandlerSelectors.basePackage("com.foxconn.iisd.rcadsvc"))
	                .paths(PathSelectors.any())
	                .build();
    	}
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("使用Swagger2建構RESTful APIs")
                .version("1.0")
                .build();
    }
}

//@Configuration
//@EnableSwagger2
//public class SwaggerConfig {
//    @Bean
//    public Docket api() {
//        return new Docket(DocumentationType.SWAGGER_2)
//                .select()
//                .apis(RequestHandlerSelectors.any())
//                .paths(PathSelectors.any())
//                .build();
//    }
//}
