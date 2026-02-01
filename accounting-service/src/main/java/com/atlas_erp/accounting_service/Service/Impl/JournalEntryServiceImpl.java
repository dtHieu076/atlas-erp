package com.atlas_erp.accounting_service.Service.Impl;

import com.atlas_erp.accounting_service.Dto.Request.JournalEntry.*;
import com.atlas_erp.accounting_service.Dto.Response.JournalEntry.*;
import com.atlas_erp.accounting_service.Service.JournalEntryService;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
public class JournalEntryServiceImpl implements JournalEntryService {

    @Override
    public CreateJournalEntryResponse createJournalEntry(CreateJournalEntryRequest request) {
        CreateJournalEntryResponse response = new CreateJournalEntryResponse();
        response.setId(UUID.randomUUID().toString());
        return response;
    }

    @Override
    public PostJournalEntryResponse postJournalEntry(String id, PostJournalEntryRequest request) {
        PostJournalEntryResponse response = new PostJournalEntryResponse();
        response.setSuccess(true);
        return response;
    }

    @Override
    public ReverseJournalEntryResponse reverseJournalEntry(String id, ReverseJournalEntryRequest request) {
        ReverseJournalEntryResponse response = new ReverseJournalEntryResponse();
        response.setNewEntryId(UUID.randomUUID().toString());
        return response;
    }

    @Override
    public JournalEntryListResponse listJournalEntries(JournalEntryListRequest request) {
        return new JournalEntryListResponse();
    }

    @Override
    public JournalEntryDetailResponse getJournalEntryDetail(String id) {
        return new JournalEntryDetailResponse();
    }
}
