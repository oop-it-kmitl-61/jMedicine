package api;

import static api.MedicineDB.getAllMedicine;
import static api.DoctorDB.getAllDoctor;
import static api.AppointmentDB.getAllAppointment;
import static core.Utils.sha256;

import core.Database;
import core.User;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;


public class Login {

  private static Connection connection;

  static {
    try {
      connection = Database.getConnection();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }


  public static User doSignIn(String username, char[] password)
      throws NoSuchAlgorithmException, SQLException, LoginException, ParseException {
    String encrypted = sha256(String.valueOf(password));

    String SQLCommand = "SELECT * FROM users WHERE username = ? AND password = ?";

    PreparedStatement pStatement = connection.prepareStatement(SQLCommand);
    pStatement.setString(1, username);
    pStatement.setString(2, encrypted);

    ResultSet result = pStatement.executeQuery();

    if (result.next()) {
      User user = new User(result.getString("id"), result.getString("title"),
          result.getString("firstname"), result.getString("lastname"), result.getString("email"),
          result.getString("gender"), String.valueOf(result.getInt("age")),
          String.valueOf(result.getFloat("weight")), String.valueOf(result.getFloat("height")));

      user.setUserMedicines(getAllMedicine(user.getUserId()));
      user.setUserDoctors(getAllDoctor(user.getUserId()));
      user.setUserAppointments(getAllAppointment(user.getUserId()));

      return user;
    }

    throw new LoginException("Login failed");
  }

  public static User doSignUp(User user, char[] password)
      throws NoSuchAlgorithmException, SQLException {
    String encrypted = sha256(String.valueOf(password));

    String SQLCommand = "WITH ROW AS ( INSERT INTO users (username, \"password\", email, title, firstname, lastname, gender, weight, height, age) VALUES (?, ?, ? ,?, ?, ?, ?, ?, ?, ?, ?) RETURNING id ) SELECT id FROM ROW";

    PreparedStatement pStatement = connection.prepareStatement(SQLCommand);
    pStatement.setString(1, user.getUserName());
    pStatement.setString(2, encrypted);
    pStatement.setString(3, user.getUserEmail());
    pStatement.setString(4, user.getUserTitle());
    pStatement.setString(5, user.getUserFirstName());
    pStatement.setString(6, user.getUserLastName());
    pStatement.setString(7, user.getUserGender());
    pStatement.setFloat(8, Float.valueOf(user.getUserWeight()));
    pStatement.setFloat(9, Float.valueOf(user.getUserHeight()));
    pStatement.setInt(10, Integer.valueOf(user.getUserAge()));

    ResultSet result = pStatement.executeQuery();

    result.next();

    user.setUserId(result.getString("id"));

    return user;
  }
}