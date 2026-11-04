package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.ProductCategory
import com.example.data.repository.FurnitureRepository
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.FurnitureViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context matches LuxeLiving`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("LuxeLiving", appName)
    }

    @Test
    fun `test repository catalog and wishlist toggle`() {
        val repository = FurnitureRepository()
        val products = repository.products.value
        assertTrue("Catalog should contain curated furniture", products.isNotEmpty())

        val firstProduct = products.first()
        val initialWishlisted = repository.isWishlisted(firstProduct.id)
        repository.toggleWishlist(firstProduct.id)
        assertEquals(!initialWishlisted, repository.isWishlisted(firstProduct.id))
    }

    @Test
    fun `test promo code validation and checkout`() {
        val repository = FurnitureRepository()
        assertTrue("LUXE15 should be valid", repository.applyPromo("LUXE15"))
        assertFalse("INVALID_CODE should fail", repository.applyPromo("FAKE"))

        val order = repository.placeOrder(
            shippingName = "Elena Vance",
            shippingAddress = "100 Pine St, San Francisco, CA",
            deliveryMethod = "White Glove Delivery & Setup ($150)",
            paymentMethod = "Apple Pay"
        )
        assertNotNull(order.id)
        assertTrue(order.total > 0)
        assertEquals(0, repository.cart.value.size)
    }

    @Test
    fun `test viewmodel screen flow navigation and AR state`() {
        val viewModel = FurnitureViewModel()
        assertEquals(AppScreen.HOME, viewModel.currentScreen.value)

        viewModel.navigateTo(AppScreen.CATALOG)
        assertEquals(AppScreen.CATALOG, viewModel.currentScreen.value)

        viewModel.openArPreview("prod_01")
        assertEquals(AppScreen.AR_PREVIEW, viewModel.currentScreen.value)
        assertNotNull(viewModel.arState.value.product)
        assertEquals("prod_01", viewModel.arState.value.product?.id)

        viewModel.updateArRotation(45f)
        assertEquals(45f, viewModel.arState.value.rotation, 0.01f)

        viewModel.navigateBack()
        assertEquals(AppScreen.CATALOG, viewModel.currentScreen.value)
    }
}
