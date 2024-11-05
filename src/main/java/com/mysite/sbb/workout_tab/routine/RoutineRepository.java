package com.mysite.sbb.workout_tab.routine;

import com.mysite.sbb.user.SiteUser;
import java.time.LocalDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RoutineRepository extends JpaRepository<Routine, Long> {
//    List<Routine> findBySiteUser(SiteUser siteUser);
    @Query(value = "select routine" +
            " from Routine routine" +
            " where routine.siteUser.username = :userName")
    List<Routine> findRoutinesBySiteUserUsername(@Param("userName") String userName);

}
