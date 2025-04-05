package by.farad.accesscontrolserver.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

public class EventLogger {

    public static void logEvent(String eventType, String eventDescription, String username) {
        String sql = "INSERT INTO system_logs (time, event_type, event_description, username) VALUES (?, ?, ?, ?)";

        try (Connection connection = DatabaseManager.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
            statement.setString(2, eventType);
            statement.setString(3, eventDescription);
            statement.setString(4, username);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
