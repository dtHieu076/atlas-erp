package com.atlas_erp.accounting_service.Service.Impl;

import com.atlas_erp.accounting_service.Cache.serviceCache.AccountingCacheService;
import com.atlas_erp.accounting_service.Dto.Request.Account.AccountListRequest;
import com.atlas_erp.accounting_service.Dto.Response.Account.AccountListResponse;
import com.atlas_erp.accounting_service.Dto.Response.Account.AccountListResponse.AccountDto;
import com.atlas_erp.accounting_service.Entity.AccountEntity;
import com.atlas_erp.accounting_service.Repository.AccountRepository;
import com.atlas_erp.accounting_service.Service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final AccountingCacheService cacheService;

    @Override
    public AccountListResponse getAccounts(AccountListRequest request) {
        // 1. Kiểm tra trong Cache trước
        AccountListResponse cached = cacheService.getCoaList(request.getAccountType(), request.getParentId());
        if (cached != null) {
            log.info("🚀 COA Cache Hit cho Type: {}", request.getAccountType());
            return cached;
        }

        log.info("🐢 COA Cache Miss, truy vấn Database...");

        // 2. Query từ Database
        UUID parentUuid = (request.getParentId() != null) ? UUID.fromString(request.getParentId()) : null;
        List<AccountEntity> entities = accountRepository.findByFilters(request.getAccountType(), parentUuid);

        // 3. Mapping sang DTO
        List<AccountDto> dtoList = entities.stream().map(entity -> {
            AccountDto dto = new AccountDto();
            dto.setId(entity.getId().toString());
            dto.setAccountCode(entity.getAccountCode());
            dto.setAccountName(entity.getAccountName());
            dto.setAccountType(entity.getAccountType());
            dto.setParentId(entity.getParentId() != null ? entity.getParentId().toString() : null);
            dto.setLevel(entity.getLevel());
            dto.setStatus(entity.getStatus());
            return dto;
        }).collect(Collectors.toList());

        // 4. Đóng gói Response
        AccountListResponse response = new AccountListResponse();
        response.setList(dtoList);

        // 5. Lưu vào Cache
        cacheService.putCoaList(request.getAccountType(), request.getParentId(), response);

        return response;
    }
}