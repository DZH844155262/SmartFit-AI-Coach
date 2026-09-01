package com.smartfit.backend.controller;

import com.smartfit.backend.common.Result;
import com.smartfit.backend.service.TrainingPlanExerciseService;
import com.smartfit.backend.vo.TrainingPlanExerciseVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/training-plan-days")
public class TrainingPlanExerciseController {

    private final TrainingPlanExerciseService trainingPlanExerciseService;


    public TrainingPlanExerciseController(
            TrainingPlanExerciseService trainingPlanExerciseService
    ) {

        this.trainingPlanExerciseService =
                trainingPlanExerciseService;
    }


    /*
     * 用户在视觉识别结果中确认某个动作后，
     * 查询今天这个动作的训练目标。
     *
     * 示例：
     *
     * GET
     * /api/training-plan-days/1/exercises/1
     */
    @GetMapping("/{planDayId}/exercises/{exerciseId}")
    public Result<TrainingPlanExerciseVO> getExerciseTarget(

            @PathVariable("planDayId")
            Long planDayId,

            @PathVariable("exerciseId")
            Long exerciseId
    ) {

        TrainingPlanExerciseVO target =
                trainingPlanExerciseService
                        .getExerciseTarget(
                                planDayId,
                                exerciseId
                        );


        return Result.success(
                target
        );
    }
}