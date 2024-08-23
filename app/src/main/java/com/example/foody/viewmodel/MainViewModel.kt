package com.example.foody.viewmodel

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.foody.data.Repository
import com.example.foody.data.database.entities.FavoritesEntity
import com.example.foody.data.database.entities.FoodJokeEntity
import com.example.foody.data.database.entities.RecipesEntity
import com.example.foody.models.FoodJoke
import com.example.foody.models.FoodRecipe
import com.example.foody.util.Constants.Companion.API_KEY
import com.example.foody.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject
import kotlin.Exception

@HiltViewModel
class MainViewModel @Inject constructor( // Instead of creating a separate viewmodel factory to inject the repository we are using Hilt
    private val repository: Repository, application: Application
) : AndroidViewModel(application) {

    /* Room Database*/

    val readRecipes : LiveData<List<RecipesEntity>> = repository.local.readRecipes().asLiveData() // Converting flow to Live Data
    val readFavoriteRecipes:LiveData<List<FavoritesEntity>> = repository.local.readFavoriteRecipes().asLiveData()
    val readFoodJoke:LiveData<List<FoodJokeEntity>> = repository.local.readFoodJoke().asLiveData()

    private fun insertRecipes(recipesEntity: RecipesEntity){
        viewModelScope.launch(Dispatchers.IO) {
            repository.local.insertRecipes(recipesEntity)
        }
    }

     fun insertFavoriteRecipe(favoritesEntity: FavoritesEntity){
        viewModelScope.launch(Dispatchers.IO) {
            repository.local.insertFavoriteRecipes(favoritesEntity)
        }
    }

    fun insertFoodJoke(foodJokeEntity: FoodJokeEntity)=viewModelScope.launch(Dispatchers.IO) {
        repository.local.insertFoodJoke(foodJokeEntity)
    }

     fun deleteFavoriteRecipe(favoritesEntity: FavoritesEntity)=
        viewModelScope.launch(Dispatchers.IO) {
            repository.local.deleteFavoriteRecipe(favoritesEntity)
        }

    fun deleteAllFavoriteRecipes() =
        viewModelScope.launch(Dispatchers.IO) {
            repository.local.deleteAllFavoriteRecipes()
        }


    /* Retrofit*/
    val recipesResponse:MutableLiveData<NetworkResult<FoodRecipe>> = MutableLiveData()
    val searchRecipesResponse: MutableLiveData<NetworkResult<FoodRecipe>> = MutableLiveData()
    var foodJokeResponse:MutableLiveData<NetworkResult<FoodJoke>> = MutableLiveData()

    fun getRecipes(queries:Map<String,String>) = viewModelScope.launch {
        getRecipesSafeCall(queries)
    }

    fun searchQuery(searchQuery : Map<String,String>) = viewModelScope.launch {
        searchRecipesSafeCall(searchQuery)
    }

    fun getFoodJoke(apiKey:String) = viewModelScope.launch {
        getFoodJokeSafeCall(API_KEY)
    }


    private suspend fun getRecipesSafeCall(queries: Map<String, String>) {
        recipesResponse.value = NetworkResult.Loading()
        if(hasInternetConnection()){
            try {
                val response = repository.remote.getRecipes(queries)
                recipesResponse.value = handleRecipeResponse(response)


                val foodRecipe = recipesResponse.value!!.data
                if(foodRecipe!=null){
                    offlineCacheRecipes(foodRecipe)
                }
            }catch (e:Exception){
                recipesResponse.value = NetworkResult.Error("Recipes Not Found")
            }
        }else{
            recipesResponse.value = NetworkResult.Error("No Internet Connection")
        }
    }

    private suspend fun searchRecipesSafeCall(searchQuery: Map<String, String>) {
        searchRecipesResponse.value = NetworkResult.Loading()
        if(hasInternetConnection()){
            try {
                val response = repository.remote.searchRecipes(searchQuery)
                searchRecipesResponse.value = handleRecipeResponse(response)

                val foodJoke = foodJokeResponse.value!!.data
                if(foodJoke!=null){
                    offlineCacheFoodJoke(foodJoke)
                }
            }catch (e:Exception){
                searchRecipesResponse.value = NetworkResult.Error("Recipes Not Found")
            }
        }else{
            searchRecipesResponse.value = NetworkResult.Error("No Internet Connection")
        }
    }

    private suspend fun getFoodJokeSafeCall(apiKey: String) {
        foodJokeResponse.value = NetworkResult.Loading()
        if(hasInternetConnection()){
            try{
                val response = repository.remote.getFoodJoke(API_KEY)
                foodJokeResponse.value = handleFoodJokeResponse(response)
            }catch (e:Exception){
                foodJokeResponse.value = NetworkResult.Error("Unable to fetch the joke")
            }
        }else{
            foodJokeResponse.value = NetworkResult.Error("No Internet Connection ")
        }
    }

    private fun handleFoodJokeResponse(response: Response<FoodJoke>): NetworkResult<FoodJoke>{
        return when{
            response.message().toString().contains("timeout")->{
                 NetworkResult.Error("Timeout")
            }

            response.code()==402->{
                 NetworkResult.Error("API Key Limited")
            }

            response.isSuccessful -> {
                val foodJoke = response.body()
                 NetworkResult.Success(foodJoke!!)
            }
            else ->{
                 NetworkResult.Error(response.message())
            }
        }
    }


    private fun offlineCacheRecipes(foodRecipe: FoodRecipe) {
        val recipesEntity = RecipesEntity(foodRecipe) // Need to convert foodRecipe into RecipesEntity in order to insert into database
        insertRecipes(recipesEntity)
    }

    private fun offlineCacheFoodJoke(foodJoke: FoodJoke) {
        val foodJokeEntity = FoodJokeEntity(foodJoke)
        insertFoodJoke(foodJokeEntity)
    }

    private fun handleRecipeResponse(response: Response<FoodRecipe>): NetworkResult<FoodRecipe>{
        when{
            response.message().toString().contains("timeout")->{
                return NetworkResult.Error("Timeout")
            }

            response.code()==402->{
                return NetworkResult.Error("API Key Limited")
            }

            response.body()!!.results.isEmpty()->{ // Response present but empty response
                return NetworkResult.Error("Recipes Not Found")
            }

            response.isSuccessful -> {
                val foodRecipes = response.body()
                Log.d("NEW DATA",foodRecipes?.results.toString())
                return NetworkResult.Success(foodRecipes!!)
            }

            else ->{
                return NetworkResult.Error(response.message())
            }
        }
    }

    private fun hasInternetConnection():Boolean{
        val connectivityManager=getApplication<Application>().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val activeNetwork = connectivityManager.activeNetwork?:return false
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork)?:return false
        return when{
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> {return false}
        }
    }
}