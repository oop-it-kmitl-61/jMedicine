package notification;

import java.util.concurrent.TimeUnit;

public class NotificationFactory {

	public static void showNotification(String subtitle) {
		BasicNotification notification = getNotification("jMedicine", subtitle, "");
		notification.show(2000, TimeUnit.MILLISECONDS);
	}

	public static void showNotification(String title, String subtitle, String message, long duration) {
		BasicNotification notification = getNotification(title, subtitle, message);
		notification.show(duration, TimeUnit.MILLISECONDS);
	}

	public static BasicNotification getNotification(String title, String subtitle, String message) {
		BasicNotification notification = new Notification();
		notification.setTitle(title);
		notification.setMessage(message);
		notification.setSubtitle(subtitle);
		notification.setSoundName("Glass");
		return notification;
	}
}
