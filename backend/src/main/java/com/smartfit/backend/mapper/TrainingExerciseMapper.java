package com.smartfit.backend.mapper;

import com.smartfit.backend.entity.TrainingExercise;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import com.smartfit.backend.vo.TrainingExerciseVO;
import org.apache.ibatis.annotations.Select;
import com.smartfit.backend.vo.ExerciseHistoryItemVO;
import org.apache.ibatis.annotations.Param;

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

    @Select("""
        SELECT
            s.id AS session_id,
            s.session_date,

            MAX(st.weight_kg) AS max_weight_kg,

            COUNT(st.id) AS total_sets,

            COALESCE(
                SUM(st.reps),
                0
            ) AS total_reps,

            COALESCE(
                SUM(st.weight_kg * st.reps),
                0
            ) AS total_volume,

            ROUND(
                AVG(st.rpe),
                1
            ) AS average_rpe

        FROM training_session s

        JOIN training_exercise te
            ON te.session_id = s.id

        JOIN training_set st
            ON st.training_exercise_id = te.id

        WHERE
            s.user_id = #{userId}
            AND te.exercise_id = #{exerciseId}

        GROUP BY
            s.id,
            s.session_date

        ORDER BY
            s.session_date DESC,
            s.id DESC
        
            LIMIT #{limit}
        """)
    List<ExerciseHistoryItemVO> findExerciseHistory(
            @Param("userId") Long userId,
            @Param("exerciseId") Long exerciseId,
            @Param("limit") Integer limit
    );
}