package me.davidgomesdev.mindcura.web

import io.opentelemetry.api.GlobalOpenTelemetry
import io.opentelemetry.api.trace.SpanKind
import io.smallrye.common.annotation.Blocking
import jakarta.ws.rs.BadRequestException
import jakarta.ws.rs.PUT
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import me.davidgomesdev.mindcura.dto.ChatEvent
import me.davidgomesdev.mindcura.service.ChatService
import org.jboss.logging.Logger
import org.jboss.resteasy.reactive.RestMulti


@Path("/chat")
class ChatAPI(val chatService: ChatService) {

    private val tracer = GlobalOpenTelemetry.getTracer(this::class.java.name)
    val log: Logger = Logger.getLogger(this::class.java)

    @PUT
    @Blocking
    @Produces("application/x-ndjson")
    fun queryModel(body: QueryPayload): RestMulti<ChatEvent> {
        val span = tracer.spanBuilder("API QueryModel")
            .setSpanKind(SpanKind.INTERNAL)
            .startSpan()

        val traceId = span.spanContext.traceId

        log.info("Querying model with trace ID: $traceId")

        return RestMulti
            .fromMultiData(chatService.query(body.input))
            .header("X-Trace-Id", traceId).build()
    }
}

data class QueryPayload(val input: String)
