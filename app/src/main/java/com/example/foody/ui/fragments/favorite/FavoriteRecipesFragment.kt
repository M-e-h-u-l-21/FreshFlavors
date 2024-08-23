 package com.example.foody.ui.fragments.favorite

import android.os.Bundle
import android.os.Message
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.foody.R
import com.example.foody.adapters.FavoriteRecipesAdapter
import com.example.foody.databinding.FragmentFavoriteRecipesBinding
import com.example.foody.viewmodel.MainViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

 @AndroidEntryPoint
 class FavoriteRecipesFragment : Fragment() {
     private val mainViewModel: MainViewModel by viewModels()
    private val mAdapter: FavoriteRecipesAdapter by lazy { FavoriteRecipesAdapter(requireActivity(),mainViewModel) }
     private var _binding: com.example.foody.databinding.FragmentFavoriteRecipesBinding? = null
     private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding =  FragmentFavoriteRecipesBinding.inflate(inflater,container,false)
        binding.lifecycleOwner = this
        binding.mainViewModel = mainViewModel
        binding.mAdapter = mAdapter
        setupRecyclerView(binding.favoriteRecipesRecyclerView)

        return binding.root
    }

     @Deprecated("Deprecated in Java")
     override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
         inflater.inflate(R.menu.favorites_recipes_menu,menu)
         super.onCreateOptionsMenu(menu, inflater)
     }

     @Deprecated("Deprecated in Java")
     override fun onOptionsItemSelected(item: MenuItem): Boolean {
         if(item.itemId == R.id.deleteAll_favorite_recipes_menu){
             mainViewModel.deleteAllFavoriteRecipes()
         }
         showSnackbar()
         return super.onOptionsItemSelected(item)
     }
     private fun showSnackbar(){
         Snackbar.make(
              binding.root,
             "All recipes removed",
             Snackbar.LENGTH_SHORT
         ).setAction("Okay"){}.show()
     }
     override fun onDestroy() {
         super.onDestroy()
         _binding = null
         mAdapter.clearContextualActionMode()
     }

     private fun setupRecyclerView(recyclerView: RecyclerView){
         recyclerView.adapter = mAdapter
         recyclerView.layoutManager = LinearLayoutManager(requireContext())
     }

}