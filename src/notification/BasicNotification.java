package notification;

import java.util.concurrent.TimeUnit;

public interface BasicNotification<T> {

  void setTitle(String title);

  String getTitle();

  void setMessage(String message);

  String getMessage();

  void setSubtitle(String subtitle);

  void setSoundName(String name);

  void show(long duration, TimeUnit unit);

  T getRoot();
}
