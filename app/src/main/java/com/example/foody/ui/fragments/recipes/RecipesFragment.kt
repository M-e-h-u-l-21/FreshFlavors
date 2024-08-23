package com.example.foody.ui.fragments.recipes

import android.net.Network
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foody.viewmodel.MainViewModel
import com.example.foody.R
import com.example.foody.adapters.RecipesAdapter
import com.example.foody.databinding.FragmentRecipesBinding
import com.example.foody.util.NetworkListener
import com.example.foody.util.NetworkResult
import com.example.foody.util.observeOnce
import com.example.foody.viewmodel.RecipesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RecipesFragment : Fragment() , SearchView.OnQueryTextListener{

    private val args by navArgs<RecipesFragmentArgs>()

    private var _binding : FragmentRecipesBinding? = null
    private val binding get() = _binding!!

    private lateinit var networkListener: NetworkListener

    private val mAdapter by lazy{
        RecipesAdapter()
    }

    private lateinit var recipesViewModel: RecipesViewModel
    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainViewModel  = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        recipesViewModel = ViewModelProvider(requireActivity())[RecipesViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inflate the layout for this fragment
        _binding= FragmentRecipesBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this // lifecycle owner is necessary because we wil be using livedata and others
        binding.mainViewModel = mainViewModel

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.recipes_menu, menu)

                val search = menu.findItem(R.id.menu_search)
                val searchView = search.actionView as? SearchView
                searchView?.isSubmitButtonEnabled = true
                searchView?.setOnQueryTextListener(this@RecipesFragment)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)


        setupRecyclerView()

        recipesViewModel.readBackOnline.observe(viewLifecycleOwner){
            recipesViewModel.backOnline = it
        }

        lifecycleScope.launch {
            networkListener = NetworkListener()
            networkListener.checkNetworkAvailability(requireContext()).collect{status->
                Log.d("NetworkListener",status.toString())
                recipesViewModel.networkStatus = status
                recipesViewModel.showNetworkStatus()
                readDatabase()
            }
        }


        binding.recipesFab.setOnClickListener {
            if(recipesViewModel.networkStatus){
                findNavController().navigate(R.id.action_recipesFragment_to_recipesBottomsheet)
            }else{
                recipesViewModel.showNetworkStatus()
            }
        }
        return binding.root
    }

    private fun readDatabase() {
        lifecycleScope.launch {
            mainViewModel.readRecipes.observeOnce(viewLifecycleOwner) {database ->
                if(database.isNotEmpty() && !args.backFromBottomSheet){
                    Log.d("RECIPEFRAGMENT","DATABASE READ")
                    mAdapter.setData(database[0].foodRecipe)
                    hideShimmer()
                }else{
                    requestApiData()
                }
            }
        }
    }

    private fun setupRecyclerView(){
        binding.recyclerView.adapter = mAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        showShimmer()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.recipes_menu,menu)

        val search = menu.findItem(R.id.menu_search)
        val searchView = search.actionView as? SearchView
        searchView?.isSubmitButtonEnabled= true
        searchView?.setOnQueryTextListener(this)
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if(query!=null){
            searchApiData(query)
        }
        return true
    }

    override fun onQueryTextChange(p0: String?): Boolean {
        return true
    }

    private fun requestApiData(){
        Log.d("RECIPEFRAGMENT","API REQUEST")
        mainViewModel.getRecipes(recipesViewModel.applyQueries())
        mainViewModel.recipesResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    hideShimmer()
                    response.data?.let {
                        mAdapter.setData(it)
                    }
                }

                is NetworkResult.Error ->{
                    hideShimmer()
                    loadDataFromCache()
                    Toast.makeText(requireContext(),response.message.toString(),Toast.LENGTH_SHORT).show()
                }

                is NetworkResult.Loading ->{
                    showShimmer()
                }
                else -> {}
            }
        }
    }

    private fun searchApiData(searchQuery:String){
        showShimmer()
        mainViewModel.searchQuery(recipesViewModel.applySearchQueries(searchQuery))
        mainViewModel.searchRecipesResponse.observe(viewLifecycleOwner){response->
            when(response){
                is NetworkResult.Success -> {
                    hideShimmer()
                    val foodRecipe = response.data
                    foodRecipe?.let {
                        mAdapter.setData(it)
                    }
                }
                is NetworkResult.Error ->{
                    hideShimmer()
                    loadDataFromCache()
                    Toast.makeText(
                        requireContext(),
                        response.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is NetworkResult.Loading -> {
                    showShimmer()
                }
            }
        }
    }


    private fun loadDataFromCache(){
        lifecycleScope.launch {
            mainViewModel.readRecipes.observe(viewLifecycleOwner) {database ->
                if(database.isNotEmpty()){
                    Log.d("RECIPEFRAGMENT","DATABASE READ")
                    mAdapter.setData(database[0].foodRecipe)
                    hideShimmer()
                }
            }
        }
    }


    private fun showShimmer(){
        binding.recyclerView.showShimmer()
    }

    private fun hideShimmer(){
        binding.recyclerView.hideShimmer()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}