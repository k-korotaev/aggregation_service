package com.smartpayments.superpos.aggregationservice.business

import com.smartpayments.superpos.aggregationservice.repository.jsonb.entity.AccountEntity
import com.smartpayments.superpos.aggregationservice.repository.jsonb.entity.TransactionEntity
import com.smartpayments.superpos.aggregationservice.resources.dto.BillingTransactionDTO

class BillingTransactionMapper {
    companion object Mapper {
        fun toDTO(transactionEntity: TransactionEntity, billingAccount: AccountEntity?): BillingTransactionDTO {
            val transaction = transactionEntity.data
            val account = billingAccount!!.data
            return  BillingTransactionDTO(
                id = transaction.id,
                type = transaction.type,
                merchantOrderId = transaction.merchantOrderId,
                endDate = transaction.endDate,
                amount = transaction.amount.amount,
                currency = transaction.amount.currency,
                rrn = transaction.processingData?.rrn,
                authorizationCode = transaction.processingData?.authorizationCode,
                commissionAmount = transaction.commission?.bank!! + transaction.commission?.system!!,
                commissionCurrency = transaction.commission?.currency,
                customer = BillingTransactionDTO.Customer(
                    firstName = transaction.system?.info?.firstName,
                    lastName = transaction.system?.info?.lastName,
                    address = transaction.system?.info?.address,
                    city = transaction.system?.info?.city,
                    zipCode = transaction.system?.info?.zipCode,
                    country = transaction.system?.info?.country,
                    phone = transaction.system?.info?.phone,
                    email = transaction.system?.info?.email,
                    ip = transaction.system?.info?.ip,
                ),
                receiver = BillingTransactionDTO.Receiver(
                    name = account.name
                ),
                card = BillingTransactionDTO.Card(
                    bin = transaction.system?.info?.card?.bin,
                    iso = transaction.system?.info?.card?.iso,
                    country = transaction.system?.info?.card?.country,
                    type = transaction.system?.info?.card?.type,
                ),
                processingData = BillingTransactionDTO.ProcessingData(
                    amount = BillingTransactionDTO.Amount(
                        amount = transaction.processingData?.amount?.amount,
                        currency = transaction.processingData?.amount?.currency,
                    ),
                    commission = BillingTransactionDTO.Commission(
                        amount = transaction.processingData?.commission?.account
                            ?: ((transaction.processingData?.commission?.bank ?: 0)
                                    + (transaction.processingData?.commission?.system ?: 0)),
                        currency = transaction.processingData?.commission?.currency,
                        accountConditions = BillingTransactionDTO.AccountConditions(
                            fee = transaction.processingData?.commission?.accountConditions?.fee
                        )
                    )
                ),
                accrualAmount = BillingTransactionDTO.AccrualAmount(
                    amount =
                        transaction.processingData?.accrualAmount?.amount
                            ?: transaction.processingData?.amount?.amount,
                    currency =
                        transaction.processingData?.accrualAmount?.currency
                            ?: transaction.processingData?.amount?.currency
                )
            )
        }
    }
}