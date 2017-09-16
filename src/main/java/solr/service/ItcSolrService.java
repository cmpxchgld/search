package solr.service;

import solr.bean.SolrResult;

/**
 * ItcSolrService
 * solr 统一接口
 */
public interface ItcSolrService {
    /**
     * 查询
     * @param params the params
     * @return solr result
     */
    SolrResult query(String params);


}
