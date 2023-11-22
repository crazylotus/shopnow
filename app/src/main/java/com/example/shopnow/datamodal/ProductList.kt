package com.example.shopnow.datamodal
data class ProductList(
    val limit: Int,
    val products: List<Product>,
    val skip: Int,
    val total: Int
)