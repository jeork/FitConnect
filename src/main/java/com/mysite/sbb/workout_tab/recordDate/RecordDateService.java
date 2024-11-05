package com.mysite.sbb.workout_tab.recordDate;

import com.mysite.sbb.user.SiteUser;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class RecordDateService {

  private final RecordDateRepository recordDateRepository;




}
