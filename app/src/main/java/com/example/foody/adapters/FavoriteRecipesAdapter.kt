package com.example.foody.adapters

import android.view.ActionMode
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.foody.R
import com.example.foody.data.database.entities.FavoritesEntity
import com.example.foody.databinding.FavoriteRecipesRowLayoutBinding
import com.example.foody.ui.fragments.favorite.FavoriteRecipesFragmentDirections
import com.example.foody.util.RecipesDiffUtil
import com.example.foody.viewmodel.MainViewModel
import com.google.android.material.card.MaterialCardView
import com.google.android.material.snackbar.Snackbar


class FavoriteRecipesAdapter(
    private val requireActivity:FragmentActivity,
    private val mainViewModel: MainViewModel
) : RecyclerView.Adapter<FavoriteRecipesAdapter.MyViewHolder>() , ActionMode.Callback {
    private var multiSelection = false
    private lateinit var rootView : View
    private lateinit var mActionMode: ActionMode

    private var myViewHolders = arrayListOf<ViewHolder>()
    private var selectedRecipes = arrayListOf<FavoritesEntity>()
    private var favoriteRecipes = emptyList<FavoritesEntity>()

    class MyViewHolder(private val binding: FavoriteRecipesRowLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(favoritesEntity: FavoritesEntity) {
            binding.favoritesEntity = favoritesEntity
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): MyViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = FavoriteRecipesRowLayoutBinding.inflate(layoutInflater, parent, false)
                return MyViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder.from(parent)
    }

    override fun getItemCount(): Int {
        return favoriteRecipes.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        myViewHolders.add(holder)
        rootView = holder.itemView.rootView
        val currentRecipe = favoriteRecipes[position]
        holder.bind(currentRecipe)


        /** Single Click Listener**/
        holder.itemView.findViewById<ConstraintLayout>(R.id.favoriteRecipesRowLayout)
            .setOnClickListener {
                if(multiSelection){
                    applySelection(holder,currentRecipe)
                }else{
                    val action =
                        FavoriteRecipesFragmentDirections.actionFavoriteRecipesFragmentToDetailsActivity(
                            currentRecipe.result
                        )
                    holder.itemView.findNavController().navigate(action)
                }
            }

        /** Long Click Listener**/
        holder.itemView.findViewById<ConstraintLayout>(R.id.favoriteRecipesRowLayout).setOnLongClickListener {
            if(!multiSelection){
                multiSelection = true
                requireActivity.startActionMode(this)
                applySelection(holder,currentRecipe)
                true
            }else{
                multiSelection = false
                false
            }
        }
    }

    private fun applySelection(holder:MyViewHolder,currentRecipe:FavoritesEntity){
        if(selectedRecipes.contains(currentRecipe)) {
            selectedRecipes.remove(currentRecipe)
            changeRecipeStyle(holder,R.color.cardBackgroundColor,R.color.strokeColor)
            applyActionModeTitle()
        }else{
            selectedRecipes.add(currentRecipe)
            changeRecipeStyle(holder,R.color.cardBackgroundLightColor,R.color.colorPrimary)
            applyActionModeTitle()
        }
    }

    private fun changeRecipeStyle(holder: ViewHolder, backgroundColor:Int, strokeColor:Int){
        holder.itemView.findViewById<ConstraintLayout>(R.id.favoriteRecipesRowLayout).setBackgroundColor(ContextCompat.getColor(requireActivity,backgroundColor))
        holder.itemView.findViewById<MaterialCardView>(R.id.favorite_row_card).strokeColor =
            ContextCompat.getColor(requireActivity,strokeColor)
    }

    private fun applyActionModeTitle(){
        when(selectedRecipes.size){
            0 -> {
                mActionMode.finish()
            }
            1->{
                mActionMode.title = "${selectedRecipes.size} item selected"
            }else->{
                mActionMode.title = "${selectedRecipes.size} items selected"
            }
        }
    }

    override fun onCreateActionMode(actionMode: ActionMode?, menu: Menu?): Boolean {
        actionMode?.menuInflater?.inflate(R.menu.favorites_contextual_menu,menu)
        mActionMode = actionMode!!
        applyStatusBarColor(R.color.contextualStatusBarColor)
        return true
    }

    override fun onPrepareActionMode(actionMode: ActionMode?, menu: Menu?): Boolean {
        return true
    }

    override fun onActionItemClicked(actionMode: ActionMode?, menu: MenuItem?): Boolean {
        if(menu?.itemId == R.id.delete_favorites_recipe_menu){
            selectedRecipes.forEach {
                mainViewModel.deleteFavoriteRecipe(favoritesEntity = it)
            }
            showSnackBar("${selectedRecipes.size} Recipes removed")
            multiSelection = false
            selectedRecipes.clear()
            actionMode?.finish()
        }
        return true
    }

    override fun onDestroyActionMode(actionMode: ActionMode?) {
        multiSelection = false
        selectedRecipes.clear()
        myViewHolders.forEach { holder ->
            changeRecipeStyle(holder,R.color.cardBackgroundColor,R.color.strokeColor)
        }
        applyStatusBarColor(R.color.statusBarColor)
    }

    private fun applyStatusBarColor(color:Int){
        requireActivity.window.statusBarColor = ContextCompat.getColor(requireActivity, color)
    }

    fun setData(newFavoriteRecipe: List<FavoritesEntity>) {
        val favoriteRecipesDiffUtil = RecipesDiffUtil(favoriteRecipes, newFavoriteRecipe)
        val diffUtilResult = DiffUtil.calculateDiff(favoriteRecipesDiffUtil)
        favoriteRecipes = newFavoriteRecipe
        diffUtilResult.dispatchUpdatesTo(this)
    }

    private fun showSnackBar(message:String){
        Snackbar.make(
            rootView,
            message,
            Snackbar.LENGTH_SHORT
        ).setAction("Okay"){}.show()
    }

    fun clearContextualActionMode(){
        if(this::mActionMode.isInitialized){
            mActionMode.finish()
        }
    }

}
