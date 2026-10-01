package com.robogyaan.invoice.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.robogyaan.invoice.ui.neoBrutal
import com.robogyaan.invoice.ui.neoBrutalClickable
import com.robogyaan.invoice.ui.theme.NeoBlack
import com.robogyaan.invoice.ui.theme.NeoYellow
import com.robogyaan.invoice.ui.theme.PoppinsFontFamily
import com.robogyaan.invoice.ui.theme.VirgilFontFamily

@Composable
fun NeoBrutalCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.White,
    borderColor: Color = Color.Black,
    borderWidth: Dp = 2.dp,
    shadowOffset: Dp = 4.dp,
    cornerRadius: Dp = 8.dp,
    title: String? = null,
    badge: String? = null,
    headerAction: @Composable (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .neoBrutal(
                backgroundColor = backgroundColor,
                borderColor = borderColor,
                borderWidth = borderWidth,
                shadowOffset = shadowOffset,
                cornerRadius = cornerRadius
            )
            .padding(14.dp)
    ) {
        if (title != null || badge != null || headerAction != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left: Title written normally
                if (title != null) {
                    Text(
                        text = title,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black,
                        color = NeoBlack,
                        fontFamily = VirgilFontFamily,
                        lineHeight = 21.sp,
                        style = TextStyle(
                            fontFamily = VirgilFontFamily,
                            fontWeight = FontWeight.Black,
                            fontSynthesis = androidx.compose.ui.text.font.FontSynthesis.Weight
                        ),
                        modifier = Modifier.weight(1f, fill = false)
                    )
                } else {
                    Spacer(modifier = Modifier.width(1.dp))
                }

                // Top Right: Badge (Recipient / Sender) & Actions sitting a little upper
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.offset(y = (-3).dp)
                ) {
                    if (badge != null) {
                        Box(
                            modifier = Modifier
                                .background(Color.Black, RoundedCornerShape(4.dp))
                                .padding(horizontal = 7.dp, vertical = 2.5.dp)
                        ) {
                            Text(
                                text = badge.uppercase(),
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = VirgilFontFamily
                            )
                        }
                    }
                    headerAction?.invoke()
                }
            }
        }
        content()
    }
}

@Composable
fun NeoBrutalButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = NeoYellow,
    contentColor: Color = Color.Black,
    borderColor: Color = Color.Black,
    enabled: Boolean = true,
    icon: (@Composable () -> Unit)? = null
) {
    Box(
        modifier = modifier
            .neoBrutalClickable(
                backgroundColor = backgroundColor,
                borderColor = borderColor,
                enabled = enabled,
                onClick = onClick
            )
            .padding(horizontal = 14.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            icon?.invoke()
            Text(
                text = text,
                color = contentColor,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                fontFamily = VirgilFontFamily
            )
        }
    }
}

@Composable
fun NeoBrutalTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Column(modifier = modifier) {
        Text(
            text = label.uppercase(),
            fontSize = 11.sp,
            fontWeight = FontWeight.Black,
            color = NeoBlack,
            fontFamily = VirgilFontFamily,
            modifier = Modifier.padding(bottom = 3.dp)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .neoBrutal(
                    backgroundColor = Color.White,
                    shadowOffset = 2.5.dp,
                    cornerRadius = 6.dp
                )
                .padding(horizontal = 10.dp, vertical = 8.dp)
        ) {
            if (value.isEmpty() && placeholder.isNotEmpty()) {
                Text(
                    text = placeholder,
                    color = Color.Gray,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal,
                    fontFamily = PoppinsFontFamily
                )
            }
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                cursorBrush = SolidColor(Color.Black),
                textStyle = TextStyle(
                    fontFamily = PoppinsFontFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 13.sp,
                    color = NeoBlack
                ),
                keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun NeoBrutalAlertDialog(
    isOpen: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    title: String,
    message: String,
    confirmText: String = "Confirm",
    cancelText: String = "Cancel",
    isDanger: Boolean = false,
    isDarkMode: Boolean = false
) {
    if (!isOpen) return

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        // Translucent overlay covering the entire screen (Dark in dark mode, light in light mode)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    if (isDarkMode) Color.Black.copy(alpha = 0.75f)
                    else Color.White.copy(alpha = 0.78f)
                )
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onDismiss() },
            contentAlignment = Alignment.Center
        ) {
            // Centered Neo-Brutalist Alert Card (Dark card in dark mode, off-white in light mode)
            Box(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .fillMaxWidth(0.92f)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { /* Absorb clicks on dialog surface */ }
                    .neoBrutal(
                        backgroundColor = if (isDarkMode) Color(0xFF18181B) else Color(0xFFFDFBF7),
                        borderColor = if (isDarkMode) Color(0xFF3F3F46) else Color.Black,
                        borderWidth = 3.dp,
                        shadowOffset = if (isDarkMode) 0.dp else 6.dp,
                        cornerRadius = 12.dp
                    )
            ) {
                Column {
                    // Header Bar (Red if isDanger, else NeoYellow)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                if (isDanger) Color(0xFFFF4D4D) else NeoYellow,
                                RoundedCornerShape(topStart = 9.dp, topEnd = 9.dp)
                            )
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = title.uppercase(),
                            fontFamily = VirgilFontFamily,
                            fontWeight = FontWeight.Black,
                            fontSize = 15.sp,
                            color = if (isDanger && isDarkMode) Color.White else Color.Black
                        )
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .background(
                                    if (isDarkMode) Color(0xFF27272A) else Color.White,
                                    RoundedCornerShape(4.dp)
                                )
                                .border(
                                    1.5.dp,
                                    if (isDarkMode) Color(0xFF52525B) else Color.Black,
                                    RoundedCornerShape(4.dp)
                                )
                                .clickable { onDismiss() },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "✕",
                                fontWeight = FontWeight.Black,
                                fontSize = 12.sp,
                                color = if (isDarkMode) Color.White else Color.Black
                            )
                        }
                    }

                    // Divider
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(2.5.dp)
                            .background(if (isDarkMode) Color(0xFF3F3F46) else Color.Black)
                    )

                    // Message Body in Virgil Font (White text in dark mode, black in light mode)
                    Text(
                        text = message,
                        fontFamily = VirgilFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = if (isDarkMode) Color.White else Color.Black,
                        lineHeight = 22.sp,
                        modifier = Modifier.padding(20.dp)
                    )

                    // Divider
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(2.5.dp)
                            .background(if (isDarkMode) Color(0xFF3F3F46) else Color.Black)
                    )

                    // Actions Row with Neo-Brutalist Buttons
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                if (isDarkMode) Color(0xFF1E1E22) else Color(0xFFF5F5F5),
                                RoundedCornerShape(bottomStart = 9.dp, bottomEnd = 9.dp)
                            )
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        NeoBrutalButton(
                            text = cancelText,
                            onClick = onDismiss,
                            backgroundColor = if (isDarkMode) Color(0xFF27272A) else Color.White,
                            contentColor = if (isDarkMode) Color.White else Color.Black,
                            borderColor = if (isDarkMode) Color(0xFF52525B) else Color.Black
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        NeoBrutalButton(
                            text = confirmText,
                            onClick = {
                                onConfirm()
                                onDismiss()
                            },
                            backgroundColor = if (isDanger) Color(0xFFFF4D4D) else NeoYellow,
                            contentColor = if (isDanger) Color.White else Color.Black,
                            borderColor = if (isDarkMode) Color(0xFF7F1D1D) else Color.Black
                        )
                    }
                }
            }
        }
    }
}

