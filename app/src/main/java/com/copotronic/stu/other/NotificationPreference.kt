package com.copotronic.stu.other

data class NotificationPreference(
    private val id: Int,
    val userId: Int,
    var notificationTypeId: Int, // can be updated from notification preference setting
    val createdAt: String,
    val updatedAt: String
)