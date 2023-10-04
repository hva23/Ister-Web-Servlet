package com.ister.isterservlet.repository;

import com.ister.isterservlet.domain.Location;
import com.ister.isterservlet.service.QueryBuilder;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class AdminRepository implements baseRepository<String, String> {

    private final String TABLE_NAME = "ADMINS";
    final private String rawUrl;
    final private String username;
    final private String password;

    public AdminRepository(String rawUrl, String username, String password) {
        this.rawUrl = rawUrl;
        this.username = username;
        this.password = password;
    }

    @Override
    public boolean create(String id) {
        try {
            QueryBuilder queryBuilder = new QueryBuilder();
            Object[] values;
            long lastAdminId = 0;
            Statement statement;
            ResultSet resultSet;
            boolean result;
            String query;

            Object[] obj = read(TABLE_NAME, new String[]{"ID"}, null);

            resultSet = (ResultSet) obj[0];
            statement = (Statement) obj[2];

            do {
                if (resultSet.getLong("ID") > lastAdminId)
                    lastAdminId = resultSet.getLong("ID");
            }
            while (resultSet.next());

            values = new Object[]{
                    lastAdminId + 1,
                    id
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
    public boolean delete(String id) {
        try {
            DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());
            Connection con = DriverManager.getConnection(rawUrl, username, password);
            Statement statement = con.createStatement();

            boolean result;
            QueryBuilder queryBuilder = new QueryBuilder();
            Map<String, Object> condition = new HashMap<>();
            String query;

            condition.put("USER_ID", id);

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
    public boolean update(String id) {
        return false;
    }

    @Override
    public List<String> getAll() {
        return null;
    }

    @Override
    public Optional<String> findById(String id) {
        try {
            Map<String, Object> conditions = new HashMap<>();

            conditions.put("USER_ID", id);

            Object[] obj = read(TABLE_NAME, null, conditions);
            if (obj == null)
                return Optional.empty();

            ((ResultSet) obj[0]).close();
            ((Statement) obj[2]).close();
            ((Connection) obj[1]).close();

            return Optional.of(id);
        } catch (Exception ex) {
            ex.printStackTrace();
            return Optional.empty();
        }
    }
}
