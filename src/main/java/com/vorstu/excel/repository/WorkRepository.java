package com.vorstu.excel.repository;

import com.vorstu.excel.model.WorkDayEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkRepository extends JpaRepository<WorkDayEntity, Long> {
}
