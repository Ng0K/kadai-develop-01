package com.techacademy.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.techacademy.entity.Employee;
import com.techacademy.entity.Report;

public interface ReportRepository extends JpaRepository<Report, Integer>{
//    @Query(value = "SELECT * FROM Report WHERE id ?1 AND report_date ?2", nativeQuery=true)
    public boolean existsByEmployeeAndReportDate(Employee employee,LocalDate reportDate);
    public boolean existsByEmployeeAndReportDateAndIdNot(Employee employee,LocalDate reportDate,Integer id);
    List<Report> findByEmployee(Employee employee);
}


