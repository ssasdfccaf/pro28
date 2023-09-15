package com.myspring.pro28.ex01;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;


@Controller
public class FileUploadController  {
	/* 임시 이미지 파일 (물리 저장소) */
	private static final String CURR_IMAGE_REPO_PATH = "c:\\spring\\image_repo";
	/* 파일 이미지 업로드를 위한 창 */
	@RequestMapping(value="/form") // 매핑 주소: form
	public String form() {
	    return "uploadForm";
	  }
	
	// 파일 이미지를 저장소에서 upload 처리 로직
	@RequestMapping(value="/upload",method = RequestMethod.POST)
	
	/* MultipartHttpServletRequest : 일반 + 파일 데이터 같이 처리 */
	public ModelAndView upload(MultipartHttpServletRequest multipartRequest,HttpServletResponse response)
	  throws Exception{
		
		// 폼 창에서 사용자가 입력한 일반 데이터 2개, 이미지 데이터 여러 개 미리 준비 ( 1번: 일반 데이터 , 2번: 파일 데이터 업로드 )
		multipartRequest.setCharacterEncoding("utf-8"); // 인코딩 utf-8
		Map map = new HashMap();
		Enumeration enu=multipartRequest.getParameterNames();
		while(enu.hasMoreElements()){
			String name=(String)enu.nextElement();
			String value=multipartRequest.getParameter(name);
			//System.out.println(name+", "+value);
			map.put(name,value);
		}
		
		/* 임의 메서드 fileProcess로 이동 */
		List fileList= fileProcess(multipartRequest); // 저장된 이미지 목록 가져오기 + 실제 물리경로 파일로 생성 및 저장
		
		map.put("fileList", fileList); // map 컬렉션 -> 파일 목록 저장
		ModelAndView mav = new ModelAndView();
		mav.addObject("map", map);
		mav.setViewName("result");
		return mav;
	}
	
	/* 실제 이미지 처리 로직 */
	private List<String> fileProcess(MultipartHttpServletRequest multipartRequest) throws Exception{
		
		List<String> fileList= new ArrayList<String>(); // List : 임시 이미지 파일 이름 저장
		
		Iterator<String> fileNames = multipartRequest.getFileNames();
		while(fileNames.hasNext()){
			
			String fileName = fileNames.next();
			// file1 처리하는 instance 생성
			MultipartFile mFile = multipartRequest.getFile(fileName);
			// mFile 인스턴스 도구 중에 실제 파일명 불러오는 메서드 사용
			String originalFileName=mFile.getOriginalFilename();
			/* 실제 저장소. 이미지 파일 업로드 하는 로직 */
			fileList.add(originalFileName);
			
			
			File file = new File(CURR_IMAGE_REPO_PATH +"\\"+ fileName); // (절대 경로 포함) 이미지 파일 처리를 위한 인스턴스
		
			if(mFile.getSize()!=0){ //File Null Check
				
				if(! file.exists()){ 
					// 파일 존재 안하면 실행
					if(file.getParentFile().mkdirs()){ 
						// 해당 경로의 부모 폴더를 만든 후 true 반환 -> 실행
						file.createNewFile(); 
						// 빈 파일 : 실제 경로에 이미지 파일 생성
					}
				}
				mFile.transferTo(new File(CURR_IMAGE_REPO_PATH +"\\"+ originalFileName)); //실제 경로에 저장 - 메모리에 있는 이미지를 실제 저장소에 옮기는 작업
			}
		}
		return fileList; // 저장된 파일 이름 목록 반환
	}
}
