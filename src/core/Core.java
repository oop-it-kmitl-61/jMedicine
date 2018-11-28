package core;

/**
 * All UI components needs to fetch signed in user. This currentUser will be set inside
 * executeSignIn() at GUIUtil.java
 *
 * @author jMedicine
 * @version 0.7.0
 * @since 0.7.0
 */

public class Core {

  private static User currentUser;

  public static void setUser(User user) {
    Core.currentUser = user;
  }

  public static User getUser() {
    return currentUser;
  }
}
