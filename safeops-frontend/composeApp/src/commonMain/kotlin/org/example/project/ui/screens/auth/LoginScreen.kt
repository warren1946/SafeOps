package org.example.project.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.example.project.ui.components.AuthDivider
import org.example.project.ui.components.AuthFooterLink
import org.example.project.ui.components.AuthPrimaryButton
import org.example.project.ui.components.AuthTextLink
import org.example.project.ui.components.SafeOpsPasswordField
import org.example.project.ui.components.SafeOpsTextField
import org.example.project.ui.theme.MiningSafetyColors

/**
 * Login screen with responsive layout for both mobile and desktop
 *
 * @param onLoginSuccess Callback when login succeeds
 * @param onNavigateToRegister Callback to navigate to registration
 */
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit = {},
    onNavigateToRegister: () -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    
    // Form state
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    
    // Error states
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    
    fun validateForm(): Boolean {
        var isValid = true
        
        if (email.isBlank()) {
            emailError = "Email is required"
            isValid = false
        } else if (!email.contains("@")) {
            emailError = "Invalid email format"
            isValid = false
        } else {
            emailError = null
        }
        
        if (password.isBlank()) {
            passwordError = "Password is required"
            isValid = false
        } else if (password.length < 6) {
            passwordError = "Password must be at least 6 characters"
            isValid = false
        } else {
            passwordError = null
        }
        
        return isValid
    }
    
    fun performLogin() {
        if (!validateForm()) return
        
        isLoading = true
        scope.launch {
            delay(1500)
            isLoading = false
            onLoginSuccess()
        }
    }
    
    // Responsive layout using BoxWithConstraints
    BoxWithConstraints(
        modifier = Modifier.fillMaxSize()
    ) {
        val maxWidth = maxWidth
        val isCompact = maxWidth < 600.dp  // Phone
        val isMedium = maxWidth >= 600.dp && maxWidth < 840.dp  // Tablet
        val isExpanded = maxWidth >= 840.dp  // Desktop
        
        if (isCompact) {
            // Mobile layout - single column, centered
            LoginMobileLayout(
                email = email,
                onEmailChange = { email = it; emailError = null },
                password = password,
                onPasswordChange = { password = it; passwordError = null },
                isPasswordVisible = isPasswordVisible,
                onTogglePasswordVisibility = { isPasswordVisible = !isPasswordVisible },
                emailError = emailError,
                passwordError = passwordError,
                isLoading = isLoading,
                onLogin = { performLogin() },
                onForgotPassword = { },
                onNavigateToRegister = onNavigateToRegister
            )
        } else {
            // Desktop/Tablet layout - split screen
            LoginDesktopLayout(
                email = email,
                onEmailChange = { email = it; emailError = null },
                password = password,
                onPasswordChange = { password = it; passwordError = null },
                isPasswordVisible = isPasswordVisible,
                onTogglePasswordVisibility = { isPasswordVisible = !isPasswordVisible },
                emailError = emailError,
                passwordError = passwordError,
                isLoading = isLoading,
                onLogin = { performLogin() },
                onForgotPassword = { },
                onNavigateToRegister = onNavigateToRegister
            )
        }
    }
}

/**
 * Mobile layout - single column centered card with gradient background
 */
@Composable
private fun LoginMobileLayout(
    email: String,
    onEmailChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    isPasswordVisible: Boolean,
    onTogglePasswordVisibility: () -> Unit,
    emailError: String?,
    passwordError: String?,
    isLoading: Boolean,
    onLogin: () -> Unit,
    onForgotPassword: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MiningSafetyColors.Primary)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Logo section
        Spacer(modifier = Modifier.height(32.dp))
        
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(MiningSafetyColors.Secondary)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "⚡",
                style = MaterialTheme.typography.displaySmall
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "SafeOps",
            style = MaterialTheme.typography.headlineLarge,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
        
        Text(
            text = "Mining Safety Management",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White.copy(alpha = 0.8f)
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Login card
        LoginCard(
            email = email,
            onEmailChange = onEmailChange,
            password = password,
            onPasswordChange = onPasswordChange,
            isPasswordVisible = isPasswordVisible,
            onTogglePasswordVisibility = onTogglePasswordVisibility,
            emailError = emailError,
            passwordError = passwordError,
            isLoading = isLoading,
            onLogin = onLogin,
            onForgotPassword = onForgotPassword,
            onNavigateToRegister = onNavigateToRegister,
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}

/**
 * Desktop layout - split screen with branding on left
 */
@Composable
private fun LoginDesktopLayout(
    email: String,
    onEmailChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    isPasswordVisible: Boolean,
    onTogglePasswordVisibility: () -> Unit,
    emailError: String?,
    passwordError: String?,
    isLoading: Boolean,
    onLogin: () -> Unit,
    onForgotPassword: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxSize()
    ) {
        // Left panel - Branding
        LoginBrandingPanel(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        )
        
        // Right panel - Form
        Box(
            modifier = Modifier
                .widthIn(max = 520.dp)
                .fillMaxHeight()
                .background(MiningSafetyColors.Background),
            contentAlignment = Alignment.Center
        ) {
            LoginCard(
                email = email,
                onEmailChange = onEmailChange,
                password = password,
                onPasswordChange = onPasswordChange,
                isPasswordVisible = isPasswordVisible,
                onTogglePasswordVisibility = onTogglePasswordVisibility,
                emailError = emailError,
                passwordError = passwordError,
                isLoading = isLoading,
                onLogin = onLogin,
                onForgotPassword = onForgotPassword,
                onNavigateToRegister = onNavigateToRegister,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(48.dp)
            )
        }
    }
}

/**
 * Login card content - shared between mobile and desktop
 */
@Composable
private fun LoginCard(
    email: String,
    onEmailChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    isPasswordVisible: Boolean,
    onTogglePasswordVisibility: () -> Unit,
    emailError: String?,
    passwordError: String?,
    isLoading: Boolean,
    onLogin: () -> Unit,
    onForgotPassword: () -> Unit,
    onNavigateToRegister: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MiningSafetyColors.Surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Text(
                text = "Welcome Back",
                style = MaterialTheme.typography.headlineMedium,
                color = MiningSafetyColors.OnBackground,
                fontWeight = FontWeight.SemiBold
            )
            
            Text(
                text = "Sign in to continue",
                style = MaterialTheme.typography.bodyLarge,
                color = MiningSafetyColors.OnSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Form fields
            SafeOpsTextField(
                value = email,
                onValueChange = onEmailChange,
                label = "Email",
                placeholder = "Enter your email",
                prefix = "📧",
                error = emailError,
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            SafeOpsPasswordField(
                value = password,
                onValueChange = onPasswordChange,
                label = "Password",
                placeholder = "Enter your password",
                passwordVisible = isPasswordVisible,
                onTogglePasswordVisibility = onTogglePasswordVisibility,
                error = passwordError,
                imeAction = ImeAction.Done,
                onImeAction = onLogin
            )
            
            // Forgot password
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                AuthTextLink(
                    text = "Forgot Password?",
                    onClick = onForgotPassword
                )
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Login button
            AuthPrimaryButton(
                text = "Sign In",
                onClick = onLogin,
                isLoading = isLoading
            )
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Divider
            AuthDivider()
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Sign up link
            AuthFooterLink(
                questionText = "Don't have an account?",
                actionText = "Sign Up",
                onActionClick = onNavigateToRegister
            )
        }
    }
}

/**
 * Left branding panel for desktop
 */
@Composable
private fun LoginBrandingPanel(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(MiningSafetyColors.Primary),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(48.dp)
        ) {
            Text(
                text = "⚡",
                style = MaterialTheme.typography.displayLarge
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "SafeOps",
                style = MaterialTheme.typography.displaySmall,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = "Mining Safety Management",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White.copy(alpha = 0.8f)
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            FeatureItem("✓", "MSHA & OSHA Compliant")
            FeatureItem("✓", "Real-time Monitoring")
            FeatureItem("✓", "WhatsApp Integration")
        }
    }
}

@Composable
private fun FeatureItem(icon: String, text: String) {
    Row(
        modifier = Modifier.padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = icon,
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.9f)
        )
    }
}
