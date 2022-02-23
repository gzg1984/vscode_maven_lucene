package admin.web;
 
import admin.domain.project;
import admin.domain.projectWithBLOBs;
import java.util.ArrayList;
import java.util.List;
import java.sql.SQLException;

import admin.domain.file;

public interface projectService {
	public List<projectWithBLOBs> getAllprojects();
	
	/*2020 1103 for add project*/
    void addProjectBase(projectWithBLOBs projectBasePO) throws SQLException;
    void updateProjectBaseStatus(String projectId,String status) throws SQLException;
    void addProjectFile(file filePO) throws SQLException;
}
