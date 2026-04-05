package me.davidgomesdev.mindcura.service

import dev.langchain4j.service.TokenStream
import io.opentelemetry.api.trace.StatusCode
import io.quarkus.runtime.Startup
import io.smallrye.mutiny.Multi
import jakarta.enterprise.context.ApplicationScoped
import me.davidgomesdev.mindcura.dto.ChatEvent
import me.davidgomesdev.mindcura.observability.attributes
import me.davidgomesdev.mindcura.observability.span
import org.jboss.logging.Logger
import kotlin.time.DurationUnit
import kotlin.time.TimeSource

fun interface Assistant {
    fun chat(userMessage: String): TokenStream
}

@ApplicationScoped
@Startup
class ChatService(val assistant: Assistant) {

    val log: Logger = Logger.getLogger(this::class.java)

    fun query(input: String): Multi<ChatEvent> {
        val span = span()
        val scope = span.makeCurrent()
        val chatStream = assistant.chat(input)
        val timeSource = TimeSource.Monotonic
        val startTime = timeSource.markNow()

        return Multi.createFrom().emitter { stream ->
            stream.emit(ChatEvent.Start(span.spanContext.traceId))

            chatStream
                .onPartialResponse { partialResponse ->
                    stream.emit(ChatEvent.Token(partialResponse))
                }
                .onCompleteResponse { response ->
                    val timeTaken = startTime.elapsedNow().toString(DurationUnit.SECONDS, 2)
                    val totalTokensUsed = response.tokenUsage().totalTokenCount()

                    log.info("Took $timeTaken to respond (used $totalTokensUsed output tokens)")

                    span.apply {
                        addEvent(
                            "Response complete",
                            attributes {
                                put("query", input)
                                put("response", response.aiMessage().text())
                                put("thinking", response.aiMessage().thinking())
                                put("model", response.metadata().modelName())
                                put("model_duration.ms", timeTaken)
                                put("total_tokens_used", totalTokensUsed.toLong())
                                put("input_tokens_used", response.tokenUsage().inputTokenCount().toLong())
                                put("output_tokens_used", response.tokenUsage().outputTokenCount().toLong())
                                put("complete_reason", response.finishReason().name)
                            }
                        )
                    }

                    stream.emit(ChatEvent.Done(totalTokensUsed, timeTaken))
                    stream.complete()
                    scope.close()
                }
                .onError { error ->
                    stream.fail(error)

                    span.apply {
                        recordException(error)
                        setStatus(StatusCode.ERROR)
                    }

                    log.error("There was a problem with the assistant!", error)
                }
                .start()
        }
    }
}
