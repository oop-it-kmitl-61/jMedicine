package core;

import GUI.GUIHelper;

public class AppointmentUtil {

  public static String getTitle(Appointment app) {
    String date = GUIHelper.formatDMYFull.format(app.getTimeStart());
    String timeStart = GUIHelper.formatHM.format(app.getTimeStart());
    String timeEnd = GUIHelper.formatHM.format(app.getTimeStop());
    return date + " เวลา " + timeStart + " น. - " + timeEnd + " น.";
  }
}
