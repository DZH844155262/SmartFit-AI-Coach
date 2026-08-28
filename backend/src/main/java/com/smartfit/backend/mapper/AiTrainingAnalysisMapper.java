package com.smartfit.backend.mapper;

import com.smartfit.backend.entity.AiTrainingAnalysis;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

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
                prompt_version,
                raw_response
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
                #{promptVersion},
                #{rawResponse}
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
}