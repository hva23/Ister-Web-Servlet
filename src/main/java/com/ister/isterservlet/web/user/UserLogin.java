package com.ister.isterservlet.web.user;

import com.ister.isterservlet.common.RequestStatus;
import com.ister.isterservlet.domain.User;
import com.ister.isterservlet.service.AdminService;
import com.ister.isterservlet.service.UserService;
import com.ister.isterservlet.web.BaseWebJson;
import com.ister.isterservlet.web.RequestWrapper;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Map;

public class UserLogin extends HttpServlet {
    UserService userService = new UserService();
    BaseWebJson webJson = new BaseWebJson();

    public void doPost(HttpServletRequest request, HttpServletResponse response) {
        try {
            PrintWriter out = response.getWriter();
            request = new RequestWrapper(request);
            Map<String, String> map = webJson.readRequestBody(((RequestWrapper)request).getBody());
            RequestStatus result;
            Cookie[] cookies = request.getCookies();

            response.setContentType("text/plain");

            //Check if this user has cookie to login
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    String cookieName = cookie.getName();
                    String cookieValue = cookie.getValue();
                    if (cookieName.contentEquals("USER_ID") && !cookieValue.contentEquals("")) {
                        User user;
                        user = userService.getUser(cookieValue); //cookieValue is equal to user id

                        result = userService.login(user);
                        out.println("Login operation status : " + result);
                        if (result == RequestStatus.Successful) {

                            if(UserSignUp.loggedInUsers == null) UserSignUp.loggedInUsers = new ArrayList<>();
                            UserSignUp.loggedInUsers.add(user.getId());

                            //Check if the user is admin
                            AdminService adminService = new AdminService();
                            if(UserSignUp.admins == null) UserSignUp.admins = new ArrayList<>();
                            if(adminService.isAdmin(user.getId()))
                                UserSignUp.admins.add(user.getId());

                            out.println("Welcome " + user.getUsername() + "\n" + userService.getUserData(user.getUsername()));
                        }
                        break;
                    }
                }
            } else {
                User user = new User();
                //if user doesn't have cookie
                user.setUsername(map.get("username"));
                user.setPassword(map.get("password"));

                result = userService.login(user);
                out.println("Login operation status : " + result);
                if (result == RequestStatus.Successful) {
                    Cookie cookie = new Cookie("USER_ID", userService.getUserId(user.getUsername()));
                    if(UserSignUp.loggedInUsers == null) UserSignUp.loggedInUsers = new ArrayList<>();
                    UserSignUp.loggedInUsers.add(user.getId());

                    //Check if the user is admin
                    AdminService adminService = new AdminService();
                    if(adminService.isAdmin(user.getId()))
                        UserSignUp.admins.add(user.getId());

                    out.println("Welcome " + user.getUsername() + "\n" + userService.getUserData(user.getUsername()));
                    response.addCookie(cookie);
                }
            }
        } catch (IOException e) {
            System.out.println("IO Exception occurred");
        }
    }
}
