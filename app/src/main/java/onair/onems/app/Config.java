package onair.onems.app;

public class Config {
    // global topic to receive app wide push notifications
    public static final String TOPIC_GLOBAL = "global";

    // broadcast receiver intent filters
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String PUSH_NOTIFICATION = "pushNotification";

    public static final String SHARED_PREF = "ah_firebase";

    //Notification channel id for creating notification channel
    public static final String NOTIFICATION_CHANNEL = "DEFAULT_CHANNEL";
}
