//package kr.ac.duksung.birth.service;
//
//import android.app.Notification;
//import android.app.NotificationChannel;
//import android.app.NotificationManager;
//import android.app.Service;
//import android.content.Intent;
//import android.os.Build;
//import android.os.Handler;
//import android.os.IBinder;
//import android.os.Looper;
//import android.util.Log;
//
//import androidx.core.app.NotificationCompat;
//
//public class BluetoothService extends Service {
//    private Handler mHandler = new Handler(Looper.getMainLooper());
//    private static final String TAG = "MyService";
//
//    public BluetoothService() {
//    }
//
//    @Override
//    public void onCreate() {
//        super.onCreate();
//        Log.d(TAG, "onCreate() called");
//        createNotificationChannel();
//        startForeground(NOTIFICATION_ID, buildNotification());
//    }
//
//    private void createNotificationChannel() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationChannel serviceChannel = new NotificationChannel(
//                    CHANNEL_ID,
//                    "MyBackgroundService Channel",
//                    NotificationManager.IMPORTANCE_DEFAULT // Change this to IMPORTANCE_DEFAULT
//            );
//            NotificationManager manager = getSystemService(NotificationManager.class);
//            manager.createNotificationChannel(serviceChannel);
//        }
//    }
//
//    private Notification buildNotification() {
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
//                .setContentTitle("MyBackgroundService")
//                .setContentText("Running in the background")
//                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
//        return builder.build();
//    }
//
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        Log.d(TAG, "onStartCommand() called");
//
//        if (intent == null) {
//            return Service.START_STICKY; // Keep the service running even if it's killed by the system
//        } else {
//            // intent is not null
//            // Extract data passed from the activity through the intent (if exists)
//            String command = intent.getStringExtra("command");
//            String name = intent.getStringExtra("name");
//            // etc..
//
//            Log.d(TAG, "Received data: " + command);
//        }
//
//        return super.onStartCommand(intent, flags, startId);
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//    }
//
//    @Override
//    public IBinder onBind(Intent intent) {
//        // TODO: Return the communication channel to the service.
//        throw new UnsupportedOperationException("Not yet implemented");
//    }
//
//    private static final String CHANNEL_ID = "MyBackgroundServiceChannel";
//    private static final int NOTIFICATION_ID = 123;
//    private static final int MESSAGE_SEND_INTERVAL = 1000; // 1 second
//}
