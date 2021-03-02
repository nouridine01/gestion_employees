package com.example.demo.dao;

import com.example.demo.domain.Competence;
import com.example.demo.domain.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CompetenceRepository extends JpaRepository<Competence,Long> {
    @Query("select c from Competence c where c.nom like :x")
    public Page<Competence> chercher(@Param("x") String mc, Pageable pageable);
}
