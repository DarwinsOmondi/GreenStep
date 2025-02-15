package com.example.greenstep

import android.util.Log
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CarbonFromSheetViewModel:ViewModel() {
    fun saveSheetData(vehicleModel: String, fuelType: String, electricitySource: String){
        val auth = FirebaseAuth.getInstance()
        val firestore = FirebaseFirestore.getInstance()
        val currentUser = auth.currentUser


        if(currentUser != null){
            val userDocRef = firestore.collection("user")
                .document(currentUser.uid)
                .collection("sheet_data")


            val sheetData = hashMapOf(
                "vehicleModel" to vehicleModel,
                "fuelType" to fuelType,
                "electricitySource" to electricitySource,
                "timestamp" to System.currentTimeMillis()
            )

            userDocRef.add(sheetData)
                .addOnSuccessListener {
                    Log.d("Firebase", "Data successfully saved for user: ${currentUser.uid}")
                }
                .addOnFailureListener { e->
                    Log.e("Firebase", "Error saving data", e)
                }
        }
        else {
            Log.e("Firebase", "No authenticated user found.")
        }
    }
}