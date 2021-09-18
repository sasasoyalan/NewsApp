package com.sssoyalan.newsapp.ui.fragments

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.sssoyalan.newsapp.MainViewModel
import com.sssoyalan.newsapp.MainViewModelFactory
import com.sssoyalan.newsapp.R
import com.sssoyalan.newsapp.databinding.FragmentProfileBinding
import com.sssoyalan.newsapp.db.ArticleDatabase
import com.sssoyalan.newsapp.generic.Constants
import com.sssoyalan.newsapp.models.users.UserModel
import com.sssoyalan.newsapp.source.DataRepository
import com.sssoyalan.newsapp.ui.activities.MainActivity
import kotlinx.android.synthetic.main.fragment_profile.*

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater,container,false)
        val view = binding.root

        val slideUp: Animation = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_up_appbar)
        (activity as MainActivity).appBar.visibility = View.VISIBLE
        (activity as MainActivity).appBar.startAnimation(slideUp)

        binding.username.text= Constants.USER_NAME
        binding.userOkunan.text = Constants.USER_TOTAL_OKUNAN.toString()
        Glide.with(this).load(Constants.USER_IMG_URL).into(binding.profileImage)

        binding.cvOkuyucu.setOnClickListener {
            checkOkuyucuLayout()
        }

        return view
    }

    private fun checkOkuyucuLayout() {
        val slideUp: Animation = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_up_appbar)
        val slideDown: Animation = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_down_appbar)
        if (binding.clOkuyucu.visibility==View.GONE){
            binding.imgOkuyucu.setBackgroundResource(R.drawable.ic_baseline_keyboard_arrow_down_24)
            binding.clOkuyucu.visibility=View.VISIBLE
            binding.clOkuyucu.startAnimation(slideUp)
        } else {
            binding.imgOkuyucu.setBackgroundResource(R.drawable.ic_baseline_chevron_right_24)
            binding.clOkuyucu.visibility=View.GONE
            binding.clOkuyucu.startAnimation(slideDown)
        }
    }
}