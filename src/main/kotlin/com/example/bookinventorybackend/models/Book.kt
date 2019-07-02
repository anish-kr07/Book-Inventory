package com.example.bookinventorybackend.models

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document  data class Book(@Id val id: String?,
                           val title: String,
                           val authors: List<String>,
                           val image: String,
                           var description: String,
                           var price: Int,
                           var quantity: Int){

}