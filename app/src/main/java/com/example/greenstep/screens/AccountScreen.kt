package com.example.greenstep.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.greenstep.CarbonFromSheetViewModel
import com.example.greenstep.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(navHostController: NavHostController, auth: FirebaseAuth, onSignOut: () -> Unit,onSheetNav:() -> Unit) {
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var downloadedUri = remember { mutableStateOf<String?>("") }
    var userName by remember { mutableStateOf("User Name") }
    var showDialog by remember { mutableStateOf(false) }
    var refreshTrigger by remember { mutableStateOf(false) } // UI refresh trigger
    val firestore = FirebaseFirestore.getInstance()
    val currentUser = auth.currentUser
    val carbonFormSheetViewModel:CarbonFromSheetViewModel = viewModel()
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
                title = { Text("Account",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                ) },
            )
        },

        bottomBar = { BottomNavigationBar(navHostController) },
        containerColor = Color(0xFFFFFFFF),
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
                .background(Color(0xFFFFFFFF))
                .padding(top = 16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile Picture
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .border(2.dp, Color.Gray, CircleShape)
            ) {
                Image(
                    painter = if (imageUri != null) rememberAsyncImagePainter(imageUri)
                    else painterResource(id = R.drawable.baseline_account_circle_24),
                    contentDescription = "Profile Picture",
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = userName,
                style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = auth.currentUser?.email ?: "user@example.com",
                style = TextStyle(fontSize = 16.sp, color = Color.Gray),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))


            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = { showDialog = true },
                    modifier = Modifier.fillMaxWidth(0.8f).height(50.dp)
                ) {
                    Text("Edit Profile")
                }

                Button(
                    colors = ButtonDefaults.buttonColors(Color.Red),
                    onClick = {
                        auth.signOut()
                        onSignOut()
                    },
                    modifier = Modifier.height(50.dp)
                ) {
                    Text("Log Out", style = TextStyle(color = Color.White))
                }

                Spacer(Modifier.height(16.dp))
                Button(
                    colors = ButtonDefaults.buttonColors(Color.Red),
                    onClick = {
                        onSheetNav()
                    },
                    modifier = Modifier.height(50.dp)
                ) {
                    Text("Edit user data", style = TextStyle(color = Color.White))
                }
            }
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
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .border(2.dp, Color.Gray, CircleShape)
                ) {
                    Image(
                        painter = if (imageUri != null) rememberAsyncImagePainter(imageUri)
                        else painterResource(id = R.drawable.baseline_account_circle_24),
                        contentDescription = "Selected Image",
                        modifier = Modifier.fillMaxSize()
                    )
                }

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

fun saveProfileToFireStore(userId: String?, name: String,onComplete: () -> Unit) {
    if (userId == null) return

    val firestore = FirebaseFirestore.getInstance()
    val profileData = hashMapOf(
        "name" to name,
    )

    firestore.collection("users")
        .document(userId)
        .set(profileData)
        .addOnSuccessListener {
            println("Profile updated successfully.")
            onComplete() // Trigger UI refresh
        }
        .addOnFailureListener { e ->
            println("Error updating profile: ${e.message}")
        }
}