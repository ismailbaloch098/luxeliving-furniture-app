package com.example.ui.screens.checkout

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LuxeBorder
import com.example.ui.theme.LuxeEspresso
import com.example.ui.theme.LuxeLinen
import com.example.ui.theme.LuxeMutedText
import com.example.ui.theme.LuxeOlive
import com.example.ui.theme.LuxeTerracotta
import com.example.ui.theme.PlayfairDisplayFamily
import com.example.ui.viewmodel.FurnitureViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun CheckoutScreen(
    viewModel: FurnitureViewModel,
    modifier: Modifier = Modifier
) {
    val form by viewModel.checkoutForm.collectAsState()
    val cartItems by viewModel.cartItems.collectAsState()
    val subtotal by viewModel.cartSubtotal.collectAsState()
    val promoCode by viewModel.promoCode.collectAsState()

    var fullName by remember { mutableStateOf(form.fullName) }
    var address by remember { mutableStateOf(form.streetAddress) }
    var city by remember { mutableStateOf(form.city) }
    var zip by remember { mutableStateOf(form.postalCode) }

    val isWhiteGlove = form.deliveryMethod.contains("White Glove")
    val shipping = if (isWhiteGlove) 150.0 else 0.0

    val discountRate = when (promoCode) {
        "LUXE15" -> 0.15
        "WELCOME20" -> 0.20
        "MINIMAL10" -> 0.10
        else -> 0.0
    }
    val discount = subtotal * discountRate
    val tax = (subtotal - discount) * 0.085
    val grandTotal = subtotal - discount + shipping + tax

    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US).apply {
        maximumFractionDigits = 0
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(top = 8.dp, bottom = 120.dp)
                .testTag("checkout_screen")
        ) {
            Text(
                text = "Checkout",
                fontFamily = PlayfairDisplayFamily,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = LuxeOlive,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "256-bit Encrypted Architectural Checkout",
                    style = MaterialTheme.typography.labelSmall,
                    color = LuxeOlive,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 1. Delivery Address Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, LuxeBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.Home,
                            contentDescription = null,
                            tint = LuxeEspresso,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Delivery Destination",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = fullName,
                        onValueChange = {
                            fullName = it
                            viewModel.updateCheckoutAddress(fullName, address, city, zip)
                        },
                        label = { Text("Recipient Name") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = LuxeEspresso,
                            unfocusedBorderColor = LuxeBorder
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("checkout_name_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = address,
                        onValueChange = {
                            address = it
                            viewModel.updateCheckoutAddress(fullName, address, city, zip)
                        },
                        label = { Text("Street Address & Apartment") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = LuxeEspresso,
                            unfocusedBorderColor = LuxeBorder
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("checkout_address_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = city,
                            onValueChange = {
                                city = it
                                viewModel.updateCheckoutAddress(fullName, address, city, zip)
                            },
                            label = { Text("City") },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = LuxeEspresso,
                                unfocusedBorderColor = LuxeBorder
                            ),
                            modifier = Modifier.weight(1f)
                        )

                        OutlinedTextField(
                            value = zip,
                            onValueChange = {
                                zip = it
                                viewModel.updateCheckoutAddress(fullName, address, city, zip)
                            },
                            label = { Text("Postal Code") },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = LuxeEspresso,
                                unfocusedBorderColor = LuxeBorder
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2. White Glove Delivery Service Selection
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, LuxeBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocalShipping,
                            contentDescription = null,
                            tint = LuxeEspresso,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Delivery Method",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Option A: White Glove
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isWhiteGlove) LuxeLinen else Color.Transparent,
                        border = BorderStroke(1.dp, if (isWhiteGlove) LuxeEspresso else LuxeBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.updateDeliveryMethod("White Glove Delivery & Setup ($150)")
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = isWhiteGlove,
                                onClick = { viewModel.updateDeliveryMethod("White Glove Delivery & Setup ($150)") },
                                colors = RadioButtonDefaults.colors(selectedColor = LuxeEspresso)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "White Glove Delivery & In-Home Assembly",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Full room placement, packaging removal, and structural assembly • $150",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = LuxeMutedText
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Option B: Curbside
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (!isWhiteGlove) LuxeLinen else Color.Transparent,
                        border = BorderStroke(1.dp, if (!isWhiteGlove) LuxeEspresso else LuxeBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.updateDeliveryMethod("Standard Curbside Freight (Complimentary)")
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = !isWhiteGlove,
                                onClick = { viewModel.updateDeliveryMethod("Standard Curbside Freight (Complimentary)") },
                                colors = RadioButtonDefaults.colors(selectedColor = LuxeEspresso)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Standard Curbside Freight",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Carefully crated threshold delivery • Complimentary",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = LuxeMutedText
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 3. Payment Method Selection
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, LuxeBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Payment,
                            contentDescription = null,
                            tint = LuxeEspresso,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Payment Method",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    val methods = listOf(
                        "Apple Pay / Google Pay",
                        "Credit Card (Ending in 4029)",
                        "Klarna (4 payments of ${currencyFormatter.format(grandTotal / 4)})"
                    )

                    methods.forEach { method ->
                        val isSelected = form.paymentMethod == method
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) LuxeLinen else Color.Transparent,
                            border = BorderStroke(1.dp, if (isSelected) LuxeEspresso else LuxeBorder),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable { viewModel.updatePaymentMethod(method) }
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = isSelected,
                                    onClick = { viewModel.updatePaymentMethod(method) },
                                    colors = RadioButtonDefaults.colors(selectedColor = LuxeEspresso)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = method,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 4. Order Summary Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = LuxeLinen),
                border = BorderStroke(1.dp, LuxeBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Total Payment Summary",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Items Subtotal (${cartItems.size} types)", color = LuxeMutedText)
                        Text(currencyFormatter.format(subtotal), fontWeight = FontWeight.SemiBold)
                    }
                    if (discount > 0) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Discount ($promoCode)", color = LuxeOlive)
                            Text("-${currencyFormatter.format(discount)}", color = LuxeOlive, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Shipping & White Glove", color = LuxeMutedText)
                        Text(if (shipping == 0.0) "Complimentary" else currencyFormatter.format(shipping), fontWeight = FontWeight.SemiBold)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Estimated Tax", color = LuxeMutedText)
                        Text(currencyFormatter.format(tax), fontWeight = FontWeight.SemiBold)
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = LuxeBorder)
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Amount Due", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text(
                            text = currencyFormatter.format(grandTotal),
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        // Sticky Place Order CTA
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp,
            shadowElevation = 12.dp,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .navigationBarsPadding()
        ) {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)) {
                Button(
                    onClick = { viewModel.submitOrder() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LuxeEspresso,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("place_order_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Authorize & Place Order • ${currencyFormatter.format(grandTotal)}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}
