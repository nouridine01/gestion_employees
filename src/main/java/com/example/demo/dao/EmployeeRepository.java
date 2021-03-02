package com.example.demo.dao;

import com.example.demo.domain.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EmployeeRepository extends JpaRepository<Employee,Long> {
    @Query("select emp from Employee emp where emp.nom like :x")
    public Page<Employee> chercher(@Param("x") String mc, Pageable pageable);
}
