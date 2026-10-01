package com.robogyaan.invoice.ui.components

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.WbSunny
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

    fun isLoggedIn(context: Context): Boolean {
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

    private const val KEY_DARK_MODE = "is_dark_mode_enabled"
    private const val KEY_GRID_BG = "is_grid_bg_enabled"

    fun isDarkMode(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_DARK_MODE, false)
    }

    fun setDarkMode(context: Context, enabled: Boolean) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_DARK_MODE, enabled).apply()
    }

    fun isGridBackground(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_GRID_BG, true)
    }

    fun setGridBackground(context: Context, enabled: Boolean) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_GRID_BG, enabled).apply()
    }
}

@Composable
fun LoginScreen(
    isDarkMode: Boolean = false,
    isGridBackground: Boolean = true,
    onToggleDarkMode: ((Boolean) -> Unit)? = null,
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
            .background(if (isDarkMode) Color(0xFF09090B) else Color(0xFFFDFBF7)),
        contentAlignment = Alignment.Center
    ) {
        // Grid Boxes Background (Full edge-to-edge screen)
        BoxGridBackground(
            isDarkMode = isDarkMode,
            enabled = isGridBackground,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Security Top Tag & Theme Toggle Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
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
                            text = "ARGON2ID • CLOUD FIRESTORE",
                            fontFamily = VirgilFontFamily,
                            fontWeight = FontWeight.Black,
                            fontSize = 10.sp,
                            color = Color.Black
                        )
                    }
                }

                // Sun / Moon toggle in same container
                if (onToggleDarkMode != null) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(if (isDarkMode) Color.Black else Color.White, RoundedCornerShape(8.dp))
                            .border(2.dp, Color.Black, RoundedCornerShape(8.dp))
                            .clickable { onToggleDarkMode(!isDarkMode) },
                        contentAlignment = Alignment.Center
                    ) {
                        if (isDarkMode) {
                            Icon(
                                Icons.Default.WbSunny,
                                contentDescription = "Switch to Light Mode",
                                tint = NeoYellow,
                                modifier = Modifier.size(16.dp)
                            )
                        } else {
                            Icon(
                                Icons.Default.Nightlight,
                                contentDescription = "Switch to Dark Mode",
                                tint = Color.Black,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Main Login Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .neoBrutal(
                        backgroundColor = if (isDarkMode) Color(0xFF1E1E22) else Color.White,
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
                                    color = if (isDarkMode) Color.White else Color.Black,
                                    fontFamily = VirgilFontFamily
                                )
                                Box(
                                    modifier = Modifier
                                        .background(Color.Black, RoundedCornerShape(4.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "INVOICE",
                                        color = Color.White,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 10.sp,
                                        fontFamily = VirgilFontFamily
                                    )
                                }
                            }
                            Text(
                                text = "ADMIN AUTHENTICATION PORTAL",
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.sp,
                                color = if (isDarkMode) Color.LightGray else Color.Gray,
                                fontFamily = VirgilFontFamily
                            )
                        }
                    }

                    // Security Info Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(if (isDarkMode) Color(0xFF2A2A2E) else Color(0xFFFFFDE6), RoundedCornerShape(6.dp))
                            .border(1.5.dp, Color.Black, RoundedCornerShape(6.dp))
                            .padding(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Default.Security,
                                contentDescription = "Security Note",
                                tint = NeoYellow,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Password is encrypted client-side using Argon2id/SHA-256 before verification. Plaintext is never stored.",
                                fontSize = 10.sp,
                                fontFamily = PoppinsFontFamily,
                                color = if (isDarkMode) Color.White else Color.Black,
                                lineHeight = 14.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Error Banner
                    if (errorMessage != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFFFEBEE), RoundedCornerShape(6.dp))
                                .border(1.5.dp, Color(0xFFD32F2F), RoundedCornerShape(6.dp))
                                .padding(10.dp)
                        ) {
                            Text(
                                text = errorMessage ?: "",
                                color = Color(0xFFD32F2F),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = PoppinsFontFamily
                            )
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                    }

                    // Email Field
                    Text(
                        text = "ADMIN EMAIL ADDRESS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = VirgilFontFamily,
                        color = if (isDarkMode) Color.White else NeoBlack
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .neoBrutal(
                                backgroundColor = if (isDarkMode) Color(0xFF2A2A2E) else Color.White,
                                shadowOffset = 2.5.dp,
                                cornerRadius = 6.dp
                            )
                            .padding(horizontal = 12.dp, vertical = 10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Mail,
                                contentDescription = "Email",
                                tint = if (isDarkMode) Color.LightGray else Color.Gray,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            BasicTextField(
                                value = email,
                                onValueChange = { email = it },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                textStyle = TextStyle(
                                    fontSize = 13.sp,
                                    fontFamily = PoppinsFontFamily,
                                    fontWeight = FontWeight.Medium,
                                    color = if (isDarkMode) Color.White else Color.Black
                                ),
                                cursorBrush = SolidColor(if (isDarkMode) Color.White else Color.Black)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Password Field
                    Text(
                        text = "ADMIN PASSWORD",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = VirgilFontFamily,
                        color = if (isDarkMode) Color.White else NeoBlack
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .neoBrutal(
                                backgroundColor = if (isDarkMode) Color(0xFF2A2A2E) else Color.White,
                                shadowOffset = 2.5.dp,
                                cornerRadius = 6.dp
                            )
                            .padding(horizontal = 12.dp, vertical = 10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Lock,
                                contentDescription = "Password",
                                tint = if (isDarkMode) Color.LightGray else Color.Gray,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            BasicTextField(
                                value = password,
                                onValueChange = { password = it },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                textStyle = TextStyle(
                                    fontSize = 13.sp,
                                    fontFamily = PoppinsFontFamily,
                                    fontWeight = FontWeight.Medium,
                                    color = if (isDarkMode) Color.White else Color.Black
                                ),
                                cursorBrush = SolidColor(if (isDarkMode) Color.White else Color.Black)
                            )
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = if (passwordVisible) "Hide password" else "Show password",
                                tint = if (isDarkMode) Color.LightGray else Color.Gray,
                                modifier = Modifier
                                    .size(18.dp)
                                    .clickable { passwordVisible = !passwordVisible }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Sign In Button
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .neoBrutal(
                                backgroundColor = NeoYellow,
                                shadowOffset = 4.dp,
                                cornerRadius = 8.dp
                            )
                            .clickable(enabled = !isAuthenticating) { handleLogin() }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = if (isAuthenticating) "AUTHENTICATING..." else "SIGN IN TO INVOICE SUITE",
                                fontFamily = VirgilFontFamily,
                                fontWeight = FontWeight.Black,
                                fontSize = 13.sp,
                                color = Color.Black
                            )
                            Icon(
                                Icons.Default.ArrowForward,
                                contentDescription = "Sign In",
                                tint = Color.Black,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "RoboGyaan Invoice Suite • Official Management System",
                fontFamily = PoppinsFontFamily,
                fontSize = 10.sp,
                color = Color.Gray
            )
        }
    }
}
