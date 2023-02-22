package com.example.alarmapp

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.annotation.RequiresApi
import java.time.LocalDateTime

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AlarmsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AlarmsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    var curr = 0
    private lateinit var contentTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_alarms, container, false)

        val timePicker: TimePicker = v.findViewById(R.id.timePicker_Alarm)
        timePicker.setIs24HourView(true)

        contentTextView = v.findViewById(R.id.content)

        val startButton: Button = v.findViewById(R.id.btn_Start)
        startButton.setOnClickListener {

//            Toast.makeText(this, LocalDateTime.now().hour.(), Toast.LENGTH_SHORT).show()
            val hour = timePicker.hour
            val min = timePicker.minute

            if (curr<5){
                var contentText = contentTextView.text
                contentTextView.text = contentText.toString() + "\nAlarm Time: " + hour.toString() + ":" + min.toString()
                curr += 1
            }

            val intent = Intent(context, AlarmService::class.java)
            intent.putExtra("Hour", hour)
            intent.putExtra("Minute", min)
            context?.startService(intent)
        }

        val stopButton: Button = v.findViewById(R.id.btn_Stop)
        stopButton.setOnClickListener {
            curr = 0
            var contentText = contentTextView.text
            contentText = ""
            contentTextView.text = contentText
            val intent = Intent(context, AlarmService::class.java)
            context?.stopService(intent)
        }

        return v
    }

    fun setText(){
        var contentText = contentTextView.text
        contentText = ""
        contentTextView.text = contentText

        curr = 0
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AlarmsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AlarmsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

}