package com.smartfit.backend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartfit.backend.client.DeepSeekVisionClient;
import com.smartfit.backend.exception.BusinessException;
import com.smartfit.backend.mapper.ExerciseMapper;
import com.smartfit.backend.mapper.TrainingPlanExerciseMapper;
import com.smartfit.backend.vo.EquipmentRecognitionVO;
import com.smartfit.backend.vo.ExerciseHistoryItemVO;
import com.smartfit.backend.vo.ExerciseMatchVO;
import com.smartfit.backend.vo.ExerciseTrendVO;
import com.smartfit.backend.vo.PersonalizedExerciseMatchVO;
import com.smartfit.backend.vo.TrainingPlanExerciseVO;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


@Service
public class EquipmentVisionService {

    /*
     * 调用DeepSeek视觉模型
     */
    private final DeepSeekVisionClient visionClient;

    /*
     * JSON <-> Java对象
     */
    private final ObjectMapper objectMapper;

    /*
     * 查询exercise表
     */
    private final ExerciseMapper exerciseMapper;

    /*
     * 查询用户历史训练数据
     */
    private final TrainingService trainingService;

    /*
     * 查询当前训练计划中的动作
     */
    private final TrainingPlanExerciseMapper trainingPlanExerciseMapper;


    public EquipmentVisionService(
            DeepSeekVisionClient visionClient,
            ObjectMapper objectMapper,
            ExerciseMapper exerciseMapper,
            TrainingService trainingService,
            TrainingPlanExerciseMapper trainingPlanExerciseMapper
    ) {

        this.visionClient = visionClient;
        this.objectMapper = objectMapper;
        this.exerciseMapper = exerciseMapper;
        this.trainingService = trainingService;
        this.trainingPlanExerciseMapper =
                trainingPlanExerciseMapper;
    }


    /*
     * =========================================================
     * 1. 器械识别主流程
     *
     * 图片
     * ↓
     * Vision识别
     * ↓
     * 动作名称匹配exercise表
     * ↓
     * 结合当前训练计划 + 用户历史
     * ↓
     * 个性化排序
     * =========================================================
     */
    public EquipmentRecognitionVO recognize(
            Long userId,
            Long planDayId,
            MultipartFile file
    ) {

        /*
         * 1. 校验上传文件
         */
        if (file == null || file.isEmpty()) {

            throw new BusinessException(
                    HttpStatus.BAD_REQUEST,
                    "请上传器械图片"
            );
        }


        try {

            /*
             * 2. 调用DeepSeek Vision
             *
             * MultipartFile
             * ↓
             * byte[]
             * ↓
             * DeepSeekVisionClient
             */
            String json =
                    visionClient.recognizeEquipment(
                            file.getBytes(),
                            file.getContentType()
                    );


            /*
             * 3. AI JSON → Java VO
             */
            EquipmentRecognitionVO result =
                    objectMapper.readValue(
                            json,
                            EquipmentRecognitionVO.class
                    );


            /*
             * 4. 校验Vision输出
             */
            validateResult(
                    result
            );


            /*
             * 5. possibleExercises
             *
             * 例如：
             * 史密斯卧推
             * 史密斯深蹲
             * 史密斯划船
             *
             * ↓
             *
             * 匹配SmartFit exercise表
             */
            List<ExerciseMatchVO> matches =
                    matchExercises(
                            result.getPossibleExercises()
                    );


            /*
             * 保存标准动作候选
             */
            result.setMatchedExercises(
                    matches
            );


            /*
             * 6. 在标准动作候选基础上，
             * 加入用户自己的训练计划和历史。
             */
            result.setPersonalizedMatches(
                    buildPersonalizedMatches(
                            userId,
                            planDayId,
                            matches
                    )
            );


            return result;


            /*
             * JsonProcessingException 是 IOException 子类，
             * 所以必须写在 IOException 前面。
             */
        } catch (JsonProcessingException e) {

            throw new BusinessException(
                    HttpStatus.BAD_GATEWAY,
                    "视觉模型返回格式解析失败"
            );

        } catch (IOException e) {

            throw new BusinessException(
                    HttpStatus.BAD_REQUEST,
                    "读取上传图片失败"
            );
        }
    }


    /*
     * =========================================================
     * 2. 校验视觉模型输出
     * =========================================================
     */
    private void validateResult(
            EquipmentRecognitionVO result
    ) {

        if (result == null) {

            throw new BusinessException(
                    HttpStatus.BAD_GATEWAY,
                    "视觉识别结果为空"
            );
        }


        if (result.getEquipmentName() == null
                || result.getEquipmentName().isBlank()) {

            throw new BusinessException(
                    HttpStatus.BAD_GATEWAY,
                    "视觉模型未返回器械名称"
            );
        }


        /*
         * Vision模型只能返回三种置信度
         */
        List<String> allowedConfidence =
                List.of(
                        "HIGH",
                        "MEDIUM",
                        "LOW"
                );


        if (result.getRecognitionConfidence() == null
                || !allowedConfidence.contains(
                result.getRecognitionConfidence()
        )) {

            throw new BusinessException(
                    HttpStatus.BAD_GATEWAY,
                    "视觉模型返回了非法置信度"
            );
        }
    }


    /*
     * =========================================================
     * 3. AI自由动作名称
     * →
     * SmartFit exercise标准实体
     *
     * 这是Entity Resolution / Entity Linking
     * =========================================================
     */
    private List<ExerciseMatchVO> matchExercises(
            List<String> possibleExercises
    ) {

        List<ExerciseMatchVO> matches =
                new ArrayList<>();


        if (possibleExercises == null
                || possibleExercises.isEmpty()) {

            return matches;
        }


        for (String exerciseName : possibleExercises) {

            if (exerciseName == null
                    || exerciseName.isBlank()) {

                continue;
            }


            /*
             * 例如：
             *
             * 史密斯卧推
             * ↓
             * 卧推
             *
             * 史密斯深蹲
             * ↓
             * 深蹲
             */
            String keyword =
                    normalizeExerciseKeyword(
                            exerciseName
                    );


            if (keyword.isBlank()) {
                continue;
            }


            /*
             * 查询exercise数据库
             */
            List<ExerciseMatchVO> found =
                    exerciseMapper.findByKeyword(
                            keyword
                    );


            /*
             * 合并结果，并按照exerciseId去重
             */
            for (ExerciseMatchVO exercise : found) {

                boolean alreadyExists =
                        matches
                                .stream()
                                .anyMatch(
                                        item ->
                                                item.getExerciseId()
                                                        .equals(
                                                                exercise.getExerciseId()
                                                        )
                                );


                if (!alreadyExists) {

                    matches.add(
                            exercise
                    );
                }
            }
        }


        return matches;
    }


    /*
     * =========================================================
     * 4. 动作名称归一化
     *
     * Vision输出的是自由文本，
     * 数据库保存的是标准名称。
     * =========================================================
     */
    private String normalizeExerciseKeyword(
            String exerciseName
    ) {

        String keyword =
                exerciseName.trim();


        /*
         * 去掉器械、姿势等修饰词，
         * 尽量留下动作核心名称。
         */
        keyword =
                keyword
                        .replace("史密斯机", "")
                        .replace("史密斯", "")
                        .replace("杠铃", "")
                        .replace("哑铃", "")
                        .replace("器械", "")
                        .replace("绳索", "")
                        .replace("坐姿", "")
                        .replace("站姿", "")
                        .replace("平板", "")
                        .replace("上斜", "")
                        .replace("下斜", "")
                        .replace("单臂", "")
                        .replace("双臂", "")
                        .trim();


        /*
         * 同义词归一化
         *
         * Vision可能返回：
         * 肩推
         *
         * 数据库：
         * 坐姿推肩
         */
        keyword =
                keyword.replace(
                        "肩推",
                        "推肩"
                );


        return keyword;
    }


    /*
     * =========================================================
     * 5. 个性化动作候选排序
     *
     * 根据：
     *
     * ① 当前Training Plan
     * ② 用户历史
     *
     * 对候选动作排序。
     * =========================================================
     */
    private List<PersonalizedExerciseMatchVO> buildPersonalizedMatches(
            Long userId,
            Long planDayId,
            List<ExerciseMatchVO> matches
    ) {

        List<PersonalizedExerciseMatchVO> result =
                new ArrayList<>();


        if (matches == null
                || matches.isEmpty()) {

            return result;
        }


        /*
         * 如果当前用户正在执行某个planDay，
         * 查询当天训练计划。
         *
         * 没有planDayId时，
         * 就当作自由训练。
         */
        List<TrainingPlanExerciseVO> plannedExercises =
                planDayId == null

                        ? Collections.emptyList()

                        : trainingPlanExerciseMapper
                        .findByPlanDayId(
                                planDayId
                        );



        /*
         * =====================================================
         * 遍历所有候选动作
         * =====================================================
         */
        for (ExerciseMatchVO match : matches) {


            PersonalizedExerciseMatchVO item =
                    new PersonalizedExerciseMatchVO();


            /*
             * 复制exercise标准实体信息
             */
            item.setExerciseId(
                    match.getExerciseId()
            );


            item.setExerciseName(
                    match.getExerciseName()
            );


            item.setMuscleGroup(
                    match.getMuscleGroup()
            );


            item.setEquipmentType(
                    match.getEquipmentType()
            );


            item.setMovementPattern(
                    match.getMovementPattern()
            );


            /*
             * 排序分
             *
             * 注意：
             *
             * 这个不是训练科学评分。
             *
             * 它只是产品层面的
             * “用户现在最可能想练哪个动作”
             * 排序分。
             */
            int priorityScore = 0;


            /*
             * =================================================
             * 信号1：
             * 当前计划有没有这个动作
             *
             * 有 → +100
             * =================================================
             */
            boolean inCurrentPlan =
                    plannedExercises
                            .stream()
                            .anyMatch(
                                    exercise ->
                                            exercise
                                                    .getExerciseId()
                                                    .equals(
                                                            match.getExerciseId()
                                                    )
                            );


            item.setInCurrentPlan(
                    inCurrentPlan
            );


            if (inCurrentPlan) {

                priorityScore += 100;
            }


            /*
             * =================================================
             * 信号2：
             * 用户以前是否练过这个动作
             * =================================================
             */
            List<ExerciseHistoryItemVO> history =
                    trainingService.getExerciseHistory(
                            userId,
                            match.getExerciseId(),
                            1
                    );


            /*
             * 用户从没练过
             */
            if (history == null
                    || history.isEmpty()) {


                item.setHasHistory(
                        false
                );


                item.setTrendStatus(
                        "INSUFFICIENT_DATA"
                );


            } else {


                /*
                 * 用户练过
                 */
                item.setHasHistory(
                        true
                );


                /*
                 * 有历史记录：
                 * +30
                 */
                priorityScore += 30;


                /*
                 * getExerciseHistory()最终返回
                 * oldest → newest，
                 *
                 * 所以最后一个元素就是最近一次。
                 */
                ExerciseHistoryItemVO latest =
                        history.get(
                                history.size() - 1
                        );


                /*
                 * 你项目里的ExerciseHistoryItemVO字段是date，
                 * 所以这里使用getDate()。
                 */
                item.setLastSessionDate(
                        latest.getSessionDate()
                );


                item.setLastMaxWeightKg(
                        latest.getMaxWeightKg()
                );


                item.setLastAverageRpe(
                        latest.getAverageRpe()
                );


                /*
                 * 查询最近20次动作趋势
                 */
                ExerciseTrendVO trend =
                        trainingService.getExerciseTrend(
                                userId,
                                match.getExerciseId(),
                                20
                        );


                item.setTrendStatus(
                        trend == null
                                ? "INSUFFICIENT_DATA"
                                : trend.getTrendStatus()
                );
            }


            item.setPriorityScore(
                    priorityScore
            );


            result.add(
                    item
            );
        }


        /*
         * =====================================================
         * 分数从高到低排序
         *
         * 例如：
         *
         * 卧推：
         * 当前计划 +100
         * 有历史   +30
         * = 130
         *
         * 深蹲：
         * 不在计划 0
         * 有历史   +30
         * = 30
         * =====================================================
         */
        result.sort(
                Comparator
                        .comparing(
                                PersonalizedExerciseMatchVO::getPriorityScore
                        )
                        .reversed()
        );


        return result;
    }

}