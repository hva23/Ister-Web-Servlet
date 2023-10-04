package com.ister.isterservlet.service;

import com.ister.isterservlet.common.RequestStatus;
import com.ister.isterservlet.domain.Location;
import com.ister.isterservlet.domain.Product;
import com.ister.isterservlet.domain.TelemetryData;
import com.ister.isterservlet.domain.User;
import com.ister.isterservlet.repository.ProductRepository;

import java.util.*;

public class ProductService {
    ProductRepository productRepository;

    public ProductService() {
        this.productRepository = new ProductRepository("jdbc:mysql://localhost:8080/Ister", "root", "v@h@bI2442");
    }

    public RequestStatus addThing(Product product) {
        Optional<Product> dbOptionalProduct = productRepository.findBySerialNumber(product.getSerialNumber());
        if (dbOptionalProduct.isPresent()) {
            System.out.println("The thing/product already exists\nOperation failed");
            return RequestStatus.AlreadyExists;
        } else {
            if (productRepository.create(product)) {
                System.out.println("Thing/Product added successfully");
                return RequestStatus.Successful;
            } else {
                System.out.println("Something went wrong!");
                return RequestStatus.Failed;
            }
        }
    }

    public RequestStatus editThing(Product product) {
        Optional<Product> dbOptionalProduct = productRepository.findBySerialNumber(product.getSerialNumber());
        if (dbOptionalProduct.isPresent()) {
            product.setId(dbOptionalProduct.get().getId());
            if (productRepository.update(product)) {
                System.out.println("the location updated successfully");
                return RequestStatus.Successful;
            } else {
                System.out.println("Something went wrong!");
                return RequestStatus.Failed;
            }
        } else {
            System.out.println("The location doesn't exist");
            return RequestStatus.DoesNotExist;
        }
    }

    public RequestStatus deleteThing(Product product) {
        Optional<Product> dbProductOptional = productRepository.findBySerialNumber(product.getSerialNumber());
        if (dbProductOptional.isPresent()) {
            product.setId(dbProductOptional.get().getId());
            if (productRepository.delete(product)) {
                System.out.println("the product/product deleted successfully");
                return RequestStatus.Successful;
            } else {
                System.out.println("Something went wrong!");
                return RequestStatus.Failed;
            }
        } else {
            System.out.println("The product/product doesn't exist");
            return RequestStatus.Failed;
        }
    }

    public Product getThing(String serialNumber) {
        Optional<Product> thingsOptional = productRepository.findBySerialNumber(serialNumber);
        return thingsOptional.orElse(null);
    }

    public String getThingData(Product product) {
       /* return String.format("""
                            Thing name : %s
                            Thing ID : %d
                            Thing serial number : %s
                            Thing location (latitude, longitude) : %s(%.2f, %.2f)
                            Thing owner (username, ID) : %s , %s
                            Telemetry data : %s
                            """,
                thing.getName(),
                thing.getId(),
                thing.getSerialNumber(),
                location.getName(),
                location.getLatitude(),
                location.getLongitude(),
                thing.getUser().getUsername(),
                thing.getUser().getId()/*,
                telemetryData);*/
        return null;
    }

    public String getThingData(String serialNumber) {
        Optional<Product> thingOptional = productRepository.findBySerialNumber(serialNumber);
        if (thingOptional.isPresent()) {
            User user = thingOptional.get().getUser();
            Location location = thingOptional.get().getLocation();
            StringBuilder telemetryData = new StringBuilder();
            Map<String, Object> telemetryDataSet;

            if (thingOptional.get().getTelemetryData() != null) {
                telemetryDataSet = thingOptional.get().getTelemetryData().getData();

                for (Map.Entry<String, Object> data : telemetryDataSet.entrySet()) {
                    telemetryData.append(data.getKey());
                    telemetryData.append(" -> ");
                    telemetryData.append(data.getValue());
                }
            }

            //If location fields are null, then fill it
            if(location.getLatitude() == null) {
                LocationService locationService = new LocationService();
                location = locationService.getLocation(thingOptional.get().getLocation().getId());
            }

            //If user fields are null, then fill it
            if(user.getUsername() == null) {
                UserService userService = new UserService();
                user = userService.getUser(thingOptional.get().getUser().getId());
            }

            return String.format("""
                            Thing name : %s
                            Thing ID : %d
                            Thing serial number : %s
                            Thing location (latitude, longitude) : %s(%.2f, %.2f)
                            Thing owner Username(ID) : %s(%s)
                            Telemetry data : %s
                            """,
                    thingOptional.get().getName(),
                    thingOptional.get().getId(),
                    thingOptional.get().getSerialNumber(),
                    (location.getName() == null ? location.getProvince() : location.getName()),
                    location.getLatitude(),
                    location.getLongitude(),
                    user.getUsername(),
                    user.getId(),
                    telemetryData);
        }
        return null;

    }

    public String getAllThingsData() {
        Product product;
        List<Product> productList = productRepository.getAll();
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < productList.size(); i++) {
            String serialNumber;
            String createdDate;
            product = productList.get(i);
            serialNumber = product.getSerialNumber();
            createdDate = product.getCreatedDate();
            stringBuilder.append(String.format("""
                            -- #%02d --
                            Thing ID : %s
                            Product name : %s
                            Serial number : %s
                            Purchase date : %s
                            Purchase time : %s
                            
                            """,
                    i + 1,
                    product.getId(),
                    (serialNumber.substring(0, 3).contentEquals("100") ? "eCam" : "Touch Switch"),
                    serialNumber,
                    createdDate.split(" ")[0],
                    createdDate.split(" ")[1]));
        }

        return stringBuilder.toString();
    }



    public String getUserThingData(User user) {
        Product product;
        List<Product> productList = productRepository.findByUser(user);
        StringBuilder stringBuilder = new StringBuilder();

        if(productList == null)
            return "This user doesn't exist or doesn't purchase any product";
        for (int i = 0; i < productList.size(); i++) {
            String serialNumber;
            String createdDate;
            product = productList.get(i);
            serialNumber = product.getSerialNumber();
            createdDate = product.getCreatedDate();
            stringBuilder.append(String.format("""
                            -- #%02d --
                            Username : %s
                            Thing ID : %s
                            Product name : %s
                            Serial number : %s
                            Purchase date : %s
                            Purchase time : %s
                            
                            """,
                    i + 1,
                    user.getUsername(),
                    product.getId(),
                    (serialNumber.substring(0, 3).contentEquals("100") ? "eCam" : "Touch Switch"),
                    serialNumber,
                    createdDate.split(" ")[0],
                    createdDate.split(" ")[1]));
        }

        return stringBuilder.toString();
    }

    public List<Product> getUserThing(User user) {
        return productRepository.findByUser(user);
    }

    public TelemetryData getTelemetryData(Product thing) {
        return thing.getTelemetryData();
    }

    public RequestStatus sendTelemetryData(TelemetryData telemetryData) {
        telemetryData.getProduct().setTelemetryData(telemetryData);
        productRepository.update(telemetryData.getProduct());
        return RequestStatus.Successful;
    }
}
