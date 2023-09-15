package com.myspring.pro28.ex01;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


/*@Controller*/
public class FileDownloadController {
	// 다운로드 위치 파일 이미지 메모리에 읽어서 출력 대상 : 웹 브라우저 -> 출력 대상: 보통 디비에 경로(로컬의 c드라이브, 보통 미디어 서버 URL) 저장 
	private static String CURR_IMAGE_REPO_PATH = "c:\\spring\\image_repo"; // C드라이브 image_repo에 임시 저장소 만들어 파일 이미지 불러오기

	@RequestMapping("/download")
	/* imageFileName 다운로드 후, 추가한 이미지들의 파일 이름들 */
	protected void download(@RequestParam("imageFileName") String imageFileName,
			                 HttpServletResponse response)throws Exception {
		// 메모리상에 임시로 저장된 이미지를 웹 브라우저에 출력하는 도구
		OutputStream out = response.getOutputStream();
		// 불러올 이미지가 저장된 위치 경로 ( 절대 경로 )
		String downFile = CURR_IMAGE_REPO_PATH + "\\" + imageFileName;
		// 파일 객체로 이미지가 저장된 파일을 메모리상에서 처리
		File file = new File(downFile); 

		// 캐시 저장 X , 매번 똑같이 파일 출력(재사용X)
		response.setHeader("Cache-Control", "no-cache");
		// 응답 객체에 파일 이름 첨부
		response.addHeader("Content-disposition", "attachment; fileName=" + imageFileName);
		
		FileInputStream in = new FileInputStream(file);
		// file = 절대 경로의 이미지, in = 물리 경로의 이미지 파일 읽어서 바이트 단위로 메모리에 저장(참조형 변수 이름으로 in 인스턴스에 담음)
		
		// 임시 저장소 버퍼라는 배열 만듦 ( 역할 : 이미지에 있는 바이트를 특정 크기만큼 잘라서 담는 공간)
		byte[] buffer = new byte[1024 * 8];
		
		/* 반복 작업 : in 메모리상에 바이트로 저장된 이미지를 out 객체에 옮기는 작업 */
		while (true) {
			
			int count = in.read(buffer); // in.read -> buffer 길이만큼 읽음
			if (count == -1) // 읽고 없으면 => 반환 음수로 파일 이미지르 다 읽은 것
				break;
			// 8byte씩 잘라서 out이라는 객체(메모리)에 옮겨 담기 - buffer라는 배열의 길이는 1024*8 전체 길이
			out.write(buffer, 0, count); // 시작 인덱스0 , count = 1024*8 길이만큼 복사
		}
		// 파일 읽는 객체 : 쓰는 객체 자원 반납
		in.close();
		out.close();
	}

}
