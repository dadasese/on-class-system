package com.onclass.bootcamp.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Builder
@Setter
@Getter
public class Bootcamp {
    private Long id;
    private String name;
    private String description;
    private LocalDate startDate;
    private Integer weeksDuration;
    private List<Long> capacityIds;

    public void validate() {
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("Name is required");
        if (name.length() > 50)
            throw new IllegalArgumentException("Name max length is 50");
        if (description == null || description.isBlank())
            throw new IllegalArgumentException("Description is required");
        if (description.length() > 90)
            throw new IllegalArgumentException("Description max length is 90");
        if (startDate == null)
            throw new IllegalArgumentException("Start date is required");
        if (weeksDuration == null || weeksDuration <= 0)
            throw new IllegalArgumentException("Duration must be positive");
        if (capacityIds == null || capacityIds.isEmpty())
            throw new IllegalArgumentException("Minimum 1 capability required");
        if (capacityIds.size() > 4)
            throw new IllegalArgumentException("Maximum 4 capabilities allowed");
        if (capacityIds.stream().distinct().count() != capacityIds.size())
            throw new IllegalArgumentException("Duplicate capabilities not allowed");
    }

    public LocalDate getEndDate(){
        return startDate != null && weeksDuration != null ?
                startDate.plusWeeks(weeksDuration)
                : null;
    }

    public boolean overlaps(Bootcamp other){
        if (this.startDate == null || other.startDate == null) return false;
        LocalDate thisEnd = this.getEndDate();
        LocalDate otherEnd = this.getEndDate();
        return !this.startDate.isAfter(otherEnd) && !other.startDate.isAfter(thisEnd);
    }


}
