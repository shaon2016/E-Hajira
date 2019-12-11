package com.copotronic.stu.other

data class User(val id : Int,
                val userName : String,
                var notificationPreference: ArrayList<NotificationPreference>) {
}