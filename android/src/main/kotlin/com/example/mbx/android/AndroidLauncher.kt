package com.example.mbx.android

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.badlogic.gdx.backends.android.AndroidApplication
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration
import com.example.mbx.MountainBikeGame
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter

class AndroidLauncher : AndroidApplication() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installCrashLogger()

        val config = AndroidApplicationConfiguration().apply {
            useAccelerometer = false
            useCompass = false
        }
        initialize(MountainBikeGame(), config)
    }

    /**
     * Writes any uncaught crash's full stack trace to a plain text file at
     * getExternalFilesDir(null)/crash_log.txt (no permission needed — this is
     * app-private external storage) AND to Logcat under tag "MBX_CRASH", so the
     * real crash reason can be retrieved two ways:
     *   1. adb logcat -s MBX_CRASH
     *   2. adb pull /sdcard/Android/data/com.example.mbx/files/crash_log.txt
     */
    private fun installCrashLogger() {
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            try {
                val sw = StringWriter()
                throwable.printStackTrace(PrintWriter(sw))
                val trace = sw.toString()

                Log.e("MBX_CRASH", trace)

                val logFile = File(getExternalFilesDir(null), "crash_log.txt")
                logFile.writeText(trace)

                runOnUiThread {
                    Toast.makeText(
                        this,
                        "Crash saved to Android/data/${packageName}/files/crash_log.txt",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } catch (ignored: Exception) {
                // Never let the crash logger itself crash the crash handler
            }
            defaultHandler?.uncaughtException(thread, throwable)
        }
    }
}
