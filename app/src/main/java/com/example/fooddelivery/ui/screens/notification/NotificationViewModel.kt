package com.example.fooddelivery.ui.screens.notification

import android.os.Build
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fooddelivery.data.FoodApi
import com.example.fooddelivery.data.modle.Notification
import com.example.fooddelivery.data.remote.ApiResponses
import com.example.fooddelivery.data.remote.SafeApiCalls
import com.example.fooddelivery.notification.NotificationEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(private val foodApi: FoodApi) : ViewModel() {

    private val _uiState = MutableStateFlow<NotificationUiState>(NotificationUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _navigationEvent= MutableSharedFlow<NotificationEvent>()
    val navigationEvent=_navigationEvent.asSharedFlow()

    private val _notificationCount= MutableStateFlow(0)
    val notificationCount=_notificationCount.asStateFlow()

    var errMsg = ""
    var dis = ""

    init {
        com.example.fooddelivery.notification.NotificationEvent.events
            .onEach {
                // When an event is received, refresh the notifications list.
                Log.d("NotificationViewModel", "Received a global notification event. Refreshing...")
                loadNotifications()
            }
            .launchIn(viewModelScope) // Launch the collector in the ViewModel's scope.
        loadNotifications()
    }

    fun loadNotifications() {
        viewModelScope.launch {
            _uiState.value = NotificationUiState.Loading
            val response = SafeApiCalls { foodApi.getNotifications() }
            Log.d("NotificationViewModel", response.toString())
            when (response) {
                is ApiResponses.Success -> {
                    val notifications = response.data?.notifications
                    _notificationCount.value = response.data.unreadCount
                    Log.d("NotificationViewModel", "unreadCount: ${response.data.unreadCount}")
                    if (notifications != null) {
                        if (notifications.isEmpty()) {
                            _uiState.value = NotificationUiState.Empty
                        } else {
                            Log.d("NotificationViewModel", response.data.toString())
                            val grouped = notifications.groupBy { getRelativeDate(it.createdAt) }
                            _uiState.value = NotificationUiState.Success(grouped)
                        }
                    } else {
                        _uiState.value = NotificationUiState.Empty
                    }
                }
                is ApiResponses.Error -> {
                    _uiState.value = NotificationUiState.Empty
                }
                is ApiResponses.Exception -> {

                }
            }
        }
    }

    private fun getRelativeDate(dateString: String): String {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return try {
                // 1. Parse the incoming string as a LocalDateTime.
                //    This represents the date and time of the event, but without timezone info.
                val parsedDateTime =
                    LocalDateTime.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE_TIME)

                // 2. Specify that this LocalDateTime is in the UTC timezone.
                //    This creates a ZonedDateTime, which is timezone-aware.
                val notificationDateTimeUtc = parsedDateTime.atZone(ZoneId.of("UTC"))

                // 3. Convert the UTC ZonedDateTime to the user's default timezone.
                //    This gives you the exact moment of the notification in the user's local context.
                val notificationDateTimeLocal = notificationDateTimeUtc.withZoneSameInstant(ZoneId.systemDefault())

                // 4. Get the date part of the notification in the user's local timezone.
                val notificationDateLocal = notificationDateTimeLocal.toLocalDate()

                // 5. Get the current date in the user's local timezone for accurate comparison.
                val todayLocal = LocalDate.now(ZoneId.systemDefault())
                val yesterdayLocal = todayLocal.minusDays(1)

                // 6. Compare the local dates.
                when (notificationDateLocal) {
                    todayLocal -> "Today"
                    yesterdayLocal -> "Yesterday"
                    else -> {
                        // Format the local date for display, e.g., "August 12, 2025"
                        val formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy", Locale.getDefault())
                        notificationDateLocal.format(formatter)
                    }
                }
            } catch (e: DateTimeParseException) {
                Log.e("NotificationViewModel", "Error parsing date: $dateString", e)
                "Unknown Date" // Fallback in case of a parsing error
            }
        }
        return "Unknown Date"
    }

    fun navigateToOrderDetail(notification: Notification) {
        viewModelScope.launch {
                val response = SafeApiCalls {
                    foodApi.readNotification(notification.id)
                }
                when(response){
                    is ApiResponses.Success->{
                        loadNotifications()
                        _navigationEvent.emit(NotificationEvent.NavigateToOrderDetail(notification.orderId))
                    }
                    is ApiResponses.Error<*> -> TODO()
                    is ApiResponses.Exception<*> -> TODO()
                }
        }
    }



    sealed class NotificationUiState {
        data object Loading : NotificationUiState()
        data class Success(val groupedNotifications: Map<String, List<Notification>>) : NotificationUiState()
        data object Empty : NotificationUiState()
    }

    sealed class NotificationEvent {
        data object NavigateBack:NotificationEvent()
        data class NavigateToOrderDetail(val id:String): NotificationEvent()
    }
}
