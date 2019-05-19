package co.id.test.bintang.chatbot


import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import com.github.messenger4j.Messenger
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean


@SpringBootApplication
class ChatbotApplication {

    @Bean
    fun messenger(@Value("\${messenger4j.pageAccessToken}") pageAccessToken: String,
                  @Value("\${messenger4j.appSecret}") appSecret: String,
                  @Value("\${messenger4j.verifyToken}") verifyToken: String): Messenger {
        return Messenger.create(pageAccessToken, appSecret, verifyToken)
    }

    fun main(args: Array<String>) {
        runApplication<ChatbotApplication>(*args)
    }
}