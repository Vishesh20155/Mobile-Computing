import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import com.example.alarmapp.AlarmService

class PhoneStateReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == TelephonyManager.ACTION_PHONE_STATE_CHANGED) {
            val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
            if (state == TelephonyManager.CALL_STATE_RINGING.toString()) {
                // An incoming call is being received, stop the service
                context?.stopService(Intent(context, AlarmService::class.java))
            }
        }
    }
}
