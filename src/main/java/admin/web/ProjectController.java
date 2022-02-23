package admin.web;
 
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.beans.factory.annotation.Value;
//import org.springframework.beans.factory.annotation.Autowired;

//import admin.domain.project;
import admin.domain.projectWithBLOBs;


//import admin.web.projectService;


/* for add project */
import util.FileUploadUtil;
import model.UploadResult;
//import java.io.File;
import java.io.IOException;
import org.apache.log4j.Logger;
//import java.util.UUID;
import java.util.LinkedList;
import java.sql.SQLException;
/* for add project, file manager*/
import admin.domain.file;
import util.EncodingDetect;
import org.apache.commons.io.FileUtils;
import lucene.analyzer.SourceFileAnalyzer;
import java.nio.file.FileSystems;

/*搜索引擎 */
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;


@Controller
@RequestMapping("/project")
public class ProjectController {
	@Resource
	private projectService ps;
	


	/*  /admin/project/list.do */
	@RequestMapping(value="/list.do")
	public String toUserList(HttpServletRequest request,HttpServletResponse response,Model model){
		System.out.printf("Enter list.do with Context %s\n",request.getContextPath());
		System.out.printf("Enter list.do with parameter %s\n",request.getServletPath());
		
		List<projectWithBLOBs> p = this.ps.getAllprojects();
		model.addAttribute("projects", p);
		return "project/list";
	    
	}
	
	@Value("${storePath}")
	private String zipFileStorePath; // 上传的压缩文件存放根目录路径
	@Value("${rootPath}")
	private String projectRootPath; // 解压后的项目文件夹存放的根目录
	@Value("${indexPath}")
	private String indexRootPath; // 项目索引文件夹根目录
	@Value("${prefix}")
	private String absolutePrefix; // 绝对路径前缀

	@Autowired
	private projectService projectServiceImpl;

	
	@RequestMapping(value = "/doUpload.do", method = RequestMethod.POST)
	public void upload(@RequestParam(value = "zipfile", required = true) MultipartFile file,
			@ModelAttribute("project") projectWithBLOBs projectPo, HttpServletRequest request,
			HttpServletResponse response) {
		UploadResult uploadResult = null;

		uploadResult = FileUploadUtil.uploadFile(absolutePrefix +File.separator+ zipFileStorePath, projectPo.getTitleEn(),
				file);
		if (uploadResult.isSuccess()) {
			projectPo.setZipUrl( uploadResult.getFileName());
			projectPo.setIndexPath( projectPo.getTitleEn());
			projectPo.setIsShow("1");
			projectPo.setStatus("UNZIPPING");
			// 暂时没有处理异常所以关掉这个调用
			//this.projectServiceImpl.addProjectBase(projectPo);
			UnzipWorker unzipWorker = new UnzipWorker(uploadResult.getAbsoluteFilePath(),
					absolutePrefix + File.separator+projectRootPath, true, projectPo.getProjectIdString());

			unzipWorker.setPriority(1);
			unzipWorker.start();
			IndexingWorker indexingWorker = new IndexingWorker(
					absolutePrefix + File.separator+ projectRootPath + File.separator + projectPo.getTitleEn(),
					absolutePrefix + File.separator+indexRootPath+ File.separator+ projectPo.getIndexPath(), unzipWorker, projectPo.getProjectIdString());
			indexingWorker.start();
			//result.put("success", true);
		}
	}
	
	/**
	 * 上传文件处理地址
	 * 
	 * @param file
	 * @param projectPo
	 * @param request
	 * @param response
	@RequestMapping(value = "/doUpload.do", method = RequestMethod.POST)
	public void upload(@RequestParam(value = "zipfile", required = true) MultipartFile file,
			@ModelAttribute("project") ProjectBasePO projectPo, HttpServletRequest request,
			HttpServletResponse response) {
		IMap result = new IMap();
		UploadResult uploadResult = null;
		if (null != file && file.getSize() > 0) {
			try {

				uploadResult = FileUploadUtil.uploadFile(absolutePrefix +File.separator+ zipFileStorePath, projectPo.getTitleEn(),
						file);
				if (uploadResult.isSuccess()) {
					projectPo.setZipUrl( uploadResult.getFileName());
					projectPo.setIndexPath( projectPo.getTitleEn());
					projectPo.setIsShow("1");
					projectPo.setStatus(ProjectStatus.UNZIPPING);
					this.projectServiceImpl.addProjectBase(projectPo);
					UnzipWorker unzipWorker = new UnzipWorker(uploadResult.getAbsoluteFilePath(),
							absolutePrefix + File.separator+projectRootPath, true, projectPo.getProjectId());

					unzipWorker.setPriority(1);
					unzipWorker.start();
					IndexingWorker indexingWorker = new IndexingWorker(
							absolutePrefix + File.separator+ projectRootPath + File.separator + projectPo.getTitleEn(),
							absolutePrefix + File.separator+indexRootPath+ File.separator+ projectPo.getIndexPath(), unzipWorker, projectPo.getProjectId());
					indexingWorker.start();
					result.put("success", true);
				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				ResponseUtils.writeJSONString(response, uploadResult);
			}

			System.out.println(file.getSize());
		}

	}
	@ExceptionHandler(Exception.class)
    public ModelAndView handleException(Exception ex, HttpServletRequest request) {
        Map<String, String> model = new HashMap< String,String>();
        if (ex instanceof MaxUploadSizeExceededException) {
            model.put(
                    "errors",
                    "文件应不大于1GB");
        } else {
            model.put("errors", "不知错误: " + ex.getMessage());
        }
        return new ModelAndView("errors",(Map)model);
    }
    	 */

	public class PersistentWorker extends Thread {
		private List<file> fileList;
		private String name;

		public PersistentWorker(String name, List<file> fileList) {

			super(name);
			this.name = name;
			this.fileList = fileList;
		}

		public void run() {
			long start = System.currentTimeMillis();
			for (file po : fileList) {
				try {
					projectServiceImpl.addProjectFile(po);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			System.out.println("线程 " + name + "耗时:" + (System.currentTimeMillis() - start) + "ms");
		}

	}

	/**
	 * 索引线程类
	 * 
	 * @author liguanghui
	 *	 */
	Logger logger=Logger.getLogger(this.getClass());

	public class IndexingWorker extends Thread {
		private String srcPath;
		private String destPath;
		private UnzipWorker unzipWorker;
		private String projectId;

		public IndexingWorker(String srcPath, String destPath, UnzipWorker unzipWorker, String projectId) {
			this.srcPath = srcPath;
			this.destPath = destPath;
			this.unzipWorker = unzipWorker;
			this.projectId = projectId;
		}

		public IndexingWorker(String srcPath, String destPath, String projectId) {
			this.srcPath = srcPath;
			this.destPath = destPath;
			this.projectId = projectId;
		}


		@Override
		public void run() {
			IndexWriter indexWriter = null;
			logger.debug("=========>>>开始创建索引");
			Directory indexDirectory = null;
			try {

				if (null != unzipWorker) { // 如果有解压中的线程,则等待解压完毕
					unzipWorker.join();
				}
				// Path path=Paths.get(destPath)
				
				
				// 索引完毕,状态变为已完成
				long startTime = System.currentTimeMillis();
				List<file> poList = new ArrayList<file>();
				LinkedList<File> fileList = this.getAllFileRecuision(srcPath, projectId, "", poList);
				long endTile = System.currentTimeMillis();
				logger.debug("递归耗时:" + (endTile - startTime) + "ms");

				// 待插入的数据超过
				int pageSize = 100;

				if (poList.size() <= 100) {
					pageSize = 10;
				}
				if (1000 >= poList.size() && poList.size() >= 100) {
					pageSize = 10;
				}
				if (poList.size() > 1000) {
					pageSize = 50;
				}
				int thredCount = poList.size() % pageSize == 0 ? (poList.size() / pageSize)
						: (poList.size() / pageSize + 1);
				for (int i = 1; i <= thredCount; i++) {
					List<file> splitedList = new ArrayList<file>();
					if (i != thredCount) {
						splitedList.addAll(poList.subList((i - 1) * pageSize, i * pageSize));

					} else {
						splitedList.addAll(poList.subList((i - 1) * pageSize, poList.size()));
					}
					PersistentWorker persistentWorker = new PersistentWorker("pThread " + i, splitedList);
					persistentWorker.start();
				}
				indexDirectory=FSDirectory.open(FileSystems.getDefault().getPath(destPath));
				Analyzer analyzer = new SourceFileAnalyzer();
				IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
				indexWriter = new IndexWriter(indexDirectory, indexWriterConfig);
				indexWriter.deleteAll();// 清除以前的index
				if(null!=fileList){
					for (File file : fileList) {
						// 3、创建Document对象

						// if(!FileUploadUtil.isBinary(file)){
						// System.out.println("索引开始,文件" + file.getAbsolutePath() +
						// "是文本文件");
						Document document = new Document();
						// 4、为Document添加Field
						// 第三个参数是FieldType 但是定义在TextField中作为静态变量，看API也不好知道怎么写
						String sourceContent="";
						try{
							String fileEncoding=EncodingDetect.getJavaEncode(file);
							sourceContent=FileUtils.readFileToString(file, fileEncoding);
							document.add(new Field("content",sourceContent, TextField.TYPE_STORED));
							document.add(new Field("fileName", file.getName(), TextField.TYPE_STORED));
							String relativeFilePath=file.getAbsolutePath().substring((absolutePrefix+File.separator+projectRootPath).length());
							relativeFilePath=relativeFilePath.replaceAll("\\\\", "/"); //windows里转换separator
									
							document.add(new Field("filePath", relativeFilePath, TextField.TYPE_STORED));
							
							document.add(new Field("projectId",projectId,TextField.TYPE_STORED));
							// 5、通过IndexWriter添加文档到索引中
							indexWriter.addDocument(document);
						}catch(Exception ex){
							ex.printStackTrace();
//							System.out.println("無法創建索引:"+file.getAbsolutePath());
							logger.error("无法创建索引:"+file.getAbsolutePath());
							continue;
						}
						
						

					}
				}
				
				projectServiceImpl.updateProjectBaseStatus(projectId, "DONE");

			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}catch(Exception e){
				e.printStackTrace();
			}
			finally {
				try {
					if (indexWriter != null) {
						indexWriter.commit();
			            indexWriter.close();
			            indexDirectory.close();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			logger.debug("在" + destPath + "indexing结束");
//			System.out.println("在" + destPath + "indexing结束");
		}
		//ProjectFilePO 是文件的表的导出结构
		
		private LinkedList<File> getAllFileRecuision(String path, String projectId, String parentFileId,
				List<file> poList) {
			LinkedList<File> list = new LinkedList<File>();
			File file = new File(path);
			if (file.exists()) {
				File[] files = file.listFiles();
				if (files.length == 0) {
					// System.out.println("文件夹是空的!");
					return null;
				} else {
					for (File file2 : files) {
						try {
							if (file2.isDirectory()) {
								String relativePath=file2.getAbsolutePath().substring((absolutePrefix+File.separator+projectRootPath).length());
								relativePath=relativePath.replaceAll("\\\\","/"); 		//windows路径分隔符替换为unix格式
								/* 2020 11 03  这里是用来新增文件的？

								ProjectFilePO filePo = new ProjectFilePO(parentFileId,relativePath,
										FileType.DIRECTORY, null, projectId, file2.getName());
								filePo.setUuid(UUID.randomUUID().toString());
								poList.add(filePo);
								*/
								// projectServiceImpl.addProjectFile(filePo);

								// System.out.println("文件夹:" +
								// file2.getAbsolutePath());
								// LinkedList<File> fileList=
								// getAllFileRecuision(file2.getAbsolutePath(),projectId,filePo.getFileId());
								/* 2020 11 03 递归调用本函数自己
								LinkedList<File> fileList = getAllFileRecuision(file2.getAbsolutePath(), projectId,
										filePo.getUuid(), poList);
								if (null != fileList) {
									list.addAll(fileList);
								}
								*/

							} else {
								// System.out.println("文件:" +
								// file2.getAbsolutePath());

								list.add(file2);

								// parentFileId++;
								String relativePath=file2.getAbsolutePath().substring((absolutePrefix+File.separator+projectRootPath).length());
								relativePath=relativePath.replaceAll("\\\\","/"); 		//
								
								/* 2020 11 03  这里是用来新增文件的？
								projectWithBLOBs filePo = new projectWithBLOBs(parentFileId, relativePath,
										FileType.FILE, String.valueOf(file2.length()), projectId, file2.getName());
								filePo.setUuid(UUID.randomUUID().toString());
								poList.add(filePo);
																*/

								// projectServiceImpl.addProjectFile(filePo);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					return list;
				}
			} else {
				System.out.println("文件不存在!");
				return null;
			}
		}
	}

	/**
	 * 解压线程类
	 * 
	 * @author liguanghui
	 *	 */

	public class UnzipWorker extends Thread {

		private String zipFilePath;
		private String unzipFilePath;
		private boolean includeZipFileName;
		private String projectId;

		public UnzipWorker(String zipFilePath, String unzipFilePath, boolean includeZipFileName, String projectId) {
			this.zipFilePath = zipFilePath;
			this.unzipFilePath = unzipFilePath;
			this.includeZipFileName = includeZipFileName;
			this.projectId = projectId;
		}

		@Override
		public void run() {
			try {
				long startTime = System.currentTimeMillis();
				logger.debug("========>解压"+zipFilePath+"开始");
				// 解压完毕,状态变为待索引
				if(zipFilePath.endsWith(".zip")){
					logger.debug("压缩类型为 zip");
					FileUploadUtil.unzip(zipFilePath, unzipFilePath, includeZipFileName);
				}else if(zipFilePath.endsWith(".tar.gz")){
					logger.debug("压缩类型为 tar.gz");
					FileUploadUtil.unZipTarGz(zipFilePath, unzipFilePath, includeZipFileName);
				}
				logger.debug("<======unzip结束,耗时:" + (System.currentTimeMillis() - startTime) + "ms");
				// 解压完毕,状态变为待索引
				projectServiceImpl.updateProjectBaseStatus(projectId, "INDEXING");

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		public String getZipFilePath() {
			return zipFilePath;
		}

		public void setZipFilePath(String zipFilePath) {
			this.zipFilePath = zipFilePath;
		}

		public String getUnzipFilePath() {
			return unzipFilePath;
		}

		public void setUnzipFilePath(String unzipFilePath) {
			this.unzipFilePath = unzipFilePath;
		}

		public boolean isIncludeZipFileName() {
			return includeZipFileName;
		}

		public void setIncludeZipFileName(boolean includeZipFileName) {
			this.includeZipFileName = includeZipFileName;
		}

	}

}

