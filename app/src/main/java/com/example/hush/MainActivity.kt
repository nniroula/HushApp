package com.example.hush

import android.Manifest
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.media.MediaPlayer
import android.os.Build
import android.support.annotation.NonNull
import android.support.annotation.RequiresApi
import android.support.v4.content.ContextCompat
import android.widget.TextView
import android.widget.Toast

private const val RECORD_AUDIO_REQUEST_CODE =123
class MainActivity : AppCompatActivity(), MediaPlayer.OnCompletionListener {
    
    lateinit var recorder: Recorder
    lateinit var player: Player
    lateinit var button1: Button
    lateinit var button2: Button
    lateinit var button3: Button
    lateinit var tv1: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getPermissionToRecordAudio()
        }

        tv1 = findViewById(R.id.tv1) as TextView
        button1 = findViewById(R.id.btnStart) as Button
        button2 = findViewById(R.id.btnStop) as Button
        button3 = findViewById(R.id.btnPlay) as Button

        button1.setOnClickListener {
            recorder = Recorder()
            recorder.setup()
            recorder.record()
            tv1.text = "Recording"
            button1.setEnabled(false)
            button2.setEnabled(true)
        }

        button2.setOnClickListener {
            recorder.stop()
            player = Player()
            player.setup(recorder.file)
            player.player.setOnCompletionListener(this)

            button1.setEnabled(true)
            button2.setEnabled(false)
            button3.setEnabled(true)
            tv1.text = "Ready to play"
        }

        button3.setOnClickListener {
            player.play()
            button1.setEnabled(false)
            button2.setEnabled(false)
            button3.setEnabled(false)
            tv1.setText("Playing")
        }
    }


    override fun onCompletion(mp: MediaPlayer) {
        button1.setEnabled(true)
        button2.setEnabled(true)
        button3.setEnabled(true)
        tv1.setText("Ready")
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    fun getPermissionToRecordAudio() {
        // 1) Use the support library version ContextCompat.checkSelfPermission(...) to avoid
        // checking the build version since Context.checkSelfPermission(...) is only available
        // in Marshmallow
        // 2) Always check for permission (even if permission has already been granted)
        // since the user can revoke permissions at any time through Settings
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                        !== PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        !== PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        !== PackageManager.PERMISSION_GRANTED)
        ) {
            // The permission is NOT already granted.
            // Check if the user has been asked about this permission already and denied
            // it. If so, we want to give more explanation about why the permission is needed.
            // Fire off an async request to actually get the permission
            // This will show the standard permission request dialog UI
            requestPermissions(
                    arrayOf<String>(
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ),
                    RECORD_AUDIO_REQUEST_CODE
            )
        }
    }

    // Callback with the request from calling requestPermissions(...)
    @RequiresApi(api = Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(
            requestCode: Int,
            @NonNull permissions: Array<String>,
            @NonNull grantResults: IntArray
    ) {
        // Make sure it's our original READ_CONTACTS request
        if (requestCode == RECORD_AUDIO_REQUEST_CODE) {
            if ((grantResults.size == 3 && grantResultsCorrect(grantResults)))
            //Toast.makeText(this, "Record Audio permission granted", Toast.LENGTH_SHORT).show();
            else {
                Toast.makeText(
                        this, "You must give permissions to use this app. App is exiting.",
                        Toast.LENGTH_SHORT
                ).show()
                finishAffinity()
            }
        }
    }

    private fun grantResultsCorrect(grantResults: IntArray): Boolean {
        return grantResults[0] == PackageManager.PERMISSION_GRANTED
                && grantResults[1] == PackageManager.PERMISSION_GRANTED
                && grantResults[2] == PackageManager.PERMISSION_GRANTED
    }
}