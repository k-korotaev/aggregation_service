package com.smartpayments.superpos.aggregationservice.repository.jsonb

import com.smartpayments.superpos.aggregationservice.repository.jsonb.entity.GateEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
@Repository
interface GateRepository : JpaRepository<GateEntity,String>{

    @Query(value =
            "SELECT gate.id, gate.document " +
            "FROM gates as gate " +
            "WHERE gate.document->>'bank'=:bankId ",
        nativeQuery = true)
    fun getGatesByBankId(@Param("bankId") bankId: String): List<GateEntity>
}