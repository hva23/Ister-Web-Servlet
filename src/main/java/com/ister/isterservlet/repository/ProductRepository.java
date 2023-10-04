package com.ister.isterservlet.repository;

import com.ister.isterservlet.domain.Location;
import com.ister.isterservlet.domain.Product;
import com.ister.isterservlet.domain.User;
import com.ister.isterservlet.service.QueryBuilder;

import java.sql.*;
import java.util.*;
import java.util.Date;

public class ProductRepository implements baseRepository<Product, Long> {

    private final String rawUrl;
    private final String username;
    private final String password;
    private final String TABLE_NAME = "USER_PRODUCTS";

    public ProductRepository(String rawUrl, String username, String password) {
        this.rawUrl = rawUrl;
        this.username = username;
        this.password = password;
    }

    @Override
    public boolean create(Product product) {
        try {
            java.util.Date date = new Date();
            String dateTime = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);

            QueryBuilder queryBuilder = new QueryBuilder();
            ResultSet resultSet;
            Statement statement;
            Object[] values;
            String query;
            long lastUserProductsId = 0;
            long productId = 0;
            boolean result;

            //Read last user product ID
            Object[] obj = read(TABLE_NAME, new String[]{"ID"}, null);

            resultSet = (ResultSet) obj[0];
            statement = (Statement) obj[2];

            do {
                if (resultSet.getLong("ID") > lastUserProductsId)
                    lastUserProductsId = resultSet.getLong("ID");
            }
            while (resultSet.next());//End

            //Because we just have 2 product
            productId = product.getSerialNumber().substring(0, 3).contentEquals("100") ? 1 : 2;

            values = new Object[]{
                    lastUserProductsId + 1,
                    product.getName(),
                    product.getSerialNumber(),
                    product.getUser().getId(),
                    productId,
                    product.getLocation().getId(),
                    dateTime
            };

            query = queryBuilder.create(TABLE_NAME, values);

            result = statement.executeUpdate(query) > 0;

            resultSet.close();
            statement.close();
            ((Connection) obj[1]).close();

            return result;

        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(Product product) {
        try {
            DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());
            Connection con = DriverManager.getConnection(rawUrl, username, password);
            Statement statement = con.createStatement();

            boolean result;
            QueryBuilder queryBuilder = new QueryBuilder();
            Map<String, Object> columnAndValues = new HashMap<>();
            Map<String, Object> conditions = new HashMap<>();
            String query;
            int productId;

            //Because we just have 2 product
            productId = product.getSerialNumber().substring(0, 3).contentEquals("100") ? 1 : 2;

            columnAndValues.put("NAME", product.getName());
            columnAndValues.put("SERIAL_NUMBER", product.getSerialNumber());
            columnAndValues.put("USER_ID", product.getUser().getId());
            columnAndValues.put("PRODUCT_ID", productId);
            columnAndValues.put("LOCATION_ID", product.getLocation().getId());

            conditions.put("ID", product.getId());

            query = queryBuilder.update(TABLE_NAME, columnAndValues, conditions);

            result = statement.executeUpdate(query) > 0;
            statement.close();
            con.close();
            return result;

        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(Product product) {
        try {
            DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());
            Connection con = DriverManager.getConnection(rawUrl, username, password);
            Statement statement = con.createStatement();

            boolean result;
            QueryBuilder queryBuilder = new QueryBuilder();
            Map<String, Object> condition = new HashMap<>();
            String query;

            condition.put("ID", product.getId().toString());

            query = queryBuilder.delete(TABLE_NAME, condition);

            result = statement.executeUpdate(query) > 0;
            statement.close();
            con.close();
            return result;

        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Product> getAll() {
        try {
            Product product;
            List<Product> userList = new ArrayList<>();
            Object[] obj;

            obj = read(TABLE_NAME, null, null);

            do {
                product = setProductFields((ResultSet) obj[0]);

                userList.add(product);
            }
            while (((ResultSet) obj[0]).next());

            ((ResultSet) obj[0]).close();
            ((Statement) obj[2]).close();
            ((Connection) obj[1]).close();

            return userList;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public Optional<Product> findById(Long id) {
        try {
            ResultSet resultSet;
            Product product;
            Map<String, Object> condition = new HashMap<>();

            condition.put("ID", id);
            Object[] obj = read(TABLE_NAME, null, condition);

            if (obj == null)
                return Optional.empty();

            resultSet = (ResultSet) obj[0];

            product = setProductFields(resultSet);

            resultSet.close();
            ((Statement) obj[2]).close();
            ((Connection) obj[1]).close();

            return Optional.of(product);
        } catch (Exception ex) {
            ex.printStackTrace();
            return Optional.empty();
        }
    }


    public Optional<Product> findBySerialNumber(String serialNumber) {
        try {
            ResultSet resultSet;
            Product product;
            Map<String, Object> condition = new HashMap<>();

            condition.put("SERIAL_NUMBER", serialNumber);
            Object[] obj = read(TABLE_NAME, null, condition);

            resultSet = (ResultSet) obj[0];

            product = setProductFields(resultSet);

            resultSet.close();
            ((Statement) obj[2]).close();
            ((Connection) obj[1]).close();

            return Optional.of(product);
        } catch (Exception ex) {
            ex.printStackTrace();
            return Optional.empty();
        }
    }


    public List<Product> findByUser(User user) {
        try {
            Product product;
            List<Product> userList = new ArrayList<>();
            Map<String, Object> condition = new HashMap<>();
            Object[] obj;

            if (user.getId() != null)
                condition.put("USER_ID", user.getId());
            else
                return null;

            obj = read(TABLE_NAME, null, condition);

            if(obj[0] == null) return null;

            do {
                product = setProductFields((ResultSet) obj[0]);

                userList.add(product);
            }
            while (((ResultSet) obj[0]).next());

            ((ResultSet) obj[0]).close();
            ((Statement) obj[2]).close();
            ((Connection) obj[1]).close();

            return userList;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }


    private Product setProductFields(ResultSet resultSet) throws SQLException {
        Product product = new Product();
        Location location = new Location();
        User user = new User();

        user.setId(resultSet.getString("USER_ID"));
        location.setId(resultSet.getLong("LOCATION_ID"));

        product.setId(resultSet.getLong("ID"));
        product.setName(resultSet.getString("NAME"));
        product.setSerialNumber(resultSet.getString("SERIAL_NUMBER"));
        product.setLocation(location);
        product.setUser(user);
        product.setCreatedDate(resultSet.getString("CREATED_DATE"));

        return product;
    }
}
