package com.atlas_erp.accounting_service.Controller;

import com.atlas_erp.accounting_service.Dto.Request.JournalEntry.*;
import com.atlas_erp.accounting_service.Dto.Response.JournalEntry.*;
import com.atlas_erp.accounting_service.Service.JournalEntryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/journal-entries")
@RequiredArgsConstructor
public class JournalEntryController {

    private final JournalEntryService journalEntryService;

    @PostMapping
    public CreateJournalEntryResponse createJournalEntry(@RequestBody CreateJournalEntryRequest request) {
        return journalEntryService.createJournalEntry(request);
    }

    @PostMapping("/{id}/post")
    public PostJournalEntryResponse postJournalEntry(
            @PathVariable String id,
            @RequestBody PostJournalEntryRequest request) {
        return journalEntryService.postJournalEntry(id, request);
    }

    @PostMapping("/{id}/reverse")
    public ReverseJournalEntryResponse reverseJournalEntry(
            @PathVariable String id,
            @RequestBody ReverseJournalEntryRequest request) {
        return journalEntryService.reverseJournalEntry(id, request);
    }

    @GetMapping
    public JournalEntryListResponse listJournalEntries(JournalEntryListRequest request) {
        return journalEntryService.listJournalEntries(request);
    }

    @GetMapping("/{id}")
    public JournalEntryDetailResponse getJournalEntryDetail(@PathVariable String id) {
        return journalEntryService.getJournalEntryDetail(id);
    }
}
