package com.example.bookinventorybackend.services;

import com.example.bookinventorybackend.models.Book;
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import java.sql.Timestamp
import java.util.*
import kotlin.collections.ArrayList

@Service
public class AuditService {

    @Autowired
    private val kafkaTemplate: KafkaTemplate<String, String>? = null

    companion object{
        var auditLogs: ArrayList<String> = ArrayList()
    }

    fun sendAddMessage(book:Book) {
        this.kafkaTemplate!!.send("booksAudit", prepareMessage("ADD",book))
    }

    fun sendEditMessage(book: Book) {
        this.kafkaTemplate!!.send("booksAudit", prepareMessage("EDIT",book))
    }

    fun sendDeleteMessage(book: Book) {
        this.kafkaTemplate!!.send("booksAudit", prepareMessage("DELETE",book))
    }

    @KafkaListener(topics = ["booksAudit"], groupId = "group_id")
    fun consume(message: String)  {
        println(message)
        auditLogs.add(message)
    }

    private fun getTimeStamp(): String{
        return Timestamp(Date().time).toString()
    }

    private fun prepareMessage(operation: String,book: Book) =

            "{\n" +
            "\t\"title\":\"${book.title}\",\n" +
            "\t\"price\":${book.price},\n" +
            "\t\"quantity\":${book.quantity}, \n" +
            "\t\"timeStamp\":\"${getTimeStamp()}\", \n" +
            "\t\"operation\":\"${operation}\" \n" +
            "}"


}