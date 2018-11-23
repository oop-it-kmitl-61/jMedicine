package api;

import core.Database;
import java.sql.Connection;
import java.sql.SQLException;

public class DoctorDB {
  private static Connection connection;

  static {
    try {
      connection = Database.getConnection();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }




}
