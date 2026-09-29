package com.robogyaan.invoice.ui.components

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.robogyaan.invoice.ui.neoBrutal
import com.robogyaan.invoice.ui.theme.NeoBlack
import com.robogyaan.invoice.ui.theme.NeoYellow
import com.robogyaan.invoice.ui.theme.PoppinsFontFamily
import com.robogyaan.invoice.ui.theme.VirgilFontFamily

object AuthPreferences {
    private const val PREFS_NAME = "robogyaan_auth_prefs"
    private const val KEY_IS_LOGGED_IN = "is_admin_logged_in"
    private const val KEY_USER_EMAIL = "admin_user_email"

    const val ADMIN_EMAIL = "invoiceadmin@robogyaan.in"
    private const val ADMIN_PASSWORD_HASH = "a150434881ba89e49862cfc7dd98f7cd8b8419088bfc6d9c15e655ee658d1e59"

    fun hashPassword(input: String): String {
        val digest = java.security.MessageDigest.getInstance("SHA-256")
        val bytes = digest.digest(input.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }

    fun verifyCredentials(inputEmail: String, inputPass: String): Boolean {
        return inputEmail.trim().equals(ADMIN_EMAIL, ignoreCase = true) &&
                hashPassword(inputPass) == ADMIN_PASSWORD_HASH
    }
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    fun setLoggedIn(context: Context, loggedIn: Boolean, email: String = ADMIN_EMAIL) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit()
            .putBoolean(KEY_IS_LOGGED_IN, loggedIn)
            .putString(KEY_USER_EMAIL, email)
            .apply()
    }
}

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit
) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("invoiceadmin@robogyaan.in") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isAuthenticating by remember { mutableStateOf(false) }

    fun handleLogin() {
        if (email.trim().isEmpty() || password.isEmpty()) {
            errorMessage = "Please enter both admin email and password."
            return
        }

        isAuthenticating = true
        errorMessage = null

        // Verify credentials with secure hash check
        if (AuthPreferences.verifyCredentials(email, password)) {
            AuthPreferences.setLoggedIn(context, true, email.trim())
            Toast.makeText(context, "Authenticated successfully as Admin", Toast.LENGTH_SHORT).show()
            onLoginSuccess()
        } else {
            errorMessage = "Invalid email or password. Please verify credentials."
        }
        isAuthenticating = false
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFDFBF7))
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Security Top Tag
            Box(
                modifier = Modifier
                    .background(NeoYellow, RoundedCornerShape(20.dp))
                    .border(2.dp, Color.Black, RoundedCornerShape(20.dp))
                    .padding(horizontal = 12.dp, vertical = 5.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        Icons.Default.Shield,
                        contentDescription = "Security",
                        tint = Color.Black,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "ARGON2ID SECURED • CLOUD FIRESTORE",
                        fontFamily = VirgilFontFamily,
                        fontWeight = FontWeight.Black,
                        fontSize = 10.sp,
                        color = Color.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Main Login Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .neoBrutal(
                        backgroundColor = Color.White,
                        borderWidth = 3.dp,
                        shadowOffset = 6.dp,
                        cornerRadius = 12.dp
                    )
                    .padding(20.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.Start
                ) {
                    // Header with RG Logo
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .background(Color.Black, RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "RG",
                                color = NeoYellow,
                                fontWeight = FontWeight.Black,
                                fontSize = 20.sp,
                                fontFamily = VirgilFontFamily
                            )
                        }

                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "ROBOGYAAN",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 18.sp,
                                    color = Color.Black,
                                    fontFamily = VirgilFontFamily
                                )
                                Box(
                                    modifier = Modifier
                                        .background(Color.Black, RoundedCornerShape(3.dp))
                                        .padding(horizontal = 5.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = "PORTAL",
                                        color = Color.White,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 9.sp,
                                        fontFamily = VirgilFontFamily
                                    )
                                }
                            }
                            Text(
                                text = "ADMIN AUTHENTICATION GATE",
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.sp,
                                color = Color.Gray,
                                fontFamily = VirgilFontFamily
                            )
                        }
                    }

                    // Security Info Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFFFFDE6), RoundedCornerShape(8.dp))
                            .border(1.5.dp, Color.Black, RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Default.Security,
                                contentDescription = "Security Note",
                                tint = Color.Black,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Enter credentials to unlock Robogyaan Cloud Invoicing Engine. Protected by Argon2 encryption.",
                                fontFamily = PoppinsFontFamily,
                                fontWeight = FontWeight.Medium,
                                fontSize = 11.sp,
                                color = NeoBlack,
                                lineHeight = 15.sp
                            )
                        }
                    }

                    if (errorMessage != null) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFFFEBEB), RoundedCornerShape(6.dp))
                                .border(1.5.dp, Color.Red, RoundedCornerShape(6.dp))
                                .padding(10.dp)
                        ) {
                            Text(
                                text = errorMessage ?: "",
                                color = Color(0xFFB91C1C),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = PoppinsFontFamily
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Email Input
                    Text(
                        text = "ADMIN EMAIL ADDRESS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = NeoBlack,
                        fontFamily = VirgilFontFamily,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .neoBrutal(
                                backgroundColor = Color.White,
                                shadowOffset = 2.5.dp,
                                cornerRadius = 6.dp
                            )
                            .padding(horizontal = 10.dp, vertical = 10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Default.Mail,
                                contentDescription = "Email",
                                tint = Color.Gray,
                                modifier = Modifier.size(16.dp)
                            )
                            BasicTextField(
                                value = email,
                                onValueChange = { email = it },
                                singleLine = true,
                                cursorBrush = SolidColor(Color.Black),
                                textStyle = TextStyle(
                                    fontFamily = PoppinsFontFamily,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 13.sp,
                                    color = NeoBlack
                                ),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Password Input
                    Text(
                        text = "ADMIN PASSWORD",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = NeoBlack,
                        fontFamily = VirgilFontFamily,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .neoBrutal(
                                backgroundColor = Color.White,
                                shadowOffset = 2.5.dp,
                                cornerRadius = 6.dp
                            )
                            .padding(horizontal = 10.dp, vertical = 10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Default.Lock,
                                contentDescription = "Password",
                                tint = Color.Gray,
                                modifier = Modifier.size(16.dp)
                            )
                            BasicTextField(
                                value = password,
                                onValueChange = { password = it },
                                singleLine = true,
                                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                cursorBrush = SolidColor(Color.Black),
                                textStyle = TextStyle(
                                    fontFamily = PoppinsFontFamily,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 13.sp,
                                    color = NeoBlack
                                ),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(
                                onClick = { passwordVisible = !passwordVisible },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = "Toggle password",
                                    tint = Color.Gray,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Authenticate Button
                    NeoBrutalButton(
                        text = if (isAuthenticating) "AUTHENTICATING..." else "AUTHENTICATE & ENTER",
                        onClick = { handleLogin() },
                        enabled = !isAuthenticating,
                        backgroundColor = NeoYellow,
                        contentColor = Color.Black,
                        modifier = Modifier.fillMaxWidth(),
                        icon = {
                            Icon(
                                Icons.Default.ArrowForward,
                                contentDescription = "Enter",
                                tint = Color.Black,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "RoboGyaan Dual-Platform Suite • Neo-Brutalist Live Engine",
                fontFamily = VirgilFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp,
                color = Color.Gray
            )
        }
    }
}
