package com.ister.isterservlet.web.user;

import com.ister.isterservlet.common.RequestStatus;
import com.ister.isterservlet.domain.User;
import com.ister.isterservlet.service.AdminService;
import com.ister.isterservlet.service.UserService;
import com.ister.isterservlet.web.BaseWebJson;
import com.ister.isterservlet.web.RequestWrapper;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class UserSignUp extends HttpServlet {
    public static List<String> loggedInUsers = new ArrayList<>();
    public static List<String> admins = new ArrayList<>();
    BaseWebJson webJson = new BaseWebJson();
    UserService userService = new UserService();

    public void doPost(HttpServletRequest request, HttpServletResponse response) {
        java.util.Date date = new java.util.Date();
        String currentDateTime = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
        try {
            User user = new User();
            PrintWriter out = response.getWriter();
            request = new RequestWrapper(request);
            Map<String, String> map = webJson.readRequestBody(((RequestWrapper)request).getBody());
            RequestStatus result;

            user.setId(UUID.randomUUID().toString());
            user.setUsername(map.get("username"));
            user.setPassword(map.get("password"));
            user.setEmail(map.get("email"));
            user.setPhoneNumber(map.get("phonenumber"));
            user.setCreatedDate(currentDateTime);
            user.setLastModifiedDate(currentDateTime);

            result = userService.signUp(user);

            out.println("Sign up operation status : " + result);

            if (result == RequestStatus.Successful) {
                Cookie cookie = new Cookie("USER_ID", user.getId());
                loggedInUsers.add(user.getId());
                response.addCookie(cookie);
            }
        } catch (IOException e) {
            System.out.println("IO exception occurred");
        }
    }
}