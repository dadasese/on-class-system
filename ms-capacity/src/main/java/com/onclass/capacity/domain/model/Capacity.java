package com.onclass.capacity.domain.model;
import com.onclass.capacity.domain.exception.InvalidCapacityException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class Capacity {

    private Long id;
    private String name;
    private String description;
    private List<Long> technologyIds;

    public void validate() {
        if (name == null || name.isBlank()) {
            throw new InvalidCapacityException("Name is required");
        }
        if (name.length() > 50) {
            throw new InvalidCapacityException("Name max length is 50");
        }
        if (description == null || description.isBlank()) {
            throw new InvalidCapacityException("Description is required");
        }
        if (description.length() > 90) {
            throw new InvalidCapacityException("Description max length is 90");
        }
        if (technologyIds == null || technologyIds.size() < 3) {
            throw new InvalidCapacityException("Minimum 3 technologies required");
        }
        if (technologyIds.size() > 20) {
            throw new InvalidCapacityException("Maximum 20 technologies allowed");
        }
        if (technologyIds.stream().distinct().count() != technologyIds.size()) {
            throw new InvalidCapacityException("Duplicate technologies not allowed");
        }
    }
}
