package com.example.bookinventorybackend.controllers

import com.example.bookinventorybackend.models.Book
import com.example.bookinventorybackend.repository.BookRepo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
class HomePage{

    @Autowired
    private lateinit var bookrepo: BookRepo

    @GetMapping("/getBooks")
    fun  getAllBooks():Flux<Book>{
       return  this.bookrepo.findAll()
    }

    @PostMapping("/addBook")
    fun  addBook(@RequestBody book:Book):Mono<Book>{
        return  this.bookrepo.save(book)
    }


    @DeleteMapping(value = "deleteBook/{id}")
    fun deleteBook(@PathVariable("id") id: String):Mono<Void>{
        return  this.bookrepo.deleteById(id)
    }

    @PutMapping(value = "editBook/{id}")
    fun editBook(@PathVariable("id") id: String, @RequestBody book: Book): Mono<Book>{

        return this.bookrepo.findById(id)
                 .map{
                     it.description = book.description
                     it.price=book.price
                     it
                  }.flatMap {
                     bookrepo.save(it)
                 }
    }

}

