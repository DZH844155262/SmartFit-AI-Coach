package com.smartfit.backend.service;

import com.smartfit.backend.dto.TrainingExerciseRequest;
import com.smartfit.backend.dto.TrainingSessionCreateRequest;
import com.smartfit.backend.dto.TrainingSetRequest;
import com.smartfit.backend.entity.TrainingExercise;
import com.smartfit.backend.entity.TrainingSession;
import com.smartfit.backend.entity.TrainingSet;
import com.smartfit.backend.exception.BusinessException;
import com.smartfit.backend.mapper.AppUserMapper;
import com.smartfit.backend.mapper.ExerciseMapper;
import com.smartfit.backend.mapper.TrainingExerciseMapper;
import com.smartfit.backend.mapper.TrainingSessionMapper;
import com.smartfit.backend.mapper.TrainingSetMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.smartfit.backend.vo.TrainingExerciseVO;
import com.smartfit.backend.vo.TrainingSessionDetailVO;
import com.smartfit.backend.vo.TrainingSetVO;
import com.smartfit.backend.dto.TrainingSessionStartRequest;
import com.smartfit.backend.vo.TrainingSessionStartVO;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.smartfit.backend.vo.TrainingSessionSummaryVO;
import com.smartfit.backend.vo.TrainingSessionDetailVO;
import com.smartfit.backend.vo.TrainingExerciseVO;
import com.smartfit.backend.vo.TrainingSetVO;

import java.math.BigDecimal;
import java.math.RoundingMode;
import com.smartfit.backend.vo.TrainingSessionListItemVO;

import com.smartfit.backend.vo.ExerciseHistoryItemVO;
import com.smartfit.backend.vo.ExerciseTrendVO;
import com.smartfit.backend.entity.TrainingPlanDay;
import com.smartfit.backend.mapper.TrainingPlanDayMapper;
import com.smartfit.backend.mapper.TrainingPlanMapper;
import com.smartfit.backend.vo.PlanExerciseComparisonVO;
import com.smartfit.backend.vo.TrainingPlanComparisonVO;
import com.smartfit.backend.vo.TrainingPlanExerciseVO;
import com.smartfit.backend.mapper.TrainingPlanExerciseMapper;
import com.smartfit.backend.dto.TrainingSetAppendRequest;
import com.smartfit.backend.vo.TrainingSetRecordVO;

@Service
public class TrainingService {

    private final AppUserMapper appUserMapper;
    private final ExerciseMapper exerciseMapper;
    private final TrainingSessionMapper trainingSessionMapper;
    private final TrainingExerciseMapper trainingExerciseMapper;
    private final TrainingSetMapper trainingSetMapper;
    private final TrainingPlanDayMapper trainingPlanDayMapper;
    private final TrainingPlanMapper trainingPlanMapper;
    private final TrainingPlanExerciseMapper trainingPlanExerciseMapper;


    public TrainingService(
            AppUserMapper appUserMapper,
            ExerciseMapper exerciseMapper,
            TrainingSessionMapper trainingSessionMapper,
            TrainingExerciseMapper trainingExerciseMapper,
            TrainingSetMapper trainingSetMapper,
            TrainingPlanDayMapper trainingPlanDayMapper,
            TrainingPlanMapper trainingPlanMapper,
            TrainingPlanExerciseMapper trainingPlanExerciseMapper
    ) {
        this.appUserMapper = appUserMapper;
        this.exerciseMapper = exerciseMapper;
        this.trainingSessionMapper = trainingSessionMapper;
        this.trainingExerciseMapper = trainingExerciseMapper;
        this.trainingSetMapper = trainingSetMapper;
        this.trainingPlanDayMapper = trainingPlanDayMapper;
        this.trainingPlanMapper = trainingPlanMapper;
        this.trainingPlanExerciseMapper = trainingPlanExerciseMapper;
    }

    @Transactional
    public TrainingSessionSummaryVO finishSession(
            Long sessionId
    ) {

        /*
         * 1. Session必须存在
         */
        TrainingSession session =
                trainingSessionMapper.findById(
                        sessionId
                );


        if (session == null) {

            throw new BusinessException(
                    HttpStatus.NOT_FOUND,
                    "训练记录不存在"
            );
        }


        /*
         * 2. 已经结束过了，不允许重复结束
         */
        if (session.getEndedTime() != null) {

            throw new BusinessException(
                    HttpStatus.CONFLICT,
                    "该训练已经结束"
            );
        }


        /*
         * 3. 写入结束时间
         */
        int updated =
                trainingSessionMapper.finishSession(
                        sessionId
                );


        if (updated == 0) {

            throw new BusinessException(
                    HttpStatus.CONFLICT,
                    "训练结束状态更新失败"
            );
        }


        /*
         * 4. 返回本场训练总结
         *
         * 直接复用之前已经写好的确定性统计逻辑。
         */
        return getSessionSummary(
                sessionId
        );
    }
    @Transactional
    public TrainingSetRecordVO appendSet(
            Long trainingExerciseId,
            TrainingSetAppendRequest request
    ) {

        /*
         * 1. trainingExercise必须存在
         */
        if (trainingExerciseMapper.countById(
                trainingExerciseId
        ) == 0) {

            throw new BusinessException(
                    HttpStatus.NOT_FOUND,
                    "训练动作记录不存在"
            );
        }


        /*
         * 2. 查询目前已经做到第几组
         *
         * 第一次：
         * max = 0
         *
         * 第二次：
         * max = 1
         */
        Integer currentMaxSetNumber =
                trainingSetMapper.findMaxSetNumber(
                        trainingExerciseId
                );


        /*
         * 3. 自动生成下一组组号
         */
        int nextSetNumber =
                currentMaxSetNumber + 1;


        /*
         * 4. setType为空时，
         * 默认按照正式工作组WORKING处理。
         */
        String setType =
                request.getSetType();

        if (setType == null
                || setType.isBlank()) {

            setType = "WORKING";
        }


        /*
         * 5. 创建TrainingSet实体
         */
        TrainingSet trainingSet =
                new TrainingSet();


        trainingSet.setTrainingExerciseId(
                trainingExerciseId
        );


        trainingSet.setSetNumber(
                nextSetNumber
        );


        trainingSet.setWeightKg(
                request.getWeightKg()
        );


        trainingSet.setReps(
                request.getReps()
        );


        trainingSet.setRpe(
                request.getRpe()
        );


        trainingSet.setSetType(
                setType
        );


        /*
         * 用户点击“完成本组”以后才调用这个接口，
         * 因此这一组已经完成。
         */
        trainingSet.setCompleted(
                true
        );


        /*
         * 6. 保存到数据库
         */
        trainingSetMapper.insert(
                trainingSet
        );


        /*
         * 7. 返回给前端
         */
        TrainingSetRecordVO result =
                new TrainingSetRecordVO();


        result.setTrainingExerciseId(
                trainingExerciseId
        );


        result.setSetNumber(
                nextSetNumber
        );


        result.setWeightKg(
                request.getWeightKg()
        );


        result.setReps(
                request.getReps()
        );


        result.setRpe(
                request.getRpe()
        );


        result.setSetType(
                setType
        );


        result.setCompleted(
                true
        );


        return result;
    }
    @Transactional
    public TrainingSession createSession(
            Long userId,
            TrainingSessionCreateRequest request
    ) {

        // 1. 用户必须存在
        if (appUserMapper.countById(userId) == 0) {
            throw new BusinessException(
                    HttpStatus.NOT_FOUND,
                    "用户不存在"
            );
        }

        // 如果这次训练来自某个训练计划日
        if (request.getPlanDayId() != null) {

            // 1. 先查这个 Plan Day 存不存在
            TrainingPlanDay planDay =
                    trainingPlanDayMapper.findById(
                            request.getPlanDayId()
                    );

            if (planDay == null) {
                throw new BusinessException(
                        HttpStatus.NOT_FOUND,
                        "训练计划日不存在"
                );
            }


            // 2. 再检查这个 Plan 是否属于当前用户
            if (trainingPlanMapper.countByIdAndUserId(
                    planDay.getPlanId(),
                    userId
            ) == 0) {

                throw new BusinessException(
                        HttpStatus.FORBIDDEN,
                        "无权使用该训练计划"
                );
            }
        }


        // 2. 先检查请求里的所有动作是否存在
        for (TrainingExerciseRequest exerciseRequest
                : request.getExercises()) {

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


        // 3. 创建一次训练 Session
        TrainingSession session = new TrainingSession();

        session.setUserId(userId);

        session.setPlanDayId(
                request.getPlanDayId()
        );

        session.setSessionDate(
                request.getSessionDate()
        );

        session.setTitle(
                request.getTitle()
        );

        session.setNotes(
                request.getNotes()
        );

        trainingSessionMapper.insert(session);


        // 4. 保存本次训练中的每个动作
        int exerciseOrder = 1;

        for (TrainingExerciseRequest exerciseRequest
                : request.getExercises()) {

            TrainingExercise trainingExercise =
                    new TrainingExercise();

            trainingExercise.setSessionId(session.getId());
            trainingExercise.setExerciseId(
                    exerciseRequest.getExerciseId()
            );
            trainingExercise.setExerciseOrder(exerciseOrder);
            trainingExercise.setNotes(
                    exerciseRequest.getNotes()
            );

            trainingExerciseMapper.insert(trainingExercise);


            // 5. 保存这个动作下面的每一组
            int setNumber = 1;

            for (TrainingSetRequest setRequest
                    : exerciseRequest.getSets()) {

                TrainingSet trainingSet =
                        new TrainingSet();

                trainingSet.setTrainingExerciseId(
                        trainingExercise.getId()
                );

                trainingSet.setSetNumber(setNumber);

                trainingSet.setWeightKg(
                        setRequest.getWeightKg()
                );

                trainingSet.setReps(
                        setRequest.getReps()
                );

                trainingSet.setRpe(
                        setRequest.getRpe()
                );

                trainingSet.setSetType(
                        setRequest.getSetType()
                );

                trainingSet.setCompleted(true);

                trainingSetMapper.insert(trainingSet);

                setNumber++;
            }

            exerciseOrder++;
        }



        // session.id 已经由 MyBatis 自动回填
        return session;
    }

    @Transactional
    public TrainingSessionStartVO startPlannedExerciseSession(
            Long userId,
            TrainingSessionStartRequest request
    ) {

        /*
         * 1. 用户必须存在
         */
        if (appUserMapper.countById(userId) == 0) {

            throw new BusinessException(
                    HttpStatus.NOT_FOUND,
                    "用户不存在"
            );
        }


        /*
         * 2. Plan Day必须存在
         */
        TrainingPlanDay planDay =
                trainingPlanDayMapper.findById(
                        request.getPlanDayId()
                );


        if (planDay == null) {

            throw new BusinessException(
                    HttpStatus.NOT_FOUND,
                    "训练计划日不存在"
            );
        }


        /*
         * 3. 这个Plan必须属于当前用户
         *
         * 防止用户1拿着用户2的planDayId启动训练。
         */
        if (trainingPlanMapper.countByIdAndUserId(
                planDay.getPlanId(),
                userId
        ) == 0) {

            throw new BusinessException(
                    HttpStatus.FORBIDDEN,
                    "无权使用该训练计划"
            );
        }


        /*
         * 4. 用户选择的动作必须真的属于这个Plan Day
         */
        TrainingPlanExerciseVO target =
                trainingPlanExerciseMapper
                        .findByPlanDayIdAndExerciseId(
                                request.getPlanDayId(),
                                request.getExerciseId()
                        );


        if (target == null) {

            throw new BusinessException(
                    HttpStatus.NOT_FOUND,
                    "当前训练计划中没有这个动作"
            );
        }


        /*
         * =========================================================
         * 5. 创建training_session
         *
         * 注意：
         * 此时只是“开始训练”，
         * 还没有任何训练组。
         * =========================================================
         */
        TrainingSession session =
                new TrainingSession();


        session.setUserId(
                userId
        );


        session.setPlanDayId(
                request.getPlanDayId()
        );


        /*
         * 用户现在点击开始训练，
         * 所以训练日期使用当前日期。
         */
        session.setSessionDate(
                LocalDate.now()
        );

        session.setTitle(
                planDay.getTitle()
        );

        session.setNotes(
                null
        );


        /*
         * 用户点击“开始训练”的真实时间。
         */
        session.setStartedTime(
                LocalDateTime.now()
        );


        /*
         * 当前训练还没有结束，
         * 所以结束时间保持NULL。
         */
        session.setEndedTime(
                null
        );


        trainingSessionMapper.insert(
                session
        );


        /*
         * =========================================================
         * 6. 创建本次training_exercise
         *
         * 现在表示：
         *
         * Session已经开始，
         * 而且用户确认要练这个动作。
         *
         * 但是还没有training_set。
         * =========================================================
         */
        TrainingExercise trainingExercise =
                new TrainingExercise();


        trainingExercise.setSessionId(
                session.getId()
        );


        trainingExercise.setExerciseId(
                request.getExerciseId()
        );


        trainingExercise.setExerciseOrder(
                target.getExerciseOrder()
        );


        trainingExercise.setNotes(
                target.getNotes()
        );


        trainingExerciseMapper.insert(
                trainingExercise
        );


        /*
         * =========================================================
         * 7. 返回前端
         * =========================================================
         */
        TrainingSessionStartVO result =
                new TrainingSessionStartVO();


        result.setSessionId(
                session.getId()
        );


        result.setTrainingExerciseId(
                trainingExercise.getId()
        );


        result.setPlanDayId(
                request.getPlanDayId()
        );


        result.setExerciseId(
                target.getExerciseId()
        );


        result.setExerciseName(
                target.getExerciseName()
        );


        result.setTargetSets(
                target.getTargetSets()
        );


        result.setTargetRepsMin(
                target.getTargetRepsMin()
        );


        result.setTargetRepsMax(
                target.getTargetRepsMax()
        );


        result.setTargetWeightKg(
                target.getTargetWeightKg()
        );


        result.setTargetRpe(
                target.getTargetRpe()
        );


        result.setNotes(
                target.getNotes()
        );


        return result;
    }

    public TrainingSessionDetailVO getSessionDetail(Long sessionId) {

        // 1. 查询训练 Session
        TrainingSession session =
                trainingSessionMapper.findById(sessionId);

        if (session == null) {
            throw new BusinessException(
                    HttpStatus.NOT_FOUND,
                    "训练记录不存在"
            );
        }


        // 2. 创建最终返回给前端的 VO
        TrainingSessionDetailVO detailVO =
                new TrainingSessionDetailVO();

        detailVO.setId(session.getId());
        detailVO.setUserId(session.getUserId());
        detailVO.setSessionDate(session.getSessionDate());
        detailVO.setTitle(session.getTitle());
        detailVO.setNotes(session.getNotes());
        detailVO.setStartedTime(session.getStartedTime());
        detailVO.setEndedTime(session.getEndedTime());
        detailVO.setCreatedTime(session.getCreatedTime());


        // 3. 查询这次训练有哪些动作
        List<TrainingExerciseVO> exercises =
                trainingExerciseMapper.findBySessionId(sessionId);


        // 4. 给每个动作继续查询它下面的 Sets
        for (TrainingExerciseVO exercise : exercises) {

            List<TrainingSetVO> sets =
                    trainingSetMapper.findByTrainingExerciseId(
                            exercise.getTrainingExerciseId()
                    );

            exercise.setSets(sets);
        }


        // 5. 把动作列表塞进最终 VO
        detailVO.setExercises(exercises);


        return detailVO;
    }
    public TrainingSessionSummaryVO getSessionSummary(Long sessionId) {

        // 1. 先取得完整训练数据
        TrainingSessionDetailVO detail =
                getSessionDetail(sessionId);


        int totalExercises = detail.getExercises().size();
        int totalSets = 0;
        int totalReps = 0;

        BigDecimal totalVolume = BigDecimal.ZERO;
        BigDecimal rpeSum = BigDecimal.ZERO;

        int rpeCount = 0;


        // 2. 遍历每个动作
        for (TrainingExerciseVO exercise : detail.getExercises()) {

            // 3. 遍历这个动作的每一组
            for (TrainingSetVO set : exercise.getSets()) {

                totalSets++;

                totalReps += set.getReps();


                // volume = 重量 × 次数
                if (set.getWeightKg() != null) {

                    BigDecimal setVolume =
                            set.getWeightKg()
                                    .multiply(
                                            BigDecimal.valueOf(
                                                    set.getReps()
                                            )
                                    );

                    totalVolume =
                            totalVolume.add(setVolume);
                }


                // RPE允许为空，所以需要判断
                if (set.getRpe() != null) {

                    rpeSum =
                            rpeSum.add(set.getRpe());

                    rpeCount++;
                }
            }
        }


        // 4. 计算平均RPE
        BigDecimal averageRpe = null;

        if (rpeCount > 0) {

            averageRpe =
                    rpeSum.divide(
                            BigDecimal.valueOf(rpeCount),
                            1,
                            RoundingMode.HALF_UP
                    );
        }


        // 5. 封装返回结果
        TrainingSessionSummaryVO summary =
                new TrainingSessionSummaryVO();

        summary.setSessionId(sessionId);
        summary.setTotalExercises(totalExercises);
        summary.setTotalSets(totalSets);
        summary.setTotalReps(totalReps);
        summary.setTotalVolume(totalVolume);
        summary.setAverageRpe(averageRpe);


        return summary;
    }
    public List<TrainingSessionListItemVO> getRecentSessions(
            Long userId,
            Integer limit
    ) {

        // 1. 用户必须存在
        if (appUserMapper.countById(userId) == 0) {
            throw new BusinessException(
                    HttpStatus.NOT_FOUND,
                    "用户不存在"
            );
        }


        // 2. 防止前端传入不合理数量
        if (limit == null) {
            limit = 10;
        }

        if (limit < 1) {
            limit = 1;
        }

        if (limit > 50) {
            limit = 50;
        }


        // 3. 查询最近训练
        return trainingSessionMapper.findRecentByUserId(
                userId,
                limit
        );
    }
    public List<ExerciseHistoryItemVO> getExerciseHistory(
            Long userId,
            Long exerciseId,
            Integer limit
    ) {

        // 1. 检查用户
        if (appUserMapper.countById(userId) == 0) {
            throw new BusinessException(
                    HttpStatus.NOT_FOUND,
                    "用户不存在"
            );
        }


        // 2. 检查动作
        if (exerciseMapper.countById(exerciseId) == 0) {
            throw new BusinessException(
                    HttpStatus.NOT_FOUND,
                    "训练动作不存在"
            );
        }


        // 3. 控制查询数量
        if (limit == null) {
            limit = 20;
        }

        if (limit < 1) {
            limit = 1;
        }

        if (limit > 100) {
            limit = 100;
        }


        // 4. 查询历史表现
        List<ExerciseHistoryItemVO> history =
                trainingExerciseMapper.findExerciseHistory(
                        userId,
                        exerciseId,
                        limit
                );

// Mapper拿的是“最新 → 最旧”
// 前端趋势图更适合“最旧 → 最新”
        Collections.reverse(history);

        return history;
    }
    public ExerciseTrendVO getExerciseTrend(
            Long userId,
            Long exerciseId,
            Integer limit
    ) {

        List<ExerciseHistoryItemVO> history =
                getExerciseHistory(
                        userId,
                        exerciseId,
                        limit
                );


        ExerciseTrendVO trend =
                new ExerciseTrendVO();

        trend.setExerciseId(exerciseId);
        trend.setSessionCount(history.size());


        // 少于2次训练，没有足够数据判断趋势
        if (history.size() < 2) {
            trend.setTrendStatus("INSUFFICIENT_DATA");
            return trend;
        }


        // 第一条 = 时间最早
        ExerciseHistoryItemVO first =
                history.get(0);

        // 最后一条 = 最近一次
        ExerciseHistoryItemVO latest =
                history.get(history.size() - 1);


        trend.setStartDate(first.getSessionDate());
        trend.setEndDate(latest.getSessionDate());

        trend.setFirstMaxWeightKg(
                first.getMaxWeightKg()
        );

        trend.setLatestMaxWeightKg(
                latest.getMaxWeightKg()
        );

        trend.setFirstVolume(
                first.getTotalVolume()
        );

        trend.setLatestVolume(
                latest.getTotalVolume()
        );


        // 1. 最高重量变化
        BigDecimal weightChange =
                latest.getMaxWeightKg()
                        .subtract(
                                first.getMaxWeightKg()
                        );

        trend.setWeightChangeKg(weightChange);


        // 2. 最高重量变化百分比
        if (first.getMaxWeightKg()
                .compareTo(BigDecimal.ZERO) != 0) {

            BigDecimal weightChangePercent =
                    weightChange
                            .divide(
                                    first.getMaxWeightKg(),
                                    4,
                                    RoundingMode.HALF_UP
                            )
                            .multiply(
                                    BigDecimal.valueOf(100)
                            )
                            .setScale(
                                    1,
                                    RoundingMode.HALF_UP
                            );

            trend.setWeightChangePercent(
                    weightChangePercent
            );
        }


        // 3. Volume变化
        BigDecimal volumeChange =
                latest.getTotalVolume()
                        .subtract(
                                first.getTotalVolume()
                        );

        trend.setVolumeChange(volumeChange);


        // 4. Volume变化百分比
        if (first.getTotalVolume()
                .compareTo(BigDecimal.ZERO) != 0) {

            BigDecimal volumeChangePercent =
                    volumeChange
                            .divide(
                                    first.getTotalVolume(),
                                    4,
                                    RoundingMode.HALF_UP
                            )
                            .multiply(
                                    BigDecimal.valueOf(100)
                            )
                            .setScale(
                                    1,
                                    RoundingMode.HALF_UP
                            );

            trend.setVolumeChangePercent(
                    volumeChangePercent
            );
        }


        // 5. RPE变化
        if (first.getAverageRpe() != null
                && latest.getAverageRpe() != null) {

            trend.setAverageRpeChange(
                    latest.getAverageRpe()
                            .subtract(
                                    first.getAverageRpe()
                            )
            );
        }


        // 6. 生成V1趋势信号
        int weightCompare =
                weightChange.compareTo(
                        BigDecimal.ZERO
                );

        int volumeCompare =
                volumeChange.compareTo(
                        BigDecimal.ZERO
                );


        if (weightCompare > 0
                && volumeCompare >= 0) {

            trend.setTrendStatus("IMPROVING");

        } else if (volumeCompare > 0
                && weightCompare >= 0) {

            trend.setTrendStatus("IMPROVING");

        } else if (weightCompare == 0
                && volumeCompare == 0) {

            trend.setTrendStatus("STABLE");

        } else if (weightCompare < 0
                && volumeCompare <= 0) {

            trend.setTrendStatus("DECLINING");

        } else if (volumeCompare < 0
                && weightCompare <= 0) {

            trend.setTrendStatus("DECLINING");

        } else {

            trend.setTrendStatus("MIXED");
        }


        return trend;
    }
    public TrainingPlanComparisonVO getPlanComparison(
            Long sessionId
    ) {

        // 1. 查询实际训练
        TrainingSession session =
                trainingSessionMapper.findById(sessionId);

        if (session == null) {
            throw new BusinessException(
                    HttpStatus.NOT_FOUND,
                    "训练记录不存在"
            );
        }


        // 2. 自由训练没有Plan Day，无法进行计划对比
        if (session.getPlanDayId() == null) {
            throw new BusinessException(
                    HttpStatus.BAD_REQUEST,
                    "该训练记录不属于任何训练计划"
            );
        }


        // 3. 查询对应的Plan Day
        TrainingPlanDay planDay =
                trainingPlanDayMapper.findById(
                        session.getPlanDayId()
                );

        if (planDay == null) {
            throw new BusinessException(
                    HttpStatus.NOT_FOUND,
                    "训练计划日不存在"
            );
        }


        // 4. 查询计划中的所有动作
        List<TrainingPlanExerciseVO> plannedExercises =
                trainingPlanExerciseMapper.findByPlanDayId(
                        planDay.getId()
                );


        // 5. 查询实际完成的所有动作
        List<TrainingExerciseVO> actualExercises =
                trainingExerciseMapper.findBySessionId(
                        sessionId
                );


        List<PlanExerciseComparisonVO> comparisons =
                new ArrayList<>();

        int totalTargetSets = 0;
        int totalSuccessfulSets = 0;


        // 6. 逐个比较计划动作
        for (TrainingPlanExerciseVO planned
                : plannedExercises) {

            PlanExerciseComparisonVO comparison =
                    new PlanExerciseComparisonVO();

            comparison.setExerciseId(
                    planned.getExerciseId()
            );

            comparison.setExerciseName(
                    planned.getExerciseName()
            );

            comparison.setTargetSets(
                    planned.getTargetSets()
            );

            comparison.setTargetRepsMin(
                    planned.getTargetRepsMin()
            );

            comparison.setTargetRepsMax(
                    planned.getTargetRepsMax()
            );

            comparison.setTargetWeightKg(
                    planned.getTargetWeightKg()
            );

            comparison.setTargetRpe(
                    planned.getTargetRpe()
            );


            totalTargetSets += planned.getTargetSets();


            // 找到对应的实际动作
            TrainingExerciseVO matchedActual = null;

            for (TrainingExerciseVO actual
                    : actualExercises) {

                if (actual.getExerciseId()
                        .equals(planned.getExerciseId())) {

                    matchedActual = actual;
                    break;
                }
            }


            // 计划里有，但实际完全没练这个动作
            if (matchedActual == null) {

                comparison.setActualSets(0);
                comparison.setSuccessfulSets(0);

                comparison.setCompletionRate(
                        BigDecimal.ZERO
                );

                comparison.setStatus("NOT_STARTED");

                comparisons.add(comparison);

                continue;
            }


            // 7. 查询实际动作所有Sets
            List<TrainingSetVO> actualSets =
                    trainingSetMapper
                            .findByTrainingExerciseId(
                                    matchedActual
                                            .getTrainingExerciseId()
                            );

            comparison.setActualSets(
                    actualSets.size()
            );


            int successfulSets = 0;

            BigDecimal rpeSum =
                    BigDecimal.ZERO;

            int rpeCount = 0;


            // 8. 判断每个Set是否达到目标
            for (TrainingSetVO actualSet
                    : actualSets) {

                boolean repsReached =
                        actualSet.getReps()
                                >= planned.getTargetRepsMin();


                boolean weightReached = true;

                // 如果计划规定了目标重量，则检查实际重量
                if (planned.getTargetWeightKg() != null) {

                    weightReached =
                            actualSet.getWeightKg() != null
                                    && actualSet
                                    .getWeightKg()
                                    .compareTo(
                                            planned.getTargetWeightKg()
                                    ) >= 0;
                }


                if (repsReached && weightReached) {
                    successfulSets++;
                }


                if (actualSet.getRpe() != null) {

                    rpeSum =
                            rpeSum.add(
                                    actualSet.getRpe()
                            );

                    rpeCount++;
                }
            }


            /*
             * 如果用户额外做了很多组，
             * 达标组不能超过计划目标组数。
             *
             * 例如计划3组、实际做5组，
             * 完成率最多仍然是100%。
             */
            int cappedSuccessfulSets =
                    Math.min(
                            successfulSets,
                            planned.getTargetSets()
                    );

            comparison.setSuccessfulSets(
                    cappedSuccessfulSets
            );


            totalSuccessfulSets +=
                    cappedSuccessfulSets;


            // 9. 计算该动作完成率
            BigDecimal completionRate =
                    BigDecimal.valueOf(
                                    cappedSuccessfulSets
                            )
                            .divide(
                                    BigDecimal.valueOf(
                                            planned.getTargetSets()
                                    ),
                                    4,
                                    RoundingMode.HALF_UP
                            )
                            .multiply(
                                    BigDecimal.valueOf(100)
                            )
                            .setScale(
                                    1,
                                    RoundingMode.HALF_UP
                            );

            comparison.setCompletionRate(
                    completionRate
            );


            // 10. 实际平均RPE
            if (rpeCount > 0) {

                comparison.setActualAverageRpe(
                        rpeSum.divide(
                                BigDecimal.valueOf(rpeCount),
                                1,
                                RoundingMode.HALF_UP
                        )
                );
            }


            // 11. 状态
            if (cappedSuccessfulSets
                    >= planned.getTargetSets()) {

                comparison.setStatus("COMPLETED");

            } else if (cappedSuccessfulSets > 0) {

                comparison.setStatus("PARTIAL");

            } else {

                comparison.setStatus("NOT_COMPLETED");
            }


            comparisons.add(comparison);
        }


        // 12. 整体完成率
        BigDecimal overallCompletionRate =
                BigDecimal.ZERO;

        if (totalTargetSets > 0) {

            overallCompletionRate =
                    BigDecimal.valueOf(
                                    totalSuccessfulSets
                            )
                            .divide(
                                    BigDecimal.valueOf(
                                            totalTargetSets
                                    ),
                                    4,
                                    RoundingMode.HALF_UP
                            )
                            .multiply(
                                    BigDecimal.valueOf(100)
                            )
                            .setScale(
                                    1,
                                    RoundingMode.HALF_UP
                            );
        }


        // 13. 组装最终VO
        TrainingPlanComparisonVO result =
                new TrainingPlanComparisonVO();

        result.setSessionId(sessionId);

        result.setPlanDayId(
                session.getPlanDayId()
        );

        result.setPlanDayTitle(
                planDay.getTitle()
        );

        result.setTotalTargetSets(
                totalTargetSets
        );

        result.setTotalSuccessfulSets(
                totalSuccessfulSets
        );

        result.setOverallCompletionRate(
                overallCompletionRate
        );

        result.setExercises(comparisons);


        return result;
    }
}