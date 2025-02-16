package com.example.greenstep

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CarbonFromSheetViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    fun saveSheetData(vehicleModel: String, fuelType: String, electricitySource: String) {
        val currentUser = auth.currentUser

        if (currentUser != null) {
            val userDocRef = firestore.collection("users")
                .document(currentUser.uid)
                .collection("sheet_data")

            val sheetData = hashMapOf(
                "vehicleModel" to vehicleModel,
                "fuelType" to fuelType,
                "electricitySource" to electricitySource,
                "timestamp" to System.currentTimeMillis()
            )

            Log.d("Firebase", "Saving data for user: ${currentUser.uid}")
            Log.d("Firebase", "Data: $sheetData")

            userDocRef.add(sheetData)
                .addOnSuccessListener {
                    Log.d("Firebase", "Data successfully saved for user: ${currentUser.uid}")
                }
                .addOnFailureListener { e ->
                    Log.e("Firebase", "Error saving data", e)
                }
        } else {
            Log.e("Firebase", "No authenticated user found.")
        }
    }

    fun fetchSheetData(onResult: (List<Map<String, Any>>) -> Unit) {
        val currentUser = auth.currentUser

        if (currentUser != null) {
            val userDocRef = firestore.collection("users")
                .document(currentUser.uid)
                .collection("sheet_data")
                .orderBy("timestamp")

            userDocRef.get()
                .addOnSuccessListener { documents ->
                    val dataList = documents.map { it.data }
                    onResult(dataList)
                }
                .addOnFailureListener { e ->
                    Log.e("Firebase", "Error fetching data", e)
                }
        } else {
            Log.e("Firebase", "No authenticated user found.")
        }
    }
}
