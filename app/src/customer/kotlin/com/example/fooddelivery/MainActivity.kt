package com.example.fooddelivery

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.OvershootInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.fooddelivery.data.FoodApi
import com.example.fooddelivery.data.FoodHubAuthSession
import com.example.fooddelivery.data.modle.FoodItem
import com.example.fooddelivery.navigation.AddAddressScreen
import com.example.fooddelivery.navigation.AddressListScreen
import com.example.fooddelivery.navigation.AuthScreen
import com.example.fooddelivery.navigation.CartScreen
import com.example.fooddelivery.navigation.FoodDetailScreen
import com.example.fooddelivery.navigation.HomeScreen
import com.example.fooddelivery.navigation.LogInScreen
import com.example.fooddelivery.navigation.NavRoutes
import com.example.fooddelivery.navigation.NotificationScreen
import com.example.fooddelivery.navigation.OrderDetailScreen
import com.example.fooddelivery.navigation.OrderSuccessScreen
import com.example.fooddelivery.navigation.OrdersListScreen
import com.example.fooddelivery.navigation.RestaurantDetailScreen
import com.example.fooddelivery.navigation.SignUpScreen
import com.example.fooddelivery.navigation.foodItemNavType
import com.example.fooddelivery.notification.MessagingService
import com.example.fooddelivery.ui.screens.add_address.AddAddress
import com.example.fooddelivery.ui.screens.address_list.AddressList
import com.example.fooddelivery.ui.screens.auth.AuthScreen
import com.example.fooddelivery.ui.screens.auth.signup.SignInScreen
import com.example.fooddelivery.ui.screens.auth.signup.SignUpScreen
import com.example.fooddelivery.ui.screens.cart.CartScreen
import com.example.fooddelivery.ui.screens.cart.CartViewModel
import com.example.fooddelivery.ui.screens.food_detail.FoodDetail
import com.example.fooddelivery.ui.screens.home.CustomerHomeScreen
import com.example.fooddelivery.ui.screens.home.HomeViewModel
import com.example.fooddelivery.ui.screens.notification.NotificationScreen
import com.example.fooddelivery.ui.screens.notification.NotificationViewModel
import com.example.fooddelivery.ui.screens.order_detail.OrderDetail
import com.example.fooddelivery.ui.screens.order_success.OrderSuccess
import com.example.fooddelivery.ui.screens.orders.OrdersList
import com.example.fooddelivery.ui.screens.restaurant_detail.RestaurantDetailScreen
import com.example.fooddelivery.ui.theme.FoodDeliveryTheme
import com.example.fooddelivery.ui.theme.Mustard
import com.example.fooddelivery.ui.theme.Primary
import com.example.fooddelivery.ui.theme.poppinsFontFamily
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.reflect.typeOf

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var foodApi: FoodApi
    @Inject
    lateinit var session: FoodHubAuthSession
    val TAG="MainActivity"

    sealed class BottomNavItems(val route:NavRoutes,val icon:Int){
        object Home:BottomNavItems(HomeScreen,R.drawable.nav_home)
        object Cart:BottomNavItems(CartScreen,R.drawable.nav_cart)
        object Notification:BottomNavItems(NotificationScreen,R.drawable.nav_notification)
        object Orders:BottomNavItems(OrdersListScreen,R.drawable.ic_order)
    }
    val viewModel by viewModels<HomeViewModel>()
    @OptIn(ExperimentalSharedTransitionApi::class)
    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        var showSplashScreen=true
        installSplashScreen().apply {
            setKeepOnScreenCondition(){
                showSplashScreen
            }
                setOnExitAnimationListener { screen ->
                    val zoomX = ObjectAnimator.ofFloat(
                        screen.iconView, View.SCALE_X, 0.5f, 0.0f
                    )
                    val zoomY = ObjectAnimator.ofFloat(
                        screen.iconView, View.SCALE_Y, 0.5f, 0.0f
                    )
                    zoomX.duration = 1000
                    zoomY.duration = 1000
                    zoomX.interpolator = OvershootInterpolator()
                    zoomY.interpolator = OvershootInterpolator()
                    zoomY.doOnEnd {
                        screen.remove()
                    }
                    zoomX.doOnEnd {
                        screen.remove()
                    }
                    zoomX.start()
                    zoomY.start()
            }
        }
        setContent {
            FoodDeliveryTheme {
                val showBottomNavSheet= remember {
                    mutableStateOf(false)
                }
                val bottomNavItems= listOf(
                    BottomNavItems.Home,
                    BottomNavItems.Cart,
                    BottomNavItems.Notification,
                    BottomNavItems.Orders)
                val navController= rememberNavController()
                val cartViewModel:CartViewModel=hiltViewModel()
                val notificationViewModel: NotificationViewModel =hiltViewModel()
                Scaffold(modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        val currRoute=navController.currentBackStackEntryAsState().value?.destination
                        AnimatedVisibility(visible = showBottomNavSheet.value) {
                            NavigationBar(
                                containerColor = Color.White
                            ) {
                                bottomNavItems.forEach {item->
                                    val selected=currRoute?.hierarchy?.any { it.route==item.route::class.qualifiedName }==true
                                    val cartCount=cartViewModel.cartItemCount.collectAsStateWithLifecycle()
                                    val notificationCount=notificationViewModel.notificationCount.collectAsStateWithLifecycle()
                                    Log.d(TAG, "notificationCount: $notificationCount cartCount: $cartCount")
                                    NavigationBarItem(
                                        selected = false,
                                        onClick = {  navController.navigate(item.route){
                                            popUpTo(navController.graph.findStartDestination().id){
                                                saveState=true
                                            }
                                            launchSingleTop=true
                                            restoreState=true
                                        } },
                                        icon = {
                                            Box(modifier = Modifier.size(48.dp)) {
                                                if(item.route==BottomNavItems.Cart.route && cartCount.value>0)Box(
                                                    modifier = Modifier
                                                        .size(18.dp)
                                                        .clip(RoundedCornerShape(8.dp))
                                                        .background(Mustard)
                                                        .align(Alignment.TopEnd)
                                                    , contentAlignment = Alignment.Center
                                                ){
                                                    Text(text=cartCount.value.toString(), color = Color.White, style = TextStyle(
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.SemiBold,
                                                        fontFamily = poppinsFontFamily
                                                    ))
                                                }
                                                if(item.route==BottomNavItems.Notification.route && notificationCount.value>0)Box(
                                                    modifier = Modifier
                                                        .size(18.dp)
                                                        .clip(RoundedCornerShape(8.dp))
                                                        .background(Mustard)
                                                        .align(Alignment.TopEnd)
                                                    , contentAlignment = Alignment.Center
                                                ){
                                                    Text(text=notificationCount.value.toString(), color = Color.White, style = TextStyle(
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.SemiBold,
                                                        fontFamily = poppinsFontFamily
                                                    ))
                                                }
                                                Icon(painter = painterResource(item.icon),
                                                    contentDescription = null,
                                                    tint = if(selected) Primary else Color.Gray,
                                                    modifier = Modifier.align(Alignment.Center))
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }) { innerPadding ->
                    SharedTransitionLayout{
                        val navHost= NavHost(navController = navController, startDestination =
                        if (session.getToken()!=null) HomeScreen else AuthScreen, modifier = Modifier.padding(innerPadding),
                            enterTransition = {
                                slideIntoContainer(
                                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                                    animationSpec = tween(700),
                                )+ fadeIn(animationSpec = tween(700), initialAlpha = 0.2f)
                            },
                            exitTransition = {
                                slideOutOfContainer(
                                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                                    animationSpec = tween(700),
                                )+ fadeOut(animationSpec = tween(700), targetAlpha = 1f)
                            },
                            popEnterTransition = {
                                slideIntoContainer(
                                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                                    animationSpec = tween(700),
                                )+ fadeIn(animationSpec = tween(700), initialAlpha = 0.2f)
                            },
                            popExitTransition = {
                                slideOutOfContainer(
                                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                                    animationSpec = tween(700),
                                )+ fadeOut(animationSpec = tween(700), targetAlpha = 1f)
                            }){
                            composable<AuthScreen> {
                                showBottomNavSheet.value=false
                                AuthScreen(navController)
                            }
                            composable<SignUpScreen> {
                                showBottomNavSheet.value=false
                                SignUpScreen(navController=navController)
                            }
                            composable<HomeScreen> {
                                showBottomNavSheet.value=true
                                CustomerHomeScreen(navController=navController)
                            }
                            composable<LogInScreen> {
                                showBottomNavSheet.value=false
                                SignInScreen(navController=navController)
                            }
                            composable<RestaurantDetailScreen>{
                                showBottomNavSheet.value=false
                                val route=it.toRoute<RestaurantDetailScreen>()
                                RestaurantDetailScreen(this,route.name,route.imageUrl,route.id,navController)
                            }
                            composable<FoodDetailScreen>(
                                mapOf(typeOf<FoodItem>() to foodItemNavType)
                            ) {
                                showBottomNavSheet.value=false
                                val route=it.toRoute<FoodDetailScreen>()
                                FoodDetail(route.foodItem,animatedVisibilityScope = this,navController){
                                    cartViewModel.getCart()
                                }
                            }
                            composable<CartScreen> {
                                showBottomNavSheet.value=true
                                CartScreen(navController,cartViewModel)
                            }
                            composable<NotificationScreen> {
                                showBottomNavSheet.value=true
                                NotificationScreen(navController,notificationViewModel)
                            }
                            composable<AddressListScreen> {
                                showBottomNavSheet.value=false
                                AddressList(navController = navController)
                            }
                            composable<AddAddressScreen> {
                                showBottomNavSheet.value=false
                                AddAddress(navController = navController)
                            }
                            composable<OrderSuccessScreen> {
                                val data=it.toRoute<OrderSuccessScreen>()
                                OrderSuccess(data.orderId,navController)
                            }
                            composable<OrdersListScreen> {
                                OrdersList(navController)
                            }
                            composable<OrderDetailScreen> {
                                showBottomNavSheet.value=false
                                val data=it.toRoute<OrderDetailScreen>()
                                OrderDetail(navController,data.orderId)
                            }
                        }
                    }
                }
            }
        }
        processIntent(intent,viewModel)
        if(::foodApi.isInitialized){
            Log.d(TAG,"FoodApi is initialized")
        }
        CoroutineScope(Dispatchers.IO).launch {
            delay(2000)
            showSplashScreen=false
        }
    }
    override fun onNewIntent(intent: Intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            super.onNewIntent(intent)
        }
        processIntent(intent,viewModel)
    }

    fun processIntent(intent: Intent,viewModel: HomeViewModel){
        if(intent.hasExtra(MessagingService.OrderId)){
            val orderId=intent.getStringExtra(MessagingService.OrderId)
            viewModel.navigateToOrderDetail(orderId!!)
            intent.removeExtra(MessagingService.OrderId)
        }
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    FoodDeliveryTheme {
    }
}