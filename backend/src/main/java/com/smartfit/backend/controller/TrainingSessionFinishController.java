package com.smartfit.backend.controller;

import com.smartfit.backend.common.Result;
import com.smartfit.backend.service.TrainingService;
import com.smartfit.backend.vo.TrainingSessionSummaryVO;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/training-sessions")
public class TrainingSessionFinishController {

    private final TrainingService trainingService;


    public TrainingSessionFinishController(
            TrainingService trainingService
    ) {

        this.trainingService =
                trainingService;
    }


    /*
     * 结束一场正在进行的训练
     *
     * POST
     * /api/training-sessions/7/finish
     */
    @PostMapping("/{sessionId}/finish")
    public Result<TrainingSessionSummaryVO> finishSession(

            @PathVariable("sessionId")
            Long sessionId
    ) {

        TrainingSessionSummaryVO result =
                trainingService.finishSession(
                        sessionId
                );


        return Result.success(
                result
        );
    }
}