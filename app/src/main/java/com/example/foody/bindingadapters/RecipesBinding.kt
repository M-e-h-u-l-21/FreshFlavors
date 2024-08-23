package com.example.foody.bindingadapters

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.foody.data.database.entities.RecipesEntity
import com.example.foody.models.FoodRecipe
import com.example.foody.util.NetworkResult

class RecipesBinding {
    companion object{

        @BindingAdapter("readApiResponse","readDatabase", requireAll = true)
        @JvmStatic
        fun errorImageViewVisibility(view: ImageView,apiResponse:NetworkResult<FoodRecipe>?,database:List<RecipesEntity>?){
            if(apiResponse is NetworkResult.Error && database.isNullOrEmpty()){
                view.visibility = View.VISIBLE
            }else if(apiResponse is NetworkResult.Loading){
                view.visibility = View.INVISIBLE
            }else if(apiResponse is NetworkResult.Success){
                view.visibility = View.INVISIBLE
            }
        }


        @BindingAdapter("readApiResponse2","readDatabase2", requireAll = true)
        @JvmStatic
        fun recipesTextViewVisibility(view:TextView,apiResponse:NetworkResult<FoodRecipe>?,database:List<RecipesEntity>?){
            if(apiResponse is NetworkResult.Error && database.isNullOrEmpty()){
                view.visibility = View.VISIBLE
            }else if(apiResponse is NetworkResult.Loading){
                view.visibility = View.INVISIBLE
            }else if(apiResponse is NetworkResult.Success){
                view.visibility = View.INVISIBLE
            }
        }

    }
}