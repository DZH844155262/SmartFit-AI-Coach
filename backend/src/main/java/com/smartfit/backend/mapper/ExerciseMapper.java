package com.smartfit.backend.mapper;
import com.smartfit.backend.vo.ExerciseMatchVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import org.apache.ibatis.annotations.Select;

public interface ExerciseMapper {

    @Select("""
            SELECT COUNT(*)
            FROM exercise
            WHERE id = #{id}
            """)
    int countById(Long id);
    @Select("""
        SELECT
            id AS exercise_id,
            name AS exercise_name,
            muscle_group,
            equipment_type,
            movement_pattern
        FROM exercise
        WHERE name LIKE CONCAT('%', #{keyword}, '%')
           OR name_en LIKE CONCAT('%', #{keyword}, '%')
        ORDER BY id
        LIMIT 10
        """)
    List<ExerciseMatchVO> findByKeyword(
            @Param("keyword") String keyword
    );
}