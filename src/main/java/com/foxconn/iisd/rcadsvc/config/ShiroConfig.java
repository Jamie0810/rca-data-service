package com.foxconn.iisd.rcadsvc.config;

import com.foxconn.iisd.rcadsvc.security.MyRealm;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.spring.web.config.DefaultShiroFilterChainDefinition;
import org.apache.shiro.spring.web.config.ShiroFilterChainDefinition;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.Cookie;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class ShiroConfig {

    @Value("${rca.shiro.cookie.name}")
    private String cookieName;

    @Value("${rca.shiro.session.timeoutmillis}")
    private long sessionTimeoutMillis;

    /**
     * 注入自定义的realm，告诉shiro如何获取用户信息来做登录认证和授权
     */
    @Bean(name = "realm")
    public Realm realm() {
        return new MyRealm();
    }

    /**
     * 这里统一做鉴权，即判断哪些请求路径需要用户登录，哪些请求路径不需要用户登录。
     * 这里只做鉴权，不做权限控制，因为权限用注解来做。
     *
     * @return
     */
    @Bean
    public ShiroFilterChainDefinition shiroFilterChainDefinition(@Qualifier("securityManager") org.apache.shiro.mgt.SecurityManager securityManager) {
        DefaultShiroFilterChainDefinition chain = new DefaultShiroFilterChainDefinition();
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);        // setLoginUrl 如果不设置值，默认会自动寻找Web工程根目录下的"/login.jsp"页面 或 "/login" 映射
//        shiroFilterFactoryBean.setLoginUrl("/notLogin");        // 设置无权限时跳转的 url;
//        shiroFilterFactoryBean.setUnauthorizedUrl("/notRole");


        // 设置哪些请求可以匿名访问
//        chain.addPathDefinition("/login/**", "anon");
//        chain.addPathDefinition("/user/**", "anon");
//        chain.addPathDefinition("/data/**", "anon");
//        chain.addPathDefinition("/test/**", "anon");

        // 由于使用Swagger调试，因此设置所有Swagger相关的请求可以匿名访问
//        chain.addPathDefinition("/swagger-ui.html", "anon");
//        chain.addPathDefinition("/swagger-resources", "anon");
//        chain.addPathDefinition("/swagger-resources/configuration/security", "anon");
//        chain.addPathDefinition("/swagger-resources/configuration/ui", "anon");
//        chain.addPathDefinition("/v2/api-docs", "anon");
//        chain.addPathDefinition("/webjars/springfox-swagger-ui/**", "anon");
//        chain.addPathDefinition("/login.jsp", "anon");


        //除了以上的请求外，其它请求都需要登录
//        chain.addPathDefinition("/**", "anon");
//        chain.addPathDefinition("/**", "authc");
        return chain;
    }

    /**
     * @param
     * @return org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator
     * @author JasonLai
     * @date 2019/3/14 上午11:30
     * @description
     */
    @Bean
    public static DefaultAdvisorAutoProxyCreator getDefaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator creator = new DefaultAdvisorAutoProxyCreator();
        /**
         * setUsePrefix(false)用于解决一个奇怪的bug。在引入spring aop的情况下。
         * 在@Controller注解的类的方法中加入@RequiresRole注解，会导致该方法无法映射请求，导致返回404。
         * 加入这项配置能解决这个bug
         */
        creator.setUsePrefix(true);
        return creator;
    }


    //    @Autowired
//    DataSource dataSource;
//
//    @Bean(name = "rcaJdbcRealm")
//    public RcaJdbcRealm rcaJdbcRealm() {
//        RcaJdbcRealm realm = new RcaJdbcRealm();
//        realm.setDataSource(dataSource);
//        realm.setCredentialsMatcher(new PasswordMatcher());
//        return realm;
//    }
//
    @Bean(name = "securityManager")
    public DefaultWebSecurityManager defaultWebSecurityManager(@Qualifier("realm") Realm realm) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(realm);
        DefaultWebSessionManager dwsm = new DefaultWebSessionManager();
        Cookie cookie = new SimpleCookie();
        cookie.setName(cookieName);
        dwsm.setSessionIdCookie(cookie);
        dwsm.setGlobalSessionTimeout(sessionTimeoutMillis);
        securityManager.setSessionManager(dwsm);

        return securityManager;
    }
//
//    @Bean
//    public static LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
//        return new LifecycleBeanPostProcessor();
//    }
//
//
////    /**
////     * 開啟shiro aop註解支持.
////     * 使用代理方式;所以需要開啟代碼支持;
////     *
////     * @param securityManager
////     * @return
////     */
////    @Bean
////    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
////        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
////        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
////        return authorizationAttributeSourceAdvisor;
////    }
//
////
////    @Bean
////    public static DefaultAdvisorAutoProxyCreator getDefaultAdvisorAutoProxyCreator() {
////        DefaultAdvisorAutoProxyCreator creator = new DefaultAdvisorAutoProxyCreator();
////        /**
////         * setUsePrefix(false)用于解决一个奇怪的bug。在引入spring aop的情况下。
////         * 在@Controller注解的类的方法中加入@RequiresRole注解，会导致该方法无法映射请求，导致返回404。
////         * 加入这项配置能解决这个bug
////         */
////        creator.setUsePrefix(true);
////        return creator;
////    }
//
//    /**
//     * 这里统一做鉴权，即判断哪些请求路径需要用户登录，哪些请求路径不需要用户登录。
//     * 这里只做鉴权，不做权限控制，因为权限用注解来做。
//     *
//     * @return
//     */
//    @Bean
//    public ShiroFilterChainDefinition shiroFilterChainDefinition() {
//        DefaultShiroFilterChainDefinition chain = new DefaultShiroFilterChainDefinition();
//
//        //哪些请求可以匿名访问
//        chain.addPathDefinition("/user/login", "anon");
//        // 由於使用Swagger調試，因此設置所有Swagger相關的請求可以匿名訪問
//        chain.addPathDefinition("/swagger-ui.html", "anon");
//        chain.addPathDefinition("/swagger-resources", "anon");
//        chain.addPathDefinition("/swagger-resources/configuration/security", "anon");
//        chain.addPathDefinition("/swagger-resources/configuration/ui", "anon");
//        chain.addPathDefinition("/v2/api-docs", "anon");
//        chain.addPathDefinition("/webjars/springfox-swagger-ui/**", "anon");
//
////        chain.addPathDefinition("/swagger-ui.html", "anon");
////        chain.addPathDefinition("/api/v2/api-docs", "anon");
////        chain.addPathDefinition("/hello", "anon");
//
////        chain.addPathDefinition("/page/401", "anon");
////        chain.addPathDefinition("/page/403", "anon");
////        chain.addPathDefinition("/t5/hello", "anon");
////        chain.addPathDefinition("/t5/guest", "anon");
//
//        //除了以上的请求外，其它请求都需要登录
//        chain.addPathDefinition("/**", "authc");
//        return chain;
//    }
}