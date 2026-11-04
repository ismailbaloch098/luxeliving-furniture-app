package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material.icons.filled.Weekend
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material.icons.outlined.ViewInAr
import androidx.compose.material.icons.outlined.Weekend
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LuxeEspresso
import com.example.ui.theme.LuxeOat
import com.example.ui.theme.LuxeTerracotta
import com.example.ui.viewmodel.AppScreen

data class NavItem(
    val screen: AppScreen,
    val label: String,
    val activeIcon: ImageVector,
    val inactiveIcon: ImageVector,
    val testTag: String
)

@Composable
fun LuxeBottomBar(
    currentScreen: AppScreen,
    onNavigate: (AppScreen) -> Unit,
    cartItemCount: Int = 0,
    wishlistCount: Int = 0,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        NavItem(
            screen = AppScreen.HOME,
            label = "Curated",
            activeIcon = Icons.Filled.Home,
            inactiveIcon = Icons.Outlined.Home,
            testTag = "nav_home"
        ),
        NavItem(
            screen = AppScreen.CATALOG,
            label = "Catalog",
            activeIcon = Icons.Filled.Weekend,
            inactiveIcon = Icons.Outlined.Weekend,
            testTag = "nav_catalog"
        ),
        NavItem(
            screen = AppScreen.AR_PREVIEW,
            label = "AR Studio",
            activeIcon = Icons.Filled.ViewInAr,
            inactiveIcon = Icons.Outlined.ViewInAr,
            testTag = "nav_ar"
        ),
        NavItem(
            screen = AppScreen.WISHLIST,
            label = "Saved",
            activeIcon = Icons.Filled.Favorite,
            inactiveIcon = Icons.Outlined.FavoriteBorder,
            testTag = "nav_wishlist"
        ),
        NavItem(
            screen = AppScreen.CART,
            label = "Bag",
            activeIcon = Icons.Filled.ShoppingBag,
            inactiveIcon = Icons.Outlined.ShoppingBag,
            testTag = "nav_cart"
        )
    )

    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp,
        shadowElevation = 8.dp,
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                val isSelected = currentScreen == item.screen
                val interactionSource = remember { MutableInteractionSource() }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .testTag(item.testTag)
                        .clip(CircleShape)
                        .clickable(
                            interactionSource = interactionSource,
                            indication = ripple(bounded = true, radius = 28.dp),
                            onClick = { onNavigate(item.screen) }
                        )
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    BadgedBox(
                        badge = {
                            if (item.screen == AppScreen.CART && cartItemCount > 0) {
                                Badge(
                                    containerColor = LuxeEspresso,
                                    contentColor = Color.White
                                ) {
                                    Text("$cartItemCount", fontSize = 10.sp)
                                }
                            } else if (item.screen == AppScreen.WISHLIST && wishlistCount > 0) {
                                Badge(
                                    containerColor = LuxeTerracotta,
                                    contentColor = Color.White
                                ) {
                                    Text("$wishlistCount", fontSize = 10.sp)
                                }
                            }
                        }
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isSelected) LuxeOat.copy(alpha = 0.6f) else Color.Transparent
                                )
                        ) {
                            Icon(
                                imageVector = if (isSelected) item.activeIcon else item.inactiveIcon,
                                contentDescription = item.label,
                                tint = if (isSelected) LuxeEspresso else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }

                    Text(
                        text = item.label,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) LuxeEspresso else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
        }
    }
}
