package solr.bean;

import java.util.Map;

/**
 * @author 890213
 * @version 1.0
 * @date 2017/8/10 11:10
 */

public class SolrPage {
    int start;
    int rows;
    String sort;
    Map<String,String> params;
    Map<String,String> fields;

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    public Map<String, String> getFields() {
        return fields;
    }

    public void setFields(Map<String, String> fields) {
        this.fields = fields;
    }
}
