package api;

import static GUI.GUIHelper.formatDMYHM;
import static GUI.GUIHelper.formatHM;
import static api.DoctorDB.getDoctorInfo;

import core.Appointment;
import core.Database;
import core.Doctor;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.stream.Collectors;

public class AppointmentDB {

  private static Connection connection;

  static {
    try {
      connection = Database.getConnection();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public static ArrayList<Appointment> getAllAppointment(String userId)
      throws SQLException, ParseException {
    String SQLCommand = "SELECT * FROM appointments WHERE \"user\" = ?";

    PreparedStatement pStatement = connection.prepareStatement(SQLCommand);
    pStatement.setObject(1, userId, Types.OTHER);

    ResultSet result = pStatement.executeQuery();

    ArrayList<Appointment> results = new ArrayList<>();

    while (result.next()) {

      ArrayList<String> time = Arrays.stream((Object[]) result.getArray("time").getArray())
          .map(Object::toString).collect(Collectors.toCollection(ArrayList::new));

      String timeStart = time.get(0);
      String timeEnd = time.get(1);
      Date date = result.getDate("date");
      String note = result.getString("note");
      Doctor doctor = getDoctorInfo(result.getString("doctor"));

      results.add(
          new Appointment(result.getString("id"), date, timeStart, timeEnd, doctor, note)
      );
    }

    return results;
  }

  public static Appointment addAppointment(Appointment appointment, String userId)
      throws SQLException {
    ArrayList<String> time = new ArrayList<>();
    time.add(appointment.getTimeStart());
    time.add(appointment.getTimeStop());
    String doctorId = appointment.getDoctor().getId();

    String SQLCommand = "WITH ROW AS ( INSERT INTO appointments (\"user\", doctor, \"date\", \"time\", note) VALUES (?, ?, ?, ?, ?) RETURNING id ) SELECT id FROM ROW";

    PreparedStatement pStatement = connection.prepareStatement(SQLCommand);
    pStatement.setObject(1, userId, Types.OTHER);
    pStatement.setObject(2, doctorId, Types.OTHER);
    pStatement.setDate(3, new java.sql.Date(appointment.getDate().getTime()));
    pStatement.setArray(4, connection.createArrayOf("text", time.toArray()));
    pStatement.setString(5, appointment.getNote());

    ResultSet result = pStatement.executeQuery();

    result.next();

    appointment.setId(result.getString("id"));

    pStatement.close();

    return appointment;
  }

  public static Appointment updateAppointment(Appointment appointment) throws SQLException {
    ArrayList<String> time = new ArrayList<>();
    time.add(formatDMYHM.format(appointment.getTimeStart()));
    time.add(formatDMYHM.format(appointment.getTimeStop()));
    String doctorId = appointment.getDoctor().getId();

    String SQLCommand = "UPDATE appointments SET doctor = ?, \"time\" = ? WHERE id = ?";

    PreparedStatement pStatement = connection.prepareStatement(SQLCommand);
    pStatement.setObject(1, doctorId, Types.OTHER);
    pStatement.setArray(2, connection.createArrayOf("text", time.toArray()));
    pStatement.setObject(3, appointment.getId(), Types.OTHER);

    pStatement.executeUpdate();

    pStatement.close();

    return appointment;
  }


  /**
   * Appointment database interface use to remove appointment data
   *
   * @param appointment appointment object with data you need to update
   */
  public static void removeAppointment(Appointment appointment) throws SQLException {
    String SQLCommand = "DELETE FROM appointments WHERE id = ?";

    PreparedStatement pStatement = connection.prepareStatement(SQLCommand);

    pStatement.setObject(1, appointment.getId(), Types.OTHER);

    pStatement.executeUpdate();

    pStatement.close();
  }


}
