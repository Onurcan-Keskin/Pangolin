package push

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.huawei.agconnect.config.AGConnectServicesConfig
import com.huawei.hms.aaid.HmsInstanceId
import com.huawei.hms.common.ApiException
import com.huawei.hms.push.HmsMessageService
import com.huawei.hms.push.RemoteMessage
import com.onesignal.OneSignalHmsEventBridge
import com.onurcan.exovideoreference.utils.showLogDebug
import com.onurcan.exovideoreference.utils.showLogError
import com.onurcan.pangolin.R

class HmsGmsMessagingService : HmsMessageService() {

    override fun onNewToken(string: String?) {
        super.onNewToken(string)
        OneSignalHmsEventBridge.onNewToken(this, string!!)
    }

    fun getToken() {
        Thread {
            try {
                val appID = AGConnectServicesConfig.fromContext(this.applicationContext)
                    .getString("client/app_id")
                val token = HmsInstanceId.getInstance(this.applicationContext)
                    .getToken(appID, "HCM")
                showLogDebug(this.javaClass.simpleName, "Token: $token")
            } catch (e: ApiException) {
                showLogError(this.javaClass.simpleName, "Failure ${e.message}")
            }
        }
    }

    override fun onMessageReceived(message: RemoteMessage?) {
        super.onMessageReceived(message)
        OneSignalHmsEventBridge.onMessageReceived(this, message!!)

        val notificationData: MutableMap<String, String> = message.dataOfMap
        if (notificationData.isEmpty()) {
            showLogError(this.javaClass.simpleName, "notificationData is empty")
            return
        }

        val appImage = BitmapFactory.decodeResource(
            applicationContext.resources,
            R.drawable.ic_stat_onesignal_default
        )

        val bigPicStyle = NotificationCompat.BigPictureStyle()
            .bigPicture(appImage)
            .bigLargeIcon(null)


        /* NotiBuilder - Friend Request */
        val icon = R.drawable.ic_stat_onesignal_default
        val title = notificationData["title"]
        val text = notificationData["text"]

        val clickAction = message.notification.clickAction

        val builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(icon)
            .setContentTitle(title)
            .setContentText(text)
            // Set the intent that will fire when the user taps the notification
            //TODO .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setStyle(bigPicStyle)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        val intent = Intent(clickAction)
        val pendingIntent =
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        builder.setContentIntent(pendingIntent)

        val mNotificationID = System.currentTimeMillis().toInt()
        val mNotificationManager = NotificationManagerCompat.from(this)
        mNotificationManager.notify(mNotificationID, builder.build())
    }

    fun NotificationManager.cancelNotifications() {
        cancelAll()
    }

    companion object {
        private const val CHANNEL_ID = "friends_notification"
    }
}