package com.example.bookinventorybackend.repository

import com.example.bookinventorybackend.models.Book
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface BookRepo : ReactiveMongoRepository<Book,Long> {
    fun findById(id:String):Mono<Book>
    fun deleteById(id: String):Mono<Void>
    fun findBookByTitleContainsIgnoreCase(query: String): Flux<Book>

}