package push

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.onurcan.exovideoreference.utils.showLogInfo
import com.onurcan.pangolin.R
import com.onurcan.pangolin.ui.activities.OnboardingActivity

/**
 * 功能描述
 *
 * @author o00559125
 * @since 2021-02-17
 */
class HmsGmsMessagingService : FirebaseMessagingService() {

    private val tag: String = this.javaClass.simpleName

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        showLogInfo(tag, "receive token:$p0")
    }

    override fun stopService(name: Intent?): Boolean {
        showLogInfo(tag, "Push service stopped")
        return super.stopService(name)
    }

    override fun onMessageReceived(rmt: RemoteMessage) {
        super.onMessageReceived(rmt)

        if (rmt.data.isNotEmpty()) {
            showLogInfo(tag, "Payload: ${rmt.data}")
        }

        if (rmt.notification != null) {
            showLogInfo(tag, "Body ${rmt.notification!!.body}")

            rmt.notification!!.title?.let { it ->
                rmt.notification!!.body?.let { that ->
                    sendNotification(it, that)
                }
            }
        }
    }

    override fun onMessageSent(p0: String) {
        super.onMessageSent(p0)
        showLogInfo(tag, "Token, $p0")
    }

    private fun sendNotification(title: String, body: String) {
        val intent = Intent(this, OnboardingActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)

        val channelId = "Pangolin"
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_stat_onesignal_default)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Channel tittle",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0, notificationBuilder.build())

    }
}