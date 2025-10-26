package com.wanderlust.WanderLust.config;


import org.springframework.session.MapSessionRepository;
import org.springframework.session.config.annotation.web.http.EnableSpringHttpSession;
import org.springframework.session.jdbc.config.annotation.web.http.EnableJdbcHttpSession;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.web.http.DefaultCookieSerializer;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

//@Configuration
//@EnableJdbcHttpSession
//public class SessionCookieConfig {
//
//    @Bean
//    public CookieSerializer cookieSerializer(){
//        DefaultCookieSerializer serializer = new DefaultCookieSerializer();
//        serializer.setCookieName("wanderlust_cookie"); // custom cookie name
//        serializer.setCookiePath("/");
//        serializer.setCookieMaxAge(60 * 60); // 1 hour
//        serializer.setUseHttpOnlyCookie(true);
//        serializer.setSameSite("Lax");
//        serializer.setUseSecureCookie(false); // false for localhost
//        System.out.println("CookieSerializer bean loaded — custom cookie config applied!");
//        return serializer;
//    }
//}

@Configuration
@EnableJdbcHttpSession
public class SessionCookieConfig {

    @Bean
    public CookieSerializer cookieSerializer(){
        DefaultCookieSerializer serializer = new DefaultCookieSerializer();
        serializer.setCookieName("wanderlust_cookie"); // custom cookie name
        serializer.setCookiePath("/"); // applies for all paths
        serializer.setCookieMaxAge(-1); // session cookie → removed when browser closes
        serializer.setUseHttpOnlyCookie(true);
        serializer.setSameSite("Lax");
        serializer.setUseSecureCookie(false); // false for localhost
        System.out.println("CookieSerializer bean loaded — custom cookie config applied!");
        return serializer;
    }
}
