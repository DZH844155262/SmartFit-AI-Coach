package com.smartfit.backend.mapper;

import com.smartfit.backend.entity.TrainingExercise;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import com.smartfit.backend.vo.TrainingExerciseVO;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface TrainingExerciseMapper {

    @Insert("""
            INSERT INTO training_exercise
            (
                session_id,
                exercise_id,
                exercise_order,
                notes
            )
            VALUES
            (
                #{sessionId},
                #{exerciseId},
                #{exerciseOrder},
                #{notes}
            )
            """)
    @Options(
            useGeneratedKeys = true,
            keyProperty = "id"
    )
    int insert(TrainingExercise trainingExercise);

    @Select("""
        SELECT
            te.id AS training_exercise_id,
            te.exercise_id,
            e.name AS exercise_name,
            te.exercise_order,
            te.notes
        FROM training_exercise te
        JOIN exercise e
            ON te.exercise_id = e.id
        WHERE te.session_id = #{sessionId}
        ORDER BY te.exercise_order
        """)
    List<TrainingExerciseVO> findBySessionId(Long sessionId);
}