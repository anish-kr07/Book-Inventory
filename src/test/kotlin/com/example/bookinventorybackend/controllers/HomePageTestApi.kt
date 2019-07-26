package com.example.bookinventorybackend.controllers

import com.example.bookinventorybackend.models.Book
import com.example.bookinventorybackend.models.GooleBook
import com.example.bookinventorybackend.models.ImageUrl
import com.example.bookinventorybackend.repository.BookRepo
import org.junit.After
import org.junit.Before
//import org.junit.jupiter.api.AfterEach
//import org.junit.jupiter.api.BeforeEach
import org.junit.Test
import org.junit.jupiter.api.AfterEach

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.BodyInserters
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.awt.PageAttributes

@RunWith(SpringRunner::class)
@SpringBootTest
@AutoConfigureWebTestClient
@DirtiesContext
internal class HomePageTestApi {

    @Autowired lateinit var webTestClient: WebTestClient

    @Autowired lateinit var repository: BookRepo

    @Before
    fun setUp() {
        val imageUrl = ImageUrl("http://books.google.com/books/content?id=twlUtwEACAAJ&printsec=frontcover&img=1&zoom=1&source=gbs_api",
                "http://books.google.com/books/content?id=twlUtwEACAAJ&printsec=frontcover&img=1&zoom=1&source=gbs_api")
        val book1 = Book("123456", "Game Of Thrones", listOf("anish","kk"), imageUrl, "angle and demons", 101, 10)
        val book2 = Book("123457", "Game Of Life", listOf("anish","kk"), imageUrl, "angle and demons", 101, 10)


        Flux.just(book1,book2)
                .flatMap { repository.save(it) }
                .thenMany ( repository.findAll() )
                .subscribe{println(it)}
    }

    @After
    fun tearDown() {
        repository.deleteAll()
    }

    @Test
    fun editBook() {

        var book: Book = Book(null, null, null, null, null, 1000, 9)

        webTestClient.put().uri("/editBook/{id}", "123457")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromObject(book))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo("123457")
                .jsonPath("$.price").isEqualTo(1000)
                .jsonPath("$.quantity").isEqualTo(9)

    }

    @Test
    fun getBookById() {

        webTestClient.get()
                .uri("/books/123456")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Book::class.java)
                .consumeWith<WebTestClient.ListBodySpec<Book>>{list ->
                    assertEquals(1, list.responseBody!!.size)
                    assertEquals("123456",list.responseBody!!.first().id)
                }
    }

    @Test
    fun deleteBook() {
        webTestClient.delete().uri("/deleteBook/123457")
                .exchange()
                .expectStatus().isOk

        webTestClient.get()
                .uri("/getBooks")
                .exchange()
                .expectStatus().isOk
                .expectBodyList(Book::class.java)
                .consumeWith<WebTestClient.ListBodySpec<Book>>{list ->
                    assert(list.responseBody!!.size ==2)
                }
    }

    @Test
    fun getDataFromApi() {
        webTestClient.get().uri("/books/api/{search}","The Power of your Subconscious Mind")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(GooleBook::class.java)
                .consumeWith<WebTestClient.ListBodySpec<GooleBook>>{list ->
                    assertEquals(1, list.responseBody!!.size)
                    assertEquals(6,list.responseBody!!.first().items!!.size)
                    assertEquals(6,list.responseBody!!.first().items!!.size)
                }
    }

    @Test
    fun getBooksBySearchString() {
        webTestClient.get().uri("/books/search/{query}", "Game Of Life")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk
                .expectBodyList(Book::class.java)
                .consumeWith<WebTestClient.ListBodySpec<Book>>{list ->
                    assertEquals(1, list.responseBody!!.size)
                    assertEquals("123457",list.responseBody!!.first().id)
                }
    }

    @Test
    fun getAuditLog() {
        val imageUrl = ImageUrl("http://books.google.com/books/content?id=twlUtwEACAAJ&printsec=frontcover&img=1&zoom=1&source=gbs_api",
                "http://books.google.com/books/content?id=twlUtwEACAAJ&printsec=frontcover&img=1&zoom=1&source=gbs_api")
        val book = Book(null, "Life of Pie", listOf("anish","kk"), imageUrl, "angle and demons", 101, 10)

        webTestClient.post()
                .uri("/addBook")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(book), Book::class.java)
                .exchange()
                .expectStatus().isCreated

        webTestClient.get()
                .uri("/books/auditLog")
                .exchange()
                .expectStatus().isOk
                .expectBodyList(String::class.java)
                .consumeWith<WebTestClient.ListBodySpec<String>>{list ->
                    assertEquals(1, list.responseBody!!.size)
                }
    }

//    @Test
//    fun addBook() {
//
//        val imageUrl = ImageUrl("http://books.google.com/books/content?id=twlUtwEACAAJ&printsec=frontcover&img=1&zoom=1&source=gbs_api",
//                "http://books.google.com/books/content?id=twlUtwEACAAJ&printsec=frontcover&img=1&zoom=1&source=gbs_api")
//        val book = Book(null, "Life of Pie", listOf("anish","kk"), imageUrl, "angle and demons", 101, 10)
//
//        webTestClient.post()
//                .uri("/addBook")
//                .contentType(MediaType.APPLICATION_JSON)
//                .body(Mono.just(book), Book::class.java)
//                .exchange()
//                .expectStatus().isCreated
//                .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
//                .expectBody()
//                //TOdo write proper Json object and modify the addBook route
//                .jsonPath("$.disposed").isEqualTo(true)
//                .jsonPath("$.scanAvailable").isEqualTo(true)
//
//        webTestClient.get()
//                .uri("/getBooks")
//                .exchange()
//                .expectStatus().isOk
//                .expectBodyList(Book::class.java)
//                .consumeWith<WebTestClient.ListBodySpec<Book>>{list ->
//                    assert(list.responseBody!!.size ==3)
//                }
//
//    }

    @Test
    fun getAllBooks() {
        webTestClient.get()
                .uri("/getBooks")
                .exchange()
                .expectStatus().isOk
                .expectBodyList(Book::class.java)
                .consumeWith<WebTestClient.ListBodySpec<Book>>{list ->
                    assert(list.responseBody!!.size ==3)
                }
    }

}
