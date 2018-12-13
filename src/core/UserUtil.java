package core;

/**
 * Utility class for User
 *
 * @author jMedicine
 * @version 0.7.2
 * @since 0.7.2
 */

public class UserUtil {

  private static String[] prefixes = {"ไม่ระบุ", "ด.ช.", "ด.ญ.", "นาย", "นาง", "นางสาว"};
  private static String[] genders = {"ไม่ระบุ", "ชาย", "หญิง", "อื่น ๆ"};

  public static String[] getPrefixes() {
    return prefixes;
  }

  public static String[] getGenders() {
    return genders;
  }

  public static int getPrefixIndex(String prefix) {
    for (int i = 0; i < prefixes.length; i++) {
      if (prefixes[i].equals(prefix)) {
        return i;
      }
    }
    return 0;
  }

  public static int getGenderIndex(String gender) {
    for (int i = 0; i < genders.length; i++) {
      if (genders[i].equals(gender)) {
        return i;
      }
    }
    return 0;
  }
}
