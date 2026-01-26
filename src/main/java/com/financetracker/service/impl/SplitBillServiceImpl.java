package com.financetracker.service.impl;

import com.financetracker.dto.ParticipantDto;
import com.financetracker.dto.SplitBillDto;
import com.financetracker.dto.SplitBillSummaryDto;
import com.financetracker.entity.Participant;
import com.financetracker.entity.SplitBill;
import com.financetracker.exception.ResourceNotFoundException;
import com.financetracker.mapper.SplitBillMapper;
import com.financetracker.repository.SplitBillRepository;
import com.financetracker.security.SecurityUtils;
import com.financetracker.service.SplitBillService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SplitBillServiceImpl implements SplitBillService {

    private final SplitBillRepository splitBillRepository;
    private final SplitBillMapper splitBillMapper;
    private final SecurityUtils securityUtils;

    @Override
    public SplitBillDto createSplitBill(SplitBillDto splitBillDto) {
        String userId = securityUtils.getCurrentUserId();
        log.info("Creating split bill for user: {}", userId);

        SplitBill splitBill = splitBillMapper.toEntity(splitBillDto);
        splitBill.setUserId(userId);
        splitBill.setSettled(false);

        // Generate IDs for participants
        if (splitBill.getParticipants() != null) {
            splitBill.getParticipants().forEach(p -> {
                if (p.getId() == null) {
                    p.setId(UUID.randomUUID().toString());
                }
            });
        }

        SplitBill savedBill = splitBillRepository.save(splitBill);
        log.info("Split bill created with ID: {}", savedBill.getId());

        return splitBillMapper.toDto(savedBill);
    }

    @Override
    public SplitBillDto getSplitBillById(String id) {
        String userId = securityUtils.getCurrentUserId();
        log.info("Fetching split bill {} for user: {}", id, userId);

        SplitBill splitBill = splitBillRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Split Bill", "id", id));

        return splitBillMapper.toDto(splitBill);
    }

    @Override
    public List<SplitBillDto> getAllSplitBills() {
        String userId = securityUtils.getCurrentUserId();
        log.info("Fetching all split bills for user: {}", userId);

        List<SplitBill> splitBills = splitBillRepository.findByUserId(userId);
        return splitBillMapper.toDtoList(splitBills);
    }

    @Override
    public SplitBillDto updateSplitBill(String id, SplitBillDto splitBillDto) {
        String userId = securityUtils.getCurrentUserId();
        log.info("Updating split bill {} for user: {}", id, userId);

        SplitBill splitBill = splitBillRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Split Bill", "id", id));

        splitBillMapper.updateEntity(splitBillDto, splitBill);
        SplitBill updatedBill = splitBillRepository.save(splitBill);
        log.info("Split bill {} updated successfully", id);

        return splitBillMapper.toDto(updatedBill);
    }

    @Override
    public void deleteSplitBill(String id) {
        String userId = securityUtils.getCurrentUserId();
        log.info("Deleting split bill {} for user: {}", id, userId);

        SplitBill splitBill = splitBillRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Split Bill", "id", id));

        splitBillRepository.delete(splitBill);
        log.info("Split bill {} deleted successfully", id);
    }

    @Override
    public SplitBillDto addParticipants(String id, List<ParticipantDto> participantDtos) {
        String userId = securityUtils.getCurrentUserId();
        log.info("Adding participants to split bill {} for user: {}", id, userId);

        SplitBill splitBill = splitBillRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Split Bill", "id", id));

        List<Participant> participants = participantDtos.stream()
                .map(dto -> {
                    Participant participant = splitBillMapper.participantToEntity(dto);
                    if (participant.getId() == null) {
                        participant.setId(UUID.randomUUID().toString());
                    }
                    return participant;
                })
                .toList();

        splitBill.getParticipants().addAll(participants);
        SplitBill updatedBill = splitBillRepository.save(splitBill);
        log.info("Participants added to split bill {}", id);

        return splitBillMapper.toDto(updatedBill);
    }

    @Override
    public SplitBillDto markParticipantAsPaid(String billId, String participantId) {
        String userId = securityUtils.getCurrentUserId();
        log.info("Marking participant {} as paid in split bill {} for user: {}", participantId, billId, userId);

        SplitBill splitBill = splitBillRepository.findByIdAndUserId(billId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Split Bill", "id", billId));

        Participant participant = splitBill.getParticipants().stream()
                .filter(p -> p.getId().equals(participantId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Participant", "id", participantId));

        participant.setPaid(true);

        // Check if all participants paid
        boolean allPaid = splitBill.getParticipants().stream().allMatch(Participant::isPaid);
        if (allPaid) {
            splitBill.setSettled(true);
            log.info("Split bill {} is now settled", billId);
        }

        SplitBill updatedBill = splitBillRepository.save(splitBill);
        return splitBillMapper.toDto(updatedBill);
    }

    @Override
    public SplitBillSummaryDto getSummary() {
        String userId = securityUtils.getCurrentUserId();
        log.info("Fetching split bill summary for user: {}", userId);

        List<SplitBill> allBills = splitBillRepository.findByUserId(userId);

        int totalBills = allBills.size();
        int settledBills = (int) allBills.stream().filter(SplitBill::isSettled).count();
        int pendingBills = totalBills - settledBills;

        BigDecimal totalAmountOwed = allBills.stream()
                .map(SplitBill::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalAmountPaid = allBills.stream()
                .map(SplitBill::getPaidAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalAmountPending = totalAmountOwed.subtract(totalAmountPaid);

        return SplitBillSummaryDto.builder()
                .totalBills(totalBills)
                .settledBills(settledBills)
                .pendingBills(pendingBills)
                .totalAmountOwed(totalAmountOwed)
                .totalAmountPaid(totalAmountPaid)
                .totalAmountPending(totalAmountPending)
                .build();
    }
}
