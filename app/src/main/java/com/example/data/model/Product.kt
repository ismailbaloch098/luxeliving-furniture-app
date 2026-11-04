package com.example.data.model

import androidx.annotation.DrawableRes

enum class ProductCategory(val displayName: String) {
    ALL("All Pieces"),
    LIVING_ROOM("Living Room"),
    BEDROOM("Bedroom"),
    DINING("Dining"),
    LIGHTING("Lighting"),
    WORKSPACE("Workspace"),
    DECOR("Decor & Accents")
}

data class ProductColor(
    val name: String,
    val colorValue: Long, // 0xFF...
    val hexString: String
)

data class Product(
    val id: String,
    val name: String,
    val tagline: String,
    val category: ProductCategory,
    val price: Double,
    val originalPrice: Double? = null,
    val rating: Float,
    val reviewsCount: Int,
    val description: String,
    val dimensions: String,
    val materials: List<String>,
    val colors: List<ProductColor>,
    @DrawableRes val primaryDrawableRes: Int,
    @DrawableRes val galleryDrawables: List<Int>,
    val isFeatured: Boolean = false,
    val isTrending: Boolean = false,
    val tags: List<String> = emptyList(),
    val inStock: Boolean = true,
    val arModelType: String = "furniture"
)

data class CartItem(
    val id: String,
    val product: Product,
    val selectedColor: ProductColor,
    val quantity: Int = 1
)

data class Order(
    val id: String,
    val items: List<CartItem>,
    val subtotal: Double,
    val discount: Double,
    val shipping: Double,
    val tax: Double,
    val total: Double,
    val placedAt: String,
    val status: String,
    val trackingCode: String,
    val shippingName: String,
    val shippingAddress: String,
    val deliveryMethod: String,
    val paymentMethod: String
)

data class CuratedRoom(
    val id: String,
    val title: String,
    val subtitle: String,
    val designerNote: String,
    @DrawableRes val imageRes: Int,
    val productIds: List<String>
)
