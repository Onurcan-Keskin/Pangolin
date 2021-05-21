package com.onurcan.pangolin.models

object DataClass {
    data class MakeFriends(
        val photoUrl: String = ""
    )

    data class PendingRequests(
        val photo: String = "",
        val name: String = ""
    )
}