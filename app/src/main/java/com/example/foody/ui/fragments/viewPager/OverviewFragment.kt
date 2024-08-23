package com.example.foody.ui.fragments.viewPager

import android.media.Image
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import coil.load
import com.example.foody.R
import com.example.foody.models.Result
import com.example.foody.util.Constants.Companion.RECIPE_RESULT_KEY
import org.jsoup.Jsoup

class OverviewFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_overview, container, false)

        val args = arguments
        val myBundle:Result? = args?.getParcelable(RECIPE_RESULT_KEY)

        view.findViewById<ImageView>(R.id.main_imageView).load(myBundle?.image)
        view.findViewById<TextView>(R.id.title_textView).text = myBundle?.title
        view.findViewById<TextView>(R.id.likes_textView).text = myBundle?.aggregateLikes.toString()
        view.findViewById<TextView>(R.id.time_textView).text = myBundle?.readyInMinutes.toString()
        myBundle?.summary.let{
            val summary = Jsoup.parse(it).text()
            view.findViewById<TextView>(R.id.summary_textView).text = summary
        }
        if(myBundle?.vegetarian == true){
            view.findViewById<ImageView>(R.id.vegetarian_imageView).setColorFilter(ContextCompat.getColor(requireContext(),R.color.green))
            view.findViewById<TextView>(R.id.vegetarian_textView).setTextColor(ContextCompat.getColor(requireContext(),R.color.green))
        }

        if(myBundle?.vegan == true){
            view.findViewById<ImageView>(R.id.vegan_imageView).setColorFilter(ContextCompat.getColor(requireContext(),R.color.green))
            view.findViewById<TextView>(R.id.vegan_textView).setTextColor(ContextCompat.getColor(requireContext(),R.color.green))
        }

        if(myBundle?.dairyFree == true){
            view.findViewById<ImageView>(R.id.dairy_free_imageView).setColorFilter(ContextCompat.getColor(requireContext(),R.color.green))
            view.findViewById<TextView>(R.id.dairy_free_textView).setTextColor(ContextCompat.getColor(requireContext(),R.color.green))
        }

        if(myBundle?.glutenFree == true){
            view.findViewById<ImageView>(R.id.gluten_free_imageView).setColorFilter(ContextCompat.getColor(requireContext(),R.color.green))
            view.findViewById<TextView>(R.id.gluten_free_textView).setTextColor(ContextCompat.getColor(requireContext(),R.color.green))
        }

        if(myBundle?.cheap == true){
            view.findViewById<ImageView>(R.id.cheap_imageView).setColorFilter(ContextCompat.getColor(requireContext(),R.color.green))
            view.findViewById<TextView>(R.id.cheap_textView).setTextColor(ContextCompat.getColor(requireContext(),R.color.green))
        }

        if(myBundle?.veryHealthy == true){
            view.findViewById<ImageView>(R.id.healthy_imageView).setColorFilter(ContextCompat.getColor(requireContext(),R.color.green))
            view.findViewById<TextView>(R.id.healthy_textView).setTextColor(ContextCompat.getColor(requireContext(),R.color.green))
        }


        return view
    }
}