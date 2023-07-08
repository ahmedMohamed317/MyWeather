package com.example.myweather

import android.content.Context
import android.content.SharedPreferences
import android.graphics.PixelFormat
import android.media.MediaPlayer
import android.os.Build
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.work.CoroutineWorker
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.myweather.databinding.AlarmPopupBinding
import com.example.myweather.databinding.FragmentHomeBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.util.*

class AlertWorker(appContext: Context, workerParams: WorkerParameters) :
CoroutineWorker(appContext, workerParams)
{
    lateinit var repo:Repo
    override suspend fun doWork(): Result {
        val id = inputData.getString("ID")
        val apiClient = ApiClient()
        repo = Repo.getInstance(apiClient, ConcreteLocalSource.getInstance(applicationContext))
        val sharedPreferences: SharedPreferences? = applicationContext.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        val language = sharedPreferences?.getString("language", "en").toString()
        val unit = sharedPreferences?.getString("units", "metric").toString()

        return withContext(Dispatchers.IO) {
            if (id != null) {
                try {
                    val alertEntity = repo.getAlertWithId(id)
                    val response = apiClient.getWeather(alertEntity.lat.toString(),alertEntity.lon.toString(),unit,language)
                    if (response.isSuccessful) {
                        val alerts = response.body()?.alerts
                        if (alerts != null) {
                            var description : String= ""
                            val alertsEvent: String = buildString {
                                for ((index, a) in alerts.withIndex()) {
                                    if (index >= 0) {
                                        if (index ==0)
                                            description = a.description
                                        append("Event : " + a.event)
                                        append("\n")
                                        append("Sender : " + a.sender_name)
                                        append("\n")
                                        if (a.description.isEmpty()) {
                                            append("Description: $description")
                                        } else {
                                            append("Description: ${a.description}")
                                        }
                                        append("\n")
                                    }

                                }
                            }
                            when (alertEntity.kind) {
                                AlertKind.ALARM -> createAlarm(applicationContext, alertsEvent)
                                AlertKind.NOTIFICATION -> sendNotification(applicationContext, alertsEvent)
                            }
                        } else {
                            getAddress(
                                applicationContext, alertEntity.lon, alertEntity.lat, Locale(
                                    getLanguageLocale()
                                )
                            ) {
                                when (alertEntity.kind) {

                                    AlertKind.ALARM -> runBlocking {
                                        createAlarm(
                                            applicationContext, applicationContext.getString(R.string.weather_is_fine)
                                            )


                                    }
                                    AlertKind.NOTIFICATION -> sendNotification(
                                        applicationContext,
                                        applicationContext.getString(R.string.weather_is_fine)
                                        )

                                }

                            }

                        }
                        removeFromDataBaseAndDismiss(repo, alertEntity, applicationContext)
                        Result.success()
                    } else {
                        Result.retry()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.d("TAG", "doWork: $e")
                    Result.failure()
                }
            } else {
                Result.failure()
            }
        }


    }

    private suspend fun createAlarm(context: Context, message: String) {
        val mediaPlayer = MediaPlayer.create(context, R.raw.ringtone)
        var _binding = AlarmPopupBinding.inflate(LayoutInflater.from(applicationContext),null , false)
        val view: View = _binding.root
        val LAYOUT_FLAG =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else WindowManager.LayoutParams.TYPE_PHONE
        val layoutParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            LAYOUT_FLAG,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )
        layoutParams.gravity = Gravity.CENTER


        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

        withContext(Dispatchers.Main) {
            windowManager.addView(view, layoutParams)
            view.visibility = View.VISIBLE
            _binding.textViewMessage.text = message
        }

        mediaPlayer.start()
        mediaPlayer.isLooping = true
        _binding.buttonClose.setOnClickListener {
            mediaPlayer?.release()
            windowManager.removeView(view)
        }
    }

    private suspend fun removeFromDataBaseAndDismiss(
        repo: Repo,
        alertPojo: AlertPojo,
        appContext: Context
    ) {

        val _Day_TIME_IN_MILLISECOND = 24*60*60*1000L
        val now = Calendar.getInstance().timeInMillis
        if((alertPojo.end -  now)  < _Day_TIME_IN_MILLISECOND){ // condition that the time of alarm ended
            WorkManager.getInstance(appContext).cancelAllWorkByTag(alertPojo.id)
            repo.removeFromAlerts(alertPojo)
        }

    }
}