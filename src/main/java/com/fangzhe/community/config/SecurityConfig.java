package com.fangzhe.community.config;

import com.fangzhe.community.util.CommunityConstant;
import com.fangzhe.community.util.CommunityUtil;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author fang
 */
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter implements CommunityConstant {
    private static final String XML_HTTP_REQUEST = "XMLHttpRequest";
    @Override
    public void configure(WebSecurity web) throws Exception {
        //静态资源不拦截
        web.ignoring().antMatchers("/resources/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //授权
        http.authorizeRequests()
                .antMatchers(
                        "/user/setting",
                        "/user/upload",
                        "/letter/**",
                        "/notice/**",
                        "/like",
                        "/follow",
                        "/unfollow"

                ).hasAnyAuthority(
                        AUTHORITY_USER,
                        AUTHORITY_ADMIN,
                        AUTHORITY_MODERATOR
                ).antMatchers(
                        "/discuss/top",
                        "/discuss/essence"
                ).hasAnyAuthority(
                        AUTHORITY_MODERATOR
                ).antMatchers(
                        "/discuss/block",
                        "/data/**"
                ).hasAnyAuthority(
                        AUTHORITY_ADMIN
                ).anyRequest().permitAll()
                .and().csrf().disable();//关闭csrf功能:跨站请求伪造,默认只能通过post方式提交logout;

        //权限不够时的处理
        http.exceptionHandling()
                .authenticationEntryPoint(new AuthenticationEntryPoint() {
                    //当发生这件事的处理
                    //没有登录
                    @Override
                    public void commence(HttpServletRequest req, HttpServletResponse resp, AuthenticationException e) throws IOException, ServletException {
                        //区别是否需要返回json、还是页面
                        String xRequestedWith = req.getHeader("x-requested-with");
                        if ("XMLHttpRequest".equals(xRequestedWith)){
                            resp.setContentType("application/plain;charset=utf-8");
                            PrintWriter writer = resp.getWriter();
                            writer.write(CommunityUtil.getJSONString(403,"你还没有登录噢！"+e.getMessage()));
                        }else {
                            resp.sendRedirect(req.getContextPath() +"/login");
                        }
                    }
                })
                .accessDeniedHandler(new AccessDeniedHandler() {
                    //权限不足
                    @Override
                    public void handle(HttpServletRequest req, HttpServletResponse resp, AccessDeniedException e) throws IOException, ServletException {
                        String xRequestedWith = req.getHeader("x-requested-with");
                        if (XML_HTTP_REQUEST.equals(xRequestedWith)){
                            resp.setContentType("application/plain;charset=utf-8");
                            PrintWriter writer = resp.getWriter();
                            writer.write(CommunityUtil.getJSONString(403,"您没有权限噢！"+e.getMessage()));
                        }else {
                            resp.sendRedirect(req.getContextPath() +"/denied");
                        }

                    }
                });
        //Security底层默认拦截/Logout 处理
        //覆盖它的默认处理的逻辑，执行自己的
        //随便写跳过就好
        http.logout().logoutUrl("/securitylogout");
    }
}
