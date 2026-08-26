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

import java.util.List;

@Service
public class TrainingService {

    private final AppUserMapper appUserMapper;
    private final ExerciseMapper exerciseMapper;
    private final TrainingSessionMapper trainingSessionMapper;
    private final TrainingExerciseMapper trainingExerciseMapper;
    private final TrainingSetMapper trainingSetMapper;


    public TrainingService(
            AppUserMapper appUserMapper,
            ExerciseMapper exerciseMapper,
            TrainingSessionMapper trainingSessionMapper,
            TrainingExerciseMapper trainingExerciseMapper,
            TrainingSetMapper trainingSetMapper
    ) {
        this.appUserMapper = appUserMapper;
        this.exerciseMapper = exerciseMapper;
        this.trainingSessionMapper = trainingSessionMapper;
        this.trainingExerciseMapper = trainingExerciseMapper;
        this.trainingSetMapper = trainingSetMapper;
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
        session.setSessionDate(request.getSessionDate());
        session.setTitle(request.getTitle());
        session.setNotes(request.getNotes());

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
}