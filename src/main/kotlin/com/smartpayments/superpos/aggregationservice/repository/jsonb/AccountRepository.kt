package com.smartpayments.superpos.aggregationservice.repository.jsonb

import com.smartpayments.superpos.aggregationservice.repository.jsonb.entity.AccountEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
@Repository
interface AccountRepository : JpaRepository<AccountEntity,String>{

    @Query(value ="SELECT account.id, account.document " +
            "FROM accounts as account " +
            "WHERE account.id=:accountId " +
            "and account.document->>'Merchant'=:merchantId ",
        nativeQuery = true)
    fun getAccountsByMerchantIdAndAccountId(
        @Param("merchantId") merchantId: String,
        @Param("accountId") accountId: String): List<AccountEntity>

    @Query(value ="SELECT count(account.id) " +
            "FROM accounts as account " +
            "WHERE account.id=:accountId " +
            "and account.document->>'Merchant'=:merchantId ",
        nativeQuery = true)
    fun getAccountsCountByMerchantIdAndAccountId(
        @Param("merchantId") merchantId: String,
        @Param("accountId") accountId: String): Long

    fun getAccountById(id: String) : AccountEntity
}