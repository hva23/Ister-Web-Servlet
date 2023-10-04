package com.ister.isterservlet.web.location;

import com.fasterxml.jackson.databind.ser.Serializers;
import com.ister.isterservlet.common.RequestStatus;
import com.ister.isterservlet.domain.Location;
import com.ister.isterservlet.service.LocationService;
import com.ister.isterservlet.web.BaseWebJson;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

public class LocationServlet extends HttpServlet {
    LocationService locationService = new LocationService();
    BaseWebJson webJson = new BaseWebJson();
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) {
        //Add, Edit, Delete
        try {
            Location location = new Location();
            PrintWriter out = response.getWriter();
            RequestStatus result;
            Map<String, String> map = webJson.readRequestBody(request);
            int paramCount = map.size();

            if(paramCount >= 4) {   //Add or Edit
                location.setProvince(map.get("province"));
                location.setCity(map.get("city"));
                location.setLatitude(Double.valueOf(map.get("latitude")));
                location.setLongitude(Double.valueOf(map.get("longitude")));

                //Processing URI to do add or edit method
                String uri = request.getRequestURI();
                uri = uri.split("/")[2];
                if (uri.contentEquals("LocationAdd")) {
                    result = locationService.addLocation(location);
                    out.println("Location adding status : " + result);
                } else {
                    location.setId(Long.valueOf(map.get("id")));
                    result = locationService.editLocation(location);
                    out.println("Editing location status : " + result);
                }
            } else {    //Delete
                location.setId(Long.valueOf(map.get("id")));
                result = locationService.deleteLocation(location);
                out.println("Delete location status : " + result);
            }
        } catch (IOException e) {

        }
    }
}
