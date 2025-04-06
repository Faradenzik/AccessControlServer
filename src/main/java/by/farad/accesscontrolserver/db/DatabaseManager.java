package by.farad.accesscontrolserver.db;

import by.farad.accesscontrolserver.util.PasswordUtil;

import java.sql.*;
import java.util.*;

public class DatabaseManager {

    private static final String DB_URL = "jdbc:postgresql://localhost:5432/AccessControlApp";
    private static final String USER = "postgres";
    private static final String PASSWORD = "postgres";
    private static Connection connection = null;

    private DatabaseManager() {
    }

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("org.postgresql.Driver");
                connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            } catch (ClassNotFoundException e) {
                throw new SQLException("PostgreSQL JDBC driver not found", e);
            }
        }
        return connection;
    }

    public static void saveUserToDatabase(String login, String hashedPassword) throws SQLException {
        String query = "INSERT INTO users (username, password) VALUES (?, ?)";

        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, login);
            stmt.setString(2, hashedPassword);
            stmt.executeUpdate();
        }
    }

    public static boolean checkUserCredentials(String login, String hashedPassword) throws SQLException {
        String sql = "SELECT password FROM users WHERE username = ?";
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, login);
            try (ResultSet resultSet = stmt.executeQuery()) {
                if (resultSet.next()) {
                    String storedPassword = resultSet.getString("password");
                    return PasswordUtil.checkPassword(hashedPassword, storedPassword);
                }
            }
        }
        return false;
    }

    public static List<Map<String, Object>> getWorkersList() throws SQLException {
        String query = "SELECT id, name, surname, patronomyc, sex, birthday, position, department FROM workers";
        List<Map<String, Object>> workersList = new ArrayList<>();

        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> worker = new LinkedHashMap<>();
                worker.put("id", rs.getInt("id"));
                worker.put("name", rs.getString("name"));
                worker.put("surname", rs.getString("surname"));
                worker.put("patronomyc", rs.getString("patronomyc"));
                worker.put("sex", rs.getString("sex"));
                worker.put("birthday", rs.getDate("birthday").toString()); // Преобразуем дату в строку
                worker.put("position", rs.getString("position"));
                worker.put("department", rs.getString("department"));
                workersList.add(worker);
            }
        }
        return workersList;
    }


    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
