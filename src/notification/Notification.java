package notification;

import java.util.concurrent.TimeUnit;
import notification.core.NSUserNotification;
import notification.core.NSUserNotificationCenter;

public class Notification implements BasicNotification<NSUserNotification> {

  private NSUserNotification notification;

  public Notification() {
    notification = new NSUserNotification();
  }

  @Override
  public void setSubtitle(String subtitle) {
    notification.setSubtitle(subtitle);
  }

  @Override
  public void setSoundName(String name) {
    notification.setSoundName(name);
  }

  @Override
  public String getTitle() {
    return notification.getTitle();
  }

  @Override
  public void setTitle(String title) {
    notification.setTitle(title);
  }

  @Override
  public String getMessage() {
    return notification.getText();
  }

  @Override
  public void setMessage(String message) {
    notification.setText(message);
  }

  @Override
  public void show(long duration, TimeUnit unit) {
    try {
      NSUserNotificationCenter.getInstance().deliverNotification(notification);
      Thread.sleep(unit.toMillis(duration));
    } catch (InterruptedException e) {
      e.printStackTrace();
    } finally {
      notification.close();
    }
  }

  @Override
  public NSUserNotification getRoot() {
    return notification;
  }
}
