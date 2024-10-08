package com.example.foody.bindingadapters

import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.navigation.findNavController
import coil.load
import com.example.foody.R
import com.example.foody.models.Result
import com.example.foody.ui.fragments.recipes.RecipesFragmentDirections
import org.jsoup.Jsoup
import java.lang.Exception

class RecipesRowBinding {
    // Since number of likes were integer we cant directly set it in view , thats why we create adapter
    companion object{

        @BindingAdapter("onRecipeClickListener")
        @JvmStatic
        fun onRecipeClickListener(recipeRowLayout:ConstraintLayout,result: Result){ // For handling the clicks of a particular recipe
            recipeRowLayout.setOnClickListener {
                try {
                    val action = RecipesFragmentDirections.actionRecipesFragmentToDetailsActivity(result)
                    recipeRowLayout.findNavController().navigate(action)
                }catch (e:Exception){
                    Log.d("onRecipeClickListener",e.toString())
                }
            }
        }

        @BindingAdapter("setNumberOfLikes") //Telling that this is a function that should be called from binding class
        @JvmStatic
        fun setNumberOfLikes(textView: TextView,likes:Int){
            textView.text=likes.toString()
        }

        @BindingAdapter("setNumberOfMinutes")
        @JvmStatic
        fun setNumberOfMinutes(textView: TextView,minutes:Int){
            textView.text=minutes.toString()
        }

        @BindingAdapter("applyVeganColor")
        @JvmStatic
        fun applyVeganColor(view: View, vegan:Boolean){ // Took view as parameter because textview and imageview both will be the child of view
            if(vegan){
                when(view){
                    is TextView -> {
                        view.setTextColor(ContextCompat.getColor(view.context, R.color.green))
                    }
                    is ImageView -> {
                        view.setColorFilter(
                            ContextCompat.getColor(
                                view.context,
                                R.color.green
                            )
                        )
                    }
                }
            }
        }

        @BindingAdapter("loadImageFromUrl")
        @JvmStatic
        fun loadImageFromUrl(imageView: ImageView,imageUrl:String){
            imageView.load(imageUrl){
                crossfade(600)
                error(R.drawable.placeholder)
            }
        }

        @BindingAdapter("parseHTML")
        @JvmStatic
        fun parseHTML(textView:TextView, description: String){
            if(description!=null){
                val desc = Jsoup.parse(description).text()
                textView.text = desc
            }


        }
    }
}