package com.smartfit.backend.service;

import com.smartfit.backend.exception.BusinessException;
import com.smartfit.backend.mapper.TrainingPlanExerciseMapper;
import com.smartfit.backend.vo.TrainingPlanExerciseVO;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;


@Service
public class TrainingPlanExerciseService {

    private final TrainingPlanExerciseMapper trainingPlanExerciseMapper;


    public TrainingPlanExerciseService(
            TrainingPlanExerciseMapper trainingPlanExerciseMapper
    ) {

        this.trainingPlanExerciseMapper =
                trainingPlanExerciseMapper;
    }


    /*
     * 根据：
     *
     * planDayId
     * +
     * exerciseId
     *
     * 查询用户确认动作后的本次训练目标。
     */
    public TrainingPlanExerciseVO getExerciseTarget(
            Long planDayId,
            Long exerciseId
    ) {

        TrainingPlanExerciseVO target =
                trainingPlanExerciseMapper
                        .findByPlanDayIdAndExerciseId(
                                planDayId,
                                exerciseId
                        );


        if (target == null) {

            throw new BusinessException(
                    HttpStatus.NOT_FOUND,
                    "当前训练计划中没有这个动作"
            );
        }


        return target;
    }
}