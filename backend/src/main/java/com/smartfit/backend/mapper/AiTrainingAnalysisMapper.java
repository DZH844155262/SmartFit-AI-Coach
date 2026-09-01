package com.smartfit.backend.mapper;

import com.smartfit.backend.entity.AiTrainingAnalysis;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
public interface AiTrainingAnalysisMapper {


    @Insert("""
            INSERT INTO ai_training_analysis
            (
                user_id,
                    session_id,
                    model,
                    score,
                    summary,
                    positive_signals,
                    risk_signals,
                    next_session_advice,
                    plan_adjustments,
                    prompt_version,
                    raw_response,
                    nutrition_analysis
            )
            VALUES
            (
                #{userId},
                    #{sessionId},
                    #{model},
                    #{score},
                    #{summary},
                    #{positiveSignals},
                    #{riskSignals},
                    #{nextSessionAdvice},
                    #{planAdjustments},
                    #{promptVersion},
                    #{rawResponse},
                    #{nutritionAnalysis}
            )
            """)
    @Options(
            useGeneratedKeys = true,
            keyProperty = "id"
    )
    int insert(AiTrainingAnalysis analysis);


    @Select("""
            SELECT *
            FROM ai_training_analysis

            WHERE session_id = #{sessionId}
              AND model = #{model}
              AND prompt_version = #{promptVersion}

            ORDER BY created_time DESC, id DESC

            LIMIT 1
            """)
    AiTrainingAnalysis findLatest(
            @Param("sessionId") Long sessionId,
            @Param("model") String model,
            @Param("promptVersion") String promptVersion
    );

    @Select("""
        SELECT *
        FROM ai_training_analysis
        WHERE id = #{id}
        """)
    AiTrainingAnalysis findById(Long id);

    @Update("""
        UPDATE ai_training_analysis
        SET applied = TRUE,
            applied_time = CURRENT_TIMESTAMP
        WHERE id = #{id}
          AND applied = FALSE
        """)
    int markAsApplied(Long id);
}