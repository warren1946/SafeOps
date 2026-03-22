package org.example.project.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.example.project.di.ServiceLocator
import org.example.project.presentation.viewmodel.RegisterViewModel
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
 * @param viewModel RegisterViewModel instance
 * @param onRegisterSuccess Callback when registration succeeds
 * @param onNavigateToLogin Callback to navigate to login
 */
@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel = remember { ServiceLocator.provideRegisterViewModel() },
    onRegisterSuccess: () -> Unit = {},
    onNavigateToLogin: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    var isPasswordVisible by remember { mutableStateOf(false) }
    var agreeToTerms by remember { mutableStateOf(false) }
    var termsError by remember { mutableStateOf<String?>(null) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    
    // Handle successful registration - show dialog instead of auto-navigating
    if (uiState.isSuccess && !showSuccessDialog) {
        showSuccessDialog = true
    }
    
    // Success Dialog
    if (showSuccessDialog) {
        RegistrationSuccessDialog(
            onLoginClick = {
                showSuccessDialog = false
                viewModel.resetState()
                agreeToTerms = false
                onNavigateToLogin()
            },
            onStayClick = {
                showSuccessDialog = false
                viewModel.resetState()
                agreeToTerms = false
            }
        )
    }
    
    fun validateAndRegister() {
        if (!agreeToTerms) {
            termsError = "You must agree to the terms"
            return
        }
        termsError = null
        viewModel.register()
    }
    
    // Responsive layout
    BoxWithConstraints(
        modifier = Modifier.fillMaxSize()
    ) {
        val maxWidth = maxWidth
        val isCompact = maxWidth < 600.dp  // Phone
        
        // Determine field errors vs API error
        val fullNameError = uiState.error.takeIf { uiState.fullName.isBlank() && uiState.error != null }
        val emailFormatError = !uiState.email.contains("@") || !uiState.email.contains(".")
        val emailError = uiState.error.takeIf { uiState.email.isNotBlank() && emailFormatError }
        val phoneError = uiState.error.takeIf { uiState.phone.isBlank() && uiState.error != null }
        val companyError = uiState.error.takeIf { uiState.company.isBlank() && uiState.error != null }
        val passwordError = uiState.error.takeIf { uiState.password.length in 1..5 }
        val confirmPasswordError = uiState.error.takeIf { 
            uiState.confirmPassword.isNotBlank() && uiState.confirmPassword != uiState.password 
        }
        // API error - only when all fields are filled but there's still an error
        val apiError = uiState.error?.takeIf { 
            uiState.fullName.isNotBlank() && uiState.email.isNotBlank() && 
            uiState.phone.isNotBlank() && uiState.company.isNotBlank() &&
            uiState.password.isNotBlank() && uiState.confirmPassword.isNotBlank() &&
            !fullNameError.isNullOrBlank() == false && !emailError.isNullOrBlank() == false &&
            !phoneError.isNullOrBlank() == false && !companyError.isNullOrBlank() == false &&
            !passwordError.isNullOrBlank() == false && !confirmPasswordError.isNullOrBlank() == false
        }
        
        if (isCompact) {
            // Mobile layout
            RegisterMobileLayout(
                fullName = uiState.fullName,
                onFullNameChange = viewModel::onFullNameChange,
                email = uiState.email,
                onEmailChange = viewModel::onEmailChange,
                phone = uiState.phone,
                onPhoneChange = viewModel::onPhoneChange,
                company = uiState.company,
                onCompanyChange = viewModel::onCompanyChange,
                password = uiState.password,
                onPasswordChange = viewModel::onPasswordChange,
                confirmPassword = uiState.confirmPassword,
                onConfirmPasswordChange = viewModel::onConfirmPasswordChange,
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
                apiError = apiError,
                termsError = termsError,
                isLoading = uiState.isLoading,
                onRegister = { validateAndRegister() },
                onNavigateToLogin = onNavigateToLogin
            )
        } else {
            // Desktop layout
            RegisterDesktopLayout(
                fullName = uiState.fullName,
                onFullNameChange = viewModel::onFullNameChange,
                email = uiState.email,
                onEmailChange = viewModel::onEmailChange,
                phone = uiState.phone,
                onPhoneChange = viewModel::onPhoneChange,
                company = uiState.company,
                onCompanyChange = viewModel::onCompanyChange,
                password = uiState.password,
                onPasswordChange = viewModel::onPasswordChange,
                confirmPassword = uiState.confirmPassword,
                onConfirmPasswordChange = viewModel::onConfirmPasswordChange,
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
                apiError = apiError,
                termsError = termsError,
                isLoading = uiState.isLoading,
                onRegister = { validateAndRegister() },
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
    apiError: String?,
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
            apiError = apiError,
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
    apiError: String?,
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
                apiError = apiError,
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
    apiError: String?,
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
            
            // API Error Banner (shown when registration fails)
            if (apiError != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MiningSafetyColors.Error.copy(alpha = 0.1f)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "⚠️",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = apiError,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MiningSafetyColors.Error,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
            
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

/**
 * Registration Success Dialog
 * Shown after successful registration with options to login or stay
 */
@Composable
private fun RegistrationSuccessDialog(
    onLoginClick: () -> Unit,
    onStayClick: () -> Unit
) {
    androidx.compose.ui.window.Dialog(
        onDismissRequest = { /* Prevent dismissal by clicking outside */ }
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
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
                // Success Icon
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(40.dp))
                        .background(MiningSafetyColors.Success.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🎉",
                        style = MaterialTheme.typography.displayMedium
                    )
                }
                
                Spacer(modifier = Modifier.height(20.dp))
                
                // Title
                Text(
                    text = "Registration Successful!",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MiningSafetyColors.OnBackground,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Message
                Text(
                    text = "Your account has been created successfully. Would you like to log in now?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MiningSafetyColors.OnSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(28.dp))
                
                // Buttons
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Yes, Log me in button
                    Button(
                        onClick = onLoginClick,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MiningSafetyColors.Primary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Yes, Log me in",
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                    
                    // No, stay here button
                    TextButton(
                        onClick = onStayClick,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "No, stay here",
                            color = MiningSafetyColors.OnSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
