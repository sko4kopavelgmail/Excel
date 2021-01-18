package com.vorstu.excel.repository;

import com.vorstu.excel.model.GroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupRepo extends JpaRepository<GroupEntity, Long> {

    GroupEntity findByName(String s);

}
