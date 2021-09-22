package com.sssoyalan.newsapp.ui.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.sssoyalan.newsapp.MainViewModel
import com.sssoyalan.newsapp.MainViewModelFactory
import com.sssoyalan.newsapp.R
import com.sssoyalan.newsapp.adapters.MessageAdapter
import com.sssoyalan.newsapp.adapters.UsersAdapter
import com.sssoyalan.newsapp.databinding.FragmentUsersBinding
import com.sssoyalan.newsapp.db.ArticleDatabase
import com.sssoyalan.newsapp.generic.Constants
import com.sssoyalan.newsapp.models.MessageModel
import com.sssoyalan.newsapp.source.DataRepository
import com.sssoyalan.newsapp.ui.activities.MainActivity


class UsersFragment : Fragment() {

    private var _binding: FragmentUsersBinding? = null
    private val binding get() = _binding!!
    private lateinit var slideOpen: Animation
    private lateinit var slideClose: Animation
    private lateinit var dialog: Dialog

    private lateinit var viewModel: MainViewModel

    companion object {
        const val isChatOpen=false
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUsersBinding.inflate(inflater, container, false)
        val view = binding.root

        val dataRepository = DataRepository(ArticleDatabase(requireContext()))
        val mainViewModelFactory =
            MainViewModelFactory(
                dataRepository
            )
        viewModel = ViewModelProvider(this, mainViewModelFactory).get(MainViewModel::class.java)
        initChatDialog()

        slideOpen = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_open_appbar_)
        slideClose = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_close_appbar_)
        val slideUp: Animation = AnimationUtils.loadAnimation(
            requireContext(),
            R.anim.slide_up_appbar_
        )
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

        binding.btnChat.setOnClickListener {
            if (binding.chatLayout.visibility==View.VISIBLE){
                hideChatdialog()
            }else{
                showChatdialog()
            }

        }
        return view
    }

    private fun hideChat() {
        binding.chatLayout.visibility=View.GONE
        binding.chatLayout.startAnimation(slideOpen)
        binding.imgChat.setBackgroundResource(R.drawable.chat)
    }
    private fun openChat() {
        binding.chatLayout.visibility=View.VISIBLE
        binding.chatLayout.startAnimation(slideClose)
        binding.imgChat.setBackgroundResource(R.drawable.back_chat)
    }

    private fun refreshAction() {
        goResfresh()
        (activity as MainActivity).goRefresh()
    }

    private fun goResfresh() {
        viewModel._users.observe(viewLifecycleOwner, Observer {
            binding.recyc2.adapter = UsersAdapter(it)
            hideProgressBar()
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initChatDialog(){
        dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_chat)
        val width : Int = (resources.displayMetrics.widthPixels*0.90).toInt()
        val height : Int = (resources.displayMetrics.heightPixels*0.70).toInt()
        dialog.window?.setLayout(width, height)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setWindowAnimations(R.style.DialogSlideAnim)
        dialog.setCancelable(true)
        val edt_message = dialog.findViewById(R.id.input_message) as EditText
        val btn_send = dialog.findViewById(R.id.send_message) as ImageView
        val hide_keyboard_layout = dialog.findViewById(R.id.hide_keyboard_layout) as ConstraintLayout
        val recyc_chat = dialog.findViewById(R.id.recyc_chat) as RecyclerView

        val  linearLayoutManager : LinearLayoutManager= LinearLayoutManager(requireContext())
        linearLayoutManager.stackFromEnd=true
        recyc_chat.apply {
            layoutManager = linearLayoutManager
        }
        viewModel._messages.observe(viewLifecycleOwner, Observer {
            recyc_chat.adapter = MessageAdapter(it)
        })

        hide_keyboard_layout.setOnTouchListener { v, event ->
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    hideKeyboard(edt_message)
                }
                }

            v?.onTouchEvent(event) ?: true
        }

        recyc_chat.setOnTouchListener { v, event ->
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    hideKeyboard(edt_message)
                }
            }

            v?.onTouchEvent(event) ?: true
        }

        btn_send.setOnClickListener {
            if (edt_message.text.toString().isNotEmpty()){
                val time : Long = Timestamp.now().toDate().time
                viewModel.saveMessage(MessageModel(Constants.USER_ID,edt_message.text.toString(),time.toString(),Constants.USER_NAME))
                edt_message.setText("")
            }
        }
    }

    private fun hideKeyboard(view: View){
        val imm : InputMethodManager = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken,0)
    }

    private fun showChatdialog() {
        dialog.show()
    }

    private fun hideChatdialog() {
        dialog.dismiss()
    }


    fun hideProgressBar() {
        binding.paginationProgressBarMain.visibility = View.INVISIBLE
    }
    fun showProgressBar() {
        binding.paginationProgressBarMain.visibility = View.VISIBLE
    }
}