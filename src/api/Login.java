package api;

import core.Database;
import java.sql.Connection;
import java.sql.SQLException;

public class Login {
  private Connection connection = Database.getConnection();

  public Login() throws SQLException {
  }
}
