package com.example.foody.ui.fragments.viewPager

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.foody.R
import com.example.foody.adapters.IngredientsAdapter
import com.example.foody.models.Result
import com.example.foody.util.Constants.Companion.RECIPE_RESULT_KEY

class IngredientsFragment : Fragment() {
    private val mAdapter : IngredientsAdapter by lazy { IngredientsAdapter() }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
         val view= inflater.inflate(R.layout.fragment_ingredients, container, false)

        val args = arguments
        val myBundle:Result? = args?.getParcelable(RECIPE_RESULT_KEY)
        setupRecyclerView(view)
        myBundle?.extendedIngredients?.let { mAdapter.setData(it) }
        return view

    }

    private fun setupRecyclerView(view:View){
        val recyclerView = view.findViewById<RecyclerView>(R.id.ingredients_recyclerView)
        recyclerView.adapter = mAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())


    }
}