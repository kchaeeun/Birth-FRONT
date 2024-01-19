package kr.ac.duksung.birth

import android.app.Application
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class BluetoothApplication : Application(), LifecycleObserver {
    override fun onCreate() {
        super.onCreate()
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onAppBackgrounded() {
        val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        val savedInfo = sharedPreferences.getString("num", null)
        println("백그라운드에서 실행중... Info : $savedInfo")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onAppForegrounded() {
        val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        val savedInfo = sharedPreferences.getString("num", null)
        println("포그라운드에서 실행중... Info: $savedInfo")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onAppCreated() {
        println("check app ON_CREATE!!!!!!!!!!!!!")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onAppResumed() {
        println("check app ON_RESUME!!!!!!!!!!!!!")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onAppDestroyed() {
        println("check app ON_DESTROY!!!!!!!!!!!!!")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onAppPaused() {
        println("check app ON_PAUSE!!!!!!!!!!!!!")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_ANY)
    fun onAppAny() {
        println("check app ON_ANY!!!!!!!!!!!!!")
    }

    companion object {
        private const val BASE_URL = "http://localhost:8080/"
        private var retrofit: Retrofit? = null
        val retrofitInstance: Retrofit?
            get() {
                if (retrofit == null) {
                    retrofit = Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()
                }
                return retrofit
            }
    }
}