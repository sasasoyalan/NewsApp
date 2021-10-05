package com.sssoyalan.newsapp.ui.activities

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.sssoyalan.newsapp.R
import com.sssoyalan.newsapp.adapters.BorsaDetailAdapter
import com.sssoyalan.newsapp.helpers.Constants
import com.sssoyalan.newsapp.helpers.Constants.USER_ID
import com.sssoyalan.newsapp.models.news.Article
import com.sssoyalan.newsapp.models.borsa.Borsalar
import com.sssoyalan.newsapp.models.users.UserModel
import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity : AppCompatActivity() {

    private var firestore : FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        showProgressBar()

        firestore.firestoreSettings = FirebaseFirestoreSettings.Builder().build()
        if (intent.extras?.getSerializable("object") is Article){
            firestore.collection("users").document(USER_ID)
                .get()
                .addOnSuccessListener { result ->
                    Log.d("TAGsss", "${result.id} => ${result.data}")
                    val userModel : UserModel? = result.toObject(UserModel::class.java)
                    userModel?.okunan?.okunanTotal?.let {
                        Constants.USER_TOTAL_OKUNAN = it +1
                        firestore.collection("users").document(USER_ID)
                            .update("okunan.okunanTotal", it+1)
                    }
                }
            goDetail()
        }else if(intent.extras?.getSerializable("object") is Borsalar){
            goBorsa()
        }
    }

    private fun goBorsa() {
        webView.visibility=View.GONE
        cl_borsa.visibility=View.VISIBLE
        val borsalar : Borsalar = intent.extras?.getSerializable("object") as Borsalar
        if (borsalar != null){
            recyc_detail.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false
            )
            recyc_detail.adapter = BorsaDetailAdapter(borsalar.borsalar)
        }
        hideProgressBar()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun goDetail() {
        webView.visibility=View.VISIBLE
        cl_borsa.visibility=View.GONE
        val article : Article = intent.extras?.getSerializable("object") as Article

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

    fun hideProgressBar() {
        paginationProgressBarMain.visibility = View.INVISIBLE
    }
    fun showProgressBar() {
        paginationProgressBarMain.visibility = View.VISIBLE
    }

    private var doubleBackToExitPressedOnce = false
    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }
        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, "Uygulamaya dönmek için bir kez daha dokunun", Toast.LENGTH_SHORT).show()
        if (webView.canGoBack()){
            webView.goBack()
        }
        Handler(Looper.getMainLooper()).postDelayed(Runnable { doubleBackToExitPressedOnce = false }, 1500)
    }

    override fun onKeyLongPress(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {

            return true
        }
        return super.onKeyLongPress(keyCode, event)
    }

    override fun onResume() {
        super.onResume()
        MainActivity.isActive =true
        lastSeenUpdate("1")
    }

    override fun onRestart() {
        super.onRestart()
        MainActivity.isActive =true
        lastSeenUpdate("1")
    }

    override fun onStart() {
        super.onStart()
        MainActivity.isActive =true
        lastSeenUpdate("1")
    }

    override fun onPause() {
        super.onPause()
        val time : Long = Timestamp.now().toDate().time
        lastSeenUpdate(time.toString())
    }

    private fun lastSeenUpdate(s: String){
        firestore.collection("users").document(USER_ID)
            .update("online", s)
    }
}