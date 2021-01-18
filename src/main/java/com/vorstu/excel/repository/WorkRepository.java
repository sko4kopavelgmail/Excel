package com.vorstu.excel.repository;

import com.vorstu.excel.model.GroupEntity;
import com.vorstu.excel.model.WorkDayEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WorkRepository extends JpaRepository<WorkDayEntity, Long> {

    Optional<WorkDayEntity> findByGroupAndName(GroupEntity g, String name);

}
