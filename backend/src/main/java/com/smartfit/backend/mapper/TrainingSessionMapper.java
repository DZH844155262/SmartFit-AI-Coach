package com.smartfit.backend.mapper;

import com.smartfit.backend.entity.TrainingSession;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import com.smartfit.backend.vo.TrainingSessionListItemVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface TrainingSessionMapper {

    @Insert("""
            INSERT INTO training_session
            (
                user_id,
                session_date,
                title,
                notes,
                started_time,
                ended_time
            )
            VALUES
            (
                #{userId},
                #{sessionDate},
                #{title},
                #{notes},
                #{startedTime},
                #{endedTime}
            )
            """)
    @Options(
            useGeneratedKeys = true,
            keyProperty = "id"
    )
    int insert(TrainingSession session);
    @Select("""
        SELECT *
        FROM training_session
        WHERE id = #{id}
        """)
    TrainingSession findById(Long id);
    @Select("""
        SELECT
            ts.id,
            ts.session_date,
            ts.title,
            ts.notes,
            COUNT(DISTINCT te.id) AS total_exercises,
            COUNT(tset.id) AS total_sets,
            ts.created_time
        FROM training_session ts

        LEFT JOIN training_exercise te
            ON te.session_id = ts.id

        LEFT JOIN training_set tset
            ON tset.training_exercise_id = te.id

        WHERE ts.user_id = #{userId}

        GROUP BY
            ts.id,
            ts.session_date,
            ts.title,
            ts.notes,
            ts.created_time

        ORDER BY
            ts.session_date DESC,
            ts.id DESC

        LIMIT #{limit}
        """)
    List<TrainingSessionListItemVO> findRecentByUserId(
            @Param("userId") Long userId,
            @Param("limit") Integer limit
    );
}