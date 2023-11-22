package kr.ac.duksung.birth;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BluetoothApplication extends Application implements LifecycleObserver {

    private static final String BASE_URL = "http://localhost:8080/";
    private static Retrofit retrofit;

    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onAppBackgrounded() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String savedInfo = sharedPreferences.getString("num", null);
        System.out.println("백그라운드에서 실행중... Info : " + savedInfo);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onAppForegrounded() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String savedInfo = sharedPreferences.getString("num", null);
        System.out.println("포그라운드에서 실행중... Info: " + savedInfo);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    public void onAppCreated() {
        System.out.println("check app ON_CREATE!!!!!!!!!!!!!");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onAppResumed() {
        System.out.println("check app ON_RESUME!!!!!!!!!!!!!");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onAppDestroyed() {
        System.out.println("check app ON_DESTROY!!!!!!!!!!!!!");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onAppPaused() {
        System.out.println("check app ON_PAUSE!!!!!!!!!!!!!");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_ANY)
    public void onAppAny() {
        System.out.println("check app ON_ANY!!!!!!!!!!!!!");
    }
}
