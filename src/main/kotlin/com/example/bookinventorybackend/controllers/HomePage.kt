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
//import org.apache.commons.lang3.StringUtils.defaultIfEmpty
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.DeleteMapping



@RestController
@CrossOrigin
class HomePage{


    @Autowired
    private lateinit var bookrepo: BookRepo

    @Autowired
    private  lateinit var webClientApi : GoogleBookApi

    @Autowired
    private lateinit var auditService : AuditService

    constructor(bookrepo: BookRepo, webClientApi: GoogleBookApi, auditService: AuditService) {
        this.bookrepo = bookrepo
        this.webClientApi = webClientApi
        this.auditService = auditService
    }


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

        //todo needs to be changed
        val result =  bookrepo.findById(id)
                .subscribe(auditService::sendDeleteMessage)
        return bookrepo.deleteById(id)

    }

//    @DeleteMapping("{id}")
//    fun deleteProduct(@PathVariable(value = "id") id: String): Mono<ResponseEntity<Void>> {
//        return repository.findById(id)
//                .flatMap({ existingProduct ->
//                    repository.delete(existingProduct)
//                            .then(Mono.just<T>(ResponseEntity.ok().build<Void>()))
//                }
//                )
//                .defaultIfEmpty(ResponseEntity.notFound().build<T>())

//    return bookrepo.findById(id)
//    .flatMap( { existingProduct ->
//        bookrepo.delete(existingProduct)
//                .doOnSuccess(Mono.just<T>(ResponseEntity.ok().build<Void>()))
//                .subscribe(auditService::sendDeleteMessage)
//    })
//    .defaultIfEmpty(ResponseEntity.notFound().build<T>())
//    }

    @PutMapping(value = "editBook/{id}")
    fun editBook(@PathVariable("id") id: String, @RequestBody book: Book): Mono<Book>{

        var result= this.bookrepo.findById(id)
                 .flatMap{
//                     it.description = book.description
                     it.price=book.price
                     it.quantity=book.quantity
                     bookrepo.save(it)
                  }
//                  flatMap {
//                     bookrepo.save(it)
//                 }

        result.subscribe(auditService:: sendEditMessage)
        return  result
    }

    @GetMapping("/books/{id}")
    fun getBookById(@PathVariable id:String): Mono<ResponseEntity<Book>> {
        return bookrepo.findById(id)
                .map { book -> ResponseEntity.ok(book) }
                .defaultIfEmpty(ResponseEntity.notFound().build())

    }

//    @GetMapping("/books/{id}")
//    fun getBookById(@PathVariable id:String): Mono<Book> {
//        return bookrepo.findById(id)
//    }


    @GetMapping("/books/api/{search}")
    fun getDataFromApi(@PathVariable search :String): Flux<GooleBook> {
        return webClientApi.getBookfromApi(search)
    }

    @GetMapping("/books/search/{query}")
    fun getBooksBySearchString(@PathVariable query:String):Flux<Book>{
        return bookrepo.findBookByTitleContainsIgnoreCase(query)
    }

    @GetMapping("/books/auditLog")
    fun getAuditLog() = AuditService.auditLogs
}

