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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignInScreen(
    auth: FirebaseAuth,
    onSignInSuccess: () -> Unit,
    onSignInNavigation: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var userEmail by rememberSaveable { mutableStateOf("") }
    var userPassword by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var isLoading by rememberSaveable { mutableStateOf(false) }
    var errorMessage by rememberSaveable { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF43A047), Color(0xFF66BB6A)) // Gradient with shades of green
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Welcome Back!", fontSize = 24.sp, color = Color.Black)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Sign in to continue", fontSize = 14.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(32.dp))

                OutlinedTextField(
                    value = userEmail,
                    onValueChange = { userEmail = it },
                    label = { Text("Email Address") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color(0xFF43A047),
                        unfocusedBorderColor = Color(0xFF66BB6A),
                      //  textColor = Color.Black, // Black text color
                        focusedLabelColor = Color(0xFF43A047),
                        unfocusedLabelColor = Color.Gray
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = userPassword,
                    onValueChange = { userPassword = it },
                    label = { Text("Password") },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(imageVector = Icons.Default.Lock, contentDescription = "Toggle Password")
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color(0xFF43A047),
                        unfocusedBorderColor = Color(0xFF66BB6A),
                       // textColor = Color.Black, // Black text color
                        focusedLabelColor = Color(0xFF43A047),
                        unfocusedLabelColor = Color.Gray
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))

                if (errorMessage != null) {
                    Text(text = errorMessage!!, color = MaterialTheme.colorScheme.error)
                }
                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        scope.launch {
                            isLoading = true
                            errorMessage = null
                            val result = signInUser(auth, userEmail, userPassword, context)
                            if (result.isSuccess) {
                                onSignInSuccess()
                                onSignInNavigation()
                            } else {
                                errorMessage = result.exceptionOrNull()?.message ?: "Sign-in failed"
                            }
                            isLoading = false
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF43A047))
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White)
                    } else {
                        Text("Sign In", fontSize = 16.sp, color = Color.White)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                TextButton(onClick = onSignInNavigation) {
                    Text("Don't have an account? Sign Up", color = Color.Black)
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

private suspend fun signInUser(
    auth: FirebaseAuth,
    email: String,
    password: String,
    context: Context
): Result<Unit> {
    return try {
        if (email.isEmpty() || password.isEmpty()) {
            throw IllegalArgumentException("Please fill all the fields")
        }
        auth.signInWithEmailAndPassword(email, password).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
}

@Preview(showBackground = true)
@Composable
fun SignInScreenPreview() {
    SignInScreen(auth = FirebaseAuth.getInstance(), onSignInSuccess = {}, onSignInNavigation = {})
}
