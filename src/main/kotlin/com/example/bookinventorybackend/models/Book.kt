package com.example.bookinventorybackend.models

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document  data class Book(@Id val id: String?,
                           var title: String?,
                           var authors: List<String>?,
                           var imageLinks: ImageUrl?,
                           var description: String?,
                           var price: Int?,
                           var quantity: Int?)

data class ImageUrl(val smallThumbnail:String?,
                    val thumbnail:String?)

data class Item(val volumeInfo: Book?)

data class GooleBook(val items:List<Item>?)