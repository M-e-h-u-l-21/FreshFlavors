package com.example.foody.data

import android.util.Log
import com.example.foody.data.network.FoodRecipesAPI
import com.example.foody.models.FoodJoke
import com.example.foody.models.FoodRecipe
import retrofit2.Response
import javax.inject.Inject

class RemoteDataSource @Inject constructor(
    private val foodRecipesAPI: FoodRecipesAPI // Injected RemoteDataSource with foodRecipesAPI
){
    suspend fun getRecipes(queries:Map<String,String>): Response<FoodRecipe> {
        return foodRecipesAPI.getRecipes(queries)
    }

    suspend fun searchRecipes(searchQuery:Map<String,String>):Response<FoodRecipe>{
        return foodRecipesAPI.searchRecipes(searchQuery)
    }

    suspend fun getFoodJoke(apiKey:String):Response<FoodJoke>{
        return foodRecipesAPI.getFoodJoke(apiKey)
    }
}