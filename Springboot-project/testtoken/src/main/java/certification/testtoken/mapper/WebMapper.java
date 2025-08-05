package certification.testtoken.mapper;

import certification.testtoken.entity.User;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface WebMapper {
    
    @Select("SELECT * FROM tokenuser WHERE username = #{user.username} AND password = #{user.password}")
    User login(@Param("user") User user);

    @Select("SELECT * FROM tokenuser WHERE username = #{username}")
    User selectByUsername(String username);

    @Insert("insert into tokenuser (username, password) values (#{username}, #{password})")
    void insert(User user);


    
}
