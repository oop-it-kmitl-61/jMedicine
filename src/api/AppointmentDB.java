package api;

import core.Database;
import java.sql.Connection;
import java.sql.SQLException;

public class AppointmentDB {

  private static Connection connection;

  static {
    try {
      connection = Database.getConnection();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

}
