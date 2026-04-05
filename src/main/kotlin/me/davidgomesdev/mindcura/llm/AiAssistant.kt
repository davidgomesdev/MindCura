package me.davidgomesdev.mindcura.llm

import dev.langchain4j.data.message.SystemMessage
import dev.langchain4j.data.message.TextContent
import dev.langchain4j.model.chat.StreamingChatModel
import dev.langchain4j.observability.api.listener.AiServiceErrorListener
import dev.langchain4j.observability.api.listener.AiServiceStartedListener
import dev.langchain4j.service.AiServices
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Singleton
import me.davidgomesdev.mindcura.observability.attributes
import me.davidgomesdev.mindcura.observability.span
import me.davidgomesdev.mindcura.service.Assistant
import org.jboss.logging.Logger
import java.io.File

private const val PROMPT_FILE_NAME = "system_message.txt"

@ApplicationScoped
class AiAssistant {

    val log: Logger = Logger.getLogger(this::class.java)

    // Used if there is no local system_message.txt
    private val systemMessage: String =
        Thread.currentThread().contextClassLoader
            .getResourceAsStream("prompts/$PROMPT_FILE_NAME")!!
            .reader().readText()

    @Singleton
    @Suppress("unused")
    fun assistant(chatModel: StreamingChatModel): Assistant {
        log.info("Creating assistant")
        return AiServices.builder(Assistant::class.java)
            .systemMessageProvider { _ ->
                val systemMessageLocalFile = File(PROMPT_FILE_NAME)

                if (systemMessageLocalFile.exists()) return@systemMessageProvider systemMessageLocalFile.readText()

                systemMessage
            }
            .registerListeners(
                AiServiceStartedListener { event ->
                    span().addEvent(
                        "LLM query",
                        attributes {
                            put(
                                "user_message",
                                event.userMessage().contents()
                                    .filterIsInstance<TextContent>()
                                    .joinToString("\n\n", transform = TextContent::text)
                            )
                            put(
                                "system_message", event.systemMessage()
                                    .map(SystemMessage::text)
                                    .orElseGet { "" })
                        })
                },
                AiServiceErrorListener { error ->
                    span().recordException(error.error())
                })
            .streamingChatModel(chatModel)
            .build()
    }
}
