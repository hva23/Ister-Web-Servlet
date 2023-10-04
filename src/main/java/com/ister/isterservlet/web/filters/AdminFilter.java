package com.ister.isterservlet.web.filters;

import com.ister.isterservlet.web.user.UserSignUp;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

public class AdminFilter implements Filter {

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
        Cookie[] cookies = ((HttpServletRequest) request).getCookies();
        //Check if this user has cookie to login
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                String cookieName = cookie.getName();
                String cookieValue = cookie.getValue();

                if (cookieName.contentEquals("USER_ID") && !cookieValue.contentEquals("")) {
                    if (UserSignUp.admins.contains(cookieValue))
                        chain.doFilter(request, response);
                    {
                        PrintWriter out = response.getWriter();
                        out.println("You are not an Admin");
                    }
                }
            }
        } else {
            //Redirect to login page

            PrintWriter out = response.getWriter();
            out.println("Please login or sign up");
        }
    }
}
