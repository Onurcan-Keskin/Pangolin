package com.onurcan.pangolin.appuser

object AppUser {

    private var userId = ""

    fun setUserId(userId: String) {
        if (userId != "")
            this.userId = userId
        else
            this.userId = "dummy"
    }

    fun getUserId() = userId

}