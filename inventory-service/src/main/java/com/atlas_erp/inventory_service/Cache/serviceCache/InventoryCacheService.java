package com.atlas_erp.inventory_service.Cache.serviceCache;

import com.atlas_erp.inventory_service.Dto.Request.Item.ItemListRequest;
import com.atlas_erp.inventory_service.Dto.Request.Warehouse.WarehouseListRequest;
import com.atlas_erp.inventory_service.Dto.Response.Item.ItemListResponse;
import com.atlas_erp.inventory_service.Dto.Response.StockDocument.StockDocumentDetailResponse;
import com.atlas_erp.inventory_service.Dto.Response.StockQuery.StockQueryResponse;
import com.atlas_erp.inventory_service.Dto.Response.Warehouse.WarehouseListResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryCacheService {

    private final RedisTemplate<String, Object> redisTemplate;

    // Định nghĩa Constants cho Prefix và TTL
    private static final String ITEM_LIST_PREFIX = "inventory:item:list:";
    private static final String STOCK_DOC_DETAIL_PREFIX = "inventory:stock_document:detail:";

    private static final Duration ITEM_LIST_TTL = Duration.ofMinutes(15);
    private static final Duration STOCK_DOC_TTL = Duration.ofMinutes(30);

    // --- Helper Methods tạo Key ---

    private String getItemListKey(ItemListRequest request) {
        return ITEM_LIST_PREFIX + String.format("%s:%s:%d:%d",
                request.getKeyword() != null ? request.getKeyword().trim() : "all",
                request.getItemType() != null ? request.getItemType() : "any",
                request.getPage() != null ? request.getPage() : 0,
                request.getPageSize() != null ? request.getPageSize() : 10);
    }

    private String getStockDocDetailKey(UUID stockDocumentId) {
        return STOCK_DOC_DETAIL_PREFIX + stockDocumentId;
    }

    // --- Thao tác với Item List Cache ---

    public ItemListResponse getItemListCache(ItemListRequest request) {
        try {
            return (ItemListResponse) redisTemplate.opsForValue().get(getItemListKey(request));
        } catch (Exception e) {
            log.error("Redis error - getItemListCache: {}", e.getMessage());
            return null;
        }
    }

    public void putItemListCache(ItemListRequest request, ItemListResponse response) {
        try {
            if (response != null) {
                redisTemplate.opsForValue().set(getItemListKey(request), response, ITEM_LIST_TTL);
            }
        } catch (Exception e) {
            log.error("Redis error - putItemListCache: {}", e.getMessage());
        }
    }

    public void evictAllItemCache() {
        try {
            Set<String> keys = redisTemplate.keys(ITEM_LIST_PREFIX + "*");
            if (keys != null && !keys.isEmpty()) {
                redisTemplate.delete(keys);
                log.info("Evicted {} keys from item list cache", keys.size());
            }
        } catch (Exception e) {
            log.error("Redis error - evictAllItemCache: {}", e.getMessage());
        }
    }

    // --- Thao tác với Stock Document Detail Cache ---

    public StockDocumentDetailResponse getDetailCache(UUID stockDocumentId) {
        try {
            return (StockDocumentDetailResponse) redisTemplate.opsForValue().get(getStockDocDetailKey(stockDocumentId));
        } catch (Exception e) {
            log.error("Redis error - getDetailCache: {}", e.getMessage());
            return null;
        }
    }

    public void putDetailCache(UUID stockDocumentId, StockDocumentDetailResponse response) {
        try {
            if (stockDocumentId != null && response != null) {
                redisTemplate.opsForValue().set(getStockDocDetailKey(stockDocumentId), response, STOCK_DOC_TTL);
            }
        } catch (Exception e) {
            log.error("Redis error - putDetailCache: {}", e.getMessage());
        }
    }

    public void evictDetailCache(UUID stockDocumentId) {
        try {
            if (stockDocumentId != null) {
                redisTemplate.delete(getStockDocDetailKey(stockDocumentId));
                log.info("Evicted stock document detail cache: {}", stockDocumentId);
            }
        } catch (Exception e) {
            log.error("Redis error - evictDetailCache: {}", e.getMessage());
        }
    }
    // Thêm vào InventoryCacheService.java

    private static final String STOCK_BALANCE_PREFIX = "inventory:stock:balance:";
    private static final Duration STOCK_TTL = Duration.ofMinutes(10);

    private String getStockBalanceKey(String warehouseId) {
        return STOCK_BALANCE_PREFIX + warehouseId;
    }

    public StockQueryResponse getStockBalanceCache(String warehouseId) {
        try {
            return (StockQueryResponse) redisTemplate.opsForValue().get(getStockBalanceKey(warehouseId));
        } catch (Exception e) {
            log.error("Redis error - getStockBalanceCache: {}", e.getMessage());
            return null;
        }
    }

    public void putStockBalanceCache(String warehouseId, StockQueryResponse response) {
        try {
            if (response != null) {
                redisTemplate.opsForValue().set(getStockBalanceKey(warehouseId), response, STOCK_TTL);
            }
        } catch (Exception e) {
            log.error("Redis error - putStockBalanceCache: {}", e.getMessage());
        }
    }

    public void evictStockBalanceCache(String warehouseId) {
        try {
            redisTemplate.delete(getStockBalanceKey(warehouseId));
            log.info("Đã xóa cache tồn kho của warehouse: {}", warehouseId);
        } catch (Exception e) {
            log.error("Redis error - evictStockBalanceCache: {}", e.getMessage());
        }
    }// Thêm vào InventoryCacheService.java

    private static final String WAREHOUSE_LIST_PREFIX = "inventory:warehouse:list:";

    private String getWarehouseListKey(WarehouseListRequest request) {
        return WAREHOUSE_LIST_PREFIX + String.format("%s:%d:%d",
                request.getPlantId(),
                request.getPage() != null ? request.getPage() : 0,
                request.getPageSize() != null ? request.getPageSize() : 10);
    }

    public WarehouseListResponse getWarehouseListCache(WarehouseListRequest request) {
        try {
            return (WarehouseListResponse) redisTemplate.opsForValue().get(getWarehouseListKey(request));
        } catch (Exception e) {
            log.error("Redis error - getWarehouseListCache: {}", e.getMessage());
            return null;
        }
    }

    public void putWarehouseListCache(WarehouseListRequest request, WarehouseListResponse response) {
        try {
            if (response != null) {
                // Cache danh sách kho trong 10 phút
                redisTemplate.opsForValue().set(getWarehouseListKey(request), response, Duration.ofMinutes(10));
            }
        } catch (Exception e) {
            log.error("Redis error - putWarehouseListCache: {}", e.getMessage());
        }
    }

    public void evictWarehouseListCache() {
        try {
            Set<String> keys = redisTemplate.keys(WAREHOUSE_LIST_PREFIX + "*");
            if (keys != null && !keys.isEmpty()) {
                redisTemplate.delete(keys);
            }
        } catch (Exception e) {
            log.error("Redis error - evictWarehouseListCache: {}", e.getMessage());
        }
    }
}