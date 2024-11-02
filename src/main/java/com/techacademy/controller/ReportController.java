package com.techacademy.controller;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
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
import com.techacademy.entity.Report;
import com.techacademy.service.ReportService;

@Controller
@RequestMapping("reports")
public class ReportController {

    private final ReportService reportService;

    @Autowired
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    // 日報一覧画面
    @GetMapping(value = "/")
    public String list(Model model) {

        model.addAttribute("listSize", reportService.findAll().size());
        model.addAttribute("reportList", reportService.findAll());

        return "reports/list";
    }

    // 日報詳細画面
    @GetMapping(value = "/{id}/")
    public String detail(@PathVariable Integer Id, LocalDate Report_date, Model model) {

        model.addAttribute("report", reportService.findByIdAndReportdate(Id, Report_date));
        return "reports/detail";
    }

    // 日報新規登録画面
    @GetMapping(value = "/add")
    public String create(@ModelAttribute Report report) {

        return "reports/new";
    }

    // 日報新規登録処理
    @PostMapping(value = "/add")
    public String add(@Validated Report report, BindingResult res, Model model) {
        if(res.hasErrors()) {
            return create(report);
        }

        try {
            ErrorKinds result = reportService.save(report);

            if (ErrorMessage.contains(result)) {
                model.addAttribute(ErrorMessage.getErrorName(result), ErrorMessage.getErrorValue(result));
                return create(report);
            }

        } catch (DataIntegrityViolationException e) {
            model.addAttribute(ErrorMessage.getErrorName(ErrorKinds.DUPLICATE_EXCEPTION_ERROR),
                    ErrorMessage.getErrorValue(ErrorKinds.DUPLICATE_EXCEPTION_ERROR));
            return create(report);
        }

        return "redirect:/reports";
    }

    // 日報削除処理
    @PostMapping(value = "/{id}/delete")
    public String delete(@PathVariable Integer Id, LocalDate report_date, Model model) {

        ErrorKinds result = reportService.delete(Id, report_date);

        if (ErrorMessage.contains(result)) {
            model.addAttribute("report", reportService.findByIdAndReportdate(Id,report_date));
            return detail(Id, report_date, model);
        }

        return "redirect:/reports";
    }

    //日報更新画面

    @GetMapping(value= "/{id}/update/")
    public String getReport(@PathVariable Integer Id, LocalDate report_date, Model model, Report report) {
        if(Id != null) {
        model.addAttribute("report", reportService.findByIdAndReportdate(Id,report_date));
    }else {
        model.addAttribute("report" , report);
    }
        return "reports/update";
    }


    //従業員更新処理（追加）
    @PostMapping(value = "/{id}/update/")

    public String postReport(@Validated Report report, BindingResult res, Model model) {


        if (res.hasErrors()) {
            return getReport(null, null, model, report);
        }

        try {
            ErrorKinds result = reportService.update(report);

            if (ErrorMessage.contains(result)) {
                model.addAttribute(ErrorMessage.getErrorName(result), ErrorMessage.getErrorValue(result));
                return getReport(null, null, model, report);
            }

        } catch (DataIntegrityViolationException e) {
            model.addAttribute(ErrorMessage.getErrorName(ErrorKinds.DUPLICATE_EXCEPTION_ERROR),
                    ErrorMessage.getErrorValue(ErrorKinds.DUPLICATE_EXCEPTION_ERROR));
            return getReport(null, null, model, report);
        }

        return "redirect:/report";
    }
}
