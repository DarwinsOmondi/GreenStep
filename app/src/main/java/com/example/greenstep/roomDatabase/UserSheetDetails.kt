package com.example.greenstep.roomDatabase

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sheet_table")
data class UserSheetDetails(

    @PrimaryKey(autoGenerate = true)
    val sheetId:Int,
    val fuelType:String,
    val vehicleModel:String,
    val electricitySource:String
)
