package com.example.ui.screens.ar

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.RotateRight
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.Product
import com.example.ui.theme.LuxeBorder
import com.example.ui.theme.LuxeEspresso
import com.example.ui.theme.LuxeGold
import com.example.ui.theme.LuxeOat
import com.example.ui.theme.LuxeTerracotta
import com.example.ui.viewmodel.FurnitureViewModel
import kotlin.math.roundToInt

@Composable
fun ArRoomPreviewScreen(
    viewModel: FurnitureViewModel,
    modifier: Modifier = Modifier
) {
    val arState by viewModel.arState.collectAsState()
    val allProducts by viewModel.allProducts.collectAsState()
    val product = arState.product ?: allProducts.firstOrNull() ?: return
    val selectedColor = arState.selectedColor ?: product.colors.first()

    var showControlsDrawer by remember { mutableStateOf(true) }
    var showRotationSlider by remember { mutableStateOf(false) }

    // Lighting mood color scrim
    val lightingColor = when (arState.lightingMood) {
        "Golden Hour" -> Color(0xFFC78440).copy(alpha = 0.22f)
        "Warm Sun" -> Color(0xFFE8D0A9).copy(alpha = 0.12f)
        else -> Color.Transparent
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("ar_preview_screen")
    ) {
        // 1. Photorealistic Room Canvas Background
        Image(
            painter = painterResource(id = R.drawable.img_room_ar_bg),
            contentDescription = "Room Background for AR placement",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Ambient Lighting Scrim
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(lightingColor)
        )

        // 2. Interactive Furniture Placement Layer (Draggable, Scalable, Rotatable)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        viewModel.updateArOffset(dragAmount.x, dragAmount.y)
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .offset {
                        IntOffset(
                            arState.offsetX.roundToInt(),
                            (arState.offsetY + 120f).roundToInt()
                        )
                    }
                    .scale(arState.scale)
                    .rotate(arState.rotation)
                    .size(280.dp),
                contentAlignment = Alignment.Center
            ) {
                // Dimension bounding box lines if enabled
                if (arState.showDimensions) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val strokeWidth = 2.dp.toPx()
                        val dashPathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f)

                        // Ground shadow ellipse
                        drawOval(
                            color = Color.Black.copy(alpha = 0.28f),
                            topLeft = Offset(size.width * 0.1f, size.height * 0.75f),
                            size = androidx.compose.ui.geometry.Size(size.width * 0.8f, size.height * 0.22f)
                        )

                        // Top & corner boundary markers
                        drawRoundRect(
                            color = Color(0xFFC59A45),
                            topLeft = Offset(0f, 0f),
                            size = size,
                            style = androidx.compose.ui.graphics.drawscope.Stroke(
                                width = strokeWidth,
                                pathEffect = dashPathEffect
                            ),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(16f, 16f)
                        )
                    }
                }

                // Photorealistic Furniture Item Asset
                Image(
                    painter = painterResource(id = product.primaryDrawableRes),
                    contentDescription = product.name,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .shadow(
                            elevation = 8.dp,
                            shape = RoundedCornerShape(12.dp),
                            spotColor = Color.Black.copy(alpha = 0.4f)
                        )
                )

                // Dimension tags
                if (arState.showDimensions) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = LuxeEspresso.copy(alpha = 0.85f),
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .offset(y = (-14).dp)
                    ) {
                        Text(
                            text = product.dimensions.split("(").firstOrNull()?.trim() ?: "W: 88\" x D: 38\"",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }
        }

        // 3. Top Control Bar (Back, Product Title, Camera Snapshot, Lighting Toggle)
        Surface(
            color = LuxeEspresso.copy(alpha = 0.85f),
            shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .statusBarsPadding()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = { viewModel.navigateBack() },
                    modifier = Modifier.testTag("ar_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Spatial AR Studio",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = product.name,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.75f),
                        fontSize = 11.sp
                    )
                }

                Row {
                    // Shutter snapshot
                    IconButton(
                        onClick = { viewModel.snapArRoom() },
                        modifier = Modifier.testTag("ar_snapshot_button")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CameraAlt,
                            contentDescription = "Take Snapshot",
                            tint = LuxeGold
                        )
                    }
                }
            }
        }

        // 4. Snapshot Notice Overlay if triggered
        AnimatedVisibility(
            visible = arState.snapshotNotice != null,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 90.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                shadowElevation = 8.dp,
                modifier = Modifier.padding(horizontal = 24.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = LuxeTerracotta,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = arState.snapshotNotice ?: "",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    IconButton(
                        onClick = { viewModel.clearArNotice() },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Text("×", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // 5. Floating AR Tools & Adjustments Panel (Bottom)
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Quick Model Selector (switch between furniture pieces)
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                items(allProducts) { item ->
                    val isCurrent = item.id == product.id
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (isCurrent) LuxeEspresso else Color.White.copy(alpha = 0.9f),
                        border = BorderStroke(1.dp, if (isCurrent) LuxeGold else LuxeBorder),
                        modifier = Modifier
                            .clickable { viewModel.openArPreview(item.id) }
                    ) {
                        Text(
                            text = item.name.split(" ").take(2).joinToString(" "),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                            color = if (isCurrent) Color.White else LuxeEspresso,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            // Controls Card
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    // Feature toggles row: Rotation slider toggle, Dimension box, Lighting mood
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Dimension bounding box toggle
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (arState.showDimensions) LuxeOat else Color.Transparent,
                            border = BorderStroke(1.dp, LuxeBorder),
                            modifier = Modifier.clickable { viewModel.toggleArDimensions() }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Straighten,
                                    contentDescription = "Dimensions",
                                    tint = LuxeEspresso,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (arState.showDimensions) "Bounds On" else "Bounds Off",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        // Rotate 360 toggle
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (showRotationSlider) LuxeOat else Color.Transparent,
                            border = BorderStroke(1.dp, LuxeBorder),
                            modifier = Modifier.clickable { showRotationSlider = !showRotationSlider }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.RotateRight,
                                    contentDescription = "Rotate",
                                    tint = LuxeEspresso,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${arState.rotation.toInt()}°",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        // Lighting mood pill
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = LuxeOat,
                            border = BorderStroke(1.dp, LuxeBorder),
                            modifier = Modifier.clickable {
                                val next = when (arState.lightingMood) {
                                    "Warm Sun" -> "Golden Hour"
                                    "Golden Hour" -> "Studio Pure"
                                    else -> "Warm Sun"
                                }
                                viewModel.setArLighting(next)
                            }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.WbSunny,
                                    contentDescription = "Lighting",
                                    tint = LuxeGold,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = arState.lightingMood,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    // Expandable Rotation Slider
                    AnimatedVisibility(visible = showRotationSlider) {
                        Column(modifier = Modifier.padding(top = 8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Rotation 360°", style = MaterialTheme.typography.labelSmall)
                                Text("${arState.rotation.toInt()}°", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                            }
                            Slider(
                                value = arState.rotation,
                                onValueChange = { viewModel.updateArRotation(it) },
                                valueRange = 0f..360f,
                                colors = SliderDefaults.colors(
                                    thumbColor = LuxeEspresso,
                                    activeTrackColor = LuxeEspresso
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Fabric/Color Swatches & Add to Bag Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Swatches
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            product.colors.forEach { color ->
                                val isSelected = color.name == selectedColor.name
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .size(30.dp)
                                        .clip(CircleShape)
                                        .background(Color(color.colorValue))
                                        .border(
                                            width = if (isSelected) 2.dp else 1.dp,
                                            color = if (isSelected) LuxeEspresso else LuxeBorder,
                                            shape = CircleShape
                                        )
                                        .clickable { viewModel.setArColor(color) }
                                ) {
                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = if (color.hexString == "#282523") Color.White else LuxeEspresso,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                            }
                        }

                        // Add to Bag CTA
                        Button(
                            onClick = {
                                viewModel.addToCart(product, selectedColor)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = LuxeEspresso),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("ar_add_to_bag_button")
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.ShoppingBag,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Add to Bag",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
