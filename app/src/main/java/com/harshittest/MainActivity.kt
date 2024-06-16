package com.harshittest

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.harshittest.adapter.MainNewsAdapter
import com.harshittest.databinding.ActivityMainBinding
import com.harshittest.room.EntityArticle
import com.harshittest.utility.AppUtility
import com.harshittest.viewmodel.NewsViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var myMandateAdapter: MainNewsAdapter
    private lateinit var viewModel: NewsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        viewModel = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(application)).get(NewsViewModel::class.java)

        // Set the context for the ViewModel
        viewModel.setRepository(this@MainActivity)

        setupRecyclerView()
        //for Api call with list refresh
        setupUI()
        //used lifeData
        observeViewModel()

        // Trigger initial data load
        viewModel.getNewsList()
    }

    private fun setupUI() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.getNewsList()
        }
        // For search news
        binding.edSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                myMandateAdapter.filter.filter(s)
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun observeViewModel() {
        viewModel.articles.observe(this, Observer { articles ->
            Log.d("MainAct", "Received ${articles.size} articles")
            if (!articles.isNullOrEmpty()) {
                setRecord(articles.toMutableList())
                binding.txtEmpty.visibility = View.GONE
            } else {
                setRecord(mutableListOf())  // Handle the case where articles list is empty
                binding.txtEmpty.visibility = View.VISIBLE
            }
            AppUtility.progressBarDissMiss()
            binding.swipeRefresh.isRefreshing = false
        })

        viewModel.errorState.observe(this, Observer { isError ->
            if (isError) {
                Toast.makeText(this, "Something went wrong, please try again.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupRecyclerView() {
        myMandateAdapter = MainNewsAdapter(this, onItemClick = ::onItemClick)
        binding.rvNewsList.layoutManager = LinearLayoutManager(this)
        binding.rvNewsList.adapter = myMandateAdapter
    }

    private fun setRecord(newsList: MutableList<EntityArticle>) {
        Log.d("MainAct", "Setting record with ${newsList.size} articles")
        myMandateAdapter.setData(newsList)
    }


    @SuppressLint("SuspiciousIndentation")
    private fun onItemClick(article: EntityArticle) {
      val intent = Intent(this@MainActivity, NewsDetailsAct::class.java)
        intent.putExtra("article_data", article)
        startActivity(intent)
    }
}