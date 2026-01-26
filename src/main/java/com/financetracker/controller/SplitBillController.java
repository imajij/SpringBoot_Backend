package com.financetracker.controller;

import com.financetracker.dto.ApiResponse;
import com.financetracker.dto.ParticipantDto;
import com.financetracker.dto.SplitBillDto;
import com.financetracker.dto.SplitBillSummaryDto;
import com.financetracker.service.SplitBillService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/split-bills")
@RequiredArgsConstructor
public class SplitBillController {

    private final SplitBillService splitBillService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<SplitBillDto>>> getAllSplitBills() {
        log.info("Fetching all split bills");
        List<SplitBillDto> splitBills = splitBillService.getAllSplitBills();
        return ResponseEntity.ok(ApiResponse.success(splitBills));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SplitBillDto>> getSplitBillById(@PathVariable String id) {
        log.info("Fetching split bill: {}", id);
        SplitBillDto splitBill = splitBillService.getSplitBillById(id);
        return ResponseEntity.ok(ApiResponse.success(splitBill));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<SplitBillDto>> createSplitBill(@Valid @RequestBody SplitBillDto splitBillDto) {
        log.info("Creating new split bill");
        SplitBillDto createdBill = splitBillService.createSplitBill(splitBillDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Split bill created successfully", createdBill));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SplitBillDto>> updateSplitBill(
            @PathVariable String id,
            @Valid @RequestBody SplitBillDto splitBillDto) {
        log.info("Updating split bill: {}", id);
        SplitBillDto updatedBill = splitBillService.updateSplitBill(id, splitBillDto);
        return ResponseEntity.ok(ApiResponse.success("Split bill updated successfully", updatedBill));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteSplitBill(@PathVariable String id) {
        log.info("Deleting split bill: {}", id);
        splitBillService.deleteSplitBill(id);
        return ResponseEntity.ok(ApiResponse.success("Split bill deleted successfully"));
    }

    @PostMapping("/{id}/participants")
    public ResponseEntity<ApiResponse<SplitBillDto>> addParticipants(
            @PathVariable String id,
            @Valid @RequestBody List<ParticipantDto> participants) {
        log.info("Adding participants to split bill: {}", id);
        SplitBillDto updatedBill = splitBillService.addParticipants(id, participants);
        return ResponseEntity.ok(ApiResponse.success("Participants added successfully", updatedBill));
    }

    @PutMapping("/{id}/participants/{participantId}/pay")
    public ResponseEntity<ApiResponse<SplitBillDto>> markParticipantAsPaid(
            @PathVariable String id,
            @PathVariable String participantId) {
        log.info("Marking participant {} as paid in split bill: {}", participantId, id);
        SplitBillDto updatedBill = splitBillService.markParticipantAsPaid(id, participantId);
        return ResponseEntity.ok(ApiResponse.success("Participant marked as paid", updatedBill));
    }

    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<SplitBillSummaryDto>> getSummary() {
        log.info("Fetching split bill summary");
        SplitBillSummaryDto summary = splitBillService.getSummary();
        return ResponseEntity.ok(ApiResponse.success(summary));
    }
}
