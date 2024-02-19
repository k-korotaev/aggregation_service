package com.smartpayments.superpos.aggregationservice.repository.jsonb.entity

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes

@Entity(name="gate")
open class GateEntity(
    @Id
    open val id: String,
    @Column(name = "document", columnDefinition = "json")
    @JdbcTypeCode(SqlTypes.JSON)
    open val data: Gate
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    data class Gate(
        @JsonProperty("_id")
        var id: String?,
        var name: String?,
        var bank: String?, // todo make Class wih _id and name
        var driver: String? = null
    )
}