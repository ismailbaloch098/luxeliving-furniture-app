package com.example.ui.screens.wishlist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.ProductCard
import com.example.ui.theme.LuxeEspresso
import com.example.ui.theme.LuxeMutedText
import com.example.ui.theme.PlayfairDisplayFamily
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.FurnitureViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun WishlistScreen(
    viewModel: FurnitureViewModel,
    modifier: Modifier = Modifier
) {
    val wishlistIds by viewModel.wishlistIds.collectAsState()
    val allProducts by viewModel.allProducts.collectAsState()

    val wishlistedProducts = allProducts.filter { wishlistIds.contains(it.id) }
    val totalWishlistValue = wishlistedProducts.sumOf { it.price }

    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US).apply {
        maximumFractionDigits = 0
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("wishlist_screen")
    ) {
        // Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            Text(
                text = "Saved Pieces",
                fontFamily = PlayfairDisplayFamily,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "${wishlistedProducts.size} items in your personal architectural wishlist • Total ${currencyFormatter.format(totalWishlistValue)}",
                style = MaterialTheme.typography.bodySmall,
                color = LuxeMutedText
            )
        }

        if (wishlistedProducts.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Outlined.FavoriteBorder,
                        contentDescription = null,
                        tint = LuxeMutedText,
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Your Wishlist is Empty",
                        fontFamily = PlayfairDisplayFamily,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Save furniture pieces you love while exploring our collections.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = LuxeMutedText,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = { viewModel.navigateTo(AppScreen.CATALOG) },
                        colors = ButtonDefaults.buttonColors(containerColor = LuxeEspresso),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Explore Catalog")
                    }
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 96.dp, top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(wishlistedProducts, key = { it.id }) { product ->
                    ProductCard(
                        product = product,
                        isWishlisted = true,
                        onCardClick = { viewModel.openProductDetail(product.id) },
                        onWishlistToggle = { viewModel.toggleWishlist(product.id) },
                        onQuickAdd = { viewModel.addToCart(product) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
