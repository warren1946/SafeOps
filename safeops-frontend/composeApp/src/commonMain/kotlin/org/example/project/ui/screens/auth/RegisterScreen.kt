package org.example.project.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
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
import org.example.project.ui.components.LogoVariant
import org.example.project.ui.components.SafeOpsConfirmPasswordField
import org.example.project.ui.components.SafeOpsLogo
import org.example.project.ui.components.SafeOpsPasswordField
import org.example.project.ui.components.SafeOpsTextField
import org.example.project.ui.theme.MiningSafetyColors

/**
 * Registration screen with responsive layout
 *
 * @param onRegisterSuccess Callback when registration succeeds
 * @param onNavigateToLogin Callback to navigate to login
 */
@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit = {},
    onNavigateToLogin: () -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    
    // Form state
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var company by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var agreeToTerms by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    
    // Error states
    var fullNameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var phoneError by remember { mutableStateOf<String?>(null) }
    var companyError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }
    var termsError by remember { mutableStateOf<String?>(null) }
    
    fun validateForm(): Boolean {
        var isValid = true
        
        if (fullName.isBlank()) {
            fullNameError = "Full name is required"
            isValid = false
        } else {
            fullNameError = null
        }
        
        if (email.isBlank()) {
            emailError = "Email is required"
            isValid = false
        } else if (!email.contains("@")) {
            emailError = "Invalid email format"
            isValid = false
        } else {
            emailError = null
        }
        
        if (phone.isBlank()) {
            phoneError = "Phone number is required"
            isValid = false
        } else {
            phoneError = null
        }
        
        if (company.isBlank()) {
            companyError = "Company/Mine name is required"
            isValid = false
        } else {
            companyError = null
        }
        
        if (password.isBlank()) {
            passwordError = "Password is required"
            isValid = false
        } else if (password.length < 8) {
            passwordError = "Password must be at least 8 characters"
            isValid = false
        } else {
            passwordError = null
        }
        
        if (confirmPassword != password) {
            confirmPasswordError = "Passwords do not match"
            isValid = false
        } else {
            confirmPasswordError = null
        }
        
        if (!agreeToTerms) {
            termsError = "You must agree to the terms"
            isValid = false
        } else {
            termsError = null
        }
        
        return isValid
    }
    
    fun performRegister() {
        if (!validateForm()) return
        
        isLoading = true
        scope.launch {
            delay(2000)
            isLoading = false
            onRegisterSuccess()
        }
    }
    
    // Responsive layout
    BoxWithConstraints(
        modifier = Modifier.fillMaxSize()
    ) {
        val maxWidth = maxWidth
        val isCompact = maxWidth < 600.dp  // Phone
        
        if (isCompact) {
            // Mobile layout
            RegisterMobileLayout(
                fullName = fullName,
                onFullNameChange = { fullName = it; fullNameError = null },
                email = email,
                onEmailChange = { email = it; emailError = null },
                phone = phone,
                onPhoneChange = { phone = it; phoneError = null },
                company = company,
                onCompanyChange = { company = it; companyError = null },
                password = password,
                onPasswordChange = { 
                    password = it
                    passwordError = null
                    if (confirmPassword.isNotEmpty() && confirmPassword != it) {
                        confirmPasswordError = "Passwords do not match"
                    } else {
                        confirmPasswordError = null
                    }
                },
                confirmPassword = confirmPassword,
                onConfirmPasswordChange = { confirmPassword = it; confirmPasswordError = null },
                isPasswordVisible = isPasswordVisible,
                onTogglePasswordVisibility = { isPasswordVisible = !isPasswordVisible },
                agreeToTerms = agreeToTerms,
                onAgreeToTermsChange = { agreeToTerms = it; termsError = null },
                fullNameError = fullNameError,
                emailError = emailError,
                phoneError = phoneError,
                companyError = companyError,
                passwordError = passwordError,
                confirmPasswordError = confirmPasswordError,
                termsError = termsError,
                isLoading = isLoading,
                onRegister = { performRegister() },
                onNavigateToLogin = onNavigateToLogin
            )
        } else {
            // Desktop layout
            RegisterDesktopLayout(
                fullName = fullName,
                onFullNameChange = { fullName = it; fullNameError = null },
                email = email,
                onEmailChange = { email = it; emailError = null },
                phone = phone,
                onPhoneChange = { phone = it; phoneError = null },
                company = company,
                onCompanyChange = { company = it; companyError = null },
                password = password,
                onPasswordChange = { 
                    password = it
                    passwordError = null
                    if (confirmPassword.isNotEmpty() && confirmPassword != it) {
                        confirmPasswordError = "Passwords do not match"
                    } else {
                        confirmPasswordError = null
                    }
                },
                confirmPassword = confirmPassword,
                onConfirmPasswordChange = { confirmPassword = it; confirmPasswordError = null },
                isPasswordVisible = isPasswordVisible,
                onTogglePasswordVisibility = { isPasswordVisible = !isPasswordVisible },
                agreeToTerms = agreeToTerms,
                onAgreeToTermsChange = { agreeToTerms = it; termsError = null },
                fullNameError = fullNameError,
                emailError = emailError,
                phoneError = phoneError,
                companyError = companyError,
                passwordError = passwordError,
                confirmPasswordError = confirmPasswordError,
                termsError = termsError,
                isLoading = isLoading,
                onRegister = { performRegister() },
                onNavigateToLogin = onNavigateToLogin
            )
        }
    }
}

/**
 * Mobile layout - single column with gradient background
 */
@Composable
private fun RegisterMobileLayout(
    fullName: String,
    onFullNameChange: (String) -> Unit,
    email: String,
    onEmailChange: (String) -> Unit,
    phone: String,
    onPhoneChange: (String) -> Unit,
    company: String,
    onCompanyChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    confirmPassword: String,
    onConfirmPasswordChange: (String) -> Unit,
    isPasswordVisible: Boolean,
    onTogglePasswordVisibility: () -> Unit,
    agreeToTerms: Boolean,
    onAgreeToTermsChange: (Boolean) -> Unit,
    fullNameError: String?,
    emailError: String?,
    phoneError: String?,
    companyError: String?,
    passwordError: String?,
    confirmPasswordError: String?,
    termsError: String?,
    isLoading: Boolean,
    onRegister: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MiningSafetyColors.Primary)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Logo
        Spacer(modifier = Modifier.height(24.dp))
        
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MiningSafetyColors.Secondary)
                .padding(14.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "⛑️",
                style = MaterialTheme.typography.headlineLarge
            )
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Text(
            text = "Join SafeOps",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(20.dp))
        
        // Form card
        RegisterCard(
            fullName = fullName,
            onFullNameChange = onFullNameChange,
            email = email,
            onEmailChange = onEmailChange,
            phone = phone,
            onPhoneChange = onPhoneChange,
            company = company,
            onCompanyChange = onCompanyChange,
            password = password,
            onPasswordChange = onPasswordChange,
            confirmPassword = confirmPassword,
            onConfirmPasswordChange = onConfirmPasswordChange,
            isPasswordVisible = isPasswordVisible,
            onTogglePasswordVisibility = onTogglePasswordVisibility,
            agreeToTerms = agreeToTerms,
            onAgreeToTermsChange = onAgreeToTermsChange,
            fullNameError = fullNameError,
            emailError = emailError,
            phoneError = phoneError,
            companyError = companyError,
            passwordError = passwordError,
            confirmPasswordError = confirmPasswordError,
            termsError = termsError,
            isLoading = isLoading,
            onRegister = onRegister,
            onNavigateToLogin = onNavigateToLogin,
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(24.dp))
    }
}

/**
 * Desktop layout - split screen
 */
@Composable
private fun RegisterDesktopLayout(
    fullName: String,
    onFullNameChange: (String) -> Unit,
    email: String,
    onEmailChange: (String) -> Unit,
    phone: String,
    onPhoneChange: (String) -> Unit,
    company: String,
    onCompanyChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    confirmPassword: String,
    onConfirmPasswordChange: (String) -> Unit,
    isPasswordVisible: Boolean,
    onTogglePasswordVisibility: () -> Unit,
    agreeToTerms: Boolean,
    onAgreeToTermsChange: (Boolean) -> Unit,
    fullNameError: String?,
    emailError: String?,
    phoneError: String?,
    companyError: String?,
    passwordError: String?,
    confirmPasswordError: String?,
    termsError: String?,
    isLoading: Boolean,
    onRegister: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxSize()
    ) {
        // Left panel
        RegisterBrandingPanel(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        )
        
        // Right panel
        Box(
            modifier = Modifier
                .widthIn(max = 560.dp)
                .fillMaxHeight()
                .background(MiningSafetyColors.Background),
            contentAlignment = Alignment.Center
        ) {
            RegisterCard(
                fullName = fullName,
                onFullNameChange = onFullNameChange,
                email = email,
                onEmailChange = onEmailChange,
                phone = phone,
                onPhoneChange = onPhoneChange,
                company = company,
                onCompanyChange = onCompanyChange,
                password = password,
                onPasswordChange = onPasswordChange,
                confirmPassword = confirmPassword,
                onConfirmPasswordChange = onConfirmPasswordChange,
                isPasswordVisible = isPasswordVisible,
                onTogglePasswordVisibility = onTogglePasswordVisibility,
                agreeToTerms = agreeToTerms,
                onAgreeToTermsChange = onAgreeToTermsChange,
                fullNameError = fullNameError,
                emailError = emailError,
                phoneError = phoneError,
                companyError = companyError,
                passwordError = passwordError,
                confirmPasswordError = confirmPasswordError,
                termsError = termsError,
                isLoading = isLoading,
                onRegister = onRegister,
                onNavigateToLogin = onNavigateToLogin,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(40.dp)
            )
        }
    }
}

/**
 * Shared registration card
 */
@Composable
private fun RegisterCard(
    fullName: String,
    onFullNameChange: (String) -> Unit,
    email: String,
    onEmailChange: (String) -> Unit,
    phone: String,
    onPhoneChange: (String) -> Unit,
    company: String,
    onCompanyChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    confirmPassword: String,
    onConfirmPasswordChange: (String) -> Unit,
    isPasswordVisible: Boolean,
    onTogglePasswordVisibility: () -> Unit,
    agreeToTerms: Boolean,
    onAgreeToTermsChange: (Boolean) -> Unit,
    fullNameError: String?,
    emailError: String?,
    phoneError: String?,
    companyError: String?,
    passwordError: String?,
    confirmPasswordError: String?,
    termsError: String?,
    isLoading: Boolean,
    onRegister: () -> Unit,
    onNavigateToLogin: () -> Unit,
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
            modifier = Modifier
                .padding(28.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Create Account",
                style = MaterialTheme.typography.headlineMedium,
                color = MiningSafetyColors.OnBackground,
                fontWeight = FontWeight.SemiBold
            )
            
            Text(
                text = "Fill in your details to get started",
                style = MaterialTheme.typography.bodyLarge,
                color = MiningSafetyColors.OnSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Form fields
            SafeOpsTextField(
                value = fullName,
                onValueChange = onFullNameChange,
                label = "Full Name",
                placeholder = "Enter your full name",
                prefix = "👤",
                error = fullNameError,
                imeAction = ImeAction.Next
            )
            
            Spacer(modifier = Modifier.height(10.dp))
            
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
            
            Spacer(modifier = Modifier.height(10.dp))
            
            SafeOpsTextField(
                value = phone,
                onValueChange = onPhoneChange,
                label = "Phone Number",
                placeholder = "Enter your phone number",
                prefix = "📱",
                error = phoneError,
                keyboardType = KeyboardType.Phone,
                imeAction = ImeAction.Next
            )
            
            Spacer(modifier = Modifier.height(10.dp))
            
            SafeOpsTextField(
                value = company,
                onValueChange = onCompanyChange,
                label = "Company / Mine Site",
                placeholder = "Enter company or mine name",
                prefix = "🏢",
                error = companyError,
                imeAction = ImeAction.Next
            )
            
            Spacer(modifier = Modifier.height(10.dp))
            
            SafeOpsPasswordField(
                value = password,
                onValueChange = onPasswordChange,
                label = "Password",
                placeholder = "Create a password",
                passwordVisible = isPasswordVisible,
                onTogglePasswordVisibility = onTogglePasswordVisibility,
                error = passwordError,
                imeAction = ImeAction.Next
            )
            
            Spacer(modifier = Modifier.height(10.dp))
            
            SafeOpsConfirmPasswordField(
                value = confirmPassword,
                onValueChange = onConfirmPasswordChange,
                password = password,
                passwordVisible = isPasswordVisible,
                onToggleVisibility = onTogglePasswordVisibility,
                error = confirmPasswordError,
                imeAction = ImeAction.Done,
                onImeAction = onRegister
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Terms
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = agreeToTerms,
                    onCheckedChange = onAgreeToTermsChange,
                    colors = CheckboxDefaults.colors(
                        checkedColor = MiningSafetyColors.Primary,
                        uncheckedColor = if (termsError != null) 
                            MiningSafetyColors.Error 
                        else 
                            MiningSafetyColors.Outline
                    )
                )
                Text(
                    text = "I agree to the Terms and Privacy Policy",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (termsError != null) 
                        MiningSafetyColors.Error 
                    else 
                        MiningSafetyColors.OnSurface
                )
            }
            
            if (termsError != null) {
                Text(
                    text = termsError,
                    style = MaterialTheme.typography.bodySmall,
                    color = MiningSafetyColors.Error,
                    modifier = Modifier.padding(start = 12.dp, top = 4.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            AuthPrimaryButton(
                text = "Create Account",
                onClick = onRegister,
                isLoading = isLoading
            )
            
            Spacer(modifier = Modifier.height(20.dp))
            
            AuthDivider()
            
            Spacer(modifier = Modifier.height(20.dp))
            
            AuthFooterLink(
                questionText = "Already have an account?",
                actionText = "Sign In",
                onActionClick = onNavigateToLogin
            )
        }
    }
}

@Composable
private fun RegisterBrandingPanel(
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
            SafeOpsLogo(
                variant = LogoVariant.WHITE,
                size = 120
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Join SafeOps",
                style = MaterialTheme.typography.displaySmall,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = "Create your safety management account",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White.copy(alpha = 0.8f)
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            FeatureItem("✓", "MSHA & OSHA Compliant")
            FeatureItem("✓", "Free 30-day trial")
            FeatureItem("✓", "No credit card required")
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
