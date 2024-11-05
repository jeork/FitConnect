package com.mysite.sbb.workout_tab.workoutSet;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class WorkoutSetService {
    private final WorkoutSetRepository workoutSetRepository;

    public WorkoutSet save(WorkoutSet workoutSet) {
        return workoutSetRepository.save(workoutSet);
    }
}
