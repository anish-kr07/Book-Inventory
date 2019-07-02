package com.example.bookinventorybackend.repository

import com.example.bookinventorybackend.models.Book
import org.springframework.data.mongodb.repository.ReactiveMongoRepository

interface BookRepo : ReactiveMongoRepository<Book,String> {
}