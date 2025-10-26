package com.wanderlust.WanderLust.security;

import com.wanderlust.WanderLust.controller.AuthController;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;


@Component
@RequiredArgsConstructor
public class RequestLoginFilter implements Filter {


    private final AuthController authController;

    @Autowired
    private DataSource dataSource;


    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        try (Connection conn = dataSource.getConnection()) {
            if (!conn.isValid(1)) {
                HttpServletResponse response = (HttpServletResponse) servletResponse;
                response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
                response.getWriter().write("Service is Unavailable");
                return;
            }
        } catch (SQLException e) {
            HttpServletResponse response = (HttpServletResponse) servletResponse;
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            response.getWriter().write("Service is Unavailable");
            return;
        }
        filterChain.doFilter(servletRequest,servletResponse);

    }
}
