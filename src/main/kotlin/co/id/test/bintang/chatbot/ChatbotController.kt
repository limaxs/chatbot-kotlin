package co.id.test.bintang.chatbot

import com.github.messenger4j.Messenger.CHALLENGE_REQUEST_PARAM_NAME
import com.github.messenger4j.Messenger.MODE_REQUEST_PARAM_NAME
import com.github.messenger4j.Messenger.SIGNATURE_HEADER_NAME
import com.github.messenger4j.Messenger.VERIFY_TOKEN_REQUEST_PARAM_NAME
import java.util.Optional.empty
import java.util.Optional.of

import com.github.messenger4j.Messenger
import com.github.messenger4j.exception.MessengerApiException
import com.github.messenger4j.exception.MessengerIOException
import com.github.messenger4j.exception.MessengerVerificationException
import com.github.messenger4j.send.MessagePayload
import com.github.messenger4j.send.MessagingType
import com.github.messenger4j.send.NotificationType
import com.github.messenger4j.send.message.TextMessage
import com.github.messenger4j.send.recipient.IdRecipient
import com.github.messenger4j.webhook.event.TextMessageEvent
import java.net.MalformedURLException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/callback")
class ChatbotController @Autowired
constructor(private val messenger: Messenger) {

    /**
     * Webhook verification endpoint.
     *
     * The passed verification token (as query parameter) must match the configured
     * verification token. In case this is true, the passed challenge string must be returned by this endpoint.
     */
    @RequestMapping(method = [RequestMethod.GET])
    fun verifyWebhook(@RequestParam(MODE_REQUEST_PARAM_NAME) mode: String,
                      @RequestParam(VERIFY_TOKEN_REQUEST_PARAM_NAME) verifyToken: String, @RequestParam(CHALLENGE_REQUEST_PARAM_NAME) challenge: String): ResponseEntity<String> {
        logger.debug("Received Webhook verification request - mode: {} | verifyToken: {} | challenge: {}", mode, verifyToken, challenge)
        try {
            this.messenger.verifyWebhook(mode, verifyToken)
            return ResponseEntity.ok(challenge)
        } catch (e: MessengerVerificationException) {
            logger.warn("Webhook verification failed: {}", e.message)
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.message)
        }

    }

    /**
     * Callback endpoint responsible for processing the inbound messages and events.
     */
    @RequestMapping(method = [RequestMethod.POST])
    fun handleCallback(@RequestBody payload: String, @RequestHeader(SIGNATURE_HEADER_NAME) signature: String): ResponseEntity<Void> {
        logger.debug("Received Messenger Platform callback - payload: {} | signature: {}", payload, signature)
        try {
            this.messenger.onReceiveEvents(payload, of(signature)) { event ->
                if (event.isTextMessageEvent) {
                    handleTextMessageEvent(event.asTextMessageEvent())
                }
            }
            logger.debug("Processed callback payload successfully")
            return ResponseEntity.status(HttpStatus.OK).build()
        } catch (e: MessengerVerificationException) {
            logger.warn("Processing of callback payload failed: {}", e.message)
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }

    }

    private fun handleTextMessageEvent(event: TextMessageEvent) {
        logger.debug("Received TextMessageEvent: {}", event)

        val messageId = event.messageId()
        val messageText = event.text()
        val senderId = event.senderId()
        val timestamp = event.timestamp()

        logger.info("Received message '{}' with text '{}' from user '{}' at '{}'", messageId, messageText, senderId, timestamp)

        try {
            when (messageText.toLowerCase()) {
                "hai" -> sendTextMessage(senderId, "Hai Juga !! :)")
                "lagi apa?" -> sendTextMessage(senderId, "Lagi mikirin kamu..")
                "mikirin apa?" -> sendTextMessage(senderId, "kasih tau gk yaaa???")
            }
        } catch (e: MessengerApiException) {
            handleSendException(e)
        } catch (e: MessengerIOException) {
            handleSendException(e)
        } catch (e: MalformedURLException) {
            handleSendException(e)
        }

    }



    private fun sendTextMessage(recipientId: String, text: String) {
        try {
            val recipient = IdRecipient.create(recipientId)
            val notificationType = NotificationType.REGULAR
            val metadata = "DEVELOPER_DEFINED_METADATA"

            val textMessage = TextMessage.create(text, empty(), of(metadata))
            val messagePayload = MessagePayload.create(recipient, MessagingType.RESPONSE, textMessage,
                    of(notificationType), empty())
            this.messenger.send(messagePayload)
        } catch (e: MessengerApiException) {
            handleSendException(e)
        } catch (e: MessengerIOException) {
            handleSendException(e)
        }

    }

    private fun handleSendException(e: Exception) {
        logger.error("Message could not be sent. An unexpected error occurred.", e)
    }

    companion object {

        private val RESOURCE_URL = "https://raw.githubusercontent.com/fbsamples/messenger-platform-samples/master/node/public"

        private val logger = LoggerFactory.getLogger(ChatbotController::class.java)
    }
}