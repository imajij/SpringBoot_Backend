package com.financetracker.service;

import com.financetracker.dto.ParticipantDto;
import com.financetracker.dto.SplitBillDto;
import com.financetracker.dto.SplitBillSummaryDto;

import java.util.List;

public interface SplitBillService {

    SplitBillDto createSplitBill(SplitBillDto splitBillDto);

    SplitBillDto getSplitBillById(String id);

    List<SplitBillDto> getAllSplitBills();

    SplitBillDto updateSplitBill(String id, SplitBillDto splitBillDto);

    void deleteSplitBill(String id);

    SplitBillDto addParticipants(String id, List<ParticipantDto> participants);

    SplitBillDto markParticipantAsPaid(String billId, String participantId);

    SplitBillSummaryDto getSummary();
}
