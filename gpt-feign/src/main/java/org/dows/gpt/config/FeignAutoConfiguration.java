//package org.dows.gpt.config;
//
//import org.dows.gpt.interceptor.FeignSecurityInterceptor;
//import org.dows.gpt.token.JwtTokenProvider;
//import org.springframework.boot.autoconfigure.AutoConfigureAfter;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//@AutoConfigureAfter({
//        JacksonConfig.class
//})
//public class FeignAutoConfiguration {
//
//    @Bean
//    @ConditionalOnMissingBean
//    public JwtTokenProvider jwtTokenProvider() {
//        return new JwtTokenProvider();
//    }
//
//    @Bean
//    @ConditionalOnMissingBean
//    public FeignSecurityInterceptor feignRequestHeaderInterceptor(JwtTokenProvider jwtTokenProvider) {
//        return new FeignSecurityInterceptor(jwtTokenProvider);
//    }
//
//}
