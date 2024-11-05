package com.mysite.sbb.workout_tab.routine;

import com.mysite.sbb.workout_tab.workout.WorkoutUpdateDto;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoutineUpdateDto {

  private Long id;
  private String date;
  private String routine_name;
  private List<WorkoutUpdateDto> workouts;
  private boolean workout_completed;
  private String username;

  public RoutineUpdateDto() {
  }

  public RoutineUpdateDto(Long id, String date, String routine_name,
      List<WorkoutUpdateDto> workouts, boolean workout_completed, String username) {
    this.id = id;
    this.date = date;
    this.routine_name = routine_name;
    this.workouts = workouts;
    this.workout_completed = workout_completed;
    this.username = username;
  }
}
