package com.mysite.sbb.workout_tab.workoutSet;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WorkoutSetUpdateDto {
    private Long id;

    @NotNull(message = "중량 입력은 필수입니다.")
    @Positive(message = "중량은 음수이면 안됩니다.")
    private int weight;
    
    @NotNull(message = "횟수 입력은 필수입니다.")
    @Positive(message = "횟수는 0보다 커야합니다.")
    private int reps;

    public WorkoutSetUpdateDto(){
    }

    public WorkoutSetUpdateDto(Long id, int weight, int reps){
        this.id = id;
        this.weight = weight;
        this.reps = reps;
    }
}
