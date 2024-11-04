package com.techacademy.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.techacademy.constants.ErrorKinds;
import com.techacademy.entity.Employee;
import com.techacademy.entity.Report;
import com.techacademy.repository.ReportRepository;

@Service
public class ReportService {
    private final ReportRepository reportRepository;

    @Autowired
    public ReportService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;

    }

    // 1件を検索
    public Report findById(Integer id) {
        // findByIdで検索
        Optional<Report> option = reportRepository.findById(id);
        // 取得できなかった場合はnullを返す
        Report report = option.orElse(null);
        return report;
    }

    public List<Report> findByEmployee(Employee employee) {
        // findByIdで検索
        List<Report> option = reportRepository.findByEmployee(employee);

        return option;
    }

    // 一覧表示
    public List<Report> findAll() {
        return reportRepository.findAll();
    }

    // 1件を検索
    public boolean existsByEmployeeAndReportDate(Employee employee, LocalDate reportDate) {
        // findByIdAndReportdateで検索
        boolean isExists = reportRepository.existsByEmployeeAndReportDate(employee, reportDate);

        return isExists;
    }

    // 日付重複チェック
    @Transactional
    public ErrorKinds save(Report report) {

        if (existsByEmployeeAndReportDate(report.getEmployee(), report.getReportDate())) {

            return ErrorKinds.DATECHECK_ERROR;
        }

        report.setDeleteFlg(false);

        LocalDateTime now = LocalDateTime.now();
        report.setCreatedAt(now);
        report.setUpdatedAt(now);

        reportRepository.save(report);
        return ErrorKinds.SUCCESS;

    }

    // 削除処理
    @Transactional
    public ErrorKinds delete(Integer id) {

        Report report = findById(id);

        LocalDateTime now = LocalDateTime.now();
        report.setUpdatedAt(now);
        report.setDeleteFlg(true);

        return ErrorKinds.SUCCESS;
    }

    // 更新処理
    @Transactional
    public ErrorKinds update(Report report) {
        Report oldReport = findById(report.getId());

        if (reportRepository.existsByEmployeeAndReportDateAndIdNot(report.getEmployee(), report.getReportDate(), report.getId()) ) {
            return ErrorKinds.DATECHECK_ERROR;
        }
        report.setDeleteFlg(oldReport.isDeleteFlg());
        report.setCreatedAt(oldReport.getCreatedAt());

        LocalDateTime now = LocalDateTime.now();

        report.setUpdatedAt(now);

        reportRepository.save(report);
        return ErrorKinds.SUCCESS;
    }

}
