package com.onclass.person.domain.model;

import java.time.LocalDate;

public record BootcampInfo(
        Long id,
        String name,
        String description,
        LocalDate startDate,
        Integer weeksDuration
) {
    public LocalDate getEndDate(){
        return startDate != null && weeksDuration != null ?
                startDate.plusWeeks(weeksDuration) : null;
    }

    public boolean overlapsWith(BootcampInfo bootcampInfo){
        if (this.startDate == null || bootcampInfo.startDate == null) return false;
        LocalDate thisEnd = this.getEndDate();
        LocalDate bootcampInfoEnd = bootcampInfo.getEndDate();
        if (thisEnd == null || bootcampInfoEnd == null) return false;
        return !this.startDate.isAfter(bootcampInfoEnd)
                && !bootcampInfo.startDate.isAfter(thisEnd);
    }
}
