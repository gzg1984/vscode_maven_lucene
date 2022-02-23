package util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.lang3.StringUtils;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Expand;
import org.springframework.web.multipart.MultipartFile;

import model.UploadResult;

public class FileUploadUtil {

	/**
	 * 上传文件到指定的路径
	 * 
	 * @param dir
	 *            保存zip文件的路径
	 * @param fileName
	 *            zip文件名
	 * @param file
	 *            MultipartFile
	 * @return 执行结果 返回0 代表成功 -1代表失败
	 */
	public static UploadResult uploadFile(String dir, String fileName,
			MultipartFile file) {

		if (file.getSize() > 0) {
			String originalFileName = file.getOriginalFilename();
			String extension="";
			if(originalFileName.endsWith(".tar.gz")){
				 extension=".tar.gz";
			}else{
				 extension = originalFileName.substring(
							originalFileName.lastIndexOf("."),
							originalFileName.length());
			}
			File targetFile = new File(dir, fileName + extension);
			if (!targetFile.exists()) {
				targetFile.mkdirs();
			}
			// 保存
			try {
				file.transferTo(targetFile);
				return new UploadResult(0, targetFile.getAbsolutePath(),
						fileName + extension, file.getSize(), extension, true,
						"上传成功");
			} catch (IllegalStateException e) {
				e.printStackTrace();
				return new UploadResult(-1, false, "文件不存在");
			} catch (IOException e) {
				e.printStackTrace();
				return new UploadResult(-2, false, "上传过程中出现错误");
			}
		} else {
			return new UploadResult(-1, false, "文件不存在");
		}

	}

	/**
	 * 判断文件是否是二进制文件
	 * 
	 * @param file
	 * @return
	 */
	@SuppressWarnings("resource")
	public static boolean isBinary(File file) {
		boolean isBinary = false;
		try {
			FileInputStream fin = new FileInputStream(file);
			long len = file.length();
			for (int j = 0; j < (int) len; j++) {
				int t = fin.read();
				if (t < 32 && t != 9 && t != 10 && t != 13) {
					isBinary = true;
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return isBinary;
	}

	public static void zip(String sourcePath, String destPath) {

	}

	/**
	 * 解压zip压缩文件
	 * 
	 * @param zipFilePath
	 * @param unzipFilePath
	 * @param includeZipFileName
	 * @throws Exception
	 */
	@SuppressWarnings({ "resource", "unchecked" })
	public static void unzip(String zipFilePath, String unzipFilePath,
			boolean includeZipFileName) throws Exception {
		if (StringUtils.isEmpty(zipFilePath)
				|| StringUtils.isEmpty(unzipFilePath)) {
			throw new Exception("路径不存在");
		}
		File zipFile = new File(zipFilePath);
		// 如果解压后的文件保存路径包含压缩文件的文件名，则追加该文件名到解压路径
		if (includeZipFileName) {
			String fileName = zipFile.getName();
			if (StringUtils.isNotEmpty(fileName)) {
				fileName = fileName.substring(0, fileName.lastIndexOf("."));
			}
			unzipFilePath = unzipFilePath + File.separator + fileName;
		}
		// 创建解压缩文件保存的路径
		File unzipFileDir = new File(unzipFilePath);
		if (!unzipFileDir.exists() || !unzipFileDir.isDirectory()) {
			unzipFileDir.mkdirs();
		}
		/* 2020 11 03 暂时找不到 ZipFile 类型，反正要用脚本替代，这里直接禁掉
		ZipFile zip = new ZipFile(zipFile, Charset.forName("UTF-8"));
		try{
			writeZipEntries(zip, unzipFilePath);
		}catch(IllegalArgumentException ex){
			zip = new ZipFile(zipFile, Charset.forName("GBK"));
			writeZipEntries(zip, unzipFilePath);
		}
		*/
		// 循环对压缩包里的每一个文件进行解压
		
	}
	private static void writeZipEntries(ZipFile zip,String unzipFilePath) throws IllegalArgumentException, IOException{
		// 开始解压
		ZipEntry entry = null;
		String entryFilePath = null;
		String entryDirPath = null;
		File entryFile = null;
		File entryDir = null;
		int index = 0, count = 0, bufferSize = 1024;
		byte[] buffer = new byte[bufferSize];
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
		
		Enumeration<ZipEntry> entries = (Enumeration<ZipEntry>) zip.entries();
		
		
		while (entries.hasMoreElements()) {
			entry = entries.nextElement();
			entryFilePath = unzipFilePath + File.separator + entry.getName();
			// 构建解压后保存的文件夹路径
			index = entryFilePath.lastIndexOf(File.separator);
			if (index != -1) {
				entryDirPath = entryFilePath.substring(0, index);
			} else {
				entryDirPath = "";
			}
			entryDir = new File(entryDirPath);
			if (entry.isDirectory()) {
				entryFile = new File(entryFilePath);
				entryFile.mkdirs();
				continue;
			} else {
				// 如果文件夹路径不存在，则创建文件夹
				if (!entryDir.exists() || !entryDir.isDirectory()) {
					entryDir.mkdirs();
				}
				// 创建解压文件
				entryFile = new File(entryFilePath);
				if (entryFile.exists()) {
					// 删除已存在的目标文件
					entryFile.delete();
				}

				// 写入文件
				bos = new BufferedOutputStream(new FileOutputStream(entryFile));
				bis = new BufferedInputStream(zip.getInputStream(entry));
				while ((count = bis.read(buffer, 0, bufferSize)) != -1) {
					bos.write(buffer, 0, count);
				}
				bos.flush();
				bos.close();
			}

		}
	}
	public static String getEncoding(String str) {
		String encode = "GB2312";
		try {
			if (str.equals(new String(str.getBytes(encode), encode))) {
				String s = encode;
				return s;
			}
		} catch (Exception exception) {
		}
		encode = "ISO-8859-1";
		try {
			if (str.equals(new String(str.getBytes(encode), encode))) {
				String s1 = encode;
				return s1;
			}
		} catch (Exception exception1) {
		}
		encode = "UTF-8";
		try {
			if (str.equals(new String(str.getBytes(encode), encode))) {
				String s2 = encode;
				return s2;
			}
		} catch (Exception exception2) {
		}
		encode = "GBK";
		try {
			if (str.equals(new String(str.getBytes(encode), encode))) {
				String s3 = encode;
				return s3;
			}
		} catch (Exception exception3) {
		}
		return "";
	}

	public static void apacheUnzip(String zipFilepath, String destDir) {
		try {
			if (!new File(zipFilepath).exists())
				throw new RuntimeException(
						"zip file " + zipFilepath + " does not exist.");
			Project proj = new Project();
			Expand expand = new Expand();
			expand.setProject(proj);
			expand.setTaskType("unzip");
			expand.setTaskName("unzip");
			// expand.setEncoding("GBK");

			expand.setSrc(new File(zipFilepath));
			expand.setDest(new File(destDir));
			expand.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void compress(ZipOutputStream out, BufferedOutputStream bos,
			File sourceFile, String base,boolean encludeRootDir) throws Exception {
		// 如果路径为目录（文件夹）
		if (sourceFile.isDirectory()) {

			// 取出文件夹中的文件（或子文件夹）
			File[] flist = sourceFile.listFiles();

			if (flist.length == 0)// 如果文件夹为空，则只需在目的地zip文件中写入一个目录进入点
			{
//				System.out.println(base + "/");
				out.putNextEntry(new ZipEntry(base + File.separator));
				
				
			} else// 如果文件夹不为空，则递归调用compress，文件夹中的每一个文件（或文件夹）进行压缩
			{
				for (int i = 0; i < flist.length; i++) {
					
					compress(out, bos, flist[i],
							base +  File.separator + flist[i].getName(),encludeRootDir);
				}
			}
		} else// 如果不是目录（文件夹），即为文件，则先写入目录进入点，之后将文件写入zip文件中
		{
			out.putNextEntry(new ZipEntry(base));
			FileInputStream fos = new FileInputStream(sourceFile);
			BufferedInputStream bis = new BufferedInputStream(fos);

			int tag;
//			System.out.println(base);
			// 将源文件写入到zip文件中
			while ((tag = bis.read()) != -1) {
				bos.write(tag);
			}
			bis.close();
			fos.close();

		}
	}
	
	public static void unZipTarGz(String gzFilePath,String destPath,boolean includeTarFileName) throws IOException{
		File gzFile=new File(gzFilePath);
		InputStream fin = new FileInputStream(gzFile);
		BufferedInputStream in = new BufferedInputStream(fin);
		File tarFile=new File(gzFile.getParent()+File.separator+gzFile.getName().substring(0,gzFile.getName().lastIndexOf(".")));
		OutputStream out = new FileOutputStream(tarFile);
		GzipCompressorInputStream gzIn = new GzipCompressorInputStream(in);
		final byte[] buffer = new byte[8192];
		int n = 0;
		while (-1 != (n = gzIn.read(buffer))) {
		    out.write(buffer, 0, n);
		}
		out.close();
		gzIn.close();
		FileUploadUtil.unZipTar(tarFile, destPath, includeTarFileName);
		
	}
	 /** 
     * 构建目录 
     * @param outputDir 
     * @param subDir 
     */  
    public static void createDirectory(String outputDir,String subDir){     
        File file = new File(outputDir);  
        if(!(subDir == null || subDir.trim().equals(""))){//子目录不为空  
            file = new File(outputDir + "/" + subDir);  
        }  
        if(!file.exists()){  
              if(!file.getParentFile().exists())
                  file.getParentFile().mkdirs();
            file.mkdirs();  
        }  
    }
	private static void unZipTar(File tarFile,String destPath,boolean includeTarFileName) throws IOException {

        TarArchiveInputStream tais = 
        new TarArchiveInputStream(new FileInputStream(tarFile));

        TarArchiveEntry tarArchiveEntry = null;
        if(includeTarFileName){
        	destPath+=File.separator+tarFile.getName().substring(0,tarFile.getName().lastIndexOf("."));
        }
        while((tarArchiveEntry = tais.getNextTarEntry()) != null){
            String name = tarArchiveEntry.getName();
            File file = new File(destPath, name);
            if(tarArchiveEntry.isDirectory()){
                createDirectory(destPath, tarArchiveEntry.getName());
           }else{
        	   BufferedOutputStream bos = 
	            new BufferedOutputStream(new FileOutputStream(file));

	            int read = -1;
	            byte[] buffer = new byte[1024];
	            while((read = tais.read(buffer)) != -1){
	                bos.write(buffer, 0, read);
	            }
	            bos.close();
           }
           
        }
        tais.close();
        tarFile.delete();//删除tar文件
    }
}
