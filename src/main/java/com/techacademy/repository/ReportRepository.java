package com.techacademy.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.techacademy.entity.Report;

public interface ReportRepository extends JpaRepository<Report, Integer> {
    Optional<Report>findByIdAndReportdate(int Id, LocalDate report_date);

}
