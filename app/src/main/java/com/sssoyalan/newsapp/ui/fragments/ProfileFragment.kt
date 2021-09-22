package com.sssoyalan.newsapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.sssoyalan.newsapp.R
import com.sssoyalan.newsapp.databinding.FragmentProfileBinding
import com.sssoyalan.newsapp.generic.Constants
import com.sssoyalan.newsapp.generic.Constants.USER_ID
import com.sssoyalan.newsapp.ui.activities.MainActivity

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

        val slideUp: Animation = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_up_appbar_)
        (activity as MainActivity).appBar.visibility = View.VISIBLE
        (activity as MainActivity).appBar.startAnimation(slideUp)

        binding.username.text= Constants.USER_NAME

        val firestore : FirebaseFirestore = FirebaseFirestore.getInstance()
        val docIdRef: DocumentReference = firestore.collection("users").document(USER_ID)
        docIdRef.get().addOnSuccessListener {
            if (it.exists()){
                binding.userOkunan.text = it.get("okunan.okunanTotal").toString()
            }
        }
        Glide.with(this).load(Constants.USER_IMG_URL).into(binding.profileImage)

        binding.cvOkuyucu.setOnClickListener {
            checkOkuyucuLayout()
        }

        return view
    }

    private fun checkOkuyucuLayout() {
        val slideUp: Animation = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_up_appbar_)
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