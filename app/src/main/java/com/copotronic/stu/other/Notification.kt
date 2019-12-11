package com.copotronic.stu.other

data class Notification(
    private val id: Int,
    val message: String,
    val receivcerId: Int,
    val targetUrl: String,
    val createdAt: String,
    val notificationType: NotificationType
)