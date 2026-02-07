package com.atlas_erp.accounting_service.Repository;

import com.atlas_erp.accounting_service.Entity.JournalEntryEntity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.UUID;

public interface JournalEntryRepository extends JpaRepository<JournalEntryEntity, UUID> {

        @Query("SELECT e.id, e.entryDate, e.description, e.status, " +
                        "SUM(l.debit) as totalDebit, SUM(l.credit) as totalCredit " +
                        "FROM JournalEntryEntity e " +
                        "JOIN JournalEntryLineEntity l ON e.id = l.journalEntryId " +
                        "WHERE (:fromDate IS NULL OR e.entryDate >= :fromDate) " +
                        "AND (:toDate IS NULL OR e.entryDate <= :toDate) " +
                        "AND (:accountId IS NULL OR l.accountId = :accountId) " +
                        "GROUP BY e.id, e.entryDate, e.description, e.status")
        Page<Object[]> findJournalEntrySummary(
                        @Param("fromDate") LocalDate fromDate,
                        @Param("toDate") LocalDate toDate,
                        @Param("accountId") UUID accountId,
                        Pageable pageable);

}