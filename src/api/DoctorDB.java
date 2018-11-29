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

      ArrayList<String> workDay = Arrays.stream((Object[]) result.getArray("workDay").getArray())
          .map(Object::toString).collect(Collectors.toCollection(ArrayList::new));

      ArrayList<String> timeStart = Arrays
          .stream((Object[]) result.getArray("timeStart").getArray())
          .map(Object::toString).collect(Collectors.toCollection(ArrayList::new));

      ArrayList<String> timeEnd = Arrays.stream((Object[]) result.getArray("timeEnd").getArray())
          .map(Object::toString).collect(Collectors.toCollection(ArrayList::new));

      for (int i = 0; i < workDay.size(); i++) {
        ArrayList<String> temp = new ArrayList<>();
        temp.add(workDay.get(i));
        temp.add(timeStart.get(i));
        temp.add(timeEnd.get(i));
        time.add(temp);
      }

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

    ArrayList<String> workDay = Arrays.stream((Object[]) result.getArray("workDay").getArray())
        .map(Object::toString).collect(Collectors.toCollection(ArrayList::new));

    ArrayList<String> timeStart = Arrays.stream((Object[]) result.getArray("timeStart").getArray())
        .map(Object::toString).collect(Collectors.toCollection(ArrayList::new));

    ArrayList<String> timeEnd = Arrays.stream((Object[]) result.getArray("timeEnd").getArray())
        .map(Object::toString).collect(Collectors.toCollection(ArrayList::new));

    for (int i = 0; i < workDay.size(); i++) {
      ArrayList<String> temp = new ArrayList<>();
      temp.add(workDay.get(i));
      temp.add(timeStart.get(i));
      temp.add(timeEnd.get(i));
      time.add(temp);
    }

    return new Doctor(result.getString("id"), result.getString("title"),
        result.getString("firstname"), result.getString("lastname"), result.getString("ward"),
        result.getString("hospital"), time);

  }

  public static Doctor addDoctor(Doctor doctor, String userId) throws SQLException {

    ArrayList workDay = new ArrayList();
    ArrayList timeStart = new ArrayList();
    ArrayList timeEnd = new ArrayList();
    if (doctor.getWorkTime() != null) {
      for (ArrayList t : doctor.getWorkTime()) {
        workDay.add(t.get(0));
        timeStart.add(t.get(1));
        timeEnd.add(t.get(2));
      }
    }

    String SQLCommand = "WITH ROW AS ( INSERT INTO doctors (\"user\", title, firstname, lastname, ward, hospital, \"workDay\", \"timeStart\", \"timeEnd\") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id ) SELECT id FROM ROW";

    PreparedStatement pStatement = connection.prepareStatement(SQLCommand);
    pStatement.setObject(1, userId, Types.OTHER);
    pStatement.setString(2, doctor.getPrefix());
    pStatement.setString(3, doctor.getFirstName());
    pStatement.setString(4, doctor.getLastName());
    pStatement.setString(5, doctor.getWard());
    pStatement.setString(6, doctor.getHospital());
    pStatement.setArray(7, connection.createArrayOf("text", workDay.toArray()));
    pStatement.setArray(8, connection.createArrayOf("text", timeStart.toArray()));
    pStatement.setArray(9, connection.createArrayOf("text", timeEnd.toArray()));

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
    ArrayList workDay = new ArrayList();
    ArrayList timeStart = new ArrayList();
    ArrayList timeEnd = new ArrayList();
    for (ArrayList t : doctor.getWorkTime()) {
      workDay.add(t.get(0));
      timeStart.add(t.get(1));
      timeEnd.add(t.get(2));
    }

    String SQLCommand = "UPDATE doctors SET title = ?, firstname = ?, lastname = ?, ward = ?, hospital = ?, \"workDay\" = ?, \"timeStart\" = ?, \"timeEnd\" = ? WHERE id = ?";

    PreparedStatement pStatement = connection.prepareStatement(SQLCommand);

    pStatement.setString(1, doctor.getPrefix());
    pStatement.setString(2, doctor.getFirstName());
    pStatement.setString(3, doctor.getLastName());
    pStatement.setString(4, doctor.getWard());
    pStatement.setString(5, doctor.getHospital());
    pStatement.setArray(6, connection.createArrayOf("text", workDay.toArray()));
    pStatement.setArray(7, connection.createArrayOf("text", timeStart.toArray()));
    pStatement.setArray(8, connection.createArrayOf("text", timeEnd.toArray()));
    pStatement.setObject(9, doctor.getId(), Types.OTHER);

    pStatement.executeUpdate();

    pStatement.close();

    return doctor;
  }

  public static void removeDoctor(Doctor doctor) throws SQLException {
    String SQLCommand = "DELETE FROM doctors WHERE \"id\" = ?";

    PreparedStatement pStatement = connection.prepareStatement(SQLCommand);

    pStatement.setObject(1, doctor.getId(), Types.OTHER);

    pStatement.executeUpdate();

    pStatement.close();
  }


}
