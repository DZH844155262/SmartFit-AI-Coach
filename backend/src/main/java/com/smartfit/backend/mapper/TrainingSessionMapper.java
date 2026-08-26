package com.smartfit.backend.mapper;

import com.smartfit.backend.entity.TrainingSession;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

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
}