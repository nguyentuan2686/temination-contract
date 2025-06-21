package com.example.demo.filter;

import com.example.demo.micserver.LoginClient;
import com.example.demo.micserver.response.TokenResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class AuthorizationTokenFilter extends OncePerRequestFilter {

    @Autowired
    LoginClient loginClient;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws IOException, ServletException {
      String authHeader = request.getHeader("Authorization");
      if (StringUtils.isEmpty(authHeader)) {
          String username = "hieupd04@mic.vn";
          String password = "6PkpoJOZ1+DIfzLve4wRUg==";
          TokenResponse login = loginClient.login(username, password);
          TokenHolder.setToken(login.getToken());
          TokenHolder.setRefreshToken(login.getRefreshToken());
      } else {
          if (authHeader.startsWith("Bearer ")) {
              String token = authHeader.substring(7);
              TokenHolder.setToken(token);
          }
      }


      filterChain.doFilter(request, response);
    }
}
