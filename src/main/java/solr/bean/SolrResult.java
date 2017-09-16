package solr.bean;

import org.apache.poi.ss.formula.functions.T;

import java.util.List;
import java.util.Map;

/**
 * @author 890211
 */
public class SolrResult {
    public static final int SUCCESS=1;
    public static final int FAILURE=0;
    /**
     * 状态信息
     */
    private String msg;
    /**
     * 状态码
     */
    private String retcode;
    /**
     * key:数据类型
     * value:数据集
     */
    Map<String, List<T>> rows;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getRetcode() {
        return retcode;
    }

    public void setRetcode(String retcode) {
        this.retcode = retcode;
    }

    public Map<String, List<T>> getRows() {
        return rows;
    }

    public void setRows(Map<String, List<T>> rows) {
        this.rows = rows;
    }
}
