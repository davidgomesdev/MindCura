package me.davidgomesdev.mindcura.llm.config

import io.smallrye.config.ConfigMapping
import java.time.Duration

@ConfigMapping(prefix = "model.ollama")
interface OllamaConfig {
    fun baseUrl(): String
    fun timeout(): Duration
    fun chatModel(): ChatModelConfig

    interface ChatModelConfig {
        fun modelId(): String
        fun temperature(): Double
        fun thinking(): Boolean
    }
}

