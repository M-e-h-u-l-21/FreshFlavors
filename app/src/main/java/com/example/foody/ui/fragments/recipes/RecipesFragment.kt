package com.example.foody.ui.fragments.recipes

import android.os.Bundle
import android.view.FrameMetrics
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.foody.R
import com.example.foody.databinding.FragmentRecipesBinding
import com.todkars.shimmer.ShimmerRecyclerView

class RecipesFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_recipes, container, false)
        view.findViewById<ShimmerRecyclerView>(R.id.recyclerView).showShimmer()
        return view
    }

}