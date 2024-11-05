package com.mysite.sbb.workout_tab.workout;

import com.mysite.sbb.workout_tab.workoutSet.WorkoutSetUpdateDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class WorkoutUpdateDto {
    private Long id;    // 운동 id
    private String workout_name;    // 운동 이름
    private List<WorkoutSetUpdateDto> workoutSet;   // 세트 목록


    public WorkoutUpdateDto() {
    }

    public WorkoutUpdateDto(Long id, String workout_name, List<WorkoutSetUpdateDto> workoutSet) {
        this.id = id;
        this.workout_name = workout_name;
        this.workoutSet = workoutSet;
    }
}
