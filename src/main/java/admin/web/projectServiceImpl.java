package admin.web;
 
import java.util.List;

import javax.annotation.Resource;
 
import org.springframework.stereotype.Service;
 
import admin.IDao.projectMapper;
import admin.domain.project;
import admin.domain.projectWithBLOBs;
import admin.web.projectService;
import java.sql.SQLException;
import admin.domain.file;

@Service("projectService")
public class projectServiceImpl implements projectService {
	@Resource
	private projectMapper pDao;

	
	public List<projectWithBLOBs> getAllprojects(){
			return this.pDao.getAllprojects();
	}

	/*2020 11 03 add  project */
    public void addProjectBase(projectWithBLOBs projectBasePO) throws SQLException {
    	this.pDao.addProjectBase(projectBasePO);
    }

    public void updateProjectBaseStatus(String projectId, String status) throws SQLException {
    	projectWithBLOBs po=new projectWithBLOBs();
        po.setStatus(status);
        //po.setProjectId(projectId);
       // projectMapper.updateProjectBase(po);

    }

    public void addProjectFile(file filePO) throws SQLException {
        this.pDao.addProjectFile(filePO);
    }

}

