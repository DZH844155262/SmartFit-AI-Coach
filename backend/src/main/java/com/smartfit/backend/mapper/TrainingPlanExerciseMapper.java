package com.smartfit.backend.mapper;

import com.smartfit.backend.entity.TrainingPlanExercise;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import com.smartfit.backend.vo.TrainingPlanExerciseVO;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;

import java.util.List;

public interface TrainingPlanExerciseMapper {
    @Select("""
        SELECT
            tpe.id AS plan_exercise_id,
            tpe.exercise_id,
            e.name AS exercise_name,
            e.instructions,
            tpe.exercise_order,
            tpe.target_sets,
            tpe.target_reps_min,
            tpe.target_reps_max,
            tpe.target_weight_kg,
            tpe.target_rpe,
            tpe.notes
        FROM training_plan_exercise tpe

        JOIN exercise e
            ON e.id = tpe.exercise_id

        WHERE tpe.plan_day_id = #{planDayId}

        ORDER BY tpe.exercise_order
        """)
    List<TrainingPlanExerciseVO> findByPlanDayId(
            Long planDayId
    );

    @Select("""
        SELECT
            tpe.id AS plan_exercise_id,
            tpe.exercise_id,
            e.name AS exercise_name,
            e.instructions,
            tpe.exercise_order,
            tpe.target_sets,
            tpe.target_reps_min,
            tpe.target_reps_max,
            tpe.target_weight_kg,
            tpe.target_rpe,
            tpe.notes
        FROM training_plan_exercise tpe

        JOIN exercise e
            ON e.id = tpe.exercise_id

        WHERE tpe.plan_day_id = #{planDayId}
          AND tpe.exercise_id = #{exerciseId}
        """)
    TrainingPlanExerciseVO findByPlanDayIdAndExerciseId(
            @Param("planDayId") Long planDayId,
            @Param("exerciseId") Long exerciseId
    );
    @Insert("""
            INSERT INTO training_plan_exercise
            (
                plan_day_id,
                exercise_id,
                exercise_order,
                target_sets,
                target_reps_min,
                target_reps_max,
                target_weight_kg,
                target_rpe,
                notes
            )
            VALUES
            (
                #{planDayId},
                #{exerciseId},
                #{exerciseOrder},
                #{targetSets},
                #{targetRepsMin},
                #{targetRepsMax},
                #{targetWeightKg},
                #{targetRpe},
                #{notes}
            )
            """)
    @Options(
            useGeneratedKeys = true,
            keyProperty = "id"
    )
    int insert(TrainingPlanExercise trainingPlanExercise);

    @Update("""
        UPDATE training_plan_exercise
        SET target_weight_kg = #{targetWeightKg},
            target_sets = #{targetSets},
            target_reps_min = #{targetRepsMin},
            target_reps_max = #{targetRepsMax},
            target_rpe = #{targetRpe}
        WHERE plan_day_id = #{planDayId}
          AND exercise_id = #{exerciseId}
        """)
    int updateTargets(
            @Param("planDayId") Long planDayId,
            @Param("exerciseId") Long exerciseId,
            @Param("targetWeightKg") BigDecimal targetWeightKg,
            @Param("targetSets") Integer targetSets,
            @Param("targetRepsMin") Integer targetRepsMin,
            @Param("targetRepsMax") Integer targetRepsMax,
            @Param("targetRpe") BigDecimal targetRpe
    );
}