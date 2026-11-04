package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.CartItem
import com.example.data.model.Order
import com.example.data.model.Product
import com.example.data.model.ProductCategory
import com.example.data.model.ProductColor
import com.example.data.repository.FurnitureRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

enum class AppScreen {
    HOME,
    CATALOG,
    PRODUCT_DETAIL,
    AR_PREVIEW,
    WISHLIST,
    CART,
    CHECKOUT,
    ORDER_SUCCESS
}

enum class SortOption(val title: String) {
    RECOMMENDED("Curated Picks"),
    PRICE_LOW_HIGH("Price: Low to High"),
    PRICE_HIGH_LOW("Price: High to Low"),
    TOP_RATED("Highest Rated")
}

data class ArViewState(
    val product: Product? = null,
    val selectedColor: ProductColor? = null,
    val rotation: Float = 0f,
    val scale: Float = 1.0f,
    val offsetX: Float = 0f,
    val offsetY: Float = 0f,
    val showDimensions: Boolean = true,
    val lightingMood: String = "Warm Sun", // "Warm Sun", "Golden Hour", "Studio Neutral"
    val snapshotNotice: String? = null
)

data class CheckoutFormData(
    val fullName: String = "Julian Vance",
    val streetAddress: String = "742 Evergreen Terrace, Apt 4B",
    val city: String = "San Francisco",
    val postalCode: String = "94107",
    val deliveryMethod: String = "White Glove Delivery & Setup ($150)",
    val paymentMethod: String = "Apple Pay / Google Pay"
)

class FurnitureViewModel(
    private val repository: FurnitureRepository = FurnitureRepository()
) : ViewModel() {

    private val _currentScreen = MutableStateFlow(AppScreen.HOME)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val screenBackStack = mutableListOf(AppScreen.HOME)

    private val _selectedProductId = MutableStateFlow<String?>("prod_01")
    val selectedProductId: StateFlow<String?> = _selectedProductId.asStateFlow()

    private val _selectedDetailColor = MutableStateFlow<ProductColor?>(null)
    val selectedDetailColor: StateFlow<ProductColor?> = _selectedDetailColor.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow(ProductCategory.ALL)
    val selectedCategory: StateFlow<ProductCategory> = _selectedCategory.asStateFlow()

    private val _selectedSort = MutableStateFlow(SortOption.RECOMMENDED)
    val selectedSort: StateFlow<SortOption> = _selectedSort.asStateFlow()

    private val _priceRangeMax = MutableStateFlow(3500f)
    val priceRangeMax: StateFlow<Float> = _priceRangeMax.asStateFlow()

    val allProducts: StateFlow<List<Product>> = repository.products
    val wishlistIds: StateFlow<Set<String>> = repository.wishlist
    val cartItems: StateFlow<List<CartItem>> = repository.cart
    val promoCode: StateFlow<String?> = repository.promoCode
    val orders: StateFlow<List<Order>> = repository.orders
    val curatedRooms = repository.curatedRooms

    private val _arState = MutableStateFlow(ArViewState())
    val arState: StateFlow<ArViewState> = _arState.asStateFlow()

    private val _checkoutForm = MutableStateFlow(CheckoutFormData())
    val checkoutForm: StateFlow<CheckoutFormData> = _checkoutForm.asStateFlow()

    private val _latestOrder = MutableStateFlow<Order?>(null)
    val latestOrder: StateFlow<Order?> = _latestOrder.asStateFlow()

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    // Filtered products list
    val filteredProducts: StateFlow<List<Product>> = combine(
        allProducts,
        _searchQuery,
        _selectedCategory,
        _selectedSort,
        _priceRangeMax
    ) { products, query, category, sort, maxPrice ->
        var list = products.filter { product ->
            val matchesCategory = (category == ProductCategory.ALL) || (product.category == category)
            val matchesQuery = query.isBlank() ||
                product.name.contains(query, ignoreCase = true) ||
                product.description.contains(query, ignoreCase = true) ||
                product.tags.any { it.contains(query, ignoreCase = true) }
            val matchesPrice = product.price <= maxPrice
            matchesCategory && matchesQuery && matchesPrice
        }

        list = when (sort) {
            SortOption.RECOMMENDED -> list.sortedByDescending { it.isFeatured }
            SortOption.PRICE_LOW_HIGH -> list.sortedBy { it.price }
            SortOption.PRICE_HIGH_LOW -> list.sortedByDescending { it.price }
            SortOption.TOP_RATED -> list.sortedByDescending { it.rating }
        }
        list
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Cart calculations
    val cartSubtotal: StateFlow<Double> = combine(cartItems) { items ->
        items[0].sumOf { it.product.price * it.quantity }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val cartTotalItemsCount: StateFlow<Int> = combine(cartItems) { items ->
        items[0].sumOf { it.quantity }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Navigation
    fun navigateTo(screen: AppScreen) {
        if (_currentScreen.value != screen) {
            screenBackStack.add(_currentScreen.value)
            _currentScreen.value = screen
        }
    }

    fun navigateBack(): Boolean {
        if (screenBackStack.isNotEmpty()) {
            val previous = screenBackStack.removeAt(screenBackStack.size - 1)
            _currentScreen.value = previous
            return true
        }
        return false
    }

    fun openProductDetail(productId: String) {
        _selectedProductId.value = productId
        val prod = repository.getProductById(productId)
        _selectedDetailColor.value = prod?.colors?.firstOrNull()
        navigateTo(AppScreen.PRODUCT_DETAIL)
    }

    fun selectDetailColor(color: ProductColor) {
        _selectedDetailColor.value = color
    }

    fun openArPreview(productId: String? = null) {
        val targetId = productId ?: _selectedProductId.value ?: "prod_01"
        val prod = repository.getProductById(targetId)
        if (prod != null) {
            val color = _selectedDetailColor.value ?: prod.colors.first()
            _arState.value = ArViewState(
                product = prod,
                selectedColor = color,
                rotation = 0f,
                scale = 1.0f,
                offsetX = 0f,
                offsetY = 0f,
                showDimensions = true,
                lightingMood = "Warm Sun"
            )
            navigateTo(AppScreen.AR_PREVIEW)
        }
    }

    // AR controls
    fun updateArRotation(degrees: Float) {
        _arState.update { it.copy(rotation = degrees) }
    }

    fun updateArScale(scaleFactor: Float) {
        _arState.update { it.copy(scale = scaleFactor.coerceIn(0.5f, 1.8f)) }
    }

    fun updateArOffset(deltaX: Float, deltaY: Float) {
        _arState.update { it.copy(offsetX = it.offsetX + deltaX, offsetY = it.offsetY + deltaY) }
    }

    fun toggleArDimensions() {
        _arState.update { it.copy(showDimensions = !it.showDimensions) }
    }

    fun setArLighting(mood: String) {
        _arState.update { it.copy(lightingMood = mood) }
    }

    fun setArColor(color: ProductColor) {
        _arState.update { it.copy(selectedColor = color) }
    }

    fun snapArRoom() {
        _arState.update { it.copy(snapshotNotice = "High-res spatial render saved to your room inspiration scrapbook!") }
    }

    fun clearArNotice() {
        _arState.update { it.copy(snapshotNotice = null) }
    }

    // Wishlist
    fun toggleWishlist(productId: String) {
        repository.toggleWishlist(productId)
    }

    fun isWishlisted(productId: String): Boolean {
        return repository.isWishlisted(productId)
    }

    // Cart operations
    fun addToCart(product: Product, color: ProductColor? = null, quantity: Int = 1) {
        val selected = color ?: product.colors.first()
        repository.addToCart(product, selected, quantity)
        _toastMessage.value = "Added \"${product.name}\" to your cart"
    }

    fun updateCartQuantity(cartItemId: String, delta: Int) {
        repository.updateCartQuantity(cartItemId, delta)
    }

    fun removeFromCart(cartItemId: String) {
        repository.removeFromCart(cartItemId)
    }

    fun applyPromoCode(code: String): Boolean {
        val success = repository.applyPromo(code)
        if (success) {
            _toastMessage.value = "Promo code $code applied successfully!"
        } else {
            _toastMessage.value = "Invalid code. Try LUXE15 or WELCOME20"
        }
        return success
    }

    fun removePromoCode() {
        repository.removePromo()
    }

    // Filter & Search
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setCategory(category: ProductCategory) {
        _selectedCategory.value = category
    }

    fun setSort(sort: SortOption) {
        _selectedSort.value = sort
    }

    fun setPriceRangeMax(max: Float) {
        _priceRangeMax.value = max
    }

    // Checkout
    fun updateCheckoutAddress(fullName: String, address: String, city: String, zip: String) {
        _checkoutForm.update {
            it.copy(
                fullName = fullName,
                streetAddress = address,
                city = city,
                postalCode = zip
            )
        }
    }

    fun updateDeliveryMethod(method: String) {
        _checkoutForm.update { it.copy(deliveryMethod = method) }
    }

    fun updatePaymentMethod(method: String) {
        _checkoutForm.update { it.copy(paymentMethod = method) }
    }

    fun submitOrder() {
        val form = _checkoutForm.value
        val order = repository.placeOrder(
            shippingName = form.fullName,
            shippingAddress = "${form.streetAddress}, ${form.city} ${form.postalCode}",
            deliveryMethod = form.deliveryMethod,
            paymentMethod = form.paymentMethod
        )
        _latestOrder.value = order
        screenBackStack.clear()
        screenBackStack.add(AppScreen.HOME)
        _currentScreen.value = AppScreen.ORDER_SUCCESS
    }

    fun clearToast() {
        _toastMessage.value = null
    }

    fun getProduct(id: String): Product? = repository.getProductById(id)
}
