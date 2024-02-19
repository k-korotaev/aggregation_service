package com.smartpayments.superpos.aggregationservice.application.config

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.io.IOException

import java.time.*

@Configuration
class JacksonConfiguration() {

    @Bean
    fun objectMapper(): ObjectMapper {
        val mapper = jacksonObjectMapper()
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
        mapper.registerModule(
            JavaTimeModule().also {
                it.addDeserializer(LocalDateTime::class.java, LocalDateTimeISODeserializer(LocalDateTime::class.java))
                it.addSerializer(LocalDateTime::class.java, LocalDateTimeISOSerializer(LocalDateTime::class.java))
            }
        )
        return mapper
    }

    class LocalDateTimeISODeserializer(t: Class<LocalDateTime>?) : StdDeserializer<LocalDateTime>(t) {
        override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?): LocalDateTime? {
            return ctxt?.readValue(p, String::class.java)?.let {
                OffsetDateTime.parse(it).toZonedDateTime().withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime()
            }
        }
    }

    class LocalDateTimeISOSerializer(t: Class<LocalDateTime>?) : StdSerializer<LocalDateTime>(t) {
        @Throws(IOException::class, JsonProcessingException::class)
        override fun serialize(
            value: LocalDateTime?,
            jsonGenerator: JsonGenerator,
            provider: SerializerProvider?
        ) {
            if (value != null) {
                val systemOffset = ZoneOffset.systemDefault().rules.getOffset(Instant.now())
                val offsetDateTimeUTC = value.atOffset(systemOffset)
                jsonGenerator.writeString(offsetDateTimeUTC.toString())
            } else {
                jsonGenerator.writeNull()
            }
        }
    }
}