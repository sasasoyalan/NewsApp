package com.sssoyalan.newsapp.ui.activities

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.sssoyalan.newsapp.BuildConfig
import com.sssoyalan.newsapp.MainViewModel
import com.sssoyalan.newsapp.MainViewModelFactory
import com.sssoyalan.newsapp.R
import com.sssoyalan.newsapp.databinding.ActivityLoginBinding
import com.sssoyalan.newsapp.db.ArticleDatabase
import com.sssoyalan.newsapp.generic.VersionChecker
import com.sssoyalan.newsapp.models.Article
import com.sssoyalan.newsapp.models.Source
import com.sssoyalan.newsapp.models.users.Okunan
import com.sssoyalan.newsapp.models.users.UserModel
import com.sssoyalan.newsapp.models.users.lastReadArticle
import com.sssoyalan.newsapp.source.DataRepository
import java.util.concurrent.ExecutionException


class LoginActivity : AppCompatActivity() {

    lateinit var mGoogleSignInClient : GoogleSignInClient
    lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    lateinit var viewModel: MainViewModel

    companion object {
        private const val TAG = "GoogleActivity"
        private const val RC_SIGN_IN =123
    }

    override fun onStart() {
        super.onStart()

        var mLatestVersionName : String =""
        val versionChecker = VersionChecker()
        try {
            mLatestVersionName = versionChecker.execute().get()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        } catch (e: ExecutionException) {
            e.printStackTrace()
        }
        if (BuildConfig.VERSION_NAME.toDouble() < mLatestVersionName.toDouble()) {
            showDialog()
        } else {
            val user  = auth.currentUser
            updateUI(user)
        }
    }

    private fun showDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_version_checker)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)
        val yesBtn = dialog.findViewById(R.id.btn_update) as Button
        yesBtn.setOnClickListener {
            dialog.dismiss()
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("market://details?id=" + BuildConfig.APPLICATION_ID)
            startActivity(intent)
            finish()
        }
        dialog.show()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth
        val dataRepository = DataRepository(ArticleDatabase(this))
        val mainViewModelFactory =
            MainViewModelFactory(
                dataRepository
            )
        viewModel = ViewModelProvider(this, mainViewModelFactory).get(MainViewModel::class.java)
        googleLoginRequest()
        binding.btnGoogleLogin.setOnClickListener {
            singIn()
        }
    }

    private fun googleLoginRequest() {
        val gso = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private fun singIn(){
        val singInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(singInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                val firestore : FirebaseFirestore = FirebaseFirestore.getInstance()
                val docIdRef: DocumentReference = firestore.collection("yourCollection").document(
                    account.id.toString()
                )
                docIdRef.get().addOnCompleteListener(OnCompleteListener {
                    if (it.isSuccessful) {
                        val document: DocumentSnapshot? = it.result
                        if (document?.exists() == true) {
                            Log.d(TAG, "Document exists!")
                        } else {
                            viewModel.saveUser(
                                UserModel(
                                    account.id.toString(),
                                    account.displayName.toString(),
                                    account.email.toString(),
                                    "1",
                                    account.photoUrl.toString(),
                                        "1",
                                        "1",
                                    Okunan(),
                                    lastReadArticle("","","","")
                                ), this
                            )
                        }
                    }
                })
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
            }
        }
    }
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    updateUI(null)
                }
            }
    }


    private fun updateUI(user: FirebaseUser?) {
        if (user!=null){
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}