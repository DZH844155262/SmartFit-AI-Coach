package com.smartfit.backend.service;

import com.smartfit.backend.dto.TrainingPlanCreateRequest;
import com.smartfit.backend.dto.TrainingPlanDayRequest;
import com.smartfit.backend.dto.TrainingPlanExerciseRequest;
import com.smartfit.backend.entity.TrainingPlan;
import com.smartfit.backend.entity.TrainingPlanDay;
import com.smartfit.backend.entity.TrainingPlanExercise;
import com.smartfit.backend.exception.BusinessException;
import com.smartfit.backend.mapper.AppUserMapper;
import com.smartfit.backend.mapper.ExerciseMapper;
import com.smartfit.backend.mapper.TrainingPlanDayMapper;
import com.smartfit.backend.mapper.TrainingPlanExerciseMapper;
import com.smartfit.backend.mapper.TrainingPlanMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.smartfit.backend.vo.TrainingPlanDayVO;
import com.smartfit.backend.vo.TrainingPlanDetailVO;
import com.smartfit.backend.vo.TrainingPlanExerciseVO;

import java.util.List;
@Service
public class TrainingPlanService {

    private final AppUserMapper appUserMapper;
    private final ExerciseMapper exerciseMapper;
    private final TrainingPlanMapper trainingPlanMapper;
    private final TrainingPlanDayMapper trainingPlanDayMapper;
    private final TrainingPlanExerciseMapper trainingPlanExerciseMapper;


    public TrainingPlanService(
            AppUserMapper appUserMapper,
            ExerciseMapper exerciseMapper,
            TrainingPlanMapper trainingPlanMapper,
            TrainingPlanDayMapper trainingPlanDayMapper,
            TrainingPlanExerciseMapper trainingPlanExerciseMapper
    ) {
        this.appUserMapper = appUserMapper;
        this.exerciseMapper = exerciseMapper;
        this.trainingPlanMapper = trainingPlanMapper;
        this.trainingPlanDayMapper = trainingPlanDayMapper;
        this.trainingPlanExerciseMapper = trainingPlanExerciseMapper;
    }


    @Transactional
    public TrainingPlan createPlan(
            Long userId,
            TrainingPlanCreateRequest request
    ) {

        // 1. 用户必须存在
        if (appUserMapper.countById(userId) == 0) {
            throw new BusinessException(
                    HttpStatus.NOT_FOUND,
                    "用户不存在"
            );
        }


        // 2. 开始日期不能晚于结束日期
        if (request.getEndDate() != null
                && request.getStartDate()
                .isAfter(request.getEndDate())) {

            throw new BusinessException(
                    HttpStatus.BAD_REQUEST,
                    "计划开始日期不能晚于结束日期"
            );
        }


        // 3. 每周训练频率与训练日数量保持一致
        if (request.getWeeklyFrequency()
                != request.getDays().size()) {

            throw new BusinessException(
                    HttpStatus.BAD_REQUEST,
                    "每周训练频率必须与训练日数量一致"
            );
        }


        // 4. 在真正INSERT之前，先检查所有训练动作
        for (TrainingPlanDayRequest dayRequest
                : request.getDays()) {

            for (TrainingPlanExerciseRequest exerciseRequest
                    : dayRequest.getExercises()) {


                // 4.1 最低次数不能大于最高次数
                if (exerciseRequest.getTargetRepsMin()
                        > exerciseRequest.getTargetRepsMax()) {

                    throw new BusinessException(
                            HttpStatus.BAD_REQUEST,
                            "最低目标次数不能大于最高目标次数"
                    );
                }


                // 4.2 exerciseId必须真实存在
                if (exerciseMapper.countById(
                        exerciseRequest.getExerciseId()
                ) == 0) {

                    throw new BusinessException(
                            HttpStatus.NOT_FOUND,
                            "训练动作不存在，exerciseId="
                                    + exerciseRequest.getExerciseId()
                    );
                }
            }
        }


        // 5. 创建计划主体
        TrainingPlan trainingPlan =
                new TrainingPlan();

        trainingPlan.setUserId(userId);
        trainingPlan.setName(request.getName());
        trainingPlan.setGoal(request.getGoal());

        // 当前第一版创建出来就是ACTIVE
        trainingPlan.setStatus("ACTIVE");

        trainingPlan.setStartDate(
                request.getStartDate()
        );

        trainingPlan.setEndDate(
                request.getEndDate()
        );

        trainingPlan.setWeeklyFrequency(
                request.getWeeklyFrequency()
        );

        trainingPlanMapper.insert(trainingPlan);


        // 6. 保存每一个训练日
        int dayNumber = 1;

        for (TrainingPlanDayRequest dayRequest
                : request.getDays()) {

            TrainingPlanDay trainingPlanDay =
                    new TrainingPlanDay();

            trainingPlanDay.setPlanId(
                    trainingPlan.getId()
            );

            trainingPlanDay.setDayNumber(
                    dayNumber
            );

            trainingPlanDay.setTitle(
                    dayRequest.getTitle()
            );

            trainingPlanDay.setNotes(
                    dayRequest.getNotes()
            );

            trainingPlanDayMapper.insert(
                    trainingPlanDay
            );


            // 7. 保存这个训练日下面的动作
            int exerciseOrder = 1;

            for (TrainingPlanExerciseRequest exerciseRequest
                    : dayRequest.getExercises()) {

                TrainingPlanExercise planExercise =
                        new TrainingPlanExercise();

                planExercise.setPlanDayId(
                        trainingPlanDay.getId()
                );

                planExercise.setExerciseId(
                        exerciseRequest.getExerciseId()
                );

                planExercise.setExerciseOrder(
                        exerciseOrder
                );

                planExercise.setTargetSets(
                        exerciseRequest.getTargetSets()
                );

                planExercise.setTargetRepsMin(
                        exerciseRequest.getTargetRepsMin()
                );

                planExercise.setTargetRepsMax(
                        exerciseRequest.getTargetRepsMax()
                );

                planExercise.setTargetWeightKg(
                        exerciseRequest.getTargetWeightKg()
                );

                planExercise.setTargetRpe(
                        exerciseRequest.getTargetRpe()
                );

                planExercise.setNotes(
                        exerciseRequest.getNotes()
                );

                trainingPlanExerciseMapper.insert(
                        planExercise
                );

                exerciseOrder++;
            }

            dayNumber++;
        }


        return trainingPlan;
    }
    public TrainingPlanDetailVO getPlanDetail(Long planId) {

        // 1. 查询Plan主体
        TrainingPlan plan =
                trainingPlanMapper.findById(planId);

        if (plan == null) {
            throw new BusinessException(
                    HttpStatus.NOT_FOUND,
                    "训练计划不存在"
            );
        }


        // 2. 创建最终返回VO
        TrainingPlanDetailVO detail =
                new TrainingPlanDetailVO();

        detail.setId(plan.getId());
        detail.setUserId(plan.getUserId());
        detail.setName(plan.getName());
        detail.setGoal(plan.getGoal());
        detail.setStatus(plan.getStatus());
        detail.setStartDate(plan.getStartDate());
        detail.setEndDate(plan.getEndDate());
        detail.setWeeklyFrequency(
                plan.getWeeklyFrequency()
        );
        detail.setCreatedTime(plan.getCreatedTime());
        detail.setUpdatedTime(plan.getUpdatedTime());


        // 3. 查询Plan下面所有训练日
        List<TrainingPlanDayVO> days =
                trainingPlanDayMapper.findByPlanId(
                        planId
                );


        // 4. 给每个Day查询动作
        for (TrainingPlanDayVO day : days) {

            List<TrainingPlanExerciseVO> exercises =
                    trainingPlanExerciseMapper
                            .findByPlanDayId(
                                    day.getId()
                            );

            day.setExercises(exercises);
        }


        // 5. 组装完整计划
        detail.setDays(days);


        return detail;
    }
}