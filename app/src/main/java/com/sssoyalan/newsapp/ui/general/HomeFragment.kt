package com.sssoyalan.newsapp.ui.general

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getDrawable
import androidx.core.content.res.ResourcesCompat
import androidx.core.content.res.ResourcesCompat.getDrawable
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.resources.MaterialResources.getDrawable
import com.sssoyalan.newsapp.MainViewModel
import com.sssoyalan.newsapp.R
import com.sssoyalan.newsapp.adapters.NewsAdapter
import com.sssoyalan.newsapp.databinding.FragmentHomeBinding
import com.sssoyalan.newsapp.ui.MainActivity


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root
        binding.paginationProgressBarMain.progressDrawable = ContextCompat.getDrawable(requireContext(),R.drawable.custom_progressbar)

        showProgressBar()
        goResfresh()
        binding.swipeContainer.setOnRefreshListener {
            refreshAction()                    // refresh your list contents somehow
            binding.swipeContainer.isRefreshing = false   // reset the SwipeRefreshLayout (stop the loading spinner)
        }

        binding.recyc2.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) {
                    (activity as MainActivity).navView.visibility = View.GONE
                } else {
                    (activity as MainActivity).navView.visibility = View.VISIBLE
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
                    // Do something
                } else if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    // Do something
                } else {
                    // Do something
                }
            }
        })
        return view
    }

    private fun refreshAction() {
        goResfresh()
        (activity as MainActivity).goRefresh()
    }

    private fun goResfresh() {
        binding.recyc2.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL,
            false
        )
        viewModel = ViewModelProviders.of(this)[MainViewModel::class.java]
        viewModel.aliveData.observe(viewLifecycleOwner) {
            if (it.status == "ok")
            {
                binding.recyc2.adapter = NewsAdapter(it.articles, requireContext())
            }else {
                Toast.makeText(
                    context,
                    "Bedava sürüm olduğundan dolayı haber limiti doldu , yarın düzelir :D",
                    Toast.LENGTH_SHORT
                ).show()
            }
            hideProgressBar()
        }
        viewModel.fetchGeneral()
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