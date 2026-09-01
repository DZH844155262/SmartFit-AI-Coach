package com.smartfit.backend.controller;

import com.smartfit.backend.common.Result;
import com.smartfit.backend.dto.TrainingSetAppendRequest;
import com.smartfit.backend.service.TrainingService;
import com.smartfit.backend.vo.TrainingSetRecordVO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/training-exercises")
public class TrainingSetController {

    private final TrainingService trainingService;


    public TrainingSetController(
            TrainingService trainingService
    ) {

        this.trainingService =
                trainingService;
    }


    /*
     * 用户完成一组以后调用。
     *
     * POST
     * /api/training-exercises/11/sets
     */
    @PostMapping("/{trainingExerciseId}/sets")
    public Result<TrainingSetRecordVO> appendSet(

            @PathVariable("trainingExerciseId")
            Long trainingExerciseId,

            @Valid
            @RequestBody
            TrainingSetAppendRequest request
    ) {

        TrainingSetRecordVO result =
                trainingService.appendSet(
                        trainingExerciseId,
                        request
                );


        return Result.success(
                result
        );
    }
}