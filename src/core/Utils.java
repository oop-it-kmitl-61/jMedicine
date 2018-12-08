package core;

import static GUI.GUIHelper.formatTimestamp;
import static GUI.GUIHelper.formatYMD;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.Date;

public class Utils {

  public static String sha256(String text) throws NoSuchAlgorithmException {
    MessageDigest mDigest = MessageDigest.getInstance("SHA-256");
    byte[] result = mDigest.digest(text.getBytes());
    StringBuffer sb = new StringBuffer();
    for (byte aResult : result) {
      sb.append(Integer.toString((aResult & 0xff) + 0x100, 16).substring(1));
    }

    return sb.toString();
  }

  public static long stringToTimestamp(String string) {
    try {
      long timestamp = formatTimestamp.parse(string).getTime();
      return timestamp;
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return 0;
  }

  public static String timestampToString(String timestamp) {
    try {
      Date date = formatTimestamp.parse(timestamp);
      return formatYMD.format(date);
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return null;
  }

}
