package com.sssoyalan.newsapp.ui.activities

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.sssoyalan.newsapp.MainViewModel
import com.sssoyalan.newsapp.MainViewModelFactory
import com.sssoyalan.newsapp.R
import com.sssoyalan.newsapp.adapters.BorsaAdapterDetail
import com.sssoyalan.newsapp.adapters.UsersAdapter
import com.sssoyalan.newsapp.db.ArticleDatabase
import com.sssoyalan.newsapp.generic.Constants
import com.sssoyalan.newsapp.generic.Constants.USER_ID
import com.sssoyalan.newsapp.models.Article
import com.sssoyalan.newsapp.models.Borsalar
import com.sssoyalan.newsapp.models.users.Okunan
import com.sssoyalan.newsapp.models.users.UserModel
import com.sssoyalan.newsapp.source.DataRepository
import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity : AppCompatActivity() {

    private var firestore : FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        showProgressBar()


        firestore.firestoreSettings = FirebaseFirestoreSettings.Builder().build()
        if (intent.extras?.getSerializable("object") is Article ){
            firestore.collection("users").document(USER_ID)
                .get()
                .addOnSuccessListener { result ->
                    Log.d("TAGsss", "${result.id} => ${result.data}")
                    val userModel : UserModel? = result.toObject(UserModel::class.java)
                    userModel?.okunan?.okunanTotal?.let {
                        Constants.USER_TOTAL_OKUNAN = it +1
                        firestore.collection("users").document(USER_ID)
                            .update("okunan.okunanTotal", it+1)
                            .addOnSuccessListener { Log.d("TAG", "DocumentSnapshot successfully updated!") }
                            .addOnFailureListener { e -> Log.w("TAG", "Error updating document", e) }
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w("TAGsss", "Error getting documents.", exception)
                }
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
            article.url?.let { webView.loadUrl(it) }
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