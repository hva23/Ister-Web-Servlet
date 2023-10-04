package com.ister.isterservlet.web.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ister.isterservlet.common.RequestStatus;
import com.ister.isterservlet.domain.Product;
import com.ister.isterservlet.domain.User;
import com.ister.isterservlet.service.UserService;
import com.ister.isterservlet.web.BaseWebJson;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

//for login, signup, edit, delete user
//assuming all inputs are correct and valid
public class UserConfiguration extends HttpServlet {
    UserService userService = new UserService();
    BaseWebJson webJson = new BaseWebJson();

    public void init() {
        System.out.println("User Configuration Servlet Initialized");
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) {
        try {
            User user = new User();
            PrintWriter out = response.getWriter();
            Map<String, String> map = webJson.readRequestBody(request);

            user.setUsername(map.get("username"));
            user.setEmail(map.get("email"));
            user.setPhoneNumber(map.get("phonenumber"));
            user.setPassword(map.get("password"));

            out.println("Edit operation status : " + userService.editProfile(user).toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void doDelete(HttpServletRequest request, HttpServletResponse response) {
        try {
            User user = new User();
            PrintWriter out = response.getWriter();
            Map<String, String> map = webJson.readRequestBody(request);
            RequestStatus result;

            user.setUsername(map.get("username"));
            result = userService.delete(user);
            out.println("Delete operation status : " + result);
            if (result == RequestStatus.Successful)
                response.addCookie(new Cookie("USER_ID", ""));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void destroy() {
        System.out.println("User Configuration Servlet Destroyed");
    }


}
