package core;

import GUI.GUIHelper;

public class AppointmentUtil {

  public static String getTitle(Appointment app) {
    String date = GUIHelper.formatDMYFull.format(app.getDate());
    String timeStart = app.getTimeStart();
    String timeEnd = app.getTimeStop();
    return date + " เวลา " + timeStart + " น. - " + timeEnd + " น.";
  }
}
