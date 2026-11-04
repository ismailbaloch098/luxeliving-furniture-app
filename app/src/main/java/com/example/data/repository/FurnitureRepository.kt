package com.example.data.repository

import com.example.R
import com.example.data.model.CartItem
import com.example.data.model.CuratedRoom
import com.example.data.model.Order
import com.example.data.model.Product
import com.example.data.model.ProductCategory
import com.example.data.model.ProductColor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class FurnitureRepository {

    private val oatColor = ProductColor("Oat Bouclé", 0xFFEBE5DB, "#EBE5DB")
    private val walnutColor = ProductColor("American Walnut", 0xFF5D4037, "#5D4037")
    private val terracottaColor = ProductColor("Warm Terracotta", 0xFFC06141, "#C06141")
    private val oliveColor = ProductColor("Olive Moss", 0xFF4A5540, "#4A5540")
    private val charcoalColor = ProductColor("Obsidian Charcoal", 0xFF282523, "#282523")
    private val creamColor = ProductColor("Warm Ivory", 0xFFFAF7F0, "#FAF7F0")

    private val initialProducts = listOf(
        Product(
            id = "prod_01",
            name = "Elysian Cloud Sectional",
            tagline = "Curved modular sanctuary in textured bouclé",
            category = ProductCategory.LIVING_ROOM,
            price = 2850.0,
            originalPrice = 3200.0,
            rating = 4.9f,
            reviewsCount = 142,
            description = "The Elysian Cloud Sectional redefines modern lounging with architectural curves, deep-seated ergonomics, and plush Italian bouclé fabric. Designed to anchor expansive contemporary living spaces with effortless warmth.",
            dimensions = "W: 114\"  x  D: 68\"  x  H: 32\" (Seat Height: 17\")",
            materials = listOf("Sustainably Harvested FSC Oak Frame", "Textured High-Performance Bouclé", "Dual-Layer CertiPUR-US Memory Foam", "Concealed Solid Brass Connectors"),
            colors = listOf(creamColor, oatColor, charcoalColor, terracottaColor),
            primaryDrawableRes = R.drawable.img_sofa_cloud,
            galleryDrawables = listOf(R.drawable.img_sofa_cloud, R.drawable.img_hero_living),
            isFeatured = true,
            isTrending = true,
            tags = listOf("Bestseller", "Curved Design", "Modular"),
            arModelType = "sofa_curved"
        ),
        Product(
            id = "prod_02",
            name = "Søren Minimalist Armchair",
            tagline = "Sculptural Danish lounge chair in walnut & wool",
            category = ProductCategory.LIVING_ROOM,
            price = 980.0,
            originalPrice = 1150.0,
            rating = 4.8f,
            reviewsCount = 98,
            description = "Handcrafted with sculpted solid American walnut, the Søren Armchair fuses mid-century Danish lineage with organic minimalism. Slanted rear stiles cradle an ergonomic seat upholstered in premium wool bouclé.",
            dimensions = "W: 32\"  x  D: 34\"  x  H: 31\" (Seat Height: 16.5\")",
            materials = listOf("Kiln-Dried American Black Walnut", "Danish Wool Blend Fabric", "Hand-Rubbed Natural Oil Finish", "Mortise & Tenon Joinery"),
            colors = listOf(oatColor, charcoalColor, oliveColor, terracottaColor),
            primaryDrawableRes = R.drawable.img_chair_nordic,
            galleryDrawables = listOf(R.drawable.img_chair_nordic, R.drawable.img_hero_living),
            isFeatured = true,
            isTrending = true,
            tags = listOf("Handcrafted", "Architectural", "Sustainable"),
            arModelType = "lounge_chair"
        ),
        Product(
            id = "prod_03",
            name = "Kyoto Low Horizon Bed",
            tagline = "Zen-inspired platform bed with floating side ledges",
            category = ProductCategory.BEDROOM,
            price = 1950.0,
            originalPrice = null,
            rating = 4.9f,
            reviewsCount = 76,
            description = "Channeling Japanese tatami culture, the Kyoto bed features an expansive floating platform silhouette with integrated cantilevered nightstands and an angled fabric headboard designed for peaceful evening reading.",
            dimensions = "King: W: 92\"  x  L: 96\"  x  H: 28\" (Platform: 9\")",
            materials = listOf("Solid White Oak", "Natural Woven Linen Headboard", "Japanese Joinery Construction", "Eco-friendly Matte Sealer"),
            colors = listOf(oatColor, walnutColor, creamColor),
            primaryDrawableRes = R.drawable.img_hero_living,
            galleryDrawables = listOf(R.drawable.img_hero_living, R.drawable.img_chair_nordic),
            isFeatured = true,
            isTrending = false,
            tags = listOf("Japandi", "Low Platform", "Floating"),
            arModelType = "bed_platform"
        ),
        Product(
            id = "prod_04",
            name = "Palermo Travertine Dining Table",
            tagline = "Honed Italian Roman travertine monolith with fluted base",
            category = ProductCategory.DINING,
            price = 2400.0,
            originalPrice = 2750.0,
            rating = 5.0f,
            reviewsCount = 54,
            description = "Each Palermo table is sculpted from hand-selected Italian Roman travertine stone. The matte-honed circular top balances over twin sculptural fluted pedestal columns, seating up to 8 guests comfortably.",
            dimensions = "Dia: 60\"  x  H: 30\" (Clearance: 28.5\")",
            materials = listOf("100% Natural Italian Travertine", "Matte Honed Surface Treatment", "Internal Reinforced Steel Core", "Anti-Stain Nano Sealer"),
            colors = listOf(creamColor, oatColor, charcoalColor),
            primaryDrawableRes = R.drawable.img_sofa_cloud,
            galleryDrawables = listOf(R.drawable.img_sofa_cloud, R.drawable.img_hero_living),
            isFeatured = false,
            isTrending = true,
            tags = listOf("Natural Stone", "Monolithic", "Artisanal"),
            arModelType = "dining_table"
        ),
        Product(
            id = "prod_05",
            name = "Aura Brushed Brass Chandelier",
            tagline = "Warm dimmable linear pendant light with hand-blown glass",
            category = ProductCategory.LIGHTING,
            price = 680.0,
            originalPrice = 750.0,
            rating = 4.7f,
            reviewsCount = 63,
            description = "The Aura chandelier floats above dining tables or kitchen islands like a luminous sculpture. Five frosted opaline globes diffuse soothing 2700K ambient warmth across your interior.",
            dimensions = "L: 54\"  x  W: 10\"  x  H: 18\" (Adjustable Drop: 24\" - 72\")",
            materials = listOf("Spun Solid Brass", "Hand-Blown Opal Matte Glass", "Braided Cotton Suspension Cord", "Dimmable Integrated Warm LED"),
            colors = listOf(walnutColor, charcoalColor, creamColor),
            primaryDrawableRes = R.drawable.img_hero_living,
            galleryDrawables = listOf(R.drawable.img_hero_living),
            isFeatured = false,
            isTrending = true,
            tags = listOf("Dimmable", "Brass", "Sculptural"),
            arModelType = "lighting_pendant"
        ),
        Product(
            id = "prod_06",
            name = "Oslo Ergonomic Writing Desk",
            tagline = "Clean minimalist workspace with concealed cable management",
            category = ProductCategory.WORKSPACE,
            price = 1120.0,
            originalPrice = null,
            rating = 4.8f,
            reviewsCount = 41,
            description = "Designed for modern creators, the Oslo desk pairs bevelled solid oak surfaces with a stealthy magnetic power strip channel, felt-lined stationary drawer, and soft-chamfered wrist bevel.",
            dimensions = "W: 58\"  x  D: 26\"  x  H: 29.5\"",
            materials = listOf("Solid European White Oak", "Anodized Black Aluminum Trim", "Wool Felt Drawer Lining", "Magnetic Cable Management"),
            colors = listOf(oatColor, walnutColor, charcoalColor),
            primaryDrawableRes = R.drawable.img_chair_nordic,
            galleryDrawables = listOf(R.drawable.img_chair_nordic),
            isFeatured = false,
            isTrending = false,
            tags = listOf("Workspace", "Oak", "Cable Stealth"),
            arModelType = "desk_work"
        ),
        Product(
            id = "prod_07",
            name = "Terra Clay Vessel & Pedestal",
            tagline = "Hand-thrown coarse ceramic urn with architectural plinth",
            category = ProductCategory.DECOR,
            price = 340.0,
            originalPrice = 390.0,
            rating = 4.9f,
            reviewsCount = 89,
            description = "Molded by Portuguese master potters using terracotta and grog-rich clay, each vessel features tactile organic ridging and an earthy matte wash that celebrates natural imperfections.",
            dimensions = "H: 22\"  x  Dia: 14\" (Plinth: H: 18\")",
            materials = listOf("Portuguese Raw Terracotta", "Stone-Washed Matte Finish", "Waterproof Internal Seal", "Oak Footing"),
            colors = listOf(terracottaColor, oatColor, charcoalColor),
            primaryDrawableRes = R.drawable.img_hero_living,
            galleryDrawables = listOf(R.drawable.img_hero_living),
            isFeatured = false,
            isTrending = true,
            tags = listOf("Ceramic", "Hand-Thrown", "Wabi-Sabi"),
            arModelType = "decor_vessel"
        )
    )

    private val _products = MutableStateFlow(initialProducts)
    val products: StateFlow<List<Product>> = _products.asStateFlow()

    private val _wishlist = MutableStateFlow<Set<String>>(setOf("prod_01", "prod_02"))
    val wishlist: StateFlow<Set<String>> = _wishlist.asStateFlow()

    private val _cart = MutableStateFlow<List<CartItem>>(
        listOf(
            CartItem(
                id = "cart_item_1",
                product = initialProducts[0],
                selectedColor = oatColor,
                quantity = 1
            )
        )
    )
    val cart: StateFlow<List<CartItem>> = _cart.asStateFlow()

    private val _promoCode = MutableStateFlow<String?>("LUXE15")
    val promoCode: StateFlow<String?> = _promoCode.asStateFlow()

    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders.asStateFlow()

    val curatedRooms = listOf(
        CuratedRoom(
            id = "room_01",
            title = "The Warm Scandinavian Solarium",
            subtitle = "Nordic curves meet sun-drenched oak floors",
            designerNote = "A study in calm textures: soft tactile bouclé paired with rich walnut accents and natural indirect sunlight.",
            imageRes = R.drawable.img_hero_living,
            productIds = listOf("prod_01", "prod_02", "prod_05")
        ),
        CuratedRoom(
            id = "room_02",
            title = "Japandi Monolith Dining",
            subtitle = "Travertine stone, sculptural lighting, and earth tones",
            designerNote = "Rooted in simplicity and durability. Honest natural materials meant to age with grace.",
            imageRes = R.drawable.img_sofa_cloud,
            productIds = listOf("prod_04", "prod_07")
        )
    )

    fun toggleWishlist(productId: String) {
        _wishlist.update { current ->
            if (current.contains(productId)) current - productId else current + productId
        }
    }

    fun isWishlisted(productId: String): Boolean {
        return _wishlist.value.contains(productId)
    }

    fun addToCart(product: Product, color: ProductColor, quantity: Int = 1) {
        _cart.update { current ->
            val existing = current.find { it.product.id == product.id && it.selectedColor.name == color.name }
            if (existing != null) {
                current.map {
                    if (it.id == existing.id) it.copy(quantity = it.quantity + quantity) else it
                }
            } else {
                current + CartItem(
                    id = UUID.randomUUID().toString(),
                    product = product,
                    selectedColor = color,
                    quantity = quantity
                )
            }
        }
    }

    fun updateCartQuantity(cartItemId: String, delta: Int) {
        _cart.update { current ->
            current.mapNotNull { item ->
                if (item.id == cartItemId) {
                    val newQty = item.quantity + delta
                    if (newQty > 0) item.copy(quantity = newQty) else null
                } else {
                    item
                }
            }
        }
    }

    fun removeFromCart(cartItemId: String) {
        _cart.update { current ->
            current.filterNot { it.id == cartItemId }
        }
    }

    fun applyPromo(code: String): Boolean {
        val trimmed = code.trim().uppercase()
        return if (trimmed == "LUXE15" || trimmed == "WELCOME20" || trimmed == "MINIMAL10") {
            _promoCode.value = trimmed
            true
        } else {
            false
        }
    }

    fun removePromo() {
        _promoCode.value = null
    }

    fun clearCart() {
        _cart.value = emptyList()
    }

    fun placeOrder(
        shippingName: String,
        shippingAddress: String,
        deliveryMethod: String,
        paymentMethod: String
    ): Order {
        val currentItems = _cart.value
        val subtotal = currentItems.sumOf { it.product.price * it.quantity }
        val discountRate = when (_promoCode.value) {
            "LUXE15" -> 0.15
            "WELCOME20" -> 0.20
            "MINIMAL10" -> 0.10
            else -> 0.0
        }
        val discount = subtotal * discountRate
        val shipping = if (deliveryMethod.contains("White Glove")) 150.0 else 0.0
        val tax = (subtotal - discount) * 0.08
        val total = subtotal - discount + shipping + tax

        val dateFormat = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
        val order = Order(
            id = "LX-" + (100000..999999).random(),
            items = currentItems,
            subtotal = subtotal,
            discount = discount,
            shipping = shipping,
            tax = tax,
            total = total,
            placedAt = dateFormat.format(Date()),
            status = "Confirmed & Preparing",
            trackingCode = "LX-TRK-" + UUID.randomUUID().toString().take(8).uppercase(),
            shippingName = shippingName,
            shippingAddress = shippingAddress,
            deliveryMethod = deliveryMethod,
            paymentMethod = paymentMethod
        )

        _orders.update { listOf(order) + it }
        clearCart()
        return order
    }

    fun getProductById(id: String): Product? {
        return _products.value.find { it.id == id }
    }
}
