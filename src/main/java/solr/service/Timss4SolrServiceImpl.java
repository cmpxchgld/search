package solr.service;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import solr.bean.QResult;
import solr.bean.SolrPage;
import solr.dataimport.FileConvertFactory;
import solr.solrj.SolrCilentService;

import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 890213
 * @version 1.0
 * @date 2017/8/10 16:08
 */

@Service
public class Timss4SolrServiceImpl implements Timss4SolrService {

    @Autowired
    SolrCilentService solrCilentService;
    @Value("#{solrURLProperties['core']}")
    private String core;

    @Override
    public QResult query(String keyWord, String site, String type, int start, int rows) {
        String split = null;
        String queryFeild = null;
        String queryFilter = null;
        String queryKey = keyWord;
        if ((split = analyseKeyWords(keyWord)) != null) {
            String[] keys = keyWord.split(split);
            queryKey = keys[1];
            queryFilter = "file_label:*" + keys[0].replaceAll(" ", "").replaceAll("　", "") + "*";
            queryFeild = "file_keywords:" + keys[1].replaceAll(" ", "").replaceAll("　", "");
        } else {
            queryFeild = "file_keywords:" + keyWord.replaceAll(" ", "").replaceAll("　", "");
        }
        SolrPage params = new SolrPage();
        params.setStart(start);
        params.setRows(rows);
        Map<String, String> fields = new HashMap<String, String>();
        Map<String, String> param = new HashMap<String, String>();
        if (site != null) {
            fields.put("site", site);
        }
        if (type != null) {
            fields.put("file_type", type);
        }
        param.put("hl", "true");
        param.put("fl", "id,file_label,file_name,file_url,site");
        params.setParams(param);
        params.setFields(fields);
        QResult result = new QResult();
        try {
            QueryResponse response = solrCilentService.query(core, queryFeild, queryFilter, params);
            Map<String, Map<String, List<String>>> hl = response.getHighlighting();
            SolrDocumentList documentList = response.getResults();
            long numFound = documentList.getNumFound();
            result.setTotal(numFound);
            List<QResult.Doc> docList = new ArrayList<QResult.Doc>();
            DateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日");
            for (int i = 0; i < documentList.size(); i++) {
                QResult.Doc doc = result.new Doc();
                String fileName = null;
                if (hl.get(documentList.get(i).get("id")) != null) {
                    fileName = hl.get(documentList.get(i).get("id")).get("file_name") != null
                            ? Highlight(hl.get(documentList.get(i).get("id")).get("file_name").get(0), queryKey) : null;

                }
                String content = null;
                if (hl.get(documentList.get(i).get("id")) != null) {
                    content = hl.get(documentList.get(i).get("id")).get("file_content") != null
                            ? Highlight(hl.get(documentList.get(i).get("id")).get("file_content").get(0), queryKey) : null;
                }
                if (fileName == null && content == null) continue;
                if (fileName == null) {
                    fileName = documentList.get(i).get("file_name").toString();
                }
                doc.setTitle(fileName);
                if (content != null) {
                    String escapeContent = StringEscapeUtils.escapeHtml4(content).replaceAll("&lt;/em&gt;", "</em>").replaceAll("&lt;em&gt;", "<em>");
                    doc.setContent(escapeContent);
                }
                doc.setAuthor(
                        documentList.get(i).get("user") != null ? documentList.get(i).get("user").toString() : null);
                doc.setSite(documentList.get(i).get("site") != null
                        ? documentList.get(i).get("site").toString() : null);
                doc.setLabel(documentList.get(i).get("file_label") != null
                        ? documentList.get(i).get("file_label").toString() : null);
                doc.setSize(documentList.get(i).get("file_size") != null
                        ? documentList.get(i).get("file_size").toString() : null);
                doc.setUrl(documentList.get(i).get("file_url") != null ? documentList.get(i).get("file_url").toString()
                        : null);
                docList.add(doc);
            }
            result.setRows(docList);
            //result.setTotal(docList.size());
            result.setRetcode(QResult.SUCCESS);
            result.setCode(QResult.SUCCESS);
        } catch (Exception e) {
            result.setRetcode(QResult.FAILURE);
            result.setCode(QResult.FAILURE);
            result.setMsg("搜索引擎故障");
            e.printStackTrace();
        }
        return result;
    }

    public String Highlight(String content, String key) {
        String[] keys = null;
        content = content.replaceAll("<em>", "").replaceAll("</em>", "");
        if (key.contains(" ")) {
            keys = key.split(" ");
        } else if (key.contains("　")) {
            keys = key.split("　");
        }
        if (keys != null) {
            for (int i = 0; i < keys.length; i++) {
                if (StringUtils.isEmpty(keys[i].replaceAll(" ", "").replaceAll("　", "")))
                    continue;
                content = analyseResult(content, keys[i]);
            }
        } else {
            content = analyseResult(content, key);
        }
        if (content.contains("<em>")) {
            return content;
        } else return null;


    }

    public String analyseResult(String content, String key) {
        StringBuffer result = new StringBuffer();
        int len = key.length();
        int start = 0;
        int end = 0;
        while ((end = content.indexOf(key, start)) != -1) {
            result.append(content.substring(start, end));
            result.append("<em>").append(key).append("</em>");
            start = end + len;
        }
        if (start > 0 && start < content.length()) {
            result.append(content.substring(start, content.length()));
        }
        if (start != 0) {
            return result.toString();
        } else
            return content;
    }

    public String analyseKeyWords(String key) {
        if (StringUtils.isEmpty(key))
            return null;
        String[] param = new String[]{":", "："};
        for (int i = 0; i < param.length; i++) {
            if (key.contains(param[i])) {
                return param[i];
            }
        }
        return null;
    }

    @Override
    public QResult insert(Map<String, Object> params, InputStream fileStream) {
        QResult result = new QResult();
        SolrInputDocument doc = new SolrInputDocument();
        if (params.get("site") == null) {
            result.setRetcode(QResult.FAILURE);
            result.setCode(QResult.FAILURE);
            result.setMsg("缺少站点信息");
            return result;
        }
        FileConvertFactory fileConvertFactory = new FileConvertFactory();
        String content = fileConvertFactory.fileCovertText(fileStream, params.get("fileName").toString());
        doc.addField("id", params.get("site").toString() + params.get("fileName").toString());
        if (content != null) {
            doc.addField("file_content", content.replaceAll("\t", "").replaceAll("\n", "").replaceAll("\r", ""));
        }
        doc.addField("file_name", params.get("fileName"));
        doc.addField("site", params.get("site"));
        doc.addField("user", params.get("userName"));
        doc.addField("userId", params.get("userId"));
        doc.addField("file_url", params.get("url"));
        doc.addField("file_label", params.get("label"));
        try {
            solrCilentService.addIndex(core, doc);
            result.setRetcode(QResult.SUCCESS);
            result.setCode(QResult.SUCCESS);
            result.setMsg("索引操作成功");
        } catch (Exception e) {
            result.setRetcode(QResult.FAILURE);
            result.setCode(QResult.FAILURE);
            result.setMsg(e.getMessage());
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public QResult update(Map<String, Object> params, InputStream fileStream) {
        return null;
    }

    @Override
    public QResult delete(String fileName, String site, String userId) {
        return null;
    }

	/*public static void main(String[] args) {
        Timss4SolrServiceImpl tt = new Timss4SolrServiceImpl();
		String test = "字符s位置如果 startindex 是负数，则 startindex 被当作零。如果它比最大的字符位s置索引还大，则它被当作最大的可能索引。字符位s置";
		System.out.println(tt.Highlight(test, "字符s位置"));
		System.out.println(tt.analyseResult(tt.Highlight(test, "字符s位置"), " 字m符 "));
	}*/
}
