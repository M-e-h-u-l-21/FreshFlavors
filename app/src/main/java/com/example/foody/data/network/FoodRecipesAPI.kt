package com.example.foody.data.network

import com.example.foody.models.FoodJoke
import com.example.foody.models.FoodRecipe
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface FoodRecipesAPI {
    @GET("/recipes/complexSearch")
    suspend fun getRecipes(
        @QueryMap queries:Map<String,String> // Used this annotation here so that we dont have to create the query string , allows to send query as hashmap
    ): Response<FoodRecipe>


    @GET("/recipes/complexSearch")
    suspend fun searchRecipes(
        @QueryMap searchQuery:Map<String,String>
    ):Response<FoodRecipe>

    @GET("/food/jokes/random")
    suspend fun getFoodJoke(
        @Query("apiKey") apiKey:String
    ):Response<FoodJoke>
}