package com.example.fooddelivery.data.modle

import com.google.gson.annotations.SerializedName

class NotificationResponse (
    @SerializedName("notifications")
    val notifications: List<Notification>,
    @SerializedName("unreadCount")
    val unreadCount: Int
)