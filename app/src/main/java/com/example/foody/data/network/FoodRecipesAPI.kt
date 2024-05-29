package com.example.foody.data.network

import com.example.foody.models.FoodRecipe
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.QueryMap

interface FoodRecipesAPI {
    @GET("/recipes/complexSearch")
    suspend fun getRecipes(
        @QueryMap queries:Map<String,String> // Used this annotation here so that we dont have to create the query string , allows to send query as hashmap
    ): Response<FoodRecipe>
}