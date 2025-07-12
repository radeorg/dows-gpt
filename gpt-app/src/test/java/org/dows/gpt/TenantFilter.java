package org.dows.gpt;

import cn.hutool.core.util.StrUtil;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 租户过滤器，用于解析URL中的组织名称
 */
public class TenantFilter implements Filter {
    
    // 匹配类似 http://xxx.com/ddd/... 或 http://xxx.com/ddd 的路径
    private static final Pattern ORG_PATH_PATTERN = Pattern.compile("^.*?/([^/]+)(.*)$");

    public static void main(String[] args) {

        Matcher matcher =  ORG_PATH_PATTERN.matcher("http://www.dev.hioas.com/ddd/abc/ddd/deee");
        if(matcher.matches()) {
            String organization = matcher.group(1);
            String identifier = matcher.group(2);
            if(StrUtil.isNotBlank(identifier)&& identifier.startsWith("/")){
                String substring = identifier.substring(1);
                identifier = substring.substring(0,substring.indexOf("/"));
            }

            System.out.println(organization);
            System.out.println(identifier);
        } else {
            System.out.println("No match found");
        }
    }
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String requestURI = httpRequest.getRequestURI();
        
        try {
            Matcher matcher = ORG_PATH_PATTERN.matcher(requestURI);
            if (matcher.matches()) {
                String organization = matcher.group(1);
                String remainingPath = matcher.group(2);
                
                // 设置租户上下文
                //TenantContext.setCurrentTenant(organization);
                
                // 重写请求URI，移除组织部分
                HttpServletRequestWrapper wrapper = new HttpServletRequestWrapper(httpRequest) {
                    @Override
                    public String getRequestURI() {
                        return remainingPath;
                    }
                };
                
                chain.doFilter(wrapper, response);
            } else {
                // 处理默认租户或返回错误
                chain.doFilter(request, response);
            }
        } finally {
            // 清除租户上下文，防止线程泄漏
            //TenantContext.clear();
        }
    }
}    