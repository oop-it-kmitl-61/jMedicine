package core;

public class DoctorUtil {

  private static String[] prefixes = {"นพ.", "พญ.", "ศ.นพ", "ผศ.นพ"};

  public static String[] getPrefixes() {
    return prefixes;
  }

  public static int getPrefixIndex(String prefix) {
    for (int i = 0; i < prefixes.length; i++) {
      if (prefixes[i].equals(prefix)) {
        return i;
      }
    }
    return -1;
  }

  public static String getDoctorFullName(Doctor doctor) {
    return doctor.getPrefix() + " " + doctor.getFirstName() + " " + doctor.getLastName();
  }
}
