package com.example.backend.util;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.backend.user.entity.Image;
import com.example.backend.user.entity.User;

@Component
public class ImageUtil {
	String mimeType;

	public boolean validateFile(MultipartFile file) {
		List<String> fileExtensions = Arrays.asList("jpg", "png", "webp", "gif", "jpeg");
		String extension = FilenameUtils.getExtension(file.getOriginalFilename().toLowerCase());
		if (extension == null || !fileExtensions.contains(extension.toLowerCase())) {
			throw new IllegalArgumentException("파일이 올바르지 않습니다.");
		}
		long maxSize = 20 * 1024 * 1024; // 20MB
		long fileSize = file.getSize();
		if (fileSize > maxSize) {
			throw new IllegalArgumentException("파일용량이 너무 큽니다.");
		}
		return true;
	}

	public String uploadToS3(MultipartFile file, AmazonS3 amazonS3, String bucket) {
		// 새 S3 객체 업로드
		String extension = FilenameUtils.getExtension(file.getOriginalFilename().toLowerCase());
		String fileUuid = UUID.randomUUID() + "." + extension; // 해당 파일의 고유한 이름

		ObjectMetadata objectMetadata = new ObjectMetadata();
		objectMetadata.setContentType(mimeType(extension));
		objectMetadata.setContentLength(file.getSize());

		PutObjectRequest request;
		try {
			request = new PutObjectRequest(bucket, fileUuid, file.getInputStream(), objectMetadata);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		amazonS3.putObject(request);
		return fileUuid;
	}

	private String mimeType(String extension) {
		switch (extension) {
			case "jpg":
			case "jpeg":
				mimeType = "image/jpeg";
				break;
			case "png":
				mimeType = "image/png";
				break;
			case "webp":
				mimeType = "image/webp";
				break;
			case "gif":
				mimeType = "image/gif";
				break;
			default:
				throw new IllegalArgumentException("Unsupported file type: " + extension);
		}
		return mimeType;
	}

	public String getImageUrlFromUser(User user) {
		if(user.getImage()!=null){
			return user.getImage().getImageUrl();
		}else{
			return null;
		}
	}
}
