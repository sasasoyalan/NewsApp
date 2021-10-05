package com.sssoyalan.newsapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.DecelerateInterpolator
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.sssoyalan.newsapp.adapters.NewsAdapter
import com.sssoyalan.newsapp.MainViewModel
import com.sssoyalan.newsapp.R
import com.sssoyalan.newsapp.databinding.FragmentSavedBinding
import com.sssoyalan.newsapp.helpers.MyRecyclerScroll
import com.sssoyalan.newsapp.models.news.Article
import com.sssoyalan.newsapp.ui.activities.MainActivity

class SavedFragment : Fragment() {

    private var _binding: FragmentSavedBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: MainViewModel

    companion object {
        var articles : List<Article> = listOf()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSavedBinding.inflate(inflater,container,false)
        val view = binding.root

        val slideUp: Animation = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_up_appbar_)
        (activity as MainActivity).appBar.visibility = View.VISIBLE
        (activity as MainActivity).appBar.startAnimation(slideUp)

        viewModel = (activity as MainActivity).viewModel
        showProgressBar()
        binding.swipeContainer.setOnRefreshListener {
            refreshAction()                    // refresh your list contents somehow
            binding.swipeContainer.isRefreshing = false   // reset the SwipeRefreshLayout (stop the loading spinner)
        }

        (activity as MainActivity).isNotWeather()

        if (articles.isEmpty()){
            binding.noFoundLayout.visibility=View.VISIBLE
        }else {
            binding.noFoundLayout.visibility=View.GONE
        }
        binding.recyc2.addOnScrollListener(object : MyRecyclerScroll() {
            override fun show() {
                (activity as MainActivity).navView.animate().translationY(0f).setInterpolator(
                    DecelerateInterpolator(
                        2f
                    )
                ).start()
            }

            override fun hide() {
                (activity as MainActivity).navView.animate()
                    .translationY((activity as MainActivity).navView.height + 60f)
                    .setInterpolator(AccelerateInterpolator(2f)).start()
            }
        })

        val itemtouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT,
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position =viewHolder.adapterPosition
                val article = articles[position]
                viewModel.deleteArticle(article)
                Snackbar.make(view,"Favorilerden kaldırıldı",Snackbar.LENGTH_LONG).apply {
                    setAction("Geri Al") {
                        viewModel.saveArticle(article)
                    }
                    show()
                }
            }

        }

        ItemTouchHelper(itemtouchHelperCallback).apply {
            attachToRecyclerView(binding.recyc2)
        }

        binding.recyc2.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
        }
        viewModel.getSavedNews().observe(viewLifecycleOwner, Observer {
            if (it.isEmpty()){
                binding.noFoundLayout.visibility=View.VISIBLE
            }else{
                binding.noFoundLayout.visibility=View.GONE
            }
            articles =it
            binding.recyc2.adapter=NewsAdapter(it)
            hideProgressBar()
        })
        return view
    }

    private fun refreshAction() {
        (activity as MainActivity).goRefresh(true)
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