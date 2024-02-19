package com.smartpayments.superpos.aggregationservice.repository.jsonb


import com.smartpayments.superpos.aggregationservice.repository.jsonb.entity.TransactionEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface TransactionRepository : JpaRepository<TransactionEntity,String>{

    @Query(value =
            "SELECT NEW com.smartpayments.superpos.aggregationservice.repository.jsonb.entity.TransactionEntity(transaction.id, transaction.data) " +
            "FROM transactions_dump as transaction " +
            "WHERE transaction.id=:id ",
        nativeQuery = false)
    fun getTransactionEntityById(@Param("id") id: String): TransactionEntity?

    @Query(value =
            "SELECT transaction.id, transaction.document " +
            "FROM transactions_dump as transaction " +
            "WHERE transaction.document->>'BillingAccount' in (:accounts) " +
            "AND (transaction.document->>'status')\\:\\:int = :status " +
            "AND transaction.document->>'type'=:tr_type " +
            "AND transaction.document->>'Gate' in (:gates) " +
            "AND (transaction.document->>'endDate')\\:\\:timestamp between :startDate and :endDate",
        nativeQuery = true)
    fun getTransactionListByBillingAccountId(
        @Param("accounts") billingAccountIds: List<String>,
        @Param("status") status: Int=2,
        @Param("startDate") startDate: LocalDateTime,
        @Param("endDate") endDate: LocalDateTime,
        @Param("tr_type") type: String,
        @Param("gates") gateIds : List<String>): List<TransactionEntity>

    @Query(value =
    "SELECT count(transaction.id) " +
            "FROM transactions_dump as transaction " +
            "WHERE transaction.document->>'BillingAccount' in (:accounts) " +
            "AND (transaction.document->>'status')\\:\\:int = :status " +
            "AND transaction.document->>'type'=:tr_type " +
            "AND transaction.document->>'Gate' in (:gates) " +
            "AND (transaction.document->>'endDate')\\:\\:timestamp between :startDate and :endDate",
        nativeQuery = true)
    fun getTransactionCountByBillingAccountId(
        @Param("accounts") billingAccountIds: List<String>,
        @Param("status") status: Int=2,
        @Param("startDate") startDate: LocalDateTime,
        @Param("endDate") endDate: LocalDateTime,
        @Param("tr_type") type: String,
        @Param("gates") gateIds : List<String>): Long
}
