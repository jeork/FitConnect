package com.mysite.sbb.workout_tab.workout;

import com.mysite.sbb.workout_tab.routine.Routine;
import com.mysite.sbb.workout_tab.workoutSet.WorkoutSet;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
public class Workout {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long workoutNum;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "routine_id") // 루틴 ID로 참조
    private Routine routine;

    @NotNull
    @Column
    private String workout_name;


    @OneToMany(mappedBy = "workout", cascade = CascadeType.ALL, orphanRemoval = true) // workout_id로 WorkoutSet 참조
    private List<WorkoutSet> workoutSet;

    public Workout() {
        this.workoutSet = new ArrayList<>();
    }

    public Workout(Routine routine, String workout_name) {
        this();
        this.routine = routine;
        this.workout_name = workout_name;

    }
}
