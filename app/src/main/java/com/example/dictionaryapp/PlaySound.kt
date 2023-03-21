package com.example.dictionaryapp

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.AsyncTask
import android.widget.Toast
import java.io.IOException

class PlaySound(private val urlSound: String?, private val mediaPlayer: MediaPlayer, private val context: Context?) : AsyncTask<Void, Void, Boolean>() {
    override fun doInBackground(vararg p0: Void?): Boolean {
        println("Inside do in BG. URL: $urlSound")
        if ((urlSound == null) || (urlSound?.length==0)) return false
        try {
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)
            mediaPlayer.setDataSource(urlSound)
            mediaPlayer.prepare()
            return true
        }
        catch (e: IOException) {
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