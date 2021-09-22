package com.sssoyalan.newsapp.ui.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.DecelerateInterpolator
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.marginBottom
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.sssoyalan.newsapp.MainViewModel
import com.sssoyalan.newsapp.MainViewModelFactory
import com.sssoyalan.newsapp.R
import com.sssoyalan.newsapp.adapters.CategoryAdapter
import com.sssoyalan.newsapp.adapters.NewsAdapter
import com.sssoyalan.newsapp.databinding.FragmentNewsBinding
import com.sssoyalan.newsapp.db.ArticleDatabase
import com.sssoyalan.newsapp.generic.MyRecyclerScroll
import com.sssoyalan.newsapp.models.Article
import com.sssoyalan.newsapp.models.CategoryInside
import com.sssoyalan.newsapp.models.Resource
import com.sssoyalan.newsapp.source.DataRepository
import com.sssoyalan.newsapp.ui.activities.MainActivity


class NewsFragment : Fragment() {

    private var _binding: FragmentNewsBinding? = null
    private val binding get() = _binding!!
    private val categoryList : ArrayList<CategoryInside> = ArrayList()

    private lateinit var viewModel: MainViewModel

    companion object {
        var instance = false
        var articles: List<Article> = listOf()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewsBinding.inflate(inflater, container, false)
        val view = binding.root
        binding.paginationProgressBarMain.progressDrawable = ContextCompat.getDrawable(
            requireContext(),
            R.drawable.custom_progressbar
        )

        val dataRepository = DataRepository(ArticleDatabase(requireContext()))
        val mainViewModelFactory =
            MainViewModelFactory(
                dataRepository
            )
        viewModel = ViewModelProvider(this, mainViewModelFactory).get(MainViewModel::class.java)
        initCategories()
        binding.recyc2.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
        }
        if (!instance){ goResfresh() } else {binding.recyc2.adapter = NewsAdapter(articles, this)}

        binding.swipeContainer.setOnRefreshListener {
            refreshAction()                    // refresh your list contents somehow
            binding.swipeContainer.isRefreshing = false   // reset the SwipeRefreshLayout (stop the loading spinner)
        }

        binding.recycCategory.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.HORIZONTAL,
            false
        )

        if (categoryList.isEmpty()){
            binding.noFoundLayout.visibility = View.VISIBLE
        }else{
            binding.noFoundLayout.visibility = View.GONE
        }

        binding.recycCategory.adapter = CategoryAdapter(categoryList, this)

        binding.imgLeftScroll.setOnClickListener(View.OnClickListener {
            if (LinearLayoutManager(
                    requireContext(),
                    LinearLayoutManager.HORIZONTAL,
                    false
                ).findFirstVisibleItemPosition() > 0
            ) {
                binding.recycCategory.smoothScrollToPosition(
                    LinearLayoutManager(
                        requireContext(),
                        LinearLayoutManager.HORIZONTAL,
                        false
                    ).findFirstVisibleItemPosition() - 1
                )
            } else {
                binding.recycCategory.smoothScrollToPosition(categoryList.size - 1)
            }
        })

        binding.imgRightScroll.setOnClickListener(View.OnClickListener {
            binding.recycCategory.smoothScrollToPosition(
                LinearLayoutManager(
                    requireContext(),
                    LinearLayoutManager.HORIZONTAL,
                    false
                ).findLastVisibleItemPosition() + 1
            )
        })


        val slideUp: Animation = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_up_appbar_)
        val slideDown: Animation = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_down_appbar)

        binding.recyc2.addOnScrollListener(object : MyRecyclerScroll() {
            override fun show() {
                (activity as MainActivity).navView.animate().translationY(0f).setInterpolator(
                    DecelerateInterpolator(
                        2f
                    )
                ).start()
                (activity as MainActivity).appBar.visibility = View.GONE
                (activity as MainActivity).appBar.startAnimation(slideDown)

            }

            override fun hide() {
                (activity as MainActivity).navView.animate()
                    .translationY((activity as MainActivity).appBar.height + 60f)
                    .setInterpolator(AccelerateInterpolator(2f)).start()
                (activity as MainActivity).appBar.visibility = View.VISIBLE
                (activity as MainActivity).appBar.startAnimation(slideUp)

            }
        })
        return view
    }

    fun savefav(view: View, isTrue: Boolean, article: Article) {
        viewModel.saveArticle(article)
        onSNACK(view, isTrue)
    }

    fun deleteFAv(view: View, isTrue: Boolean, article: Article){
        viewModel.deleteArticle(article)
        onSNACK(view, isTrue)
    }


    fun onSNACK(view: View, isTrue: Boolean){
        var message = "Favorilere eklendi"
        message = if (isTrue){ "Favorilere eklendi" } else {"Favorilerden çıkartıldı"}
        val snackbar = Snackbar.make(
            view, message,
            Snackbar.LENGTH_SHORT
        ).setAction("Action", null)
        snackbar.setActionTextColor(Color.WHITE)
        val snackbarView = snackbar.view
        snackbarView.setBackgroundColor(Color.parseColor("#242526"))
        val textView =
            snackbarView.findViewById(com.google.android.material.R.id.snackbar_text) as TextView
        textView.setTextColor(Color.WHITE)
        textView.textSize = 15f
        snackbar.show()
    }

    private fun initCategories() {
        categoryList.add(CategoryInside(1, "İş", "business"))
        categoryList.add(CategoryInside(2, "Eğlence", "entertainment"))
        categoryList.add(CategoryInside(3, "Genel", "general"))
        categoryList.add(CategoryInside(4, "Sağlık", "health"))
        categoryList.add(CategoryInside(5, "Bilim", "science"))
        categoryList.add(CategoryInside(6, "Spor", "sports"))
        categoryList.add(CategoryInside(7, "Teknoloji", "technology"))
    }

    private fun refreshAction() {
        goResfresh()
        (activity as MainActivity).goRefresh()
    }

    fun goResfresh(categoryInside: CategoryInside = CategoryInside(3, "Genel", "general")) {
        showProgressBar()
        viewModel.getNewsCategory(categoryInside.tag)
        viewModel.aliveData.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Success -> {
                    hideProgressBar()
                    if (it.data == null) {
                        binding.noFoundLayout.visibility = View.VISIBLE
                    } else {
                        binding.noFoundLayout.visibility = View.GONE
                        binding.recyc2.adapter = NewsAdapter(it.data.articles, this)
                        articles = it.data.articles
                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
                    it.message?.let {
                        Toast.makeText(
                            context,
                            "Bedava sürüm olduğundan dolayı haber limiti doldu , yarın düzelir :D",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                is Resource.Loading ->
                    showProgressBar()
            }
        })
        instance =true
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