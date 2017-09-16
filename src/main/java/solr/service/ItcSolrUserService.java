package solr.service;


import solr.bean.SolrUserResult;

/**
 * The interface Itc solr user service.
 *
 * @author 890211
 * @version 1.0
 */
public interface ItcSolrUserService {

    SolrUserResult query(String keyword,int start,int rows);

    SolrUserResult update();

    SolrUserResult delete();

    /**
     * 从数据库导入索引库
     * @return
     */
    SolrUserResult insert();



}
