package com.onurcan.pangolin.models

import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue

object FirebaseDBObject {
    fun rootRef(): DatabaseReference {
        return FirebaseDatabase.getInstance().reference
    }

    fun getUser(userID: String): DatabaseReference {
        return FirebaseDatabase.getInstance().reference.child("User").child(userID)
    }

    fun onDisconnect(db: DatabaseReference): Task<Void> {
        return db.child("onlineStatus").onDisconnect().setValue(ServerValue.TIMESTAMP)
    }

    fun onConnect(db: DatabaseReference): Task<Void> {
        return db.child("onlineStatus").setValue("true")
    }

    fun getRootUserInfo(): DatabaseReference {
        return FirebaseDatabase.getInstance().reference.child("UserInfo")
    }

    fun getUserInfo(userID: String): DatabaseReference {
        return FirebaseDatabase.getInstance().reference.child("UserInfo").child(userID)
    }

    fun getShared(): DatabaseReference {
        return FirebaseDatabase.getInstance().reference.child("uploads/Shareable")
    }

    /**
     * Receive Friend Request
     */
    fun getReceivedFriendRequests(userID: String): DatabaseReference {
        return FirebaseDatabase.getInstance().reference.child("ReceivedFriendRequest").child(userID)
    }

    /**
     * Used for revoking received friends request
     */
    fun getReceivedFriendRequestAccess(fromID: String, toID: String):DatabaseReference{
        return FirebaseDatabase.getInstance().reference.child("ReceivedFriendRequest").child(toID).child(fromID)
    }

    /**
     * Sent Friend Request
     */
    fun getSentFriendRequest(userID: String):DatabaseReference{
        return FirebaseDatabase.getInstance().reference.child("SentFriendRequest").child(userID)
    }

    /**
     * Used for revoking sent friends request
     */
    fun getSentFriendRequestAccess(fromID: String,toID: String):DatabaseReference{
        return FirebaseDatabase.getInstance().reference.child("SentFriendRequest").child(fromID).child(toID)
    }

    /**
     * Notifications
     */
    fun getNotifications(userID: String):DatabaseReference{
        return FirebaseDatabase.getInstance().reference.child("notifications").child(userID)
    }

    /**
     * Friend Requests
     * @param fromID Request comes from ID
     * @param toID Send to ID
     * @return request_type received
     */
    fun getTypeReceivedFromTo(fromID: String,toID:String): DatabaseReference{
        return FirebaseDatabase.getInstance().reference.child("Friend_Requests").child(toID).child(fromID)
    }

    /**
     * Friend Requests
     * @param fromID Request comes from ID
     * @param toID Send to ID
     * @return request_type sent
     */
    fun getTypeSentFromTo(fromID: String,toID: String):DatabaseReference{
        return FirebaseDatabase.getInstance().reference.child("Friend_Requests").child(fromID).child(toID)
    }

    /**
     * If friend request is accepted
     */
    fun getFriends(userID: String): DatabaseReference {
        return FirebaseDatabase.getInstance().reference.child("Friends").child(userID)
    }

    fun getShareItem(listId: String): DatabaseReference {
        return FirebaseDatabase.getInstance().reference.child("uploads/Shareable/$listId")
    }

    fun getUnShared(): DatabaseReference {
        return FirebaseDatabase.getInstance().reference.child("uploads/UnShareable")
    }

    fun getUnSharedItem(listId: String): DatabaseReference {
        return FirebaseDatabase.getInstance().reference.child("uploads/UnShareable/$listId")
    }

    fun getNotShared(): DatabaseReference {
        return FirebaseDatabase.getInstance().reference.child("uploads/Unshareable")
    }

    fun getUserInfoRoot(): DatabaseReference {
        return FirebaseDatabase.getInstance().reference.child("UserInfo")
    }
}