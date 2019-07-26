package com.example.bookinventorybackend.services

//import org.junit.jupiter.api.AfterEach
//import org.junit.jupiter.api.BeforeEach
import com.example.bookinventorybackend.models.Book
import com.example.bookinventorybackend.models.ImageUrl
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.junit.After
import org.junit.Before
import org.junit.ClassRule
import org.junit.Test
import org.junit.jupiter.api.AfterEach

import org.junit.jupiter.api.Assertions.*
import org.junit.runner.RunWith
import org.springframework.kafka.test.utils.KafkaTestUtils
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.listener.ContainerProperties
import org.springframework.kafka.listener.KafkaMessageListenerContainer
import org.springframework.kafka.listener.MessageListener
import org.springframework.kafka.test.rule.EmbeddedKafkaRule
import org.springframework.kafka.test.utils.ContainerTestUtils
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.junit4.SpringRunner
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit

@RunWith(SpringRunner::class)
@SpringBootTest
@DirtiesContext
class AuditServiceTest {

    companion object {
        val logger = LoggerFactory.getLogger(AuditServiceTest::class.java)
        val topic="booksAudit"

        @ClassRule
        @JvmField
        val embeddedKafkaRule = EmbeddedKafkaRule(1,true, topic)
    }

    @Autowired
    lateinit var auditService : AuditService
    lateinit var container : KafkaMessageListenerContainer<String, String>
    lateinit var records : BlockingQueue<ConsumerRecord<String, String>>


    @Before
    fun setUp() {
        val consumerProperties = KafkaTestUtils.consumerProps("sender",
                "false", embeddedKafkaRule.embeddedKafka)

        val consumerFactory = DefaultKafkaConsumerFactory<String, String>(consumerProperties)
        val containerProperties = ContainerProperties(topic)

        container = KafkaMessageListenerContainer(consumerFactory,containerProperties)
        records = LinkedBlockingQueue()

        container.setupMessageListener(MessageListener<String, String> { record ->
            logger.debug("test-listener received message='{}'", record.toString())
            records!!.add(record)
        })

        container.start()

        ContainerTestUtils.waitForAssignment(container,
                embeddedKafkaRule.embeddedKafka.partitionsPerTopic)
    }

    @After
    fun tearDown(){
        container.stop()
    }


    @Test
    fun sendAddMessage() {

        val imageUrl = ImageUrl("http://books.google.com/books/content?id=twlUtwEACAAJ&printsec=frontcover&img=1&zoom=1&source=gbs_api",
                "http://books.google.com/books/content?id=twlUtwEACAAJ&printsec=frontcover&img=1&zoom=1&source=gbs_api")
        val book = Book("123456", "Game Of Thrones", listOf("anish","kk"), imageUrl, "angle and demons", 101, 10)
//        val book2 = Book("123457", "Game Of Life", listOf("anish","kk"), imageUrl, "angle and demons", 101, 10)


        auditService.sendAddMessage(book)

        records.poll(10, TimeUnit.SECONDS)
        val auditLog = AuditService.auditLogs

        assert(auditLog.last().contains("Game Of Thrones"))
        assert(auditLog.last().contains("ADD"))

    }

    @Test
    fun sendEditMessage() {
        val imageUrl = ImageUrl("http://books.google.com/books/content?id=twlUtwEACAAJ&printsec=frontcover&img=1&zoom=1&source=gbs_api",
                "http://books.google.com/books/content?id=twlUtwEACAAJ&printsec=frontcover&img=1&zoom=1&source=gbs_api")
        val book = Book("123456", "Game Of Thrones", listOf("anish","kk"), imageUrl, "angle and demons", 101, 10)
        auditService.sendEditMessage(book)

        records.poll(10,TimeUnit.SECONDS)
        val auditLog = AuditService.auditLogs
        assert(auditLog.last().contains("Game Of Thrones"))
        assert(auditLog.last().contains("EDIT"))
    }

    @Test
    fun sendDeleteMessage() {
        val imageUrl = ImageUrl("http://books.google.com/books/content?id=twlUtwEACAAJ&printsec=frontcover&img=1&zoom=1&source=gbs_api",
                "http://books.google.com/books/content?id=twlUtwEACAAJ&printsec=frontcover&img=1&zoom=1&source=gbs_api")
        val book = Book("123456", "Game Of Thrones", listOf("anish","kk"), imageUrl, "angle and demons", 101, 10)
        auditService.sendDeleteMessage(book)

        records.poll(10,TimeUnit.SECONDS)
        val auditLog = AuditService.auditLogs
        assert(auditLog.last().contains("Game Of Thrones"))
        assert(auditLog.last().contains("DELETE"))
    }
}