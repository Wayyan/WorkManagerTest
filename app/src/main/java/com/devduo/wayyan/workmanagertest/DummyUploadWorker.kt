package com.devduo.wayyan.workmanagertest

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import kotlinx.coroutines.delay
import java.lang.Exception
import java.text.SimpleDateFormat

class DummyUploadWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
    companion object {
        const val KEY_SAMPLE_OUTPUT = "key_output"
    }

    override fun doWork(): Result {
        try {
            val startMs = System.currentTimeMillis()
            val inputData = inputData.getInt(MainActivity.KEY_SAMPLE_INPUT, 3000)
            for (i: Int in 0 until inputData) {
                Log.i("JUST", "Uploading $i")
            }

            val takeTime = System.currentTimeMillis() - startMs
            val outputData = Data.Builder()
                    .putString(KEY_SAMPLE_OUTPUT, ("Took $takeTime ms to execute."))
                    .build()
            Toast.makeText(applicationContext, "Took $takeTime ms to execute.", Toast.LENGTH_SHORT).show()
            return Result.success(outputData)
        } catch (e: Exception) {
            return Result.failure()
        }
    }
}