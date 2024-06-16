package com.harshittest

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.harshittest.databinding.ActivityNewsDetailsBinding

import com.harshittest.room.EntityArticle
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.Locale

class NewsDetailsAct : AppCompatActivity() {
    lateinit var binding : ActivityNewsDetailsBinding
    private lateinit var article: EntityArticle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= DataBindingUtil.setContentView(this,R.layout.activity_news_details)

        // Retrieve article data from intent
        article = intent.getParcelableExtra("article_data") ?: return
        // Bind article data to views
        binding.txtTitle.text = article.title
        binding.txtDescription.text = article.description
        binding.txtContent.text = article.content
        binding.txtAuthor.text = article.author
      // Format and display the date
        val formattedDate = parseDate(article.publishedAt ?: "")
        binding.txtDate.text = formattedDate
        // Load image using Picasso or Glide
        Picasso.get().load(article.urlToImage).placeholder(R.drawable.demo).into(binding.imgNews)

        click()
    }


   private fun click() {
        binding.imgBack.setOnClickListener {
           finish()
        }
    }

    // Helper method to parse and format the date
    private fun parseDate(dateString: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = inputFormat.parse(dateString)
        return date?.let { outputFormat.format(it) } ?: ""
    }
}