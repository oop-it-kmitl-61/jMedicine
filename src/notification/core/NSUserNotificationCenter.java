package notification.core;

import com.sun.jna.Callback;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import java.util.ArrayList;
import notification.libraries.NSUserNotificationCenterCInterface;
import notification.libraries.NSUserNotificationCenterDelegate;


public class NSUserNotificationCenter {

  //	final static Logger logger = Logger.getLogger(NSUserNotification.class);
  private static NSUserNotificationCenterCInterface library = null;
  private static NSUserNotificationCenter instance = null;

  private static Callback defaultActivateCallback = new Callback() {
    public void callback(Pointer n) {
      System.out.println("ActivateCallback. No delegate defined.");
    }
  };

  private static Callback defaultDeliverCallback = new Callback() {
    public void callback(Pointer n) {
      System.out.println("DeliverCallback. No delegate defined.");
    }
  };

  private static Callback defaultPresentCallback = new Callback() {
    public byte callback(Pointer n) {
      System.out.println("PresentCallback. No delegate defined.");
      return 0;
    }
  };

  private NSUserNotificationCenter() {
    if (library == null) {
      library = (NSUserNotificationCenterCInterface) Native
          .loadLibrary("OSXNotification", NSUserNotificationCenterCInterface.class);
    }
  }

  public static NSUserNotificationCenter getInstance() {
    if (instance == null) {
      instance = new NSUserNotificationCenter();
      instance.setDelegate(null);
    }
    return instance;
  }

  public ArrayList<NSUserNotification> getDeliveredNotifications() {
    int count = library.NSUserNotificationCenterGetDeliveredNotificationsCount();
    ArrayList mNotifications = new ArrayList();
    for (int i = 0; i < count; i++) {
      Pointer tmp = library.NSUserNotificationCenterGetDeliveredNotification(i);
      mNotifications.add(new NSUserNotification(tmp));
    }
    return mNotifications;
  }

  public void deliverNotification(NSUserNotification notice) {
    library.NSUserNotificationCenterDeliverNotification(notice.getPointer());
  }

  public ArrayList<NSUserNotification> getScheduledNotifications() {
    int count = library.NSUserNotificationCenterGetScheduledNotificationsCount();
    ArrayList mNotifications = new ArrayList();
    for (int i = 0; i < count; i++) {
      Pointer tmp = library.NSUserNotificationCenterGetScheduledNotification(i);
      mNotifications.add(new NSUserNotification(tmp));
    }
    return mNotifications;
  }

  public void scheduleNotification(NSUserNotification notice) {
    library.NSUserNotificationCenterScheduleNotification(notice.getPointer());
  }

  public void removeScheduledNotification(NSUserNotification notice) {
    library.NSUserNotificationCenterRemoveScheduledNotification(notice.getPointer());
  }

  public void removeDeliveredNotification(NSUserNotification notice) {
    library.NSUserNotificationCenterRemoveDeliveredNotification(notice.getPointer());
  }

  public void removeAllDeliveredNotifications() {
    library.NSUserNotificationCenterRemoveAllDeliveredNotifications();
  }

  public void setDefaultDelegate() {
    library.NSUserNotificationCenterSetDefaultDelegate();
  }

  public void setDelegate(final NSUserNotificationCenterDelegate delegate) {
    Callback activate;
    Callback deliver;
    Callback present;
    if (delegate != null) {
      activate = new Callback() {
        public void callback(Pointer n) {
          delegate.didActivateNotification(new NSUserNotification(n));
        }
      };
      deliver = new Callback() {
        public void callback(Pointer n) {
          delegate.didDeliverNotification(new NSUserNotification(n));
        }
      };
      present = new Callback() {
        public byte callback(Pointer n) {
          return delegate.shouldPresentNotification(new NSUserNotification(n));
        }
      };
    } else {
      activate = defaultActivateCallback;
      deliver = defaultDeliverCallback;
      present = defaultPresentCallback;
    }
    library.NSUserNotificationCenterSetDelegate(activate, deliver, present);
  }
}
