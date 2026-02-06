package com.onclass.bootcamp.domain.saga;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Getter
@Setter
public class SagaContext {

    private final Map<String, Object> data = new ConcurrentHashMap<>();
    private final List<String> completedSteps = new CopyOnWriteArrayList<>();
    private final List<String> compensatedSteps = new CopyOnWriteArrayList<>();

    public <T> void put(String key, T value) {
        data.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key, Class<T> type) {
        return (T) data.get(key);
    }

    public void markCompleted(String stepName) {
        completedSteps.add(stepName);
    }

    public void markCompensated(String stepName) {
        compensatedSteps.add(stepName);
    }

    public List<String> getCompletedStepsReversed() {
        List<String> reversed = new ArrayList<>(completedSteps);
        Collections.reverse(reversed);
        return reversed;
    }
}