package com.atlas_erp.accounting_service.Repository;

import com.atlas_erp.accounting_service.Entity.JournalEntryLineEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface JournalEntryLineRepository extends JpaRepository<JournalEntryLineEntity, UUID> {

        // Lấy tổng phát sinh Nợ và Có của tất cả tài khoản trong một kỳ
        @Query(value = "SELECT CAST(l.account_id AS VARCHAR) as accountId, " +
                        "SUM(l.debit) as totalDebit, " +
                        "SUM(l.credit) as totalCredit " +
                        "FROM journal_entry_line l " +
                        "JOIN journal_entry e ON l.journal_entry_id = e.id " +
                        "WHERE e.accounting_period_id = :periodId AND e.status = 'POSTED' " +
                        "GROUP BY l.account_id", nativeQuery = true)
        List<Object[]> sumAllAccountActivities(@Param("periodId") UUID periodId);

        // Tính số dư cho Financial Report dựa trên Account Type (Asset, Liability,
        // v.v.)
        @Query(value = "SELECT SUM(l.debit) - SUM(l.credit) FROM journal_entry_line l " +
                        "JOIN journal_entry e ON l.journal_entry_id = e.id " +
                        "JOIN account a ON l.account_id = a.id " +
                        "WHERE e.accounting_period_id = :periodId AND e.status = 'POSTED' " +
                        "AND a.account_type = :accountType", nativeQuery = true)
        BigDecimal sumNetBalanceByType(@Param("periodId") UUID periodId, @Param("accountType") String accountType);

        List<JournalEntryLineEntity> findByJournalEntryId(UUID journalEntryId);

        // Bổ sung vào JournalEntryLineRepository.java
        @Query("SELECT e.id, e.entryDate, e.description, l.debit, l.credit " +
                        "FROM JournalEntryLineEntity l " +
                        "JOIN JournalEntryEntity e ON l.journalEntryId = e.id " +
                        "WHERE l.accountId = :accountId AND e.accountingPeriodId = :periodId " +
                        "AND e.status = 'POSTED' " +
                        "ORDER BY e.entryDate ASC, e.createdAt ASC")
        List<Object[]> findLedgerData(@Param("accountId") UUID accountId, @Param("periodId") UUID periodId);
}