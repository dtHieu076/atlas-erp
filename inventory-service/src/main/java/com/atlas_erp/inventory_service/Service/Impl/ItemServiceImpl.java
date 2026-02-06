package com.atlas_erp.inventory_service.Service.Impl;

import com.atlas_erp.inventory_service.Cache.serviceCache.InventoryCacheService; // Giả định tên service cache của bạn
import com.atlas_erp.inventory_service.Dto.ItemDto;
import com.atlas_erp.inventory_service.Dto.Request.Item.ItemListRequest;
import com.atlas_erp.inventory_service.Dto.Response.Item.ItemListResponse;
import com.atlas_erp.inventory_service.Entity.ItemEntity;
import com.atlas_erp.inventory_service.Repository.ItemRepository;
import com.atlas_erp.inventory_service.Service.ItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final InventoryCacheService inventoryCacheService; // Service xử lý Redis

    @Override
    public ItemListResponse searchItems(ItemListRequest request) {
        // 1. Kiểm tra trong Cache trước
        // Key được tạo dựa trên các tham số: keyword, itemType, page, pageSize
        ItemListResponse cachedResponse = inventoryCacheService.getItemListCache(request);
        if (cachedResponse != null) {
            log.info("Cache Hit - Lấy danh mục vật tư từ Redis");
            return cachedResponse;
        }

        log.info("Cache Miss - Truy vấn Database cho keyword: {}", request.getKeyword());

        // 2. Thiết lập phân trang
        int page = (request.getPage() != null && request.getPage() >= 0) ? request.getPage() : 0;
        int size = (request.getPageSize() != null && request.getPageSize() > 0) ? request.getPageSize() : 10;
        Pageable pageable = PageRequest.of(page, size);

        // 3. Tiền xử lý tham số
        String keyword = (request.getKeyword() != null && !request.getKeyword().isBlank())
                ? request.getKeyword().trim()
                : null;
        String itemType = (request.getItemType() != null && !request.getItemType().isBlank())
                ? request.getItemType().trim()
                : null;

        // 4. Query Database
        Page<ItemEntity> itemPage = itemRepository.search(keyword, itemType, pageable);

        // 5. Map sang DTO
        List<ItemDto> dtoList = itemPage.getContent().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        ItemListResponse response = new ItemListResponse();
        response.setList(dtoList);
        response.setTotal(itemPage.getTotalElements());

        // 6. Lưu kết quả vào Cache cho lần gọi sau
        inventoryCacheService.putItemListCache(request, response);

        return response;
    }

    private ItemDto convertToDto(ItemEntity entity) {
        ItemDto dto = new ItemDto();
        dto.setId(entity.getId().toString());
        dto.setItemCode(entity.getCode());
        dto.setItemName(entity.getName());
        dto.setUnit(entity.getUnit());
        dto.setItemType(entity.getItemType());
        return dto;
    }
}