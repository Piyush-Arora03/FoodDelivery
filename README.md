# FoodDelivery (Android)

A multi-role food delivery Android app built with Kotlin, Jetpack libraries, and Hilt DI. It includes Customer, Restaurant, and Rider experiences, REST APIs for core operations, and a WebSocket channel for real-time rider location/messages.

## Features

- **Customer**
  - Browse items and place orders
  - Track order status updates
- **Restaurant**
  - View order details (`RestaurantOrderDetailScreen`)
  - Update order status through well-defined transitions
- **Rider**
  - View assigned orders and details (`RiderOrderDetailScreen`)
  - Real-time socket connection to broadcast/receive location/messages
- **Networking**
  - REST via Retrofit/OkHttp
  - WebSocket via OkHttp for live updates
- **Architecture**
  - MVVM with repositories and ViewModels
  - Kotlin Coroutines + Flows
  - Dependency Injection with Hilt

## Tech Stack

- Kotlin, Coroutines, Flow
- Jetpack: Lifecycle, Navigation, (Compose-based UI components in screens)
- Retrofit + Gson, OkHttp, HttpLoggingInterceptor
- Hilt for DI
- OkHttp WebSocket

## Key Concepts

- **MVVM + Repository pattern**: ViewModels (e.g., `RiderOrderDetailViewModel.kt`) expose UI state and one-off events; repositories (e.g., `LocationUpdateSocketRepository.kt`) abstract data sources.
- **Dependency Injection (Hilt)**: Providers in `data/AppModule.kt` for `Retrofit`, `OkHttpClient`, `FoodApi`, `SocketService`, repositories, and auth session.
- **Coroutines & Flows**: `StateFlow` for screen state and socket messages; `SharedFlow` for navigation/events; `viewModelScope` for structured concurrency.
- **Networking**: REST via Retrofit/OkHttp for API calls; WebSocket via OkHttp for realtime messaging/location.
- **Lifecycle-aware collection**: Compose collects flows using `collectAsStateWithLifecycle(...)` to avoid leaks and respect lifecycle.
- **Sealed classes for state/events**: `UiState` for Loading/Success/Error, `NavigationEvent`, and `SocketConnection` for connection state.
- **Domain logic**: Centralized order statuses in `restaurant/.../utils/OrderStatusUtils.kt` and transition helper `availableStatus(...)` in `RestaurantOrderDetailScreen.kt`.

## Project Structure

- `app/build.gradle.kts`, `settings.gradle.kts`, `gradle/` – Gradle build & dependency management
- `app/src/main/java/com/example/fooddelivery/`
  - `data/` – Data layer (API, DI module, session, repositories)
    - `AppModule.kt` – Hilt providers (Retrofit/OkHttp, FoodApi, SocketService, etc.)
    - `SocketService.kt` – Socket service interface
    - `repository/LocationUpdateSocketRepository.kt` – WebSocket orchestration and connection state
  - `SocketServiceImpl.kt` – OkHttp WebSocket implementation
- Multi-role UI modules (same app module, different source sets):
  - `app/src/customer/...`
  - `app/src/rider/...`
    - `ui/screens/order_detail/detail/RiderOrderDetailViewModel.kt` – Fetch order details, connect/disconnect socket, expose messages flow
    - `ui/screens/order_detail/item/ItemScreen.kt` – Rider item screen
    - `MainActivity.kt` – App navigation including Rider flows
  - `app/src/restaurant/...`
    - `ui/screens/order_detail/RestaurantOrderDetailScreen.kt` – Order details and status updates
    - `utils/OrderStatusUtils.kt` – Central enum of order statuses

Note: Exact screens and modules evolve; see the directories above for the latest.

## Configuration

Back-end endpoints are configured in `AppModule.kt` and `SocketServiceImpl.kt`.

- REST Base URL (`Retrofit`): set in `app/src/main/java/com/example/fooddelivery/data/AppModule.kt`
  - Example: `http://192.168.29.117:8080`
- WebSocket Base URL: set in `app/src/main/java/com/example/fooddelivery/SocketServiceImpl.kt`
  - Example: `ws://192.168.29.117:8080`

If you run the backend on a different host/port, update both values consistently.

### Emulator vs Physical Device

- Android Emulator accessing a host machine service typically uses `10.0.2.2`. If your backend runs on the host, update both base URLs accordingly (`http://10.0.2.2:8080` and `ws://10.0.2.2:8080`).
- For LAN devices, use the machine’s LAN IP (e.g., `192.168.x.x`). Ensure the mobile device and server are on the same network.

## Building & Running

Prerequisites:
- Android Studio (latest stable)
- Android SDK 24+
- JDK 17 (aligned with Android Gradle Plugin requirements)

Steps:
1. Open the project folder `FoodDelivery/` in Android Studio.
2. Sync Gradle when prompted.
3. Configure REST and WS base URLs in `data/AppModule.kt` and `SocketServiceImpl.kt`.
4. Ensure your backend is running and reachable at the configured host/port.
5. Run the app on an emulator or connected device.

### Build commands (optional)

From a terminal in project root:
```bash
# Unix-like shells
./gradlew assembleDebug

# Windows PowerShell
./gradlew.bat assembleDebug
```
Generated APKs will be under `app/build/outputs/apk/`.

## Screenshots

Add screenshots or short GIFs of key flows (Customer ordering, Restaurant status update, Rider tracking) here.

## Core Flows

### Order Details + Status Updates (Restaurant)
- Screen: `app/src/restaurant/kotlin/.../RestaurantOrderDetailScreen.kt`
- Shows order items and allowed next statuses via `availableStatus(status)`.
- Calls `viewModel.updateOrderStatus(orderId, newStatus)` to update on server.

### Rider Socket Connection & Messages
- Interface: `SocketService` exposes `connect`, `disconnect`, `sendMessage`, and `messages: Flow<String>`.
- Impl: `SocketServiceImpl` uses OkHttp WebSocket and emits messages to a `MutableStateFlow`.
- Repository: `LocationUpdateSocketRepository` connects with current location (placeholder), forwards `messages` to consumers, and maintains connection state.
- ViewModel: `RiderOrderDetailViewModel`
  - `getOrderDetails(orderId)` fetches details, then calls `connectSocket(riderId)`.
  - `message` is exposed from the repository for UI observation.

### Observing Messages in Compose
A simple pattern to avoid logging the initial empty state flow value:
```kotlin
val message by viewModel.message.collectAsStateWithLifecycle("")
LaunchedEffect(message) {
    if (message.isNotBlank()) {
        Log.d("Messages", message)
    }
}
```

## Troubleshooting

- **No WebSocket messages**
  - Ensure both base URLs (HTTP/WS) point to the same reachable host and port.
  - Verify `connectSocket(...)` gets a valid `riderId` (not `"null"`).
  - Add logging in `WebSocketListener.onOpen`, `onFailure`, `onClosed` to diagnose connection.
- **messages Flow always empty**
  - `SocketServiceImpl` seeds `MutableStateFlow("")`. You will see an initial empty value; wait for real `onMessage` events.
  - Consider switching to `SharedFlow` if you prefer event-style semantics without an initial value.
- **Emulator can’t reach server**
  - If server runs on host machine, use `10.0.2.2` in URLs. Open firewall for port 8080.
- **HTTP 401/403**
  - Check `FoodHubAuthSession` token configuration in `AppModule.kt` interceptor.

## FAQ

- **Why is my `messages` flow empty?** The WebSocket flow starts with an empty `StateFlow("")` and only updates when the server sends messages. Also verify connection parameters (URLs, `riderId`) and server availability.
- **Emulator can’t hit my local server** Use `10.0.2.2` for the host machine, or deploy the backend to a LAN IP and use that address.

## Extending the App

- **Order Status Machine**: Centralize valid transitions in `availableStatus` or an enum-based map to ensure consistency across roles.
- **Location Provider**: Replace `LocationUpdateSocketRepository.getUserLocation()` placeholder with fused location provider and runtime permissions.
- **Connection State UI**: Observe `socketConnection` `StateFlow` to show connection banners/spinners.
- **Error Handling**: Propagate `onFailure` from the WebSocket to UI via repository state.

## Testing

- Unit-test ViewModels and repositories with coroutine test rules and Turbine for flows.
- Add UI tests for key navigation paths.

## License

This project is provided as-is without a specific license. Add your preferred license (e.g., MIT) if you plan to distribute.
