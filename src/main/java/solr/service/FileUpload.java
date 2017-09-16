package solr.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileUpload {
	void upload(MultipartFile[] file,String site,String userid,String userName,String label);
}
