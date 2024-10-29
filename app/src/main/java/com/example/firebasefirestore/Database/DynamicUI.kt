package com.example.firebasefirestore.Database

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.firebasefirestore.R


@Composable
fun DynamicWalletCard() {
    // State to control the background color and text color on hover
    var backgroundColor by remember { mutableStateOf(Color(0xFFFFFFFF)) }
    var circleColor by remember { mutableStateOf(Color(0xFFEDEDED)) }
    var textColor by remember { mutableStateOf(Color(0xFF4C5656)) }

    // Icon Painter Resource (assuming you have a wallet image in drawable)
    val walletIcon: Painter = painterResource(id = R.drawable.applelogo) // Replace with your wallet icon

    Card(
        modifier = Modifier
            .size(width = 220.dp, height = 320.dp)
            .pointerInput(Unit) {
                // Await pointer event scope
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        event.changes.forEach { change ->
                            if (change.pressed) {
                                // Change colors when the pointer hovers
                                backgroundColor = Color(0xFFceb2fc) // Change background color
                                circleColor = Color(0xFFAC8BE9)     // Change circle color
                                textColor = Color.White              // Change text color
                            } else {
                                // Reset to original colors when the pointer leaves
                                backgroundColor = Color(0xFFFFFFFF)
                                circleColor = Color(0xFFEDEDED)
                                textColor = Color(0xFF4C5656)
                            }
                        }
                    }
                }
            },
        shape = RoundedCornerShape(topEnd = 10.dp),
        elevation = CardDefaults.elevatedCardElevation(8.dp),
        content = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                // Circle that holds the wallet icon
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(131.dp)
                        .clip(CircleShape)
                        .background(circleColor)
                ) {
                    // Wallet Icon inside the circle
                    Image(
                        painter = walletIcon,
                        contentDescription = "Wallet Icon",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(60.dp)
                    )
                }

                // Text below the circle
                Text(
                    text = "Wallet",
                    fontSize = 17.sp,
                    color = textColor,
                    modifier = Modifier.padding(top = 20.dp)
                )
            }
        }
    )
}

