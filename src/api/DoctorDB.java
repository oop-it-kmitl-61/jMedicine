package api;

import core.Database;
import core.Doctor;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public class DoctorDB {

  private static Connection connection;

  static {
    try {
      connection = Database.getConnection();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }


  public static ArrayList<Doctor> getAllDoctor(String userId) throws SQLException {
    String SQLCommand = "SELECT * FROM doctors WHERE \"user\" = ?";

    PreparedStatement pStatement = connection.prepareStatement(SQLCommand);
    pStatement.setObject(1, userId, Types.OTHER);

    ResultSet result = pStatement.executeQuery();

    ArrayList<Doctor> results = new ArrayList<>();

    while (result.next()) {
      ArrayList<String> time;
      time = Arrays.stream((Object[]) result.getArray("time").getArray())
          .map(Object::toString).collect(Collectors.toCollection(ArrayList::new));

      results.add(
          new Doctor(result.getString("id"), result.getString("title"),
              result.getString("firstname"), result.getString("lastname"), result.getString("ward"),
              result.getString("hospital"), time)
      );
    }

    return results;
  }

  public static Doctor addDoctor(Doctor doctor, String userId) throws SQLException {

    String SQLCommand = "WITH ROW AS ( INSERT INTO doctors (user, title, firstname, lastname, ward, hospital, time) VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING id ) SELECT id FROM ROW";

    PreparedStatement pStatement = connection.prepareStatement(SQLCommand);
    pStatement.setObject(1, userId, Types.OTHER);
    pStatement.setString(2, doctor.getPrefix());
    pStatement.setString(3, doctor.getFirstName());
    pStatement.setString(4, doctor.getLastName());
    pStatement.setString(5, doctor.getWard());
    pStatement.setString(6, doctor.getHospital());
    pStatement.setArray(7, connection.createArrayOf("text", doctor.getWorkTime().toArray()));

    ResultSet result = pStatement.executeQuery();

    result.next();

    doctor.setId(result.getString("id"));

    pStatement.close();

    return doctor;
  }

}
