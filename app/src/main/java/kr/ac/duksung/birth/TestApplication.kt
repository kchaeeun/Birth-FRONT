package kr.ac.duksung.birth

import android.app.Application
import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner

class TestApplication: Application(), LifecycleObserver {

    override fun onCreate() {
        super.onCreate()
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onAppBackgrounded() {
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val savedInfo = sharedPreferences.getString("num", null)
        println("백그라운드에서 실행중... Info: $savedInfo")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onAppForegrounded() {
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val savedInfo = sharedPreferences.getString("num", null)
        println("포그라운드에서 실행중... Info: $savedInfo")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onAppCreated() {
        println("check app ON_CREATE!!!!!!!!!!!!!!!!!")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onAppResumed() {
        println("check app ON_RESUME!!!!!!!!!!!!!!!!!")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onAppDestroyed() {
        println("check app ON_DESTROY!!!!!!!!!!!!!!!!!")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onAppPaused() {
        println("check app ON_PAUSE!!!!!!!!!!!!!!!!!")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_ANY)
    fun onAppAny() {
        println("check app ON_ANY!!!!!!!!!!!!!!!!!")
    }
}