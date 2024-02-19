package com.smartpayments.superpos.aggregationservice.repository.jsonb.entity

import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.time.LocalDateTime

@Entity(name="aggregation_service_tasks")
open class TaskEntity(
    @Id()
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open val id: Long?=null,
    open val type: EAggregationServiceTaskType,
    open var status: Int,
    open var error: String? = null,
    @Column(name="created_at")
    open val createdAt: String = LocalDateTime.now().toString(),

    @Column(name = "document", columnDefinition = "json")
    @JdbcTypeCode(SqlTypes.JSON)
    open var customProps: Map<String,Any>?=null
){
    enum class EAggregationServiceTaskType(value: String) {
        TransactionsBetweenTxFile("TransactionsBetweenTxFile"),
        BillingExcel("BillingExcel"),
        TransactionsExcel("TransactionsExcel"),
        TerminalsExcel("TerminalsExcel"),
        GatesExcel("GatesExcel"),
    }
}