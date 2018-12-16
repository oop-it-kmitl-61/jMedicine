package notification.libraries;

import notification.core.NSUserNotification;

public interface NSUserNotificationCenterDelegate {

  /**
   * Sent to the delegate when a user clicks on a notification in the notification center. This
   * would be a good time to take action in response to user interacting with a specific
   * notification.
   *
   * @param paramNSUserNotification A notification that can be scheduled for display in the
   * notification center.
   */
  void didActivateNotification(NSUserNotification paramNSUserNotification);

  /**
   * Sent to the delegate when a notification delivery date has arrived. At this time, the
   * notification has either been presented to the user or the notification center has decided not
   * to present it because your application was already frontmost.
   *
   * @param paramNSUserNotification A notification that can be scheduled for display in the
   * notification center.
   */
  void didDeliverNotification(NSUserNotification paramNSUserNotification);


  /**
   * Sent to the delegate when the Notification Center has decided not to present your notification,
   * for example when your application is front most. If you want the notification to be displayed
   * anyway, return YES.
   *
   * @param paramNSUserNotification A notification that can be scheduled for display in the
   * notification center.
   */
  byte shouldPresentNotification(NSUserNotification paramNSUserNotification);
}
