package com.example.shopnow

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.shopnow.databinding.FragmentProductDetailsBinding
import com.example.shopnow.datamodal.Category
import com.example.shopnow.datamodal.Product
import com.example.shopnow.datamodal.ProductList


class ProductDetailsFragment : Fragment(),MainAdapter.OnItemClickListerner {

    lateinit var binding : FragmentProductDetailsBinding

    lateinit var viewModel: MainViewModel
    lateinit var adapter: MainAdapter
    lateinit var productAdapter : ProductAdapter

    var categoriesList : ArrayList<Category> = ArrayList()
    var productList : ArrayList<Product> = ArrayList()

    var skip = 0
    var selectedPosition = 0
    lateinit var product : Product
    lateinit var productDetails : ProductList
    var categoryname = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProductDetailsBinding.inflate(inflater,container,false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val apiSerive = ApiSerive.getInstance()
        val mainRepository = Repository(apiSerive)
        adapter = MainAdapter(requireContext(),this)

        var layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL,false)

        binding.rvCategorylist.layoutManager = layoutManager
        binding.rvCategorylist.adapter = adapter


        viewModel = ViewModelProvider(this, MyViewModelFactory(mainRepository))[MainViewModel::class.java]


        viewModel.categoriesList.observe(this.viewLifecycleOwner) {

            skip = 0
            categoriesList.clear()
            for (item in it) {
                var categoryitem = item.capitalize().replace("-"," ")
                val category = Category(categoryitem,item)
                categoriesList.add(category)
            }
            categoriesList[0].selected=true
            adapter.setMovieList(categoriesList)

            viewModel.getProducts(categoriesList[0].key,skip,1)
            Log.e("MainActivity","category fetched")
        }

        viewModel.productList.observe(this.viewLifecycleOwner){

            productList.clear()

            productList.addAll(it.products)
            productAdapter.setProduct(productList)
            Log.e("MainActivity","product fetched")
            Log.e("MainActivity","product items count ${productList.size}")
            productDetails = it
            product = it.products[0]

            binding.productTitleTextView.text=product.title
            binding.productDescriptionTextView.text=product.description
            binding.productPriceTextView.text = "Price: \$"+product.price

            Glide.with(this).load(product.thumbnail).into(binding.productImageView)

            if(productDetails.total-1==skip)
                binding.skipButton.visibility = View.GONE
            else
                binding.skipButton.visibility = View.VISIBLE

        }

        binding.skipButton.setOnClickListener {
            skip++
            if(skip<productDetails.total)
                viewModel.getProducts(categoriesList[selectedPosition].key,skip,1)
        }

        binding.addToCartButton.setOnClickListener {
            showSuccessDialog(requireContext(),"Item added in the cart")
        }
        viewModel.errorMessage.observe(this.viewLifecycleOwner) {
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
        }
        viewModel.loading.observe(this.viewLifecycleOwner) {
            if (it) {
                binding.progressDialog.visibility = View.VISIBLE
            } else {
                binding.progressDialog.visibility = View.GONE
            }
        }
        viewModel.getAllCategories()
    }

    fun showSuccessDialog(context: Context, message: String, onDismiss: () -> Unit = {}) {
        val view = LayoutInflater.from(context).inflate(R.layout.alert_dialog, null)

        val dialogTitle = view.findViewById<TextView>(R.id.dialogTitle)
        dialogTitle.text = "Success"

        val dialogMessage = view.findViewById<TextView>(R.id.dialogMessage)
        dialogMessage.text = message
        val alertDialog = AlertDialog.Builder(context)
            .setView(view)
            .setCancelable(false)
            .create()
        val okButton = view.findViewById<TextView>(R.id.okButton)
        okButton.setOnClickListener {
            onDismiss.invoke()
            alertDialog.dismiss()
        }

        alertDialog.show()
    }

    override fun onProductClicked(position: Int) {

    }
}
