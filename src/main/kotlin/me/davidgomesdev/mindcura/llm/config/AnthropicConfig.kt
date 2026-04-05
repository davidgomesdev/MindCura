package me.davidgomesdev.mindcura.llm.config

import io.smallrye.config.ConfigMapping
import java.time.Duration

@ConfigMapping(prefix = "model.anthropic")
@Suppress("unused")
interface AnthropicConfig {
    fun apiKey(): String
    fun timeout(): Duration
    fun chatModel(): ChatModelConfig

    interface ChatModelConfig {
        fun modelId(): String
        fun temperature(): Double
        fun thinking(): Boolean
        fun maxTokens(): Int
    }
}

