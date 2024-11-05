package com.mysite.sbb.user;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<SiteUser, Long> {
    Optional<SiteUser> findByUsername(String username);
    
    @Query("select distinct u from SiteUser u where u.username like %:kw%")
    Page<SiteUser> findByUsernameKeyword(@Param("kw") String kw, Pageable pageable);
}

