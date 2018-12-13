package api;

import core.Database;
import core.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class UserDB {
  private static Connection connection;

  static {
    try {
      connection = Database.getConnection();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public static User updateUserTime(User user) throws SQLException {

    String SQLCommand = "UPDATE users SET \"time\" = ? WHERE id = ?";

    PreparedStatement pStatement = connection.prepareStatement(SQLCommand);
    pStatement.setArray(1, connection.createArrayOf("text", user.getUserTime()));
    pStatement.setObject(2, user.getUserId(), Types.OTHER);

    pStatement.executeUpdate();

    return user;
  }
}
