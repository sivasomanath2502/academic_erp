package com.academic.erp.backend.repository;

import com.academic.erp.backend.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    Optional<Student> findTopByJoinYearAndSeqNoBetweenAndRollNumberStartingWithOrderBySeqNoDesc(
            Integer joinYear,
            Integer start,
            Integer end,
            String rollBase
    );
}
