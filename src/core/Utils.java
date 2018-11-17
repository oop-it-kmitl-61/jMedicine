package core;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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

}
