package com.mysite.sbb.workout_tab.workoutSet;

import com.mysite.sbb.workout_tab.workout.Workout;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkoutSetRepository extends JpaRepository<WorkoutSet, Long> {
  void deleteByWorkoutIn(List<Workout> workouts);
}
