package com.ister.isterservlet.repository;

import com.ister.isterservlet.domain.Location;
import com.ister.isterservlet.service.QueryBuilder;

import java.sql.*;
import java.util.*;

public class LocationRepository implements baseRepository<Location, Long> {
    final private String rawUrl;
    final private String username;
    final private String password;
    final private String TABLE_NAME = "LOCATION";


    public LocationRepository(String rawUrl, String username, String password) {
        this.rawUrl = rawUrl;
        this.username = username;
        this.password = password;
    }

    @Override
    public boolean create(Location location) {
        try {
            QueryBuilder queryBuilder = new QueryBuilder();
            Object[] values;
            long lastLocationId = 0;
            Statement statement;
            ResultSet resultSet;
            boolean result;
            String query;

            Object[] obj = read(TABLE_NAME, new String[]{"ID"}, null);

            resultSet = (ResultSet) obj[0];
            statement = (Statement) obj[2];

            do {
                if (resultSet.getLong("ID") > lastLocationId)
                    lastLocationId = resultSet.getLong("ID");
            }
            while (resultSet.next());

            values = new Object[]{
                    lastLocationId + 1,
                    location.getProvince(),
                    location.getCity(),
                    location.getLatitude(),
                    location.getLongitude()
            };

            query = queryBuilder.create(TABLE_NAME, values);

            result = statement.executeUpdate(query) > 0;

            ((ResultSet) obj[0]).close(); //or resultSet.close();

            ((Statement) obj[2]).close(); //or statement.close();

            ((Connection) obj[1]).close();

            return result;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(Location location) {
        try {
            DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());
            Connection con = DriverManager.getConnection(rawUrl, username, password);
            Statement statement = con.createStatement();

            boolean result;
            QueryBuilder queryBuilder = new QueryBuilder();
            Map<String, Object> condition = new HashMap<>();
            String query;

            condition.put("ID", location.getId().toString());

            query = queryBuilder.delete(TABLE_NAME, condition);

            result = statement.executeUpdate(query) > 0;
            statement.close();
            con.close();
            return result;

        } catch (
                Exception ex) {
            ex.printStackTrace();
            return false;
        }

    }

    @Override
    public boolean update(Location location) {
        try {
            DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());
            Connection con = DriverManager.getConnection(rawUrl, username, password);
            Statement statement = con.createStatement();

            boolean result;
            QueryBuilder queryBuilder = new QueryBuilder();
            Map<String, Object> columnAndValues = new HashMap<>();
            Map<String, Object> conditions = new HashMap<>();
            String query;

            columnAndValues.put("PROVINCE", location.getProvince());
            columnAndValues.put("CITY", location.getCity());
            columnAndValues.put("LATITUDE", location.getLatitude());
            columnAndValues.put("LONGITUDE", location.getLongitude());

            conditions.put("ID", location.getId());

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
    public List<Location> getAll() {
        try {
            Location location;
            List<Location> locationList = new ArrayList<>();
            Object[] obj;

            obj = read(TABLE_NAME, null, null);

            do {
                location = setLocationFields((ResultSet) obj[0]);

                locationList.add(location);
            }
            while (((ResultSet) obj[0]).next());

            ((ResultSet) obj[0]).close();
            ((Statement) obj[2]).close();
            ((Connection) obj[1]).close();

            return locationList;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public Optional<Location> findById(Long id) {
        /*
            obj[0] -> ResultSet
            obj[1] -> Connection
            obj[2] -> Statement
         */
        try {
            Location location;
            Map<String, Object> conditions = new HashMap<>();

            conditions.put("ID", id);

            Object[] obj = read(TABLE_NAME, null, conditions);
            if (obj == null)
                return Optional.empty();

            location = setLocationFields((ResultSet) obj[0]);

            ((ResultSet) obj[0]).close();
            ((Statement) obj[2]).close();
            ((Connection) obj[1]).close();

            return Optional.of(location);
        } catch (Exception ex) {
            ex.printStackTrace();
            return Optional.empty();
        }
    }

    public Optional<Location> findByProvinceAndCity(String province, String city) {
        try {
            Location location;
            Map<String, Object> condition = new HashMap<>();

            condition.put("PROVINCE", province);
            condition.put("CITY", city);

            Object[] obj = read(TABLE_NAME, null, condition);
            if(obj[0] == null)
                return Optional.empty();

            location = setLocationFields((ResultSet) obj[0]);

            ((ResultSet) obj[0]).close();
            ((Statement) obj[2]).close();
            ((Connection) obj[1]).close();

            return Optional.of(location);
        } catch (Exception ex) {
            ex.printStackTrace();
            return Optional.empty();
        }
    }

    private Location setLocationFields(ResultSet resultSet) throws SQLException {
        Location location = new Location();
        location.setId(resultSet.getLong("ID"));
        location.setProvince(resultSet.getString("PROVINCE"));
        location.setCity(resultSet.getString("CITY"));
        location.setLatitude(resultSet.getDouble("LATITUDE"));
        location.setLongitude(resultSet.getDouble("LONGITUDE"));

        return location;
    }
}
