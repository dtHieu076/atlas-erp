package com.atlas_erp.accounting_service.Service;

import com.atlas_erp.accounting_service.Dto.Request.JournalEntry.*;
import com.atlas_erp.accounting_service.Dto.Response.JournalEntry.*;

public interface JournalEntryService {

    CreateJournalEntryResponse createJournalEntry(CreateJournalEntryRequest request);

    PostJournalEntryResponse postJournalEntry(String id, PostJournalEntryRequest request);

    ReverseJournalEntryResponse reverseJournalEntry(String id, ReverseJournalEntryRequest request);

    JournalEntryListResponse listJournalEntries(JournalEntryListRequest request);

    JournalEntryDetailResponse getJournalEntryDetail(String id);
}
