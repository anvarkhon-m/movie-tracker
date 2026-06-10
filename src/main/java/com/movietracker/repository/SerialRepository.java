package com.movietracker.repository;

import com.movietracker.domain.Serial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface SerialRepository extends JpaRepository<Serial, Long>, JpaSpecificationExecutor<Serial> {

    Optional<Serial> findByIdAndUserId(Long id, Long userId);
}
