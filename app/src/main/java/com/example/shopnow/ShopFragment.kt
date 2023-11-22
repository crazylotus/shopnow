package com.example.shopnow

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.shopnow.databinding.FragmentShopBinding
import com.example.shopnow.datamodal.Category
import com.example.shopnow.datamodal.Product


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER


class ShopFragment : Fragment(),MainAdapter.OnItemClickListerner,ProductAdapter.OnItemClickListener {


    private lateinit var binding : FragmentShopBinding

    private lateinit var viewModel: MainViewModel
    private lateinit var adapter: MainAdapter
    private lateinit var productAdapter : ProductAdapter

    private var categoriesList : ArrayList<Category> = ArrayList()
    private var productList : ArrayList<Product> = ArrayList()

    private var linear = false
    private var total = 0
    private var categoryPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentShopBinding.inflate(inflater,container,false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val apiSerive = ApiSerive.getInstance()
        val mainRepository = Repository(apiSerive)
        adapter = MainAdapter(requireContext(),this)
        productAdapter = ProductAdapter(requireContext(),this)

        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL,false)
        val layoutManager2 = GridLayoutManager(requireContext(),2)
        val layoutManager3 = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL,false)


        binding.rvCategorylist.layoutManager = layoutManager
        binding.rvCategorylist.adapter = adapter

        binding.rvProductList.layoutManager = layoutManager2
        binding.rvProductList.adapter = productAdapter

        viewModel = ViewModelProvider(this, MyViewModelFactory(mainRepository))[MainViewModel::class.java]


        viewModel.categoriesList.observe(this.viewLifecycleOwner) {

            categoriesList.clear()
            for (item in it) {
                var categoryitem = item.capitalize().replace("-"," ")
                val category = Category(categoryitem,item)
                categoriesList.add(category)
            }
            categoriesList[0].selected=true
            adapter.setMovieList(categoriesList)
            binding.title.text = categoriesList[0].title
            viewModel.getProducts(categoriesList[0].key)
            Log.e("MainActivity","category fetched")
        }
        binding.layoutChange.setOnClickListener {
            linear = !linear

            if(linear)
                Glide.with(requireContext()).load(resources.getDrawable(R.drawable.baseline_grid_on_24)).into(binding.ivLayoutView)
            else
                Glide.with(requireContext()).load(resources.getDrawable(R.drawable.baseline_list_24)).into(binding.ivLayoutView)

            for(product in productList)
                product.linearType = linear

            if(linear){
                binding.rvProductList.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL,false)
            }else{
                binding.rvProductList.layoutManager = GridLayoutManager(requireContext(),2)
            }
            adapter.notifyDataSetChanged()
        }

        viewModel.productList.observe(this.viewLifecycleOwner){

            productList.clear()

            total = it.total
            productList.addAll(it.products)

            for(product in productList)
                product.linearType = linear


            productAdapter.setProduct(productList)
            Log.e("MainActivity","product fetched")
            Log.e("MainActivity","product items count ${productList.size}")



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


    override fun onProductClicked(position: Int) {
        viewModel.getProducts(categoriesList[position].key)
        binding.title.text = categoriesList[position].title
        categoryPosition = position

    }

    override fun onClick(position: Int) {

        val intent = Intent(activity, ProductDetailsActivity::class.java)
        val bundle = Bundle()
        bundle.putSerializable("data",productList[position])
        intent.putExtra("position",position)
        intent.putExtra("total", total)
        intent.putExtra("data",productList[position])
        intent.putExtra("category_position",categoryPosition)
        startActivity(intent,bundle)

    }
}