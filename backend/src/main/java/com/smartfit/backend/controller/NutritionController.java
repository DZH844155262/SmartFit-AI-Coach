package com.smartfit.backend.controller;

import com.smartfit.backend.common.Result;
import com.smartfit.backend.dto.NutritionMealCreateRequest;
import com.smartfit.backend.service.NutritionService;
import com.smartfit.backend.vo.NutritionMealCreateVO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.smartfit.backend.vo.DailyNutritionSummaryVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.smartfit.backend.vo.NutritionPeriodSummaryVO;
import com.smartfit.backend.dto.NutritionVisionMealCreateRequest;
import java.time.LocalDate;
import java.util.List;



@RestController
@RequestMapping("/api/users/{userId}/nutrition")
public class NutritionController {

    private final NutritionService nutritionService;


    public NutritionController(
            NutritionService nutritionService
    ) {

        this.nutritionService =
                nutritionService;
    }


    /*
     * 手动记录一顿饭
     *
     * POST
     * /api/users/1/nutrition/meals
     */
    @PostMapping("/meals")
    public Result<NutritionMealCreateVO> createMeal(

            @PathVariable("userId")
            Long userId,

            @Valid
            @RequestBody
            NutritionMealCreateRequest request
    ) {

        NutritionMealCreateVO result =
                nutritionService.createManualMeal(
                        userId,
                        request
                );


        return Result.success(
                result
        );
    }

    /*
     * Vision识别后，
     * 用户确认/修改重量并保存。
     *
     * POST
     * /api/users/1/nutrition/vision-meals
     */
    @PostMapping("/vision-meals")
    public Result<NutritionMealCreateVO> createVisionMeal(

            @PathVariable("userId")
            Long userId,

            @Valid
            @RequestBody
            NutritionVisionMealCreateRequest request
    ) {

        NutritionMealCreateVO result =
                nutritionService.createVisionMeal(
                        userId,
                        request
                );


        return Result.success(
                result
        );
    }
    @GetMapping("/summary")
    public Result<List<DailyNutritionSummaryVO>> getSummary(

            @PathVariable("userId")
            Long userId,

            @RequestParam("startDate")
            LocalDate startDate,

            @RequestParam("endDate")
            LocalDate endDate
    ) {

        List<DailyNutritionSummaryVO> result =
                nutritionService.getNutritionSummary(
                        userId,
                        startDate,
                        endDate
                );


        return Result.success(
                result
        );
    }
    @GetMapping("/period-summary")
    public Result<NutritionPeriodSummaryVO> getPeriodSummary(

            @PathVariable("userId")
            Long userId,

            @RequestParam("startDate")
            LocalDate startDate,

            @RequestParam("endDate")
            LocalDate endDate
    ) {

        NutritionPeriodSummaryVO result =
                nutritionService
                        .getNutritionPeriodSummary(
                                userId,
                                startDate,
                                endDate
                        );


        return Result.success(
                result
        );
    }
}