package com.example.shopnow

import com.example.shopnow.datamodal.ProductList
import retrofit2.Response

class Repository constructor(private val apiSerive: ApiSerive) {

    suspend fun getCategoriesList() = apiSerive.getCategories()

    suspend fun getProducts( product : String) : Response<ProductList>  {
        return apiSerive.getProductList(product)
    }
    suspend fun getProducts( product : String,skip:Int,limit:Int) : Response<ProductList>  {
        return apiSerive.getProductList(product,skip,limit)
    }

}