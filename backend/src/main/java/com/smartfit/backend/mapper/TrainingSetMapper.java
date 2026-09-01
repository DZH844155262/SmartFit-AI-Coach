package com.smartfit.backend.mapper;

import com.smartfit.backend.entity.TrainingSet;
import com.smartfit.backend.vo.TrainingSetVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface TrainingSetMapper {

    /*
     * 保存一组训练记录
     */
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


    /*
     * 查询某个训练动作已经记录的所有组
     */
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


    /*
     * 查询这个动作目前最大的组号。
     *
     * 没有任何Set时：
     * MAX(set_number) = NULL
     *
     * COALESCE(..., 0)
     * 会把NULL转换成0。
     *
     * 因此：
     * 第一次保存 → 0 + 1 = 第1组
     * 第二次保存 → 1 + 1 = 第2组
     */
    @Select("""
            SELECT COALESCE(
                MAX(set_number),
                0
            )
            FROM training_set
            WHERE training_exercise_id = #{trainingExerciseId}
            """)
    Integer findMaxSetNumber(
            Long trainingExerciseId
    );
}