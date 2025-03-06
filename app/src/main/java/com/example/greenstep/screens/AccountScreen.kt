package com.example.greenstep.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.greenstep.CarbonFromSheetViewModel
import com.example.greenstep.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(
    navHostController: NavHostController,
    auth: FirebaseAuth,
    onSignOut: () -> Unit,
    onSheetNav: () -> Unit
) {
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var userName by remember { mutableStateOf("User Name") }
    var showDialog by remember { mutableStateOf(false) }
    val firestore = FirebaseFirestore.getInstance()
    val carbonFormSheetViewModel: CarbonFromSheetViewModel = viewModel()
    val formSheetData = remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }

    LaunchedEffect(Unit) {
        carbonFormSheetViewModel.fetchSheetData { data ->
            formSheetData.value = data
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = Color(0xFF43A047)),
                title = {
                    Text(
                        "Account",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            )
        },
        bottomBar = { BottomNavigationBar(navHostController) },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFFFFFF))
                .padding(paddingValues)
                .padding(top = 16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ProfileImage(imageUri)

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = userName,
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = auth.currentUser?.email ?: "user@example.com",
                style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            ActionButtons(
                onEditProfile = { showDialog = true },
                onSignOut = onSignOut,
                onSheetNav = onSheetNav
            )
        }
    }

    if (showDialog) {
        EditProfileDialog(
            initialName = userName,
            initialImageUri = imageUri,
            onDismiss = { showDialog = false },
            onSave = { newName ->
                userName = newName
                showDialog = false
            }
        )
    }
}

@Composable
fun ProfileImage(imageUri: Uri?) {
    Box(
        modifier = Modifier
            .size(120.dp)
            .clip(CircleShape)
            .border(2.dp, Color.Gray, CircleShape)
    ) {
        AsyncImage(
            model = imageUri ?: R.drawable.baseline_account_circle_24,
            contentDescription = "Profile Picture",
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun ActionButtons(
    onEditProfile: () -> Unit,
    onSignOut: () -> Unit,
    onSheetNav: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = onEditProfile,
            modifier = Modifier.fillMaxWidth(0.8f).height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF43A047))
        ) {
            Icon(Icons.Default.Edit, contentDescription = "Edit", modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Edit Profile")
        }

        Button(
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
            onClick = onSignOut,
            modifier = Modifier.fillMaxWidth(0.8f).height(50.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Log Out", modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Log Out", color = Color.White)
        }

        Button(
            colors = ButtonDefaults.buttonColors(containerColor = Color.Blue),
            onClick = onSheetNav,
            modifier = Modifier.fillMaxWidth(0.8f).height(50.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Edit User Data", color = Color.White)
        }
    }
}

@Composable
fun EditProfileDialog(
    initialName: String,
    initialImageUri: Uri?,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var userName by remember { mutableStateOf(initialName) }
    var imageUri by remember { mutableStateOf(initialImageUri) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Profile") },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ProfileImage(imageUri)

                Button(onClick = { launcher.launch("image/*") }) {
                    Text("Choose Image")
                }

                OutlinedTextField(
                    value = userName,
                    onValueChange = { userName = it },
                    label = { Text("Name") },
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(onClick = { onSave(userName) }) {
                Text("Save")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
