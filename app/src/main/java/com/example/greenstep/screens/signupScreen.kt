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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
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



    val PrimaryGreen = Color(0xFF0BC226)
    val LightGreen = Color(0xFFA4FFB1)
    val DarkGreen = Color(0xFF43A047)
    val LightGray = Color(0xFF66BB6A)
    val White = Color(0xFFFFFFFF)
    val Black = Color(0xFF000000)



    Box (
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
                        text = "Already have an account ?",
                        color = Color.White,
                        modifier = Modifier.padding(top = 4.dp),
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Spacer(Modifier.width(5.dp))
                    Button(
                        onClick = {
                            onSignUpNavigation()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = LightGreen,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            "Sign in",
                            color = PrimaryGreen
                        )
                    }
                }
            }
            //Spacer(Modifier.height(16.dp))
            Text(
                "GreenStep",
                style = TextStyle(
                    color = White,
                    fontSize = MaterialTheme.typography.titleLarge.fontSize,
                    fontFamily = MaterialTheme.typography.titleLarge.fontFamily,
                    fontWeight = MaterialTheme.typography.titleLarge.fontWeight,
                ),
                modifier = Modifier.align(Alignment.Center)
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(PrimaryGreen)
                .align(Alignment.BottomEnd)
                .height(600.dp)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxSize(),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                shape = RoundedCornerShape(
                    topStart = 35.dp,
                    topEnd = 35.dp,
                    bottomStart = 0.dp,
                    bottomEnd = 0.dp
                )
            ) {
                Text(
                    "Get started free.",
                    style = TextStyle(
                        fontWeight = MaterialTheme.typography.titleLarge.fontWeight,
                        fontSize = MaterialTheme.typography.titleLarge.fontSize,
                        fontFamily = MaterialTheme.typography.titleLarge.fontFamily
                    ),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                        .padding(top = 20.dp)
                )
                Text(
                    "Free forever no credit card needed",
                    style = TextStyle(
                        fontWeight = MaterialTheme.typography.labelSmall.fontWeight,
                        fontSize = MaterialTheme.typography.labelSmall.fontSize,
                        fontFamily = MaterialTheme.typography.labelSmall.fontFamily
                    ),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                        .padding(top = 16.dp)
                )

                OutlinedTextField(
                    value = userName,
                    onValueChange = { userName = it },
                    label = { Text("Name") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth()
                        .padding(16.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color(0xFF43A047),
                        unfocusedBorderColor = Color(0xFF66BB6A),
                        //  textColor = Color.Black, // Black text color
                        focusedLabelColor = Color(0xFF43A047),
                        unfocusedLabelColor = Color.Gray
                    )
                )
                OutlinedTextField(
                    value = userEmail,
                    onValueChange = { userEmail = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth()
                        .padding(16.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    colors =
                    TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color(0xFF43A047),
                        //  textColor = Color.Balck,
                        unfocusedBorderColor = Color(0xFF66BB6A),
                        focusedLabelColor = Color(0xFF43A047),
                        unfocusedLabelColor = Color.Gray
                    )
                )
                OutlinedTextField(
                    value = userPassword,
                    onValueChange = { userPassword = it },
                    label = { Text("Password") },
                    modifier = Modifier.fillMaxWidth()
                        .padding(16.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    colors =
                    TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color(0xFF43A047),
                        //  textColor = Color.Balck,
                        unfocusedBorderColor = Color(0xFF66BB6A),
                        focusedLabelColor = Color(0xFF43A047),
                        unfocusedLabelColor = Color.Gray
                    ) ,
                    trailingIcon = {
                        IconButton(onClick = {
                            passwordVisible = !passwordVisible
                        }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                contentDescription = "Toggle Password Visibility"
                            )
                        }
                    }
                )
                OutlinedTextField(
                    value = userConfirmPassword,
                    onValueChange = { userConfirmPassword = it },
                    label = { Text("Confirm Password") },
                    modifier = Modifier.fillMaxWidth()
                        .padding(16.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    colors =
                    TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color(0xFF43A047),
                        //  textColor = Color.Balck,
                        unfocusedBorderColor = Color(0xFF66BB6A),
                        focusedLabelColor = Color(0xFF43A047),
                        unfocusedLabelColor = Color.Gray
                    ) ,
                    trailingIcon = {
                        IconButton(onClick = {
                            passwordVisible = !passwordVisible
                        }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                contentDescription = "Toggle Password Visibility"
                            )
                        }
                    }
                )
                Spacer(Modifier.height(16.dp))
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
                    colors = ButtonDefaults.buttonColors(Color(0xFF0BC226)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {

                    if (isLoading) {
                        CircularProgressIndicator(color = Color(0xFF43A047))
                    } else {
                        Text(
                            "Sign Up",
                            style = TextStyle(
                                color = Color.Black,
                                fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                                fontFamily = MaterialTheme.typography.bodyLarge.fontFamily,
                                fontWeight = MaterialTheme.typography.titleLarge.fontWeight
                            )
                        )
                    }
                }
                Spacer(Modifier.height(16.dp))
            }
        }
    }
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