package com.atlas_erp.inventory_service.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;
import com.atlas_erp.inventory_service.Dto.Response.StockQuery.StockQueryResponse.StockBalanceDto;
import com.atlas_erp.inventory_service.Entity.StockTransactionEntity;

@Repository
public interface StockTransactionRepository
        extends JpaRepository<StockTransactionEntity, UUID> {

    List<StockTransactionEntity> findByWarehouseId(UUID warehouseId);

    List<StockTransactionEntity> findByBatchIdOrderByTransactionDateAsc(UUID batchId);

    @Query("""
                SELECT new com.atlas_erp.inventory_service.Dto.Response.StockQuery.StockQueryResponse$StockBalanceDto(
                    i.id, i.code, i.name, SUM(t.quantity), i.unit
                )
                FROM StockTransactionEntity t
                JOIN ItemEntity i ON t.itemId = i.id
                WHERE t.warehouseId = :warehouseId
                GROUP BY i.id, i.code, i.name, i.unit
                HAVING SUM(t.quantity) > 0
            """)
    List<StockBalanceDto> calculateCurrentStockByWarehouse(@Param("warehouseId") UUID warehouseId);

    @Query("""
                SELECT COUNT(DISTINCT st.itemId)
                FROM StockTransactionEntity st
                WHERE st.warehouseId = :warehouseId
            """)
    Integer countDistinctItems(UUID warehouseId);

    @Query("""
                SELECT COALESCE(SUM(st.quantity), 0)
                FROM StockTransactionEntity st
                WHERE st.warehouseId = :warehouseId
            """)
    BigDecimal sumQuantity(UUID warehouseId);
}
