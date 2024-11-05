package com.mysite.sbb.workout_tab.workout;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class WorkoutService {
    private final WorkoutRepository workoutRepository;

    public Workout save(Workout workout) {
        return workoutRepository.save(workout);
    }
}
