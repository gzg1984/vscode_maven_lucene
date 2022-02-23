package admin.IDao;

import admin.domain.project;
import admin.domain.projectWithBLOBs;

import java.util.List;
import admin.domain.file;

public interface projectMapper {
    int deleteByPrimaryKey(Integer projectId);

    int insert(projectWithBLOBs record);

    int insertSelective(projectWithBLOBs record);

    projectWithBLOBs selectByPrimaryKey(Integer projectId);
    
    List<projectWithBLOBs> getAllprojects();

    int updateByPrimaryKeySelective(projectWithBLOBs record);

    int updateByPrimaryKeyWithBLOBs(projectWithBLOBs record);

    int updateByPrimaryKey(project record);
    
    /* new for upload */
    void addProjectBase(projectWithBLOBs po);
    void addProjectFile(file filePo);

}