package com.example.dictionaryapp

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.AsyncTask
import android.os.Environment
import android.util.Log
import android.widget.Toast
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.URL

class PlaySound(private val urlSound: String?, private val mediaPlayer: MediaPlayer, private val context: Context?) : AsyncTask<Void, Void, Boolean>() {
    override fun doInBackground(vararg p0: Void?): Boolean {
        println("Inside do in BG. URL: $urlSound")
        if ((urlSound == null) || (urlSound?.length==0)) return false
        try {
            val url = URL(urlSound)
            val connection = url.openConnection()
            connection.connect()
            val input = BufferedInputStream(url.openStream(), 8192)
            val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC), "abc.mp3")
            val output = FileOutputStream(file)

            val data = ByteArray(1024)
            var count: Int = input.read(data)
            while (count != -1) {
                output.write(data, 0, count);
                count = input.read(data)
            }

            output.flush();
            output.close();
            input.close();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)
            mediaPlayer.setDataSource(urlSound)
            mediaPlayer.prepare()
            return true
        }
        catch (e: IOException) {
            Log.e("Play Sound", "Error in download")
            e.printStackTrace()
        }
        return false
    }

    override fun onPostExecute(result: Boolean?) {
        super.onPostExecute(result)
        if (result == true){
            mediaPlayer.start()
        }
        else{
            Toast.makeText(context, "Audio Link unavailable", Toast.LENGTH_SHORT).show()
        }
    }
}