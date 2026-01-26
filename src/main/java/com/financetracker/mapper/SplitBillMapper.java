package com.financetracker.mapper;

import com.financetracker.dto.ParticipantDto;
import com.financetracker.dto.SplitBillDto;
import com.financetracker.entity.Participant;
import com.financetracker.entity.SplitBill;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SplitBillMapper {

    @Mapping(target = "paidAmount", expression = "java(splitBill.getPaidAmount())")
    @Mapping(target = "remainingAmount", expression = "java(splitBill.getRemainingAmount())")
    SplitBillDto toDto(SplitBill splitBill);

    SplitBill toEntity(SplitBillDto splitBillDto);

    List<SplitBillDto> toDtoList(List<SplitBill> splitBills);

    void updateEntity(SplitBillDto splitBillDto, @MappingTarget SplitBill splitBill);

    ParticipantDto participantToDto(Participant participant);

    Participant participantToEntity(ParticipantDto participantDto);

    List<ParticipantDto> participantsToDtoList(List<Participant> participants);
}
