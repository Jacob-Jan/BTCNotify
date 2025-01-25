package com.bitcoin.btcnotify

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request

class PriceCheckWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    enum class NotificationAction {
        OPEN_APP,
        OPEN_BROWSER
    }
    override suspend fun doWork(): Result {
        val sharedPreferences = applicationContext.getSharedPreferences("APP_PREFERENCES", Context.MODE_PRIVATE)

        val multiple = sharedPreferences.getInt("MULTIPLE", 1000) // Default to 100
        val apiKey = sharedPreferences.getString("API_KEY", "")

        if (apiKey.isNullOrEmpty()) {
            return Result.failure()
        }

        val currentPrice = fetchBitcoinPrice(apiKey)
        if (currentPrice != null) {
            val lastPrice = sharedPreferences.getInt("LAST_PRICE", 0)

            if (lastPrice != 0) {
                val lastThreshold = (lastPrice / multiple) * multiple
                val currentThreshold = (currentPrice / multiple) * multiple

                // Check if we crossed a boundary
                if (currentThreshold != lastThreshold) {
                    // Determine direction and send notification
                    val direction = if (currentThreshold > lastThreshold) "upwards" else "downwards"
                    val thresholdCrossed = if (direction == "upwards") {
                        currentThreshold
                    } else {
                        currentThreshold + multiple
                    }
                    val notificationAction = if (sharedPreferences.getBoolean("NotificationActionTradingView", false)) {
                        NotificationAction.OPEN_BROWSER
                    } else {
                        NotificationAction.OPEN_APP
                    }
                    sendNotification(
                        "Bitcoin price crossed $thresholdCrossed $direction",
                        "New Price: $${currentPrice.toInt()}",
                        notificationAction
                    )
                }
            }

            sharedPreferences.edit()
                .putInt("LAST_PRICE", currentPrice)
                .putString(
                    "LAST_UPDATED",
                    java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date())
                )
                .apply()
        }

        return Result.success()
    }

    private suspend fun fetchBitcoinPrice(apiKey: String): Int? = withContext(Dispatchers.IO) {
        try {
            val client = OkHttpClient()
            val request = Request.Builder()
                .url("https://api.coingecko.com/api/v3/simple/price?ids=bitcoin&vs_currencies=usd")
                .addHeader("Authorization", "Bearer $apiKey") // Optional if API key is required
                .build()
            val response = client.newCall(request).execute()
            val body = response.body?.string()
            val price = Gson().fromJson(body, PriceResponse::class.java)
            price.bitcoin["usd"]?.toInt()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun sendNotification(title: String, message: String, type: NotificationAction) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (applicationContext.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                return
            }
        }

        val intent = when (type) {
            NotificationAction.OPEN_APP -> {
                applicationContext.packageManager.getLaunchIntentForPackage(applicationContext.packageName)
            }
            NotificationAction.OPEN_BROWSER -> {
                Intent(Intent.ACTION_VIEW, Uri.parse("https://www.tradingview.com/symbols/BTCUSD/"))
            }
        }

        intent?.let {
            it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE // FLAG_IMMUTABLE is required for Android 12+
        )

        val notificationManager = androidx.core.app.NotificationManagerCompat.from(applicationContext)
        val notification = androidx.core.app.NotificationCompat.Builder(applicationContext, "PRICE_ALERT_CHANNEL")
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.baseline_currency_bitcoin_24)
            .setPriority(androidx.core.app.NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent) // Attach the intent
            .setAutoCancel(true) // Dismiss the notification when clicked
            .build()

        notificationManager.notify(1, notification)
    }
}

data class PriceResponse(val bitcoin: Map<String, Float>)
