package com.example.greenstep.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

// Define colors for easy maintenance
val PrimaryGreen = Color(0xFF0BC226)
val LightGreen = Color(0xFFA4FFB1)
val DarkGreen = Color(0xFF43A047)
val LightGray = Color(0xFF66BB6A)
val White = Color(0xFFFFFFFF)
val Black = Color(0xFF000000)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignInScreen(
    auth: FirebaseAuth,
    onSignInSuccess: () -> Unit,
    onSignInNavigation: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var userEmail by remember { mutableStateOf("") }
    var userPassword by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxSize()
            .background(PrimaryGreen)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(PrimaryGreen)
                .height(300.dp)
        ) {
            Column(
                modifier = Modifier.align(Alignment.TopEnd)
                .padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 20.dp)
                ) {
                    Text(
                        text = "Don't have an account?",
                        modifier = Modifier.padding(top = 4.dp),
                        color = White,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = onSignInNavigation,
                        colors = ButtonDefaults.buttonColors(containerColor = LightGreen),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Get Started", color = PrimaryGreen)
                    }
                }
            }
            Text(
                "GreenStep",
                style = MaterialTheme.typography.titleLarge.copy(color = White),
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(PrimaryGreen)
                .align(Alignment.BottomCenter)
                .height(600.dp),
        ) {
            Card(
                modifier = Modifier.fillMaxSize(),
                colors = CardDefaults.cardColors(containerColor = White),
                shape = RoundedCornerShape(topStart = 35.dp, topEnd = 35.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Welcome Back!", style = MaterialTheme.typography.titleLarge)
                    Spacer(Modifier.height(8.dp))
                    Text("Enter your details below", style = MaterialTheme.typography.labelSmall)

                    Spacer(Modifier.height(16.dp))

                    OutlinedTextField(
                        value = userEmail,
                        onValueChange = { userEmail = it },
                        label = { Text("Email Address",
                            color = Color.Black,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor =Color.Black,
                            focusedBorderColor = Color.DarkGray,
                            unfocusedBorderColor = Color.Black
                        )
                    )

                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value = userPassword,
                        onValueChange = { userPassword = it },
                        label = { Text("Password",
                            color = Color.Black,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor =Color.Black,
                            focusedBorderColor = Color.DarkGray,
                            unfocusedBorderColor = Color.Black
                        ),
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                    contentDescription = "Toggle Password Visibility",
                                )
                            }
                        }
                    )

                    Spacer(Modifier.height(20.dp))

                    Button(
                        onClick = {
                            scope.launch {
                                isLoading = true
                                errorMessage = null
                                val result = signInUser(auth, userEmail, userPassword, context)
                                if (result.isSuccess) {
                                    onSignInSuccess()
                                } else {
                                    errorMessage = result.exceptionOrNull()?.message ?: "Sign-in failed"
                                }
                                isLoading = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(color = White, modifier = Modifier.size(24.dp))
                        } else {
                            Text("Sign In", style = MaterialTheme.typography.bodyLarge.copy(color = Black))
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    Text("Forgot Password?", color = Black, style = MaterialTheme.typography.labelSmall)

                    errorMessage?.let {
                        Spacer(Modifier.height(12.dp))
                        Text(it, color = Color.Red, style = MaterialTheme.typography.bodySmall)
                    }
                }
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
            throw IllegalArgumentException("Please fill all fields")
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
