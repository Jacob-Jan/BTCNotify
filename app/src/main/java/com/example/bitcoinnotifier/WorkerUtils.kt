package com.bitcoin.btcnotify

import android.content.Context
import androidx.work.*
import java.util.concurrent.TimeUnit

object WorkerUtils {

    fun startPriceWorker(context: Context, apiKey: String) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED) // Ensure the worker only runs when connected
            .build()

        val workRequest = PeriodicWorkRequestBuilder<PriceCheckWorker>(
            15, TimeUnit.MINUTES // Minimum allowed interval
        )
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "BitcoinPriceChecker",
            ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE, // Cancel existing and start fresh
            workRequest
        )
    }
}
