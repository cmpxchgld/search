package solr.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import solr.dataimport.IAttachmentManager;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@Service
public class FileUploadImpl implements FileUpload {
    // 上传文件存储目录
    @Value("#{solrURLProperties['UPLOAD_DIRECTORY']}")
    private String UPLOAD_DIRECTORY;
    @Value("#{solrURLProperties['picPath']}")
    private String picPath;
    @Autowired
    Timss4SolrService timss4SolrService;
    @Autowired
    IAttachmentManager attachManager;

    @Override
    public void upload(MultipartFile[] files, String site, String userid, String userName, String label) {
        // TODO Auto-generated method stub
        String filePath = "";
        for (MultipartFile file : files) {
            String fileName = site + file.getOriginalFilename();
            if (fileName.endsWith(".png") || fileName.endsWith(".jpg")) {
                filePath = picPath;
            }else {
                filePath = UPLOAD_DIRECTORY;
            }
            File targetFile = new File(filePath, fileName);
            if (!targetFile.exists()) {
                targetFile.setWritable(true, false);
                targetFile.mkdirs();
            }
            try {
                Map<String, Object> params = new HashMap<>();
                params.put("site", site);
                params.put("fileName", file.getOriginalFilename());
                params.put("userId", userid);
                params.put("userName", userName);
                params.put("label", label);
                params.put("url", filePath + fileName);
                timss4SolrService.insert(params, file.getInputStream());
                file.transferTo(targetFile);
                if (!fileName.endsWith(".png") || fileName.endsWith(".jpg")) {
                    attachManager.file2img(targetFile);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

}
