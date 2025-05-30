package org.example.backend.domain.manage.repository;

import org.example.backend.domain.manage.entity.Manage;
import org.example.backend.domain.performance.entity.Performance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ManageRepository extends JpaRepository<Manage, Long> {
    List<Manage> findByManagerId(Long managerId);
}
