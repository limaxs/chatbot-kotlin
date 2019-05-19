package co.id.test.bintang.chatbot


import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import com.github.messenger4j.Messenger
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean



@SpringBootApplication
open class ChatbotApplication

fun main(args: Array<String>) {
    runApplication<ChatbotApplication>(*args)
}
