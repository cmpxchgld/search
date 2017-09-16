package solr.solrj;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import solr.bean.SolrPage;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author 890213
 * @version 1.0
 * @date 2017/8/10 10:16
 */

@Service
public class SolrClientServiceImpl implements SolrCilentService {

    private HttpSolrClient solrClient;

    SolrClientServiceImpl(@Value("#{solrURLProperties['url']}") String url) {
        solrClient = new HttpSolrClient(url);
    }

    @Override
    public void addIndexs(String core, Collection<SolrInputDocument> documents) throws Exception {
        solrClient.add(core, documents);
        solrClient.commit(core);
    }

    @Override
    public void addIndex(String core, SolrInputDocument document) throws Exception {
        solrClient.add(core, document);
        solrClient.commit(core);
    }

    @Override
    public void deleteIndexs(String core, List<String> indexIds) throws Exception {
        solrClient.deleteById(core, indexIds);
        solrClient.commit(core);
    }

    @Override
    public void deleteIndex(String core, String indexId) throws Exception {
        solrClient.deleteById(core, indexId);
        solrClient.commit(core);
    }

    @Override
    public QueryResponse query(String core, String q, String fq, SolrPage params) throws Exception {
        SolrQuery query = new SolrQuery();
        //高亮
        if (params.getParams().get("hl") != null) {
            query.setHighlight(true);
            query.addHighlightField("file_content,file_name");
            /*query.setHighlightSimplePre("<font color='red'>");
            query.setHighlightSimplePost("</font>");*/
            query.setHighlightFragsize(90);
        }
        //指定显示字段
        if (params.getParams().get("fl") != null) {
            query.set("fl", params.getParams().get("fl"));
        }
        //拼接操作 ?
        StringBuilder queryParams = new StringBuilder(q);
        if (params != null) {
            query.setStart(params.getStart());
            query.setRows(params.getRows());
            if (params.getFields() != null) {
                for (Map.Entry<String, String> entry : params.getFields().entrySet()) {
                    queryParams.append(" AND " + entry.getKey() + ":" + entry.getValue());
                }
            }
        }
        if (fq != null) {
            query.addFilterQuery(fq);
        }
        query.setQuery(queryParams.toString());
        return solrClient.query(core, query);
    }

    @Override
    public QueryResponse queryUser(String userCore, String q, String fq, SolrPage params) throws Exception {
        SolrQuery solrQuery = new SolrQuery();
        if(params!=null) {
            solrQuery.setStart(params.getStart());
            solrQuery.setRows(params.getRows());
            //指定返回字段
            if (params.getParams().get("fl") != null) {
                solrQuery.set("fl", params.getParams().get("fl"));
            }
        }
        //设置搜索域
        solrQuery.setQuery(q);
        return solrClient.query(userCore, solrQuery);
    }

}
