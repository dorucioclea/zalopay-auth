package com.zalopay.auth.repository;

import com.zalopay.auth.model.Log;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LogRepository extends JpaRepository<Log, Long> {

    Optional<Log> findById(Long logId);

    Page<Log> findByCreatedBy(Long userId, Pageable pageable);

    long countByCreatedBy(Long userId);

    List<Log> findByIdIn(List<Long> logId);

    List<Log> findByIdIn(List<Long> logIds, Sort sort);
}
