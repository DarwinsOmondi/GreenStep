package com.example.greenstep.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun SignUpScreen(
    auth: FirebaseAuth,
    onSignUpSuccess: () -> Unit,
    onSignUpNavigation: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var userName by rememberSaveable { mutableStateOf("") }
    var userEmail by rememberSaveable { mutableStateOf("") }
    var userPassword by rememberSaveable { mutableStateOf("") }
    var userConfirmPassword by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var confirmPasswordVisible by rememberSaveable { mutableStateOf(false) }
    var isLoading by rememberSaveable { mutableStateOf(false) }
    var errorMessage by rememberSaveable { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF43A047), Color(0xFF66BB6A)))),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.padding(24.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Create an Account", style = MaterialTheme.typography.headlineMedium, color = Color.Black)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Join us to get started", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                Spacer(modifier = Modifier.height(24.dp))

                CustomTextField(value = userName, onValueChange = { userName = it }, label = "Name")
                CustomTextField(value = userEmail, onValueChange = { userEmail = it }, label = "Email")
                CustomPasswordField(value = userPassword, onValueChange = { userPassword = it }, label = "Password", isVisible = passwordVisible, onVisibilityChange = { passwordVisible = it })
                CustomPasswordField(value = userConfirmPassword, onValueChange = { userConfirmPassword = it }, label = "Confirm Password", isVisible = confirmPasswordVisible, onVisibilityChange = { confirmPasswordVisible = it })
                Spacer(modifier = Modifier.height(8.dp))

                if (errorMessage != null) {
                    Text(text = errorMessage!!, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(8.dp))
                }

                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        scope.launch {
                            isLoading = true
                            errorMessage = null
                            if (userPassword != userConfirmPassword) {
                                errorMessage = "Passwords do not match"
                            } else {
                                val result = signUpUser(auth, userEmail, userPassword, context)
                                if (result.isSuccess) {
                                    onSignUpSuccess()
                                } else {
                                    errorMessage = result.exceptionOrNull()?.message ?: "Sign-up failed"
                                }
                            }
                            isLoading = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF43A047)),
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color(0xFF43A047))
                    } else {
                        Text("Sign Up", fontSize = 16.sp, color = Color.White)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                TextButton(onClick = onSignUpNavigation) {
                    Text("Already have an account? Sign In", color = Color.Black)
                }
            }
        }
    }
}

@Composable
fun CustomTextField(value: String, onValueChange: (String) -> Unit, label: String) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(8.dp))
}

@Composable
fun CustomPasswordField(value: String, onValueChange: (String) -> Unit, label: String, isVisible: Boolean, onVisibilityChange: (Boolean) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        visualTransformation = if (isVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        trailingIcon = {
            IconButton(onClick = { onVisibilityChange(!isVisible) }) {
                Icon(imageVector = Icons.Default.Lock, contentDescription = "Toggle Password Visibility")
            }
        },
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(8.dp))
}

private suspend fun signUpUser(auth: FirebaseAuth, email: String, password: String, context: Context): Result<Unit> {
    return try {
        if (email.isEmpty() || password.isEmpty()) {
            throw IllegalArgumentException("Please fill all the fields")
        }
        auth.createUserWithEmailAndPassword(email, password).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
}

@Preview(showBackground = true)
@Composable
fun SignUpScreenPreview() {
    SignUpScreen(auth = FirebaseAuth.getInstance(), onSignUpSuccess = {}, onSignUpNavigation = {})
}
