package com.mysite.sbb.workout_tab.workout;

import com.mysite.sbb.workout_tab.routine.Routine;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkoutRepository extends JpaRepository<Workout, Long> {
  List<Workout> findByRoutine(Routine routine);

  void deleteByRoutine(Routine routine);
}
