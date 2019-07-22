package com.example.bookinventorybackend.controllers

import com.example.bookinventorybackend.models.Book
import com.example.bookinventorybackend.models.GooleBook
import com.example.bookinventorybackend.repository.BookRepo
import com.example.bookinventorybackend.services.AuditService
import com.example.bookinventorybackend.services.GoogleBookApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@CrossOrigin
class HomePage{

    @Autowired
    private lateinit var bookrepo: BookRepo

    @Autowired
    private  lateinit var webClientApi : GoogleBookApi

    @Autowired
    private lateinit var auditService : AuditService


    @GetMapping("/getBooks")
    fun  getAllBooks():Flux<Book>{
       return  this.bookrepo.findAll()

    }


//    @PostMapping("/addBook")
//    @ResponseStatus(HttpStatus.CREATED)
//    fun  addBook(@RequestBody book:Book):Mono<Book>{
//        var result = this.bookrepo.save(book)
//        result.subscribe(auditService::sendAddMessage)
//        return  result
//    }

    @PostMapping("/addBook")
    @ResponseStatus(HttpStatus.CREATED)
    fun addBook(@RequestBody book: Book)= bookrepo.save(book).subscribe(auditService::sendAddMessage)


    @DeleteMapping(value = "deleteBook/{id}")
    fun deleteBook(@PathVariable("id") id: String):Mono<Void>{

        //needs to be changed
        val result =  bookrepo.findById(id)
                .subscribe(auditService::sendDeleteMessage)
        return bookrepo.deleteById(id)
    }

    @PutMapping(value = "editBook/{id}")
    fun editBook(@PathVariable("id") id: String, @RequestBody book: Book): Mono<Book>{

        var result= this.bookrepo.findById(id)
                 .map{
                     it.description = book.description
                     it.price=book.price
                     it.quantity=book.quantity
                     it
                  }.flatMap {
                     bookrepo.save(it)
                 }

        result.subscribe(auditService:: sendEditMessage)
        return  result
    }

    @GetMapping("/books/{id}")
    fun getBookById(@PathVariable id:String): Mono<ResponseEntity<Book>> {
        return bookrepo.findById(id)
                .map { book -> ResponseEntity.ok(book) }
                .defaultIfEmpty(ResponseEntity.notFound().build())

    }

    @GetMapping("/books/api/{search}")
    fun getDataFromApi(@PathVariable search :String): Flux<GooleBook> {
        return webClientApi.getBookfromApi(search)
    }

    @GetMapping("/books/search/{query}")
    fun getBooksBySearch(@PathVariable query:String):Flux<Book>{
        return bookrepo.findBookByTitleContainsIgnoreCase(query)
    }

    @GetMapping("/books/auditLog")
    fun getAuditLog() = AuditService.auditLogs
}

