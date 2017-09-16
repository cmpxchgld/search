package solr.dao;

import org.springframework.stereotype.Repository;
import solr.bean.SolrOrganization;
import solr.bean.SolrUser;
import java.util.List;
@Repository
public interface ItcSolrUserDao {
    /**
     * 查询用户信息
     * @return
     */
    List<SolrUser> queryUser();

    /**
     * 根据用户id查询所属部门
     * @param userId
     * @return
     */
    List<String> queryDeptCodeByUserId(String userId);

    /**
     * 根据用户管理的部门查询所有父级部门信息
     * @param orgCode
     * @return
     */
    List<SolrOrganization> queryUserDeptment(String orgCode);

}
