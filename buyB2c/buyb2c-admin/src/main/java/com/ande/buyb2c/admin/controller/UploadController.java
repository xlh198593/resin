package com.ande.buyb2c.admin.controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.ande.buyb2c.common.util.JsonResponse;
import com.ande.buyb2c.common.util.SystemCode;
/**上传文件controller*/
@RequestMapping("/upload")
@RestController
public class UploadController{
    private   Logger log = LoggerFactory.getLogger(UploadController.class);

/**
 * 
 * @param request
 * @param project
 * @param module
 * @param data 客户端数据回传
 * @return
 * @throws Exception
 */
	@RequestMapping(value = "/uploadimg/{project}/{module}/{data}", method = RequestMethod.POST)
	public JsonResponse<String> upload(HttpServletRequest request,
			@PathVariable String project,@PathVariable String module,@PathVariable String data) throws Exception {
    	JsonResponse<String> jr= uploadImg(request, project, module);
    	if(jr.getRes()==SystemCode.SUCCESS.getCode()){
    		jr.setResult(data);
    	}
    	return jr;
	}
	/**单图片上传
	 * 目录结构：项目名/商户名/模块
	 * 返回结构：一致
	 * */
	@ResponseBody
	@RequestMapping(value = "/uploadimg/{project}/{module}", method = RequestMethod.POST)
	public JsonResponse<String> uploadImg(HttpServletRequest request,
			@PathVariable String project,@PathVariable String module) throws Exception {
		MultipartHttpServletRequest r=(MultipartHttpServletRequest)request;
		MultipartFile mFile=r.getFile("file");
		return uploadFile(mFile, project, module);
	}
	/**
	 * 多文件上传
	 * 目录结构：项目名/商户名/模块
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/batch/{project}/{module}", method = RequestMethod.POST)
	    public JsonResponse<String> handleFileUpload(HttpServletRequest request,@PathVariable String project,@PathVariable String module) {  
	        List<MultipartFile> files = ((MultipartHttpServletRequest) request)  
	                .getFiles("file");  
	        JsonResponse<String> json=new JsonResponse<String>();
	        MultipartFile file = null;  
	        List<String> list=new ArrayList<String>();
	        for (int i = 0; i < files.size(); ++i) {  
	            file = files.get(i);  
	            if (!file.isEmpty()) {  
	            	json=uploadFile(file, project, module);
	            	if(json.getRes()==SystemCode.SUCCESS.getCode()){
	            		list.add(json.getObj());
	            	}
	            }  
	        }
	        if(list.size()!=0){
	        	json.setResult(SystemCode.SUCCESS.getMsg());
	        	json.setRes(SystemCode.SUCCESS.getCode());
	        	json.setList(list);
	        }
	        return json;  
	  
	    }  
	public JsonResponse<String> uploadFile(MultipartFile mFile,String project,String module){
		JsonResponse<String> jr=new JsonResponse<String>();
		if(mFile!=null){
			String OriginalfileName=mFile.getOriginalFilename();
			String fileFix = OriginalfileName.substring(OriginalfileName.lastIndexOf(".")+1);
			 if(mFile.getSize()>5242880){
				//如果文件大于5M就不给上传
				jr=new JsonResponse<String>();
				jr.setResult(SystemCode.FILE_SIZE_OUT.getMsg());
				jr.setRes(SystemCode.FILE_SIZE_OUT.getCode());
				return jr;
			}else{
				SimpleDateFormat sdf=new SimpleDateFormat("yyyyMM");
				String month=sdf.format(new Date());
				String fileDirPath="/file/"+project+"/"+module+"/"+month;
				try {
					String fileName = saveFile(mFile.getBytes(),fileFix,fileDirPath);
					jr.setResult(SystemCode.SUCCESS.getMsg());
					jr.setRes(SystemCode.SUCCESS.getCode());
					jr.setObj(fileDirPath+"/"+fileName);
					log.info("上传图片："+fileName+"成功，图片大小为："+mFile.getSize());
					return jr;
				} catch (IOException e) {
					log.error("图片上传失败", e);
				}
			}
		}
		jr.setResult(SystemCode.FAILURE.getMsg());
		jr.setRes(SystemCode.FAILURE.getCode());
		return jr;
	}
	public String saveFile(byte[] content, String fileFix,String path)
			throws IOException {
		File fileDir=new File(path);
		if(!fileDir.exists()){
			fileDir.mkdirs();
		}
		String saveFileName=System.currentTimeMillis()+"."+fileFix;
		FileOutputStream fo=new FileOutputStream(new File(path)+"/"+saveFileName);
		try {
			fo.write(content);
		} catch (Exception e) {
			throw new IOException(e);
		}finally{
			fo.flush();
			fo.close();
		}
		return saveFileName;
	}
	@RequestMapping("/removeFile")
	public  JsonResponse<String> remove(String filePath){
		String[] filePaths=filePath.split(",");
		JsonResponse<String> jr=new JsonResponse<String>();
		for(String fp:filePaths){
			File file=new File(fp);
			if(file.exists()){
				file.delete();
			}
			jr.setResult(SystemCode.SUCCESS.getMsg());
			jr.setRes(SystemCode.SUCCESS.getCode());
		}
		return jr;
	}
	/**
	 * 下载
	 * @param path
	 * @param response
	 * @return
	 */
	@RequestMapping("/downLoad")
	public HttpServletResponse download(String perImg,String fileName, HttpServletResponse response) {
        try {
        	  // perImg是指欲下载的文件的路径。
    		File file=new File(perImg);
            // 取得文件名。
            // 以流的形式下载文件。
            InputStream fis = new BufferedInputStream(new FileInputStream(perImg));
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            fis.close();
            // 清空response
            response.reset();
            // 设置response的Header
            fileName = URLEncoder.encode(fileName,"UTF-8");
            response.addHeader("Content-Disposition", "attachment;filename=" +fileName);
            response.addHeader("Content-Length", "" + file.length());
            OutputStream toClient = new BufferedOutputStream(response.getOutputStream());
            response.setContentType("application/octet-stream");
            toClient.write(buffer);
            toClient.flush();
            toClient.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return response;
    }
	/**
	 * 
	 * @param perImg
	 * @param fileName
	 * @param response
	 * @return 批量下载
	 *  perImg 文件路径 以','隔开，fileName zip包的名字
	 */
	@RequestMapping("/zipDownLoad")
	public HttpServletResponse zipDownLoad(String perImg, HttpServletResponse response) {
        try {
        	String strs[]=perImg.split(",");
        	File[] file1 = new File[strs.length];
        	long length=0;
	   		 for(int i=0;i<file1.length;i++){
	   			file1[i]=new File(strs[i]);
	   			length=length+file1[i].length();
	   		 }
	   		SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd");
			String month=sdf.format(new Date());
        	//fileName = URLEncoder.encode(fileName,"UTF-8");
	        String strZipName = month+".zip";
	    	response.addHeader("Content-Disposition", "attachment;filename=" +strZipName);
            //response.addHeader("Content-Length", ""+length);
            response.setContentType("application/octet-stream");
    		 ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(response.getOutputStream()));
    		
    		for(int i=0;i<file1.length;i++) {
    			FileInputStream fis = new FileInputStream(file1[i]);
    			byte[] buffer = new byte[fis.available()];
    			out.putNextEntry(new ZipEntry(file1[i].getName()));
    			int len;
    			while((len = fis.read(buffer))>0) {
    				out.write(buffer,0,len);
    			}
    			out.closeEntry();
    			fis.close();
    		}
    		out.flush();
    		out.close();
	      }
        catch (IOException ex) {
            ex.printStackTrace();
        }
        return response;
    }
	public static void main(String[] args) throws Exception {
		String s="1,2,";
		String[] str=s.split(",");
		System.out.println(str.length);
		for(String ss:str){
			System.out.println(ss+"---");
		}
	}
}
