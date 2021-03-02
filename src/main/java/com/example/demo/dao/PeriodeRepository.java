package com.example.demo.dao;

import com.example.demo.domain.Periode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PeriodeRepository extends JpaRepository<Periode,Long> {
}
