package com.ister.isterservlet.web.product;

import com.ister.isterservlet.common.RequestStatus;
import com.ister.isterservlet.domain.Location;
import com.ister.isterservlet.domain.Product;
import com.ister.isterservlet.domain.User;
import com.ister.isterservlet.service.ProductService;
import com.ister.isterservlet.web.BaseWebJson;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

public class ProductServlet extends HttpServlet {

    ProductService productService = new ProductService();
    BaseWebJson webJson = new BaseWebJson();
    public void doPost(HttpServletRequest request, HttpServletResponse response) {
        try {
            Product product = new Product();
            Location location = new Location();
            User user = new User();
            Cookie[] cookies;
            PrintWriter out = response.getWriter();
            RequestStatus result;
            Map<String, String> map = webJson.readRequestBody(request);

            product.setName(map.get("name"));
            product.setSerialNumber(map.get("serialnumber"));

            location.setId(Long.parseLong(map.get("locationid")));
            product.setLocation(location);

            cookies = request.getCookies();
            for (Cookie cookie : cookies) {
                if (cookie.getName().contentEquals("USER_ID")) user.setId(cookie.getValue());
            }
            product.setUser(user);

            //Processing URI to do add or edit method
            String uri = request.getRequestURI();
            uri = uri.split("/")[2];

            if (uri.contentEquals("ProductAdd")) {  //Add
                result = productService.addThing(product);
                out.println("Product adding status : " + result);
            } else {    //Edit
                result = productService.editThing(product);
                out.println("Editing product status : " + result);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void doDelete(HttpServletRequest request, HttpServletResponse response) {
        try {
            Product product = new Product();
            PrintWriter out = response.getWriter();
            Map<String, String> map = webJson.readRequestBody(request);

            product.setSerialNumber(map.get("serialnumber"));
            out.println("Deleting product status : " + productService.deleteThing(product));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
