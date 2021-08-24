package com.devduo.wayyan.workmanagertest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.work.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    companion object {
        const val KEY_SAMPLE_INPUT = "key_input"
    }

    private lateinit var tvStatus: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvStatus = findViewById(R.id.tv_status)
        findViewById<Button>(R.id.btn_onetime_work).setOnClickListener {
            setOneTimeWorkRequest()
        }

        findViewById<Button>(R.id.btn_onetime_work_constraints).setOnClickListener {
            setOneTimeWorkRequestWithConstrains()
        }

        findViewById<Button>(R.id.btn_sequential_chain).setOnClickListener {
            setSequentialChaining()
        }

        findViewById<Button>(R.id.btn_parallel_chain).setOnClickListener {
            setParallelChaining()
        }

        findViewById<Button>(R.id.btn_periodic_work).setOnClickListener {
            setPeriodicWork()
        }
    }

    private fun setOneTimeWorkRequest() {
        val workManager = WorkManager.getInstance(applicationContext)

        val inputData = Data.Builder()
                .putInt(KEY_SAMPLE_INPUT, 1000)
                .build()

        val uploadWorker = OneTimeWorkRequest.Builder(DummyUploadWorker::class.java)
                .setInputData(inputData)
                .build()
        workManager.enqueue(uploadWorker)
        workManager.getWorkInfoByIdLiveData(uploadWorker.id).observe(this, {
            tvStatus.text = it.state.name

            if (it.state.isFinished) {
                val data = it.outputData
                val message = data.getString(DummyUploadWorker.KEY_SAMPLE_OUTPUT)
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setOneTimeWorkRequestWithConstrains() {
        val workManager = WorkManager.getInstance(applicationContext)
        val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
        val uploadWorker = OneTimeWorkRequest.Builder(DummyUploadWorker::class.java)
                .setConstraints(constraints)
                .build()
        workManager.enqueue(uploadWorker)
        workManager.getWorkInfoByIdLiveData(uploadWorker.id).observe(this, Observer {
            tvStatus.text = it.state.name
        })
    }

    private fun setSequentialChaining() {
        val workManager = WorkManager.getInstance(applicationContext)
        val filterWorker = OneTimeWorkRequest.Builder(DummyFilterWorker::class.java)
                .build()
        val compressWorker = OneTimeWorkRequest.Builder(DummyCompressWorker::class.java)
                .build()
        val uploadWorker = OneTimeWorkRequest.Builder(DummyUploadWorker::class.java)
                .build()
        workManager.beginWith(filterWorker)
                .then(compressWorker)
                .then(uploadWorker)
                .enqueue()

        workManager.getWorkInfoByIdLiveData(uploadWorker.id).observe(this, Observer {
            tvStatus.text = it.state.name
        })
    }

    private fun setParallelChaining() {
        val workManager = WorkManager.getInstance(applicationContext)
        val downloadWorker = OneTimeWorkRequest.Builder(DummyDownloadWorker::class.java)
                .build()
        val filterWorker = OneTimeWorkRequest.Builder(DummyFilterWorker::class.java)
                .build()
        val compressWorker = OneTimeWorkRequest.Builder(DummyCompressWorker::class.java)
                .build()
        val uploadWorker = OneTimeWorkRequest.Builder(DummyUploadWorker::class.java)
                .build()

        val parallelWork = mutableListOf<OneTimeWorkRequest>()
        parallelWork.add(downloadWorker)
        parallelWork.add(filterWorker)

        workManager.beginWith(parallelWork)
                .then(compressWorker)
                .then(uploadWorker)
                .enqueue()

        workManager.getWorkInfoByIdLiveData(uploadWorker.id).observe(this, Observer {
            tvStatus.text = it.state.name
        })
    }

    private fun setPeriodicWork() {
        val workManager = WorkManager.getInstance(applicationContext)
        val periodicWorkRequest = PeriodicWorkRequest.Builder(
                DummyUploadWorker::class.java,
                16, TimeUnit.MINUTES
        ).build()

        workManager.enqueue(periodicWorkRequest)
    }
}