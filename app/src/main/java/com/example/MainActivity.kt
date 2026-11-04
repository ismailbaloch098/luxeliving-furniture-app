package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.LuxeBottomBar
import com.example.ui.components.LuxeTopBar
import com.example.ui.screens.ar.ArRoomPreviewScreen
import com.example.ui.screens.catalog.CatalogScreen
import com.example.ui.screens.cart.CartScreen
import com.example.ui.screens.checkout.CheckoutScreen
import com.example.ui.screens.checkout.OrderSuccessScreen
import com.example.ui.screens.detail.ProductDetailScreen
import com.example.ui.screens.home.HomeScreen
import com.example.ui.screens.wishlist.WishlistScreen
import com.example.ui.theme.LuxeEspresso
import com.example.ui.theme.LuxeLivingTheme
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.FurnitureViewModel
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LuxeLivingTheme {
                LuxeLivingApp()
            }
        }
    }
}

@Composable
fun LuxeLivingApp(
    viewModel: FurnitureViewModel = viewModel()
) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val cartCount by viewModel.cartTotalItemsCount.collectAsState()
    val wishlistIds by viewModel.wishlistIds.collectAsState()
    val toastMessage by viewModel.toastMessage.collectAsState()

    // Handle system back navigation
    BackHandler(enabled = currentScreen != AppScreen.HOME) {
        viewModel.navigateBack()
    }

    // Auto-dismiss toast
    LaunchedEffect(toastMessage) {
        if (toastMessage != null) {
            delay(2500)
            viewModel.clearToast()
        }
    }

    val showBottomBar = currentScreen != AppScreen.CHECKOUT &&
        currentScreen != AppScreen.ORDER_SUCCESS &&
        currentScreen != AppScreen.AR_PREVIEW

    val showTopBar = currentScreen != AppScreen.AR_PREVIEW &&
        currentScreen != AppScreen.ORDER_SUCCESS

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            if (showTopBar) {
                val canBack = currentScreen != AppScreen.HOME
                val title = when (currentScreen) {
                    AppScreen.HOME -> null
                    AppScreen.CATALOG -> "Catalog"
                    AppScreen.PRODUCT_DETAIL -> null
                    AppScreen.WISHLIST -> "Wishlist"
                    AppScreen.CART -> "Bag"
                    AppScreen.CHECKOUT -> "Checkout"
                    else -> null
                }
                LuxeTopBar(
                    title = title,
                    canNavigateBack = canBack,
                    onNavigateBack = { viewModel.navigateBack() },
                    wishlistCount = wishlistIds.size,
                    cartCount = cartCount,
                    onSearchClick = { viewModel.navigateTo(AppScreen.CATALOG) },
                    onWishlistClick = { viewModel.navigateTo(AppScreen.WISHLIST) },
                    onCartClick = { viewModel.navigateTo(AppScreen.CART) }
                )
            }
        },
        bottomBar = {
            if (showBottomBar) {
                LuxeBottomBar(
                    currentScreen = currentScreen,
                    onNavigate = { viewModel.navigateTo(it) },
                    cartItemCount = cartCount,
                    wishlistCount = wishlistIds.size
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                AppScreen.HOME -> HomeScreen(viewModel = viewModel)
                AppScreen.CATALOG -> CatalogScreen(viewModel = viewModel)
                AppScreen.PRODUCT_DETAIL -> ProductDetailScreen(viewModel = viewModel)
                AppScreen.AR_PREVIEW -> ArRoomPreviewScreen(viewModel = viewModel)
                AppScreen.WISHLIST -> WishlistScreen(viewModel = viewModel)
                AppScreen.CART -> CartScreen(viewModel = viewModel)
                AppScreen.CHECKOUT -> CheckoutScreen(viewModel = viewModel)
                AppScreen.ORDER_SUCCESS -> OrderSuccessScreen(viewModel = viewModel)
            }

            // Global Notification Toast
            AnimatedVisibility(
                visible = toastMessage != null,
                enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(16.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = LuxeEspresso,
                    shadowElevation = 8.dp,
                    modifier = Modifier.fillMaxWidth(0.9f)
                ) {
                    Text(
                        text = toastMessage ?: "",
                        color = Color.White,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                    )
                }
            }
        }
    }
}
