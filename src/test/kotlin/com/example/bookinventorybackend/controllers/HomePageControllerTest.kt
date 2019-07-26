package com.example.bookinventorybackend.controllers

import com.example.bookinventorybackend.models.Book

import com.example.bookinventorybackend.models.GooleBook
import com.example.bookinventorybackend.models.ImageUrl
import com.example.bookinventorybackend.models.Item
import com.example.bookinventorybackend.repository.BookRepo
import com.example.bookinventorybackend.services.AuditService
import com.example.bookinventorybackend.services.GoogleBookApi
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import org.mockito.ArgumentMatchers
import org.springframework.http.ResponseEntity


@RunWith(SpringRunner::class)
@SpringBootTest
internal class HomePageControllerTest {

    val imageUrl = ImageUrl("http://books.google.com/books/content?id=twlUtwEACAAJ&printsec=frontcover&img=1&zoom=1&source=gbs_api",
            "http://books.google.com/books/content?id=twlUtwEACAAJ&printsec=frontcover&img=1&zoom=1&source=gbs_api")

    val book = Book("1a23456", "Game Of Thrones", listOf("anish","kk"), imageUrl, "angle and demons", 101, 10)
    val item = Item(book)
    val gooleBook = GooleBook(listOf(item))

    var repository = mock(BookRepo::class.java)
    var webClientApi = mock(GoogleBookApi::class.java)
    var auditService = mock(AuditService::class.java)

    @Test
    fun getAllBooks() {
        val fluxOfBooks = Flux.just(book)
        `when`(repository.findAll()).thenReturn(fluxOfBooks)
        val result = HomePage(repository,webClientApi,auditService).getAllBooks()
        assert(result==fluxOfBooks)
    }

    @Test
    fun addBook() {
//        val monoOfBook = Mono.just(book)
//        `when`(repository.save(ArgumentMatchers.any(Book::class.java))).thenReturn(monoOfBook)
//
//        val result = HomePage(repository,webClientApi,auditService).addBook(book)
//        assert(result==monoOfBook)
    }

    @Test
    fun deleteBook() {
//        val fluxOfBooks = Mono.just(book)
//        `when`(repository.save(ArgumentMatchers.any(Book::class.java))).thenReturn(fluxOfBooks)
//
//        val result = HomePage(repository,webClientApi,auditService).addBook(book)
//        assert(result==fluxOfBooks)
    }

    @Test
    fun editBook() {
//        val monoOfBook = Mono.just(book)
//        `when`(repository.findById(ArgumentMatchers.anyString())).thenReturn(monoOfBook)
//        `when`(repository.save(ArgumentMatchers.any(Book::class.java))).thenReturn(monoOfBook)
//
//        val result = HomePage(repository,webClientApi,auditService).editBook("1a23456",book)
//        assert(result==monoOfBook)

    }


//    @Test
//    fun getBookById() {
//        val monoOfBook = Mono.just(book)
//        `when`(repository.findById(ArgumentMatchers.anyString())).thenReturn(monoOfBook)
//        val result = HomePage(repository,webClientApi,auditService).getBookById("1a23456")
//        assert(result==monoOfBook)
//    }


    @Test
    fun getDataFromApi() {
        val fluxGooleBook = Flux.just(gooleBook)
        `when`(webClientApi.getBookfromApi(ArgumentMatchers.anyString())).thenReturn(fluxGooleBook)

        val result = HomePage(repository,webClientApi,auditService).getDataFromApi("Game Of Thrones")
        assert(result==fluxGooleBook)

    }

    @Test
    fun getBooksBySearch() {
        val fluxGooleBook = Flux.just(book)
        `when`(repository.findBookByTitleContainsIgnoreCase(ArgumentMatchers.anyString())).thenReturn(fluxGooleBook)

        val result = HomePage(repository,webClientApi,auditService).getBooksBySearchString("Game Of Thrones")
        assert(result==fluxGooleBook)

    }

}

