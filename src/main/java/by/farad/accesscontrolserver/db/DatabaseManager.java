package by.farad.accesscontrolserver.db;

import by.farad.accesscontrolserver.util.PasswordUtil;

import java.sql.*;

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
