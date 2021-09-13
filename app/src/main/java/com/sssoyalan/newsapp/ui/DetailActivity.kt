package com.sssoyalan.newsapp.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.sssoyalan.newsapp.R
import com.sssoyalan.newsapp.adapters.BorsaAdapterDetail
import com.sssoyalan.newsapp.adapters.NewsAdapter
import com.sssoyalan.newsapp.models.Article
import com.sssoyalan.newsapp.models.Borsalar
import com.sssoyalan.newsapp.models.modelInside
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.item_recyclerview.view.*
import java.util.*

class DetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        showProgressBar()
        if (intent.extras?.getSerializable("object") is Article ){
            goDetail()
        }else if(intent.extras?.getSerializable("object") is Borsalar){
            goBorsa()
            hideProgressBar()
        }

    }

    private fun goBorsa() {
        webView.visibility=View.GONE
        cl_borsa.visibility=View.VISIBLE
        val borsalar : Borsalar  = intent.extras?.getSerializable("object") as Borsalar
        if (borsalar != null){
            recyc_detail.layoutManager = LinearLayoutManager(
                this,
                LinearLayoutManager.VERTICAL,
                false
            )
            recyc_detail.adapter = BorsaAdapterDetail(borsalar.borsalar)
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun goDetail() {
        webView.visibility=View.VISIBLE
        cl_borsa.visibility=View.GONE
        val article : Article  = intent.extras?.getSerializable("object") as Article
        if (article != null){
            webView.loadUrl(article.url)
            webView.settings.javaScriptEnabled = true
            webView.settings.setSupportZoom(true)
            webView.webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    hideProgressBar()
                }
            }
        }
    }

    override fun onBackPressed() {
        if (webView.canGoBack())
            webView.goBack()
        else
            super.onBackPressed()
    }

    fun hideProgressBar() {
        paginationProgressBarMain.visibility = View.INVISIBLE
    }
    fun showProgressBar() {
        paginationProgressBarMain.visibility = View.VISIBLE
    }
}