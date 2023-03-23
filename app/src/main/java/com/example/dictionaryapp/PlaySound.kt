package com.example.dictionaryapp

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.AsyncTask
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.core.net.toUri
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.URL

class PlaySound(private val context: Context?, private val url: String?) :
    AsyncTask<Unit, Unit, File?>() {

    private lateinit var mediaPlayer: MediaPlayer

    override fun doInBackground(vararg params: Unit?): File? {
        if ((url == null) || (url.isEmpty())) return null
        try {
            val filename = "audio.mp3"
            val directory = context?.filesDir
            val file = File(directory, filename)
            val inputStream = URL(url).openStream()
            val outputStream = FileOutputStream(file)
            inputStream.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }

            return file
        }
        catch (e: IOException) {
            Log.e("Play Sound", "Error in download")
            e.printStackTrace()
            return null
        }
        return null
    }

    override fun onPostExecute(result: File?) {
        super.onPostExecute(result)
        if (result == null) {
            Toast.makeText(context, "Audio Link unavailable", Toast.LENGTH_SHORT).show()
        }
        else {
            mediaPlayer = MediaPlayer.create(context, result.toUri())
            mediaPlayer.start()
        }
    }
}