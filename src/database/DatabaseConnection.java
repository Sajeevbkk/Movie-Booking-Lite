package database;

import exception.BookingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    // ====== CHANGE THESE FOR YOUR MYSQL ======
    private static final String URL =
        "jdbc:mysql://localhost:3306/movie_booking_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "password";
    // =========================================

    // throws: the caller must handle BookingException
    public static Connection getConnection() throws BookingException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");               // load driver
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new BookingException("MySQL driver not found. Add the connector JAR to lib/.", e);
        } catch (SQLException e) {
            throw new BookingException("Cannot connect to database. Check MySQL is running and the password.", e);
        }
    }

    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.out.println("Error closing connection: " + e.getMessage());
            }
        }
    }
}
