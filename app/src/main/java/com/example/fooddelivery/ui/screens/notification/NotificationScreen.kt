package com.example.fooddelivery.ui.screens.notification

import com.example.fooddelivery.R
import androidx.compose.ui.input.key.type
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.fooddelivery.data.modle.Notification
import com.example.fooddelivery.navigation.OrderDetailScreen
import com.example.fooddelivery.notification.NotificationManager
import com.example.fooddelivery.ui.theme.FoodDeliveryTheme
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    navController: NavController,
    viewModel: NotificationViewModel,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collectLatest {
            when(it){
                is NotificationViewModel.NotificationEvent.NavigateBack ->{
                    navController.popBackStack()
                }
                is NotificationViewModel.NotificationEvent.NavigateToOrderDetail ->{
                    navController.navigate(OrderDetailScreen(it.id))
                }
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        NotificationHeaderView {
            navController.popBackStack()
        }
        when (val state = uiState) {
            is NotificationViewModel.NotificationUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is NotificationViewModel.NotificationUiState.Empty -> {
                EmptyState()
            }
            is NotificationViewModel.NotificationUiState.Success -> {
                NotificationList(groupedNotifications = state.groupedNotifications,viewModel)
            }
        }
    }
}



@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NotificationList(groupedNotifications: Map<String, List<Notification>>,viewModel: NotificationViewModel) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        groupedNotifications.forEach { (dateHeader, notifications) ->
            stickyHeader {
                Text(
                    text = dateHeader,
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp))
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
            items(notifications, key = { it.id }) { notification ->
                NotificationItem(notification = notification,onClick={
                    viewModel.navigateToOrderDetail(notification)
                })
                Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            }
        }
    }
}

@Composable
fun NotificationItem(notification: Notification,onClick:()->Unit) {
    val iconDetails = getIconForType(NotificationType.fromString(notification.type))
    val containerColor = if (!notification.isRead) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f) else MaterialTheme.colorScheme.surface

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(containerColor)
            .padding(16.dp)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = iconDetails.icon,
                contentDescription = iconDetails.contentDescription,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = notification.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = if (!notification.isRead) FontWeight.Bold else FontWeight.Normal
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = notification.message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (!notification.isRead) {
            Spacer(modifier = Modifier.width(16.dp))
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
            )
        }
    }
}

@Composable
fun EmptyState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.NotificationsOff,
            contentDescription = "No notifications",
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No Notifications Yet",
            style = MaterialTheme.typography.titleLarge
        )
        Text(
            text = "When you get updates, they'll show up here.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

enum class NotificationType(val icon: ImageVector, val contentDescription: String) {
    ORDER(Icons.Default.Moped, "Order notification"),
    PROMOTION(Icons.Default.Discount, "Promotion notification"),
    REVIEW(Icons.Default.RateReview, "Review notification"),
    ACCOUNT(Icons.Default.AccountCircle, "Account notification");

    companion object {
        fun fromString(type: String): NotificationType {
            return when (type.lowercase()) {
                "order" -> ORDER
                "general" -> PROMOTION // Assuming 'general' maps to PROMOTION
                "review" -> REVIEW
                "account" -> ACCOUNT
                else -> ACCOUNT // Default case
            }
        }
    }
}

@Composable
fun getIconForType(type: NotificationType): NotificationType {
    return type
}

@Composable
fun NotificationHeaderView(onBack: () -> Unit) {
    Box(modifier = Modifier.fillMaxWidth()) {
        IconButton(
            onClick = { onBack() },
            modifier = Modifier
                .padding(8.dp)
                .align(Alignment.CenterStart)
        ) {
            Image(
                painter = painterResource(R.drawable.back_button),
                contentDescription = "Back",
                modifier = Modifier.size(60.dp)
            )
        }
        Text(
            text = "Notifications",
            modifier = Modifier.align(Alignment.Center),
            style = MaterialTheme.typography.titleLarge
        )
    }
}