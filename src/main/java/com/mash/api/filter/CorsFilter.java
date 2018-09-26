package com.mash.api.filter;

import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Component
public class CorsFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        // 多个域名
//        String []  allowDomain= {"http://192.168.1.104:8082"};
//        Set allowedOrigins= new HashSet(Arrays.asList(allowDomain));
//        String originHeader=((HttpServletRequest) servletRequest).getHeader("Origin");
//        if (allowedOrigins.contains(originHeader)){


            HttpServletResponse response = (HttpServletResponse) servletResponse;

            //response.setHeader("Access-Control-Allow-Origin", "http://192.168.0.102:8083");
            response.setHeader("Access-Control-Allow-Origin", "http://m.hunchg.com");
            response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, PUT");
            response.setHeader("Access-Control-Max-Age", "3600");
            response.setHeader("Access-Control-Allow-Headers", "x-requested-with");
            response.setHeader("Access-Control-Allow-Credentials", "true");
            filterChain.doFilter(servletRequest, servletResponse);

//        }
    }

    @Override
    public void destroy() {

    }
}
