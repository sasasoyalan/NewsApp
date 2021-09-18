package com.sssoyalan.newsapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.sssoyalan.newsapp.MainViewModel
import com.sssoyalan.newsapp.MainViewModelFactory
import com.sssoyalan.newsapp.R
import com.sssoyalan.newsapp.models.Resource
import com.sssoyalan.newsapp.adapters.UsersAdapter
import com.sssoyalan.newsapp.databinding.FragmentUsersBinding
import com.sssoyalan.newsapp.db.ArticleDatabase
import com.sssoyalan.newsapp.source.DataRepository
import com.sssoyalan.newsapp.ui.activities.MainActivity

class UsersFragment : Fragment() {

    private var _binding: FragmentUsersBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUsersBinding.inflate(inflater,container,false)
        val view = binding.root

        val slideUp: Animation = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_up_appbar)
        (activity as MainActivity).appBar.visibility = View.VISIBLE
        (activity as MainActivity).appBar.startAnimation(slideUp)

        binding.recyc2.apply {
            layoutManager = LinearLayoutManager(requireContext())
        }
        showProgressBar()
        goResfresh()
        binding.swipeContainer.setOnRefreshListener {
            refreshAction()                    // refresh your list contents somehow
            binding.swipeContainer.isRefreshing = false   // reset the SwipeRefreshLayout (stop the loading spinner)
        }
        return view
    }

    private fun refreshAction() {
        goResfresh()
        (activity as MainActivity).goRefresh()
    }

    private fun goResfresh() {
        val dataRepository = DataRepository(ArticleDatabase(requireContext()))
        val mainViewModelFactory =
            MainViewModelFactory(
                dataRepository
            )
        viewModel = ViewModelProvider(this,mainViewModelFactory).get(MainViewModel::class.java)
        viewModel._users.observe(viewLifecycleOwner, Observer {
            binding.recyc2.adapter = UsersAdapter(it)
            hideProgressBar()
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    fun hideProgressBar() {
        binding.paginationProgressBarMain.visibility = View.INVISIBLE
    }
    fun showProgressBar() {
        binding.paginationProgressBarMain.visibility = View.VISIBLE
    }
}