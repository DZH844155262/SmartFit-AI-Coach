package com.smartfit.backend.mapper;

import com.smartfit.backend.entity.TrainingSet;
import org.apache.ibatis.annotations.Insert;
import com.smartfit.backend.vo.TrainingSetVO;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface TrainingSetMapper {

    @Insert("""
            INSERT INTO training_set
            (
                training_exercise_id,
                set_number,
                weight_kg,
                reps,
                rpe,
                set_type,
                completed
            )
            VALUES
            (
                #{trainingExerciseId},
                #{setNumber},
                #{weightKg},
                #{reps},
                #{rpe},
                #{setType},
                #{completed}
            )
            """)
    int insert(TrainingSet trainingSet);

    @Select("""
        SELECT
            set_number,
            weight_kg,
            reps,
            rpe,
            set_type
        FROM training_set
        WHERE training_exercise_id = #{trainingExerciseId}
        ORDER BY set_number
        """)
    List<TrainingSetVO> findByTrainingExerciseId(
            Long trainingExerciseId
    );
}