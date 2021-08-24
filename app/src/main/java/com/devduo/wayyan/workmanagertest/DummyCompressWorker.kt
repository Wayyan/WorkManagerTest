package com.devduo.wayyan.workmanagertest

import android.content.Context
import android.util.Log
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import kotlinx.coroutines.delay
import java.lang.Exception
import java.text.SimpleDateFormat

class DummyCompressWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
    override fun doWork(): Result {
        try {
            for (i: Int in 0 until 301) {
                Log.i("JUST", "Compressing $i")
            }

            return Result.success()
        } catch (e: Exception) {
            return Result.failure()
        }
    }
}