package com.techacademy.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.techacademy.constants.ErrorKinds;
import com.techacademy.constants.ErrorMessage;
import com.techacademy.entity.Employee;
import com.techacademy.entity.Report;
import com.techacademy.service.ReportService;
import com.techacademy.service.UserDetail;

@Controller
@RequestMapping("reports")
public class ReportController {

    private final ReportService reportService;

    @Autowired
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    // 日報一覧画面
    @GetMapping
    public String list(@AuthenticationPrincipal UserDetail userDetail, Model model) {
        Employee employee = userDetail.getEmployee();
        List<Report> reports;
        if(employee.getRole() == Employee.Role.ADMIN) {
            reports = reportService.findAll();
        }else {
            reports = reportService.findByEmployee(employee);
        }

        model.addAttribute("listSize", reports.size());
        model.addAttribute("reportList", reports);



        return "reports/list";
    }

    // 日報詳細画面
    @GetMapping(value = "/{id}/")
    public String detail(@PathVariable Integer id, Model model) {

        model.addAttribute("report", reportService.findById(id));
        return "reports/detail";
    }

    // 日報新規登録画面
    @GetMapping(value = "/add")
    public String create(@ModelAttribute Report report, @AuthenticationPrincipal UserDetail userDetail, Model model) {

        model.addAttribute("employee", userDetail.getEmployee());

        return "reports/new";
    }

    // 日報新規登録処理
    @PostMapping(value = "/add")
    public String add(@Validated Report report, BindingResult res, Model model,
            @AuthenticationPrincipal UserDetail userDetail) {
        report.setEmployee(userDetail.getEmployee());

        if (res.hasErrors()) {
            return create(report, userDetail, model);
        }
        try {
            ErrorKinds result = reportService.save(report);

            if (ErrorMessage.contains(result)) {
                model.addAttribute(ErrorMessage.getErrorName(result), ErrorMessage.getErrorValue(result));
                return create(report, userDetail, model);
            }

        } catch (DataIntegrityViolationException e) {
            model.addAttribute(ErrorMessage.getErrorName(ErrorKinds.DUPLICATE_EXCEPTION_ERROR),
                    ErrorMessage.getErrorValue(ErrorKinds.DUPLICATE_EXCEPTION_ERROR));
            return create(report, userDetail, model);
        }

        return "redirect:/reports";
    }

    // 日報削除処理
    @PostMapping(value = "/{id}/delete")
    public String delete(@PathVariable Integer id, Model model) {

        reportService.delete(id);

        return "redirect:/reports";
    }

    // 日報更新画面

    @GetMapping(value = "/{id}/update/")
    public String getReport(@PathVariable Integer id, Model model, Report report,
            @AuthenticationPrincipal UserDetail userDetail) {

        model.addAttribute("employee", userDetail.getEmployee());

        if (id != null) {
            model.addAttribute("report", reportService.findById(id));

        } else {
            model.addAttribute("report", report);
        }
        return "reports/update";
    }

    // 日報更新処理
    @PostMapping(value = "/{id}/update/")

    public String postReport(@Validated Report report, BindingResult res, Model model,
            @AuthenticationPrincipal UserDetail userDetail) {
        report.setEmployee(userDetail.getEmployee());

        if (res.hasErrors()) {
            return getReport(null, model, report, userDetail);
        }

        try {
            ErrorKinds result = reportService.update(report);

            if (ErrorMessage.contains(result)) {
                model.addAttribute(ErrorMessage.getErrorName(result), ErrorMessage.getErrorValue(result));
                return getReport(null, model, report, userDetail);
            }

        } catch (DataIntegrityViolationException e) {
            model.addAttribute(ErrorMessage.getErrorName(ErrorKinds.DUPLICATE_EXCEPTION_ERROR),
                    ErrorMessage.getErrorValue(ErrorKinds.DUPLICATE_EXCEPTION_ERROR));
            return getReport(null, model, report, userDetail);
        }

        return "redirect:/reports";
    }
}
