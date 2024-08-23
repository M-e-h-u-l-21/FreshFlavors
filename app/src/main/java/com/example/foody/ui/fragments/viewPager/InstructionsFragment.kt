package com.example.foody.ui.fragments.viewPager

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import com.example.foody.R
import com.example.foody.models.Result
import com.example.foody.util.Constants

class InstructionsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_instructions, container, false)


        val args = arguments
        val myBundle: Result? = args?.getParcelable(Constants.RECIPE_RESULT_KEY)

        val webView = view.findViewById<WebView>(R.id.instructions_webView)
        webView.webViewClient = object : WebViewClient(){
        }
        val websiteUrl:String = myBundle?.sourceUrl!!
        webView.loadUrl(websiteUrl)
        webView.settings.builtInZoomControls = true
        webView.settings.setSupportZoom(true)
        webView.settings.useWideViewPort = true
        webView.settings.loadWithOverviewMode=true
//        web.getSettings().setBuiltInZoomControls(true);
//        web.getSettings().setSupportZoom(true);
//        web.getSettings().setUseWideViewPort(true);
//        web.getSettings().setLoadWithOverviewMode(true);

        return view
    }
}