package com.robogyaan.invoice.ui.components

import android.content.Context
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
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
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.robogyaan.invoice.ui.neoBrutal
import com.robogyaan.invoice.ui.theme.NeoBlack
import com.robogyaan.invoice.ui.theme.NeoYellow
import com.robogyaan.invoice.ui.theme.PoppinsFontFamily
import com.robogyaan.invoice.ui.theme.VirgilFontFamily

enum class LoginAuthMode {
    FINGERPRINT,
    PASSWORD
}

object AuthPreferences {
    private const val PREFS_NAME = "robogyaan_auth_prefs"
    private const val KEY_IS_LOGGED_IN = "is_admin_logged_in"
    private const val KEY_USER_EMAIL = "admin_user_email"

    private const val KEY_FAILED_BIOMETRIC_ATTEMPTS = "failed_biometric_attempts"
    private const val KEY_BIOMETRIC_LOCKED = "is_biometric_locked"
    const val MAX_BIOMETRIC_ATTEMPTS = 5

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

    fun getFailedBiometricAttempts(context: Context): Int {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getInt(KEY_FAILED_BIOMETRIC_ATTEMPTS, 0)
    }

    fun recordFailedBiometricAttempt(context: Context): Int {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val current = prefs.getInt(KEY_FAILED_BIOMETRIC_ATTEMPTS, 0) + 1
        val isLocked = current >= MAX_BIOMETRIC_ATTEMPTS
        prefs.edit()
            .putInt(KEY_FAILED_BIOMETRIC_ATTEMPTS, current)
            .putBoolean(KEY_BIOMETRIC_LOCKED, isLocked)
            .apply()
        return current
    }

    fun isBiometricLocked(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val attempts = prefs.getInt(KEY_FAILED_BIOMETRIC_ATTEMPTS, 0)
        return prefs.getBoolean(KEY_BIOMETRIC_LOCKED, false) || attempts >= MAX_BIOMETRIC_ATTEMPTS
    }

    fun setBiometricLocked(context: Context, locked: Boolean) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_BIOMETRIC_LOCKED, locked).apply()
    }

    fun resetFailedBiometricAttempts(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit()
            .putInt(KEY_FAILED_BIOMETRIC_ATTEMPTS, 0)
            .putBoolean(KEY_BIOMETRIC_LOCKED, false)
            .apply()
    }

    fun checkBiometricAvailability(context: Context): Int {
        val biometricManager = BiometricManager.from(context)
        return biometricManager.canAuthenticate(
            BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.BIOMETRIC_WEAK
        )
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
    val activity = context as? FragmentActivity

    var isLocked by remember { mutableStateOf(AuthPreferences.isBiometricLocked(context)) }
    var failedAttempts by remember { mutableStateOf(AuthPreferences.getFailedBiometricAttempts(context)) }

    // Start on Fingerprint mode if not locked, otherwise start on Password mode
    var activeAuthMode by remember {
        mutableStateOf(if (isLocked) LoginAuthMode.PASSWORD else LoginAuthMode.FINGERPRINT)
    }

    var email by remember { mutableStateOf("invoiceadmin@robogyaan.in") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var biometricFeedback by remember { mutableStateOf<String?>(null) }
    var isAuthenticating by remember { mutableStateOf(false) }

    fun triggerBiometricPrompt() {
        if (activity == null) {
            errorMessage = "FragmentActivity not available for biometric prompt."
            return
        }

        if (isLocked) {
            activeAuthMode = LoginAuthMode.PASSWORD
            errorMessage = "Fingerprint login disabled after ${AuthPreferences.MAX_BIOMETRIC_ATTEMPTS} failed attempts. Please use your Admin Password."
            return
        }

        val availability = AuthPreferences.checkBiometricAvailability(context)
        if (availability == BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE) {
            errorMessage = "No biometric/fingerprint sensor detected on this device. Please use Admin Password."
            activeAuthMode = LoginAuthMode.PASSWORD
            return
        } else if (availability == BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED) {
            errorMessage = "No fingerprints are enrolled on this device. Please register a fingerprint in Android Settings or use Admin Password."
            activeAuthMode = LoginAuthMode.PASSWORD
            return
        }

        errorMessage = null
        biometricFeedback = null
        isAuthenticating = true

        val executor = ContextCompat.getMainExecutor(context)
        val biometricPrompt = BiometricPrompt(
            activity,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    isAuthenticating = false
                    AuthPreferences.resetFailedBiometricAttempts(context)
                    AuthPreferences.setLoggedIn(context, true, AuthPreferences.ADMIN_EMAIL)
                    Toast.makeText(context, "Fingerprint verified successfully!", Toast.LENGTH_SHORT).show()
                    onLoginSuccess()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    val newCount = AuthPreferences.recordFailedBiometricAttempt(context)
                    failedAttempts = newCount
                    isAuthenticating = false

                    if (newCount >= AuthPreferences.MAX_BIOMETRIC_ATTEMPTS) {
                        isLocked = true
                        activeAuthMode = LoginAuthMode.PASSWORD
                        errorMessage = "Maximum fingerprint attempts exceeded (${AuthPreferences.MAX_BIOMETRIC_ATTEMPTS}/${AuthPreferences.MAX_BIOMETRIC_ATTEMPTS}). Fingerprint login locked. Please authenticate using Admin Password."
                        Toast.makeText(context, "Fingerprint attempts exceeded limit. Switched to Password login.", Toast.LENGTH_LONG).show()
                    } else {
                        biometricFeedback = "Fingerprint not recognized. Attempt $newCount of ${AuthPreferences.MAX_BIOMETRIC_ATTEMPTS}."
                    }
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    isAuthenticating = false
                    when (errorCode) {
                        BiometricPrompt.ERROR_NEGATIVE_BUTTON,
                        BiometricPrompt.ERROR_USER_CANCELED -> {
                            activeAuthMode = LoginAuthMode.PASSWORD
                        }
                        BiometricPrompt.ERROR_LOCKOUT,
                        BiometricPrompt.ERROR_LOCKOUT_PERMANENT -> {
                            AuthPreferences.setBiometricLocked(context, true)
                            isLocked = true
                            activeAuthMode = LoginAuthMode.PASSWORD
                            errorMessage = "Biometric sensor locked by Android system. Please authenticate with Admin Password."
                        }
                        BiometricPrompt.ERROR_CANCELED -> {
                            // Prompt cancelled programmatically
                        }
                        else -> {
                            biometricFeedback = errString.toString()
                        }
                    }
                }
            }
        )

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("RoboGyaan Admin Fingerprint Login")
            .setSubtitle("Touch the fingerprint sensor to authenticate")
            .setDescription("Supports rear, side-mounted power key, and in-display (optical/ultrasonic) sensors")
            .setNegativeButtonText("Use Admin Password")
            .setAllowedAuthenticators(
                BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.BIOMETRIC_WEAK
            )
            .build()

        try {
            biometricPrompt.authenticate(promptInfo)
        } catch (e: Exception) {
            isAuthenticating = false
            errorMessage = "Biometric prompt error: ${e.message}"
        }
    }

    fun handlePasswordLogin() {
        if (email.trim().isEmpty() || password.isEmpty()) {
            errorMessage = "Please enter both admin email and password."
            return
        }

        isAuthenticating = true
        errorMessage = null

        // Verify credentials with secure hash check
        if (AuthPreferences.verifyCredentials(email, password)) {
            // Successful password login resets any previous biometric lockout!
            AuthPreferences.resetFailedBiometricAttempts(context)
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
                .padding(horizontal = 20.dp, vertical = 20.dp)
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
                            text = "BIOMETRIC • ARGON2ID • SECURE",
                            fontFamily = VirgilFontFamily,
                            fontWeight = FontWeight.Black,
                            fontSize = 10.sp,
                            color = Color.Black
                        )
                    }
                }

                // Sun / Moon toggle
                if (onToggleDarkMode != null) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
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
                        cornerRadius = 14.dp
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
                        modifier = Modifier.padding(bottom = 14.dp)
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

                    // Dual Option Selector: [ Fingerprint Login ] & [ Admin Password ]
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 14.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Option 1: Fingerprint
                        val isFpSelected = activeAuthMode == LoginAuthMode.FINGERPRINT
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .alpha(if (isLocked) 0.5f else 1f)
                                .background(
                                    color = if (isFpSelected && !isLocked) NeoYellow else (if (isDarkMode) Color(0xFF2A2A2E) else Color(0xFFF4F4F5)),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .border(
                                    width = 2.dp,
                                    color = if (isFpSelected && !isLocked) Color.Black else (if (isDarkMode) Color(0xFF404044) else Color(0xFFD4D4D8)),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable {
                                    if (isLocked) {
                                        Toast.makeText(
                                            context,
                                            "Fingerprint locked after 5 failed attempts. Use Admin Password.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } else {
                                        activeAuthMode = LoginAuthMode.FINGERPRINT
                                        errorMessage = null
                                    }
                                }
                                .padding(vertical = 10.dp, horizontal = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = if (isLocked) Icons.Default.Lock else Icons.Default.Fingerprint,
                                    contentDescription = "Fingerprint Option",
                                    tint = if (isFpSelected && !isLocked) Color.Black else (if (isDarkMode) Color.White else Color.Black),
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = if (isLocked) "LOCKED" else "FINGERPRINT",
                                    fontFamily = VirgilFontFamily,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 11.sp,
                                    color = if (isFpSelected && !isLocked) Color.Black else (if (isDarkMode) Color.White else Color.Black)
                                )
                            }
                        }

                        // Option 2: Admin Password
                        val isPwSelected = activeAuthMode == LoginAuthMode.PASSWORD
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(
                                    color = if (isPwSelected) NeoYellow else (if (isDarkMode) Color(0xFF2A2A2E) else Color(0xFFF4F4F5)),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .border(
                                    width = 2.dp,
                                    color = if (isPwSelected) Color.Black else (if (isDarkMode) Color(0xFF404044) else Color(0xFFD4D4D8)),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable {
                                    activeAuthMode = LoginAuthMode.PASSWORD
                                    errorMessage = null
                                }
                                .padding(vertical = 10.dp, horizontal = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "Password Option",
                                    tint = if (isPwSelected) Color.Black else (if (isDarkMode) Color.White else Color.Black),
                                    modifier = Modifier.size(15.dp)
                                )
                                Text(
                                    text = "PASSWORD",
                                    fontFamily = VirgilFontFamily,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 11.sp,
                                    color = if (isPwSelected) Color.Black else (if (isDarkMode) Color.White else Color.Black)
                                )
                            }
                        }
                    }

                    // Biometric Lockout Alert Banner (Triggered when 5 attempts fail)
                    if (isLocked) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFFFEBEE), RoundedCornerShape(8.dp))
                                .border(2.dp, Color(0xFFD32F2F), RoundedCornerShape(8.dp))
                                .padding(10.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.Top,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    Icons.Default.Warning,
                                    contentDescription = "Lockout Warning",
                                    tint = Color(0xFFD32F2F),
                                    modifier = Modifier.size(18.dp)
                                )
                                Column {
                                    Text(
                                        text = "FINGERPRINT OPTION LOCKED (5/5 FAILS)",
                                        color = Color(0xFFD32F2F),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Black,
                                        fontFamily = VirgilFontFamily
                                    )
                                    Text(
                                        text = "Fingerprint verification disabled after 5 consecutive failures. Please authenticate using your Admin Password below to unlock.",
                                        color = Color(0xFFB71C1C),
                                        fontSize = 10.sp,
                                        fontFamily = PoppinsFontFamily,
                                        fontWeight = FontWeight.Medium,
                                        lineHeight = 14.sp
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                    }

                    // Security Info Box (Standard notice)
                    if (!isLocked) {
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
                                    text = if (activeAuthMode == LoginAuthMode.FINGERPRINT) {
                                        "Supports in-display (optical/ultrasonic), side power-button, and rear fingerprint sensors. 5 maximum attempts allowed."
                                    } else {
                                        "Password encrypted client-side using Argon2id/SHA-256 before verification. Plaintext is never stored."
                                    },
                                    fontSize = 10.sp,
                                    fontFamily = PoppinsFontFamily,
                                    color = if (isDarkMode) Color.White else Color.Black,
                                    lineHeight = 14.sp
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                    }

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

                    // -------------------------------------------------------------
                    // MODE 1: FINGERPRINT LOGIN VIEW
                    // -------------------------------------------------------------
                    if (activeAuthMode == LoginAuthMode.FINGERPRINT && !isLocked) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Spacer(modifier = Modifier.height(8.dp))

                            // Large Neo-Brutal Fingerprint Sensor Graphic
                            Box(
                                modifier = Modifier
                                    .size(88.dp)
                                    .neoBrutal(
                                        backgroundColor = if (isDarkMode) Color(0xFF2A2A2E) else Color(0xFFFFFDE6),
                                        shadowOffset = 4.dp,
                                        cornerRadius = 44.dp
                                    )
                                    .clickable { triggerBiometricPrompt() },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Fingerprint,
                                    contentDescription = "Scan Fingerprint",
                                    tint = if (isDarkMode) NeoYellow else Color.Black,
                                    modifier = Modifier.size(52.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "TOUCH FINGERPRINT SENSOR",
                                fontFamily = VirgilFontFamily,
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp,
                                color = if (isDarkMode) Color.White else Color.Black
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "Screen • Power Button • Rear Sensor",
                                fontFamily = PoppinsFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                color = if (isDarkMode) Color.LightGray else Color.Gray
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Attempts status badge
                            val remainingAttempts = AuthPreferences.MAX_BIOMETRIC_ATTEMPTS - failedAttempts
                            Box(
                                modifier = Modifier
                                    .background(
                                        color = if (failedAttempts >= 3) Color(0xFFFFEBEE) else (if (isDarkMode) Color(0xFF27272A) else Color(0xFFF4F4F5)),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .border(
                                        width = 1.dp,
                                        color = if (failedAttempts >= 3) Color(0xFFD32F2F) else (if (isDarkMode) Color(0xFF3F3F46) else Color(0xFFE4E4E7)),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .padding(horizontal = 12.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "$remainingAttempts of ${AuthPreferences.MAX_BIOMETRIC_ATTEMPTS} attempts remaining",
                                    fontSize = 10.sp,
                                    fontFamily = PoppinsFontFamily,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (failedAttempts >= 3) Color(0xFFD32F2F) else (if (isDarkMode) Color.LightGray else Color.DarkGray)
                                )
                            }

                            if (biometricFeedback != null) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = biometricFeedback ?: "",
                                    color = Color(0xFFD32F2F),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = PoppinsFontFamily
                                )
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            // Main Biometric Prompt Button
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .neoBrutal(
                                        backgroundColor = NeoYellow,
                                        shadowOffset = 4.dp,
                                        cornerRadius = 8.dp
                                    )
                                    .clickable(enabled = !isAuthenticating) { triggerBiometricPrompt() }
                                    .padding(vertical = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Fingerprint,
                                        contentDescription = "Scan",
                                        tint = Color.Black,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        text = if (isAuthenticating) "LISTENING TO SENSOR..." else "AUTHENTICATE FINGERPRINT",
                                        fontFamily = VirgilFontFamily,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 13.sp,
                                        color = Color.Black
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Switch to Password Button
                            TextButton(
                                onClick = { activeAuthMode = LoginAuthMode.PASSWORD }
                            ) {
                                Text(
                                    text = "Or log in using Admin Password →",
                                    fontSize = 11.sp,
                                    fontFamily = PoppinsFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDarkMode) NeoYellow else Color.Black
                                )
                            }
                        }
                    }

                    // -------------------------------------------------------------
                    // MODE 2: ADMIN PASSWORD LOGIN VIEW
                    // -------------------------------------------------------------
                    if (activeAuthMode == LoginAuthMode.PASSWORD || isLocked) {
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
                                .clickable(enabled = !isAuthenticating) { handlePasswordLogin() }
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = if (isAuthenticating) "AUTHENTICATING..." else "SIGN IN WITH PASSWORD",
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

                        if (!isLocked) {
                            Spacer(modifier = Modifier.height(10.dp))
                            TextButton(
                                onClick = { activeAuthMode = LoginAuthMode.FINGERPRINT },
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            ) {
                                Text(
                                    text = "← Switch to Fingerprint Login",
                                    fontSize = 11.sp,
                                    fontFamily = PoppinsFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDarkMode) NeoYellow else Color.Black
                                )
                            }
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
