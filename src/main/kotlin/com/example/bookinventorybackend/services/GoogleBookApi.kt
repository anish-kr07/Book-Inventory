package com.example.bookinventorybackend.services

import com.example.bookinventorybackend.models.GooleBook
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.util.UriComponentsBuilder
import reactor.core.publisher.Flux

@Service
class  GoogleBookApi{
        var apikey = "AIzaSyDtAmN5XBA5_lQBQUf4MOGZ45ZV3-C83kQ"
        var uri="https://www.googleapis.com/books/v1/volumes"

    fun getBookfromApi(query: String): Flux<GooleBook> {

        return WebClient.create(buildUrl(query ))
                .get()
                .retrieve()
                .bodyToFlux(GooleBook::class.java)
    }

    fun buildUrl(query: String): String {
        return UriComponentsBuilder.fromHttpUrl(uri)
                .replaceQueryParam("q",query)
                .replaceQueryParam("key",apikey)
                .encode().toUriString()
    }
}

