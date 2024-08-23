package com.example.foody.data.database.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.foody.models.FoodJoke
import com.example.foody.util.Constants.Companion.FOOD_JOKE_TABLE

@Entity(tableName = FOOD_JOKE_TABLE)
class FoodJokeEntity(
    @Embedded // Will inspect the FoodJoke class and store its field value in the table
    var foodJoke:FoodJoke
) {
    @PrimaryKey(autoGenerate = false)
    var id:Int = 0
}