package com.onurcan.pangolin.ui.presenters

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.onurcan.exovideoreference.utils.showLogError
import com.onurcan.pangolin.appuser.AppUser
import com.onurcan.pangolin.models.FirebaseDBObject
import com.onurcan.pangolin.ui.contracts.ISingleUser
import java.text.DateFormat
import java.util.*
import kotlin.collections.HashMap

class SingleUserPresenter constructor(private val contract: ISingleUser.ViewSingleUser) :
    ISingleUser.PresenterSingleUser {

    override fun onViewsCreated() {
        contract.initUI()
    }

    override fun onDataPassed(string: String) {
        contract.populateViews(string)
    }

    override fun setupOffset(offsetPX: Int) {
        contract.setOffset(offsetPX)
    }

    private fun createNotificationsDB(lisResID: String) {
        val notifierDB = FirebaseDBObject.rootRef().child("notifications").child(lisResID).push()
        val notificationID = notifierDB.key

        val notificationMap: MutableMap<String, String> = HashMap()
        notificationMap["from"] = AppUser.getUserId()
        notificationMap["type"] = "request"

        val requestMap: MutableMap<String, Any> = HashMap()
        requestMap["Friend_Requests/" + AppUser.getUserId() + "/" + lisResID + "/request_type"] =
            "sent"
        requestMap["Friend_Requests/" + lisResID + "/" + AppUser.getUserId() + "/request_type"] =
            "received"
        requestMap["notifications/$lisResID/$notificationID"] = notificationMap

        FirebaseDBObject.rootRef().updateChildren(requestMap)
    }

    override fun friendMap(currentID: String, lisResID: String) {
        val timeDate = DateFormat.getDateInstance().format(Date())
        val timeMillis = System.currentTimeMillis().toString()
        val sentFriendRequestRef = "SentFriendRequest"
        val receiveFriendRequestRef = "ReceivedFriendRequest"

        /* Build Notification DB */
        createNotificationsDB(lisResID)

        /* SentFriendRequest */
        val mSentRef = FirebaseDBObject.getUserInfo(lisResID)
        mSentRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val photo = snapshot.child("photoUrl").value.toString()
                val name = snapshot.child("name").value.toString()
                val bio = snapshot.child("memory").value.toString()

                val sentRefMap: MutableMap<String, String> = HashMap()
                sentRefMap["request_time"] = timeDate
                sentRefMap["request_millis"] = timeMillis
                sentRefMap["photo"] = photo
                sentRefMap["name"] = name
                sentRefMap["bio"] = bio

                val mapSentRef: MutableMap<String, Any> = HashMap()
                mapSentRef["$sentFriendRequestRef/$currentID/$lisResID"] = sentRefMap

                FirebaseDBObject.rootRef().updateChildren(mapSentRef)
            }

            override fun onCancelled(error: DatabaseError) {
                showLogError(this.javaClass.simpleName, error.message)
            }
        })

        /* ReceiveFriendRequest */
        val mReceiveRef = FirebaseDBObject.getUserInfo(currentID)
        mReceiveRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val photo = snapshot.child("photoUrl").value.toString()
                val name = snapshot.child("name").value.toString()
                val bio = snapshot.child("memory").value.toString()

                val receiveRefMap: MutableMap<String, String> = HashMap()
                receiveRefMap["request_time"] = timeDate
                receiveRefMap["request_millis"] = timeMillis
                receiveRefMap["photo"] = photo
                receiveRefMap["name"] = name
                receiveRefMap["bio"] = bio

                val mapReceiveRef: MutableMap<String, Any> = HashMap()
                mapReceiveRef["$receiveFriendRequestRef/$lisResID/$currentID"] = receiveRefMap

                FirebaseDBObject.rootRef().updateChildren(mapReceiveRef)
            }

            override fun onCancelled(error: DatabaseError) {
                showLogError(this.javaClass.simpleName, error.message)
            }
        })
    }

    fun revokeMap(fromID: String, toID: String) {
        FirebaseDBObject.getReceivedFriendRequestAccess(fromID, toID).removeValue()
        FirebaseDBObject.getSentFriendRequestAccess(fromID, toID).removeValue()
        FirebaseDBObject.getTypeReceivedFromTo(fromID, toID).removeValue()
        FirebaseDBObject.getTypeSentFromTo(fromID, toID).removeValue()
        FirebaseDBObject.getTypeReceivedFromTo(fromID, toID).removeValue()
    }
}