package com.eldorado.microservico.schedule.mocks;

import com.eldorado.microservico.schedule.dto.WeekEnum;
import com.eldorado.microservico.schedule.dto.WorkScheduleDto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class WorkScheduleMock {

    public static WorkScheduleDto createValidWorkScheduleDto(UUID id, WeekEnum dayOfWeek, List<String> workTimes, LocalDate workDate) {
        return WorkScheduleDto.builder().employeeId(id).dayOfWeek(dayOfWeek).workTimes(workTimes).workDate(workDate).build();
    }


}
