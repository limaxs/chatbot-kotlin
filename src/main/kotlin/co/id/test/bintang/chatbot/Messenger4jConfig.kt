package co.id.test.bintang.chatbot

import com.github.messenger4j.Messenger
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class Messenger4jConfig {

    @Bean
    fun messenger(@Value("\${messenger4j.pageAccessToken}") pageAccessToken: String,
                  @Value("\${messenger4j.appSecret}") appSecret: String,
                  @Value("\${messenger4j.verifyToken}") verifyToken: String): Messenger {
        println("TES: "+pageAccessToken + "" + appSecret + " " + verifyToken)
        return Messenger.create(pageAccessToken, appSecret, verifyToken)
    }
}