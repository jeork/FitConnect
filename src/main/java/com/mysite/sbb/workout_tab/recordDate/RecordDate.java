package com.mysite.sbb.workout_tab.recordDate;

import com.mysite.sbb.user.SiteUser;
import com.mysite.sbb.workout_tab.routine.Routine;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Data
@Entity
public class RecordDate {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long recordDateNum;


  @Column(unique = true)
  private LocalDate date;

  @Column(name = "workout_completed")
  private Boolean workoutCompleted;

  @ManyToOne
  @OnDelete(action = OnDeleteAction.SET_NULL)
  @JoinColumn(name = "routine_id", referencedColumnName = "routineNum")
  private Routine routine;

  @ManyToOne
  @JoinColumn(name="username", referencedColumnName = "username")
  @OnDelete(action = OnDeleteAction.CASCADE)
  private SiteUser siteUser;


  public RecordDate(){
  }

  public RecordDate(LocalDate date, Routine routine, Boolean workoutCompleted) {
    this.date = date;
    this.routine = routine;
    this.workoutCompleted = workoutCompleted;
  }

}
