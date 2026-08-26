package com.smartfit.backend.mapper;


import com.smartfit.backend.entity.UserProfile;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Delete;
public interface UserProfileMapper {


    @Select("""
            SELECT *
            FROM user_profile
            WHERE id = #{id}
            """)
    UserProfile findById(Long id);

    @Insert("""
INSERT INTO user_profile
(
nickname,
age,
height,
weight
)
VALUES
(
#{nickname},
#{age},
#{height},
#{weight}
)
""")
    void insert(UserProfile userProfile);
    @Update("""
UPDATE user_profile
SET
nickname = #{nickname},
age = #{age},
height = #{height},
weight = #{weight}
WHERE id = #{id}
""")
    void update(UserProfile userProfile);

    @Delete("""
DELETE FROM user_profile
WHERE id = #{id}
""")
    void delete(Long id);
}
