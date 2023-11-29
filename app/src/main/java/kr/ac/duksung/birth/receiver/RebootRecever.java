//package kr.ac.duksung.birth.receiver;
//
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.os.Build;
//
//import kr.ac.duksung.birth.service.RealService;
//import kr.ac.duksung.birth.service.RestartService;
//
//public class RebootRecever extends BroadcastReceiver {
//    @Override
//    public void onReceive(Context context, Intent intent) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            Intent in = new Intent(context, RestartService.class);
//            context.startForegroundService(in);
//        } else {
//            Intent in = new Intent(context, RealService.class);
//            context.startService(in);
//        }
//    }
//}
