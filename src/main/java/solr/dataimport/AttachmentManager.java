package solr.dataimport;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

@Service
public class AttachmentManager implements IAttachmentManager {
    @Value("#{solrURLProperties['docxUrl']}")
    private String docxUrl;
    @Value("#{solrURLProperties['pubId']}")
    private String pubId;
    @Value("#{solrURLProperties['notifyUrl']}")
    private String notifyUrl;
    @Value("#{solrURLProperties['imgType']}")
    private String imgType;
    @Value("#{solrURLProperties['imgSize']}")
    private String imgSize;
    @Value("#{solrURLProperties['picCompressedFile']}")
    private String picCompressedFile;
    @Value("#{solrURLProperties['picPath']}")
    private String  picPath;

    /**
     * 上传附件到远程服务器
     *
     * @param attachment 当前附件信息
     * @return
     */
    @Override
    public Map<String, Object> file2img(File file) {
        Map<String, Object> jObj = null;
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        String fileId = file.getName();
        try {

            if (null != file) {
                httpClient = HttpClients.createDefault();
                HttpPost httpPost = new HttpPost(docxUrl + "restful/upload");
                MultipartEntityBuilder builder = MultipartEntityBuilder.create();
                ContentType contentType = ContentType.create("text/plain", Charset.forName("UTF-8"));
                builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
                builder.addTextBody("pubId", pubId, ContentType.TEXT_PLAIN);
                builder.addTextBody("imgType", "png-md", ContentType.TEXT_PLAIN);
                builder.addTextBody("notifyUrl", notifyUrl, ContentType.TEXT_PLAIN);
                builder.addTextBody("notifyArgs", "{\"fileId\":\"" + fileId + "\"}", contentType);

                if (file.exists()) {
                    builder.addBinaryBody("file", file, ContentType.APPLICATION_OCTET_STREAM, fileId);
                    httpPost.setEntity(builder.build());
                    response = httpClient.execute(httpPost);
                    String jStr = EntityUtils.toString(response.getEntity());
                    jObj = JSON.parseObject(jStr, new TypeReference<Map<String, Object>>() {
                    });
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (null != response) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return jObj;
    }

    @Override
    public void callBack2ReceiveNotify(String id, int status, String args) {
        Map<String, Object> jObj = JSON.parseObject(args, new TypeReference<Map<String, Object>>() {
        });
        String fileId;
        try {
            fileId = URLDecoder.decode(String.valueOf(jObj.get("fileId")), "UTF-8");
            int total = jObj.get("total") == null ? 0 : Integer.valueOf(String.valueOf(jObj.get("total")));
            if (status == 3) {
                // 转换成功
                Thread t = new Thread(new Runnable() {
                    @Override
					public void run() {
						// 执行下载
						CloseableHttpClient httpclient = HttpClients.createDefault();
						try {
							for (int i = 1; i <= total; i++) {
								downloadImage(httpclient, id, i, fileId);
								Thread.sleep(200);
							}
						} catch (Exception e) {
							e.getStackTrace();
						} finally {

							if (null != httpclient) {
								try {
									httpclient.close();
								} catch (IOException e) {
									e.getStackTrace();
								}
							}
						}
					}
				});
                t.start();
            }
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
    }


    /**
     * 远程图片下载
     *
     * @param client
     * @param id
     * @param i
     */
    private void downloadImage(HttpClient client, String id, int i, String fileId) {
        StringBuilder sbUrl = new StringBuilder(docxUrl);
        sbUrl.append("restful/image/").append(id).append("/");
        sbUrl.append(imgSize).append("/").append(imgType).append("/").append(i);
        HttpGet httpget = new HttpGet(sbUrl.toString());
        InputStream inputStream = null;
        OutputStream outputStream = null;
        HttpResponse response = null;
        HttpEntity entity = null;
        try {
            response = client.execute(httpget);
            entity = response.getEntity();
            File path = new File(picPath + fileId);
            if (!path.exists())
                path.setWritable(true, false);
            path.mkdirs();
            File targetFile = new File(picPath + fileId + "/" + i + ".png");
            if (entity != null) {
                inputStream = entity.getContent();
                outputStream = new FileOutputStream(targetFile);
                byte[] buffer = new byte[4096];
                long count = 0;
                int n = 0;
                while (-1 != (n = inputStream.read(buffer))) {
                    outputStream.write(buffer, 0, n);
                    count += n;
                }
                outputStream.close();
            }
        } catch (Exception ex) {
            ex.getStackTrace();
        } finally {
            try {
                if (null != outputStream) {
                    outputStream.close();
                }
                if (null != inputStream) {
                    inputStream.close();
                }
                if (null != entity) {
                    httpget.releaseConnection();
                }
            } catch (Exception e) {
                e.getStackTrace();
            }
        }
    }
}
