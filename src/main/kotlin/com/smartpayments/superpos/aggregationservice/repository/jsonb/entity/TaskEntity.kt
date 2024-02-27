package com.smartpayments.superpos.aggregationservice.repository.jsonb.entity

import com.smartpayments.superpos.aggregationservice.resources.dto.BillingRequestDTO
import jakarta.persistence.*
import org.hibernate.annotations.DynamicUpdate
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.time.LocalDateTime

@Entity(name="aggregation_service_tasks")
@DynamicUpdate
open class TaskEntity(
    open val type: EAggregationServiceTaskType,
    @Enumerated(EnumType.ORDINAL)
    open var status: TaskStatus=TaskStatus.CREATED,
    open var error: String? = null,
    @Column(name = "request", columnDefinition = "json")
    @JdbcTypeCode(SqlTypes.JSON)
    open var taskParams: List<BillingRequestDTO>,
    @Column(name="created_at")
    open val createdAt: LocalDateTime = LocalDateTime.now(),
    @Column(name = "document", columnDefinition = "json")
    @JdbcTypeCode(SqlTypes.JSON)
    open var customProps: Map<String,Any>?=null
) {
    @Id()
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open val id: Long?=null

    @Version
    open var version: Long = 0

    enum class EAggregationServiceTaskType(value: String) {
        TransactionsBetweenTxFile("TransactionsBetweenTxFile"),
        BillingExcel("BillingExcel"),
        TransactionsExcel("TransactionsExcel"),
        TerminalsExcel("TerminalsExcel"),
        GatesExcel("GatesExcel"),
    }

    enum class TaskStatus(val value: Int) {
        CREATED(0),
        IN_PROGRESS(1),
        COMPLETE(2),
        ERROR(-1);

        constructor (string:String):this(string.toInt())
    }
}