package com.mysite.sbb.workout_tab;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mysite.sbb.user.SiteUser;
import com.mysite.sbb.user.UserRepository;
import com.mysite.sbb.user.UserService;
import com.mysite.sbb.workout_tab.recordDate.RecordDate;
import com.mysite.sbb.workout_tab.recordDate.RecordDateRepository;
import com.mysite.sbb.workout_tab.recordDate.RecordDateService;
import com.mysite.sbb.workout_tab.routine.Routine;
import com.mysite.sbb.workout_tab.routine.RoutineRepository;
import com.mysite.sbb.workout_tab.routine.RoutineService;
import com.mysite.sbb.workout_tab.routine.RoutineUpdateDto;
import com.mysite.sbb.workout_tab.workout.Workout;
import com.mysite.sbb.workout_tab.workout.WorkoutRepository;
import com.mysite.sbb.workout_tab.workout.WorkoutService;
import com.mysite.sbb.workout_tab.workout.WorkoutUpdateDto;
import com.mysite.sbb.workout_tab.workoutSet.WorkoutSet;
import com.mysite.sbb.workout_tab.workoutSet.WorkoutSetRepository;
import com.mysite.sbb.workout_tab.workoutSet.WorkoutSetService;
import com.mysite.sbb.workout_tab.workoutSet.WorkoutSetUpdateDto;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/workout")
@Controller
@RequiredArgsConstructor
public class WorkoutController {

  private final UserService userService;
  private final RoutineService routineService;
  private final WorkoutService workoutService;
  private final WorkoutSetService workoutSetService;
  private final RoutineRepository routineRepository;
  private final WorkoutRepository workoutRepository;
  private final WorkoutSetRepository workoutSetRepository;
  private final RecordDateRepository recordDateRepository;
  private final RecordDateService recordDateService;


  // 운동 가이드
  @GetMapping("/guide")
  public String Guide() {
    return "workout_guide";
  }

  @GetMapping("/guide/chest")
  public String Chest() {
    return "workout_content/chest";
  }

  @GetMapping("/guide/triceps")
  public String Triceps() {
    return "workout_content/triceps";
  }

  @GetMapping("/guide/back")
  public String Back() {
    return "workout_content/back";
  }

  @GetMapping("/guide/biceps")
  public String Biceps() {
    return "workout_content/biceps";
  }

  @GetMapping("/guide/leg")
  public String Leg() {
    return "workout_content/leg";
  }

  @GetMapping("/guide/shoulder")
  public String Shoulder() {
    return "workout_content/shoulder";
  }

  @GetMapping("/guide/abs")
  public String Abs() {
    return "workout_content/abs";
  }

  // 루틴 만들기
  @PreAuthorize("isAuthenticated()")
  @GetMapping("/make_routine")
  public String Make_routine(Model model) {
    model.addAttribute("routineUpdateDto", new RoutineUpdateDto());
    return "make_routine";
  }

  @PreAuthorize("isAuthenticated()")
  @PostMapping("/make_routine")
  public String makeRoutinePost(Model model, RoutineUpdateDto routineUpdateDto,
      Principal principal) {
    SiteUser siteUser = userService.findByUsername(principal.getName());

    // 루틴 생성
    Routine routine = new Routine();
    routine.setSiteUser(siteUser);
    routine.setRoutine_name(routineUpdateDto.getRoutine_name());

    // 루틴 저장
    routineService.save(routine);

    // 운동과 세트 저장
    for (WorkoutUpdateDto workoutUpdateDto : routineUpdateDto.getWorkouts()) {
      Workout workout = new Workout();
      workout.setRoutine(routine);
      workout.setWorkout_name(workoutUpdateDto.getWorkout_name());

      for (WorkoutSetUpdateDto setUpdateDto : workoutUpdateDto.getWorkoutSet()) {
        WorkoutSet workoutSet = new WorkoutSet();
        workoutSet.setWorkout(workout);
        workoutSet.setWeight(setUpdateDto.getWeight());
        workoutSet.setReps(setUpdateDto.getReps());

        workout.getWorkoutSet().add(workoutSet);
      }

      workoutService.save(workout);
    }

    model.addAttribute("message", "루틴이 저장되었습니다");
    return "redirect:my_routine"; // 리다이렉션
  }


  @Transactional
  @PreAuthorize("isAuthenticated()")
  @PostMapping("/record_routine")
  public ResponseEntity<String> recordRoutine(@RequestBody RoutineUpdateDto routineUpdateDto,
      Principal principal) {
    try {
      // 사용자 정보 가져오기
      SiteUser siteUser = userService.findByUsername(principal.getName());

      Routine routine;
      if (routineUpdateDto.getId() != null) {
        // 기존 루틴 가져오기
        routine = routineService.findById(routineUpdateDto.getId())
            .orElseThrow(() -> new RuntimeException("루틴을 찾을 수 없습니다."));

        // 기존 운동 세트와 운동 삭제
        workoutSetRepository.deleteByWorkoutIn(workoutRepository.findByRoutine(routine));
        workoutRepository.deleteByRoutine(routine);

      } else {
        // 새로운 루틴 생성
        routine = new Routine();
        routine.setSiteUser(siteUser);
      }

      // 루틴 정보 업데이트
      routine.setSiteUser(siteUser);
      routine.setRoutine_name(routineUpdateDto.getRoutine_name());
      routine.setDate(routineUpdateDto.getDate());

      // 루틴 저장
      routineService.save(routine);

      // 운동 및 세트 저장
      for (WorkoutUpdateDto workoutUpdateDto : routineUpdateDto.getWorkouts()) {
        Workout workout = workoutUpdateDto.getId() != null
            ? workoutRepository.findById(workoutUpdateDto.getId()).orElse(new Workout())
            : new Workout();

        workout.setWorkout_name(workoutUpdateDto.getWorkout_name());
        workout.setRoutine(routine);

        // 운동 저장
        workoutRepository.save(workout);

        // 세트 저장
        for (WorkoutSetUpdateDto workoutSetUpdateDto : workoutUpdateDto.getWorkoutSet()) {
          WorkoutSet workoutSet = workoutSetUpdateDto.getId() != null
              ? workoutSetRepository.findById(workoutSetUpdateDto.getId()).orElse(new WorkoutSet())
              : new WorkoutSet();

          workoutSet.setWorkout(workout);
          workoutSet.setWeight(workoutSetUpdateDto.getWeight());
          workoutSet.setReps(workoutSetUpdateDto.getReps());

          // 세트 저장
          workoutSetRepository.save(workoutSet);
        }
      }

      // 날짜 데이터 저장

      if (recordDateRepository.findByDate(LocalDate.parse(routineUpdateDto.getDate()))
          .isPresent()) {
        recordDateRepository.updateDate(routine.getRoutineNum(),
            LocalDate.parse(routineUpdateDto.getDate()));
      } else {

        RecordDate recordDate = new RecordDate();
        recordDate.setDate(LocalDate.parse(routineUpdateDto.getDate()));
        recordDate.setWorkoutCompleted(true);
        recordDate.setRoutine(routine);
        recordDate.setSiteUser(siteUser);
        recordDateRepository.save(recordDate);
      }

      return ResponseEntity.ok("운동 기록이 저장되었습니다."); // 성공 응답
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("운동 기록 저장 중 오류 발생: " + e.getMessage());
    }
  }

  // 나의 루틴
  @PreAuthorize("isAuthenticated()")
  @GetMapping("/my_routine")
  public String myRoutine(Model model, Principal principal) {
    // 로그인한 사용자에 대한 루틴 목록 가져오기
    List<Routine> routines = /*routineService.findByUser(principal.getName())*/routineRepository.findRoutinesBySiteUserUsername(
        principal.getName());
    model.addAttribute("routines", routines);
    return "my_routine";
  }

  // 루틴 삭제
  @PreAuthorize("isAuthenticated()")
  @DeleteMapping("/delete/{id}")
  public ResponseEntity<String> deleteRoutine(@PathVariable("id") Long id, Principal principal) {
    Optional<Routine> optionalRoutine = this.routineRepository.findById(id);
    System.out.println(1);
    if (optionalRoutine.isPresent()) {
      System.out.println(2);
      Routine routine = optionalRoutine.get();

      // 현재 로그인한 사용자가 루틴의 소유자인지 확인
      if (!routine.getSiteUser().getUsername().equals(principal.getName())) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("삭제할 권한이 없습니다");
      }
      System.out.println("try");
      System.out.println("routineNum : " + routine.getRoutineNum());
      this.routineRepository.deleteById(routine.getRoutineNum());
      System.out.println("fail");
      return ResponseEntity.ok("루틴이 성공적으로 삭제되었습니다.");
    } else {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("루틴을 찾을 수 없습니다.");
    }
  }

  @PreAuthorize("isAuthenticated()")
  @GetMapping("/my_goal")
  public String myGoal(Model model, Principal principal) {

    // 사용자 정보 가져오기
    SiteUser siteUser;

    // 사용자의 루틴 리스트 가져오기
    List<Routine> routines = routineRepository.findRoutinesBySiteUserUsername(principal.getName());
    for (Routine routine : routines) {
      siteUser = userService.findByUsername(routine.getSiteUser().getUsername());
      routine.setSiteUser(siteUser);
      System.out.println("siteUser : " + siteUser);
    }

    // 사용자의 운동 완료 날짜 리스트 가져오기
    List<LocalDate> completedDates = recordDateRepository.selectDate(principal.getName());


    List<Object[]> completedDateRoutines = recordDateRepository.selectRoutine(principal.getName());

    // 모델에 추가
    model.addAttribute("routines", routines);
    model.addAttribute("completedDates", completedDates);
    model.addAttribute("completedDateRoutines", completedDateRoutines);

    if (routines.size() > 0) {
      return "my_goal";
    }else{
      return "redirect:my_routine"; // 리다이렉션
    }
  }

  @PreAuthorize("isAuthenticated()")
  @GetMapping("/routines/{id}")
  public String getRoutineById(@PathVariable Long id, Model model) {
    Optional<Routine> routine = routineService.findById(id);
    model.addAttribute("routine", routine);
    return "my_goal";
  }

//    @PostMapping("/api/save_routine")
//    public ResponseEntity<Void> saveRoutine(@RequestBody RoutineUpdateDto routineUpdateDto) {
//        routineService.saveRoutine(routineUpdateDto);
//        return ResponseEntity.ok().build();
//    }
}
