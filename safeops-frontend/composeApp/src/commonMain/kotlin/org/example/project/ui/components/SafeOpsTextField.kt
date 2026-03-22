package org.example.project.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import org.example.project.ui.theme.MiningSafetyColors

/**
 * A reusable text field component for SafeOps with consistent styling
 *
 * @param value Current text value
 * @param onValueChange Callback when text changes
 * @param label Label text displayed above the field
 * @param placeholder Placeholder text when empty
 * @param prefix Optional prefix icon/text (e.g., emoji)
 * @param error Error message to display (null for no error)
 * @param keyboardType Type of keyboard to show
 * @param imeAction IME action for the keyboard
 * @param onImeAction Callback when IME action is triggered
 * @param singleLine Whether the field is single line
 * @param enabled Whether the field is enabled
 * @param modifier Modifier for styling
 */
@Composable
fun SafeOpsTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String = "",
    prefix: String? = null,
    error: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    onImeAction: () -> Unit = {},
    singleLine: Boolean = true,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = { Text(placeholder) },
        prefix = prefix?.let {
            { Text(text = "$it ", style = MaterialTheme.typography.bodyMedium) }
        },
        isError = error != null,
        supportingText = error?.let { { Text(it) } },
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            imeAction = imeAction
        ),
        keyboardActions = KeyboardActions(
            onAny = { onImeAction() }
        ),
        singleLine = singleLine,
        enabled = enabled,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MiningSafetyColors.Primary,
            focusedLabelColor = MiningSafetyColors.Primary,
            errorBorderColor = MiningSafetyColors.Error,
            disabledBorderColor = MiningSafetyColors.Outline.copy(alpha = 0.5f),
            disabledLabelColor = MiningSafetyColors.OnSurfaceVariant.copy(alpha = 0.5f),
            disabledTextColor = MiningSafetyColors.OnSurfaceVariant.copy(alpha = 0.5f)
        )
    )
}

/**
 * A password text field with show/hide toggle
 *
 * @param value Current password value
 * @param onValueChange Callback when password changes
 * @param label Label text
 * @param placeholder Placeholder text
 * @param passwordVisible Whether password is currently visible
 * @param onTogglePasswordVisibility Callback to toggle visibility
 * @param error Error message (null for no error)
 * @param imeAction IME action for keyboard
 * @param onImeAction Callback when IME action triggered
 * @param enabled Whether the field is enabled
 * @param modifier Modifier for styling
 */
@Composable
fun SafeOpsPasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String = "Password",
    placeholder: String = "Enter your password",
    passwordVisible: Boolean,
    onTogglePasswordVisibility: () -> Unit,
    error: String? = null,
    imeAction: ImeAction = ImeAction.Done,
    onImeAction: () -> Unit = {},
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = { Text(placeholder) },
        prefix = { Text(text = "🔒 ", style = MaterialTheme.typography.bodyMedium) },
        trailingIcon = {
            TextButton(
                onClick = onTogglePasswordVisibility,
                enabled = enabled
            ) {
                Text(
                    text = if (passwordVisible) "HIDE" else "SHOW",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (enabled) MiningSafetyColors.Primary else MiningSafetyColors.OnSurfaceVariant.copy(alpha = 0.5f)
                )
            }
        },
        isError = error != null,
        supportingText = error?.let { { Text(it) } },
        visualTransformation = if (passwordVisible) 
            VisualTransformation.None 
        else 
            PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = imeAction
        ),
        keyboardActions = KeyboardActions(
            onAny = { onImeAction() }
        ),
        singleLine = true,
        enabled = enabled,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MiningSafetyColors.Primary,
            focusedLabelColor = MiningSafetyColors.Primary,
            errorBorderColor = MiningSafetyColors.Error,
            disabledBorderColor = MiningSafetyColors.Outline.copy(alpha = 0.5f),
            disabledLabelColor = MiningSafetyColors.OnSurfaceVariant.copy(alpha = 0.5f),
            disabledTextColor = MiningSafetyColors.OnSurfaceVariant.copy(alpha = 0.5f)
        )
    )
}

/**
 * A confirm password field that is disabled until password is entered
 *
 * @param value Current confirm password value
 * @param onValueChange Callback when value changes
 * @param password The original password value (determines if enabled)
 * @param passwordVisible Whether to show password text
 * @param onToggleVisibility Callback to toggle visibility
 * @param error Error message (null for no error)
 * @param imeAction IME action
 * @param onImeAction Callback when IME action triggered
 * @param modifier Modifier for styling
 */
@Composable
fun SafeOpsConfirmPasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    password: String,
    passwordVisible: Boolean,
    onToggleVisibility: () -> Unit,
    error: String? = null,
    imeAction: ImeAction = ImeAction.Done,
    onImeAction: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val enabled = password.isNotEmpty()
    
    Column {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { 
                Text(
                    if (enabled) "Confirm Password" 
                    else "Enter password first"
                ) 
            },
            placeholder = { 
                Text(
                    if (enabled) "Re-enter your password" 
                    else "Please enter password above"
                ) 
            },
            prefix = { Text(text = "🔐 ", style = MaterialTheme.typography.bodyMedium) },
            trailingIcon = {
                if (enabled) {
                    TextButton(onClick = onToggleVisibility) {
                        Text(
                            text = if (passwordVisible) "HIDE" else "SHOW",
                            style = MaterialTheme.typography.labelSmall,
                            color = MiningSafetyColors.Primary
                        )
                    }
                }
            },
            isError = error != null,
            supportingText = error?.let { { Text(it) } },
            visualTransformation = if (passwordVisible) 
                VisualTransformation.None 
            else 
                PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = imeAction
            ),
            keyboardActions = KeyboardActions(
                onAny = { onImeAction() }
            ),
            singleLine = true,
            enabled = enabled,
            modifier = modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MiningSafetyColors.Primary,
                focusedLabelColor = MiningSafetyColors.Primary,
                errorBorderColor = MiningSafetyColors.Error,
                disabledBorderColor = MiningSafetyColors.Outline.copy(alpha = 0.3f),
                disabledLabelColor = MiningSafetyColors.OnSurfaceVariant.copy(alpha = 0.4f),
                disabledTextColor = MiningSafetyColors.OnSurfaceVariant.copy(alpha = 0.4f),
                disabledPlaceholderColor = MiningSafetyColors.OnSurfaceVariant.copy(alpha = 0.3f)
            )
        )
    }
}
