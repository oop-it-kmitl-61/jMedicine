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

/**
 * Doctor database class help GUI connect to database using JDBC
 */
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

      ArrayList<ArrayList> time = new ArrayList<>();

      Arrays.stream((Object[]) result.getArray("time").getArray()).forEach(obj -> {
        ArrayList<String> t = new ArrayList<>();
        t.addAll(Arrays.asList((String[]) obj));
        time.add(t);
      });

      results.add(
          new Doctor(result.getString("id"), result.getString("title"),
              result.getString("firstname"), result.getString("lastname"), result.getString("ward"),
              result.getString("hospital"), time)
      );
    }

    return results;
  }

  public static Doctor getDoctorInfo(String doctorId) throws SQLException {
    String SQLCommand = "SELECT * FROM doctors WHERE \"id\" = ?";

    PreparedStatement pStatement = connection.prepareStatement(SQLCommand);
    pStatement.setObject(1, doctorId, Types.OTHER);

    ResultSet result = pStatement.executeQuery();

    result.next();

    ArrayList<ArrayList> time = new ArrayList<>();

    Arrays.stream((Object[]) result.getArray("time").getArray()).forEach(obj -> {
      ArrayList<String> t = new ArrayList<>();
      t.addAll(Arrays.asList((String[]) obj));
      time.add(t);
    });

    return new Doctor(result.getString("id"), result.getString("title"),
        result.getString("firstname"), result.getString("lastname"), result.getString("ward"),
        result.getString("hospital"), time);

  }

  public static Doctor addDoctor(Doctor doctor, String userId) throws SQLException {

    ArrayList time = new ArrayList();
    for (ArrayList t : doctor.getWorkTime()) {
      time.add(connection.createArrayOf("text", t.toArray()));
    }

    String SQLCommand = "WITH ROW AS ( INSERT INTO doctors (user, title, firstname, lastname, ward, hospital, time) VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING id ) SELECT id FROM ROW";

    PreparedStatement pStatement = connection.prepareStatement(SQLCommand);
    pStatement.setObject(1, userId, Types.OTHER);
    pStatement.setString(2, doctor.getPrefix());
    pStatement.setString(3, doctor.getFirstName());
    pStatement.setString(4, doctor.getLastName());
    pStatement.setString(5, doctor.getWard());
    pStatement.setString(6, doctor.getHospital());
    pStatement.setArray(7, connection.createArrayOf("text", time.toArray()));

    ResultSet result = pStatement.executeQuery();

    result.next();

    doctor.setId(result.getString("id"));

    pStatement.close();

    return doctor;
  }

  /**
   * Doctor database interface use to update doctor data
   *
   * @param doctor doctor object with data you need to update
   * @return Doctor object which updated
   */

  public static Doctor updateDoctor(Doctor doctor) throws SQLException {
    ArrayList time = new ArrayList();
    for (ArrayList t : doctor.getWorkTime()) {
      time.add(connection.createArrayOf("text", t.toArray()));
    }

    String SQLCommand = "UPDATE doctors SET title = ?, firstname = ?, lastname = ?, ward = ?, hospital = ?, time = ? WHERE id = ?";

    PreparedStatement pStatement = connection.prepareStatement(SQLCommand);

    pStatement.setString(1, doctor.getPrefix());
    pStatement.setString(2, doctor.getFirstName());
    pStatement.setString(3, doctor.getLastName());
    pStatement.setString(4, doctor.getWard());
    pStatement.setString(5, doctor.getHospital());
    pStatement.setArray(6, connection.createArrayOf("text", time.toArray()));
    pStatement.setObject(7, doctor.getId(), Types.OTHER);

    pStatement.executeUpdate();

    pStatement.close();

    return doctor;
  }

  public static void removeDoctor(Doctor doctor) throws SQLException {
    String SQLCommand = "DELETE FROM doctors WHERE id = ?";

    PreparedStatement pStatement = connection.prepareStatement(SQLCommand);

    pStatement.setObject(1, doctor.getId(), Types.OTHER);

    pStatement.executeUpdate();

    pStatement.close();
  }


}
