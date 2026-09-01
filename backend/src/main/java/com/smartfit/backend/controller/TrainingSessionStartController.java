package com.smartfit.backend.controller;

import com.smartfit.backend.common.Result;
import com.smartfit.backend.dto.TrainingSessionStartRequest;
import com.smartfit.backend.service.TrainingService;
import com.smartfit.backend.vo.TrainingSessionStartVO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/users/{userId}/training-sessions")
public class TrainingSessionStartController {

    private final TrainingService trainingService;


    public TrainingSessionStartController(
            TrainingService trainingService
    ) {

        this.trainingService =
                trainingService;
    }


    /*
     * 用户确认动作之后，
     * 正式开始一次训练。
     *
     * POST
     * /api/users/1/training-sessions/start
     */
    @PostMapping("/start")
    public Result<TrainingSessionStartVO> startTraining(

            @PathVariable("userId")
            Long userId,

            @Valid
            @RequestBody
            TrainingSessionStartRequest request
    ) {

        TrainingSessionStartVO result =
                trainingService
                        .startPlannedExerciseSession(
                                userId,
                                request
                        );


        return Result.success(
                result
        );
    }
}