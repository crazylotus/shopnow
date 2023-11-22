package com.example.shopnow

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.shopnow.databinding.ActivityProductDetailsBinding
import com.example.shopnow.datamodal.Category
import com.example.shopnow.datamodal.Product
import com.example.shopnow.datamodal.ProductList

class ProductDetailsActivity : AppCompatActivity(){

    private lateinit var binding : ActivityProductDetailsBinding

    lateinit var viewModel: MainViewModel

    var categoriesList : ArrayList<Category> = ArrayList()
    var productList : ArrayList<Product> = ArrayList()

    var skip = 0
    var selectedPosition = 0
    var total = 0
    lateinit var product : Product
    lateinit var productDetails : ProductList

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val apiSerive = ApiSerive.getInstance()
        val mainRepository = Repository(apiSerive)


        product = intent.getSerializableExtra("data") as Product

        skip = intent.getIntExtra("position",0)
        total = intent.getIntExtra("total",0)
        selectedPosition = intent.getIntExtra("category_position",0)

        binding.productBrandTextView.text = product.brand
        binding.productTitleTextView.text = product.title
        binding.productDescriptionTextView.text = product.description
        binding.productPriceTextView.text = "Price: \$"+product.price
        Glide.with(this).load(product.thumbnail).into(binding.productImageView)


        viewModel = ViewModelProvider(this, MyViewModelFactory(mainRepository))[MainViewModel::class.java]


        viewModel.categoriesList.observe(this) {

            categoriesList.clear()
            for (item in it) {
                var categoryitem = item.capitalize().replace("-"," ")
                val category = Category(categoryitem,item)
                categoriesList.add(category)
            }
            categoriesList[0].selected=true

            binding.title.text = categoriesList[selectedPosition].title
            Log.e("MainActivity","category fetched")
        }

        viewModel.productList.observe(this){

            productList.clear()

            productList.addAll(it.products)

            Log.e("MainActivity","product fetched")
            Log.e("MainActivity","product items count ${productList.size}")
            productDetails = it
            product = it.products[0]

            total = it.total
            binding.productBrandTextView.text = product.brand
            binding.productTitleTextView.text=product.title
            binding.productDescriptionTextView.text=product.description
            binding.productPriceTextView.text = "Price: \$"+product.price

            Glide.with(this).load(product.thumbnail).into(binding.productImageView)

            if(total-1==skip)
                binding.skipButton.visibility = View.GONE
            else
                binding.skipButton.visibility = View.VISIBLE

        }

        binding.skipButton.setOnClickListener {
            skip++
            if(skip<total)
                viewModel.getProducts(categoriesList[selectedPosition].key,skip,1)
        }

        viewModel.errorMessage.observe(this) {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }
        viewModel.loading.observe(this) {
            if (it) {
                binding.progressDialog.visibility = View.VISIBLE
            } else {
                binding.progressDialog.visibility = View.GONE
            }
        }
        binding.ivBack.setOnClickListener {
            onBackPressed()
        }
        viewModel.getAllCategories()
    }

}