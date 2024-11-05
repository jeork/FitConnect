package com.mysite.sbb.workout_tab.workoutSet;

import com.mysite.sbb.workout_tab.workout.Workout;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Entity
public class WorkoutSet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long workoutSetNum;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "workout_id")
    private Workout workout;

    @NotNull
    @Column
    private Integer weight;

    @NotNull
    @Column
    private Integer reps;

    public WorkoutSet(){}

    public WorkoutSet(Workout workout, Integer weight, Integer reps) {
        this.workout = workout;
        this.weight = weight;
        this.reps = reps;
    }

}
