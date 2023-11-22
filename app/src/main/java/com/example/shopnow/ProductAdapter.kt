package com.example.shopnow

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.shopnow.databinding.LinearProductViewBinding
import com.example.shopnow.databinding.ProductLayoutBinding
import com.example.shopnow.datamodal.Product

class ProductAdapter (context : Context,listener: OnItemClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>()  {

    lateinit var context : Context

    lateinit var listener: OnItemClickListener

    init {
        this.context = context
        this.listener = listener
    }

    private var productList = ArrayList<Product>()
    fun setProduct(productList : List<Product>){
        this.productList = productList as ArrayList<Product>
        notifyDataSetChanged()
    }

    interface OnItemClickListener{
        fun onClick(position: Int)
    }

   inner class ViewHolder(val binding : ProductLayoutBinding) : RecyclerView.ViewHolder(binding.root)  {
        fun bind(item : Product){
          binding.textViewProductName.text = item.title
            binding.textViewProductBrand.text = item.brand
            binding.textViewProductPrice.text = item.price.toString()+"\$"
            binding.rBar.rating = item.rating!!.toFloat()

            binding.imageViewProduct.setOnClickListener {
                listener.onClick(position)
            }
            Glide.with(context).load(item.thumbnail).into(binding.imageViewProduct)
        }
    }

    inner class LinearViewHolder(val binding : LinearProductViewBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(item : Product){
            binding.textViewProductName.text = item.title
            binding.textViewProductBrand.text = item.brand
            binding.textViewProductPrice.text = item.price.toString()+"\$"
            binding.rBar.rating = item.rating!!.toFloat()
            Glide.with(context).load(item.thumbnail).into(binding.imageViewProduct)
        binding.imageViewProduct.setOnClickListener {
            listener.onClick(position)
        }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        if(viewType == 1)
        return ViewHolder(
            ProductLayoutBinding.inflate(
                LayoutInflater.from(
                    parent.context
                )
            )
        )
        else
            return LinearViewHolder(
                LinearProductViewBinding.inflate(
                    LayoutInflater.from(
                        parent.context
                    )
                )
            )
    }

    override fun getItemViewType(position: Int): Int {
        if(productList[position].linearType)
            return 0
        else
            return 1

    }

    override fun getItemCount(): Int {
        return productList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(productList[position].linearType)
            (holder as LinearViewHolder).bind(productList[position])
        else
            (holder as ViewHolder).bind(productList[position])
    }


}