package admin.IDao;

import admin.domain.file;
import admin.domain.fileExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface fileMapper {
    long countByExample(fileExample example);

    int deleteByExample(fileExample example);

    int deleteByPrimaryKey(Integer fileId);

    int insert(file record);

    int insertSelective(file record);

    List<file> selectByExample(fileExample example);

    file selectByPrimaryKey(Integer fileId);

    int updateByExampleSelective(@Param("record") file record, @Param("example") fileExample example);

    int updateByExample(@Param("record") file record, @Param("example") fileExample example);

    int updateByPrimaryKeySelective(file record);

    int updateByPrimaryKey(file record);
}