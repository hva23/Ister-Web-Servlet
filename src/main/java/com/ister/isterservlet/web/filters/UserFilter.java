package com.ister.isterservlet.web.filters;

import com.ister.isterservlet.common.RequestStatus;
import com.ister.isterservlet.domain.User;
import com.ister.isterservlet.web.BaseWebJson;
import com.ister.isterservlet.web.RequestWrapper;
import com.ister.isterservlet.web.user.UserSignUp;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

@WebFilter
public class UserFilter implements Filter {

    BaseWebJson webJson = new BaseWebJson();

    @Override
    public void init(FilterConfig config) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        Cookie[] cookies = ((HttpServletRequest) request).getCookies();
        //Check if this user has cookie to login
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                String cookieName = cookie.getName();
                String cookieValue = cookie.getValue();

                if (cookieName.contentEquals("USER_ID") && !cookieValue.contentEquals("")) {
                    if (UserSignUp.loggedInUsers.contains(cookieValue))
                        chain.doFilter(request, response);
                }
            }
        } else {
            //Redirect to login page

            PrintWriter out = response.getWriter();
            out.println("Please login or signup");
        }
    }

    @Override
    public void destroy() {

    }
}
