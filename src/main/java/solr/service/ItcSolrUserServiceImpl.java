package solr.service;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import solr.bean.SolrOrganization;
import solr.bean.SolrPage;
import solr.bean.SolrUser;
import solr.bean.SolrUserResult;
import solr.dao.ItcSolrUserDao;
import solr.solrj.SolrCilentService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ItcSolrUserServiceImpl implements ItcSolrUserService {
    private static final Logger LOG = Logger.getLogger(ItcSolrUserServiceImpl.class);
    @Autowired
    private SolrCilentService solrCilentService;
    @Autowired
    private ItcSolrUserDao itcSolrUserDao;
    @Value("#{solrURLProperties['userCore']}")
    private String userCore;

    @Override
    public SolrUserResult query(String keyword, int start, int rows) {
        SolrPage params = new SolrPage();
        params.setStart(start);
        params.setRows(rows);
        //fields 可以做拼接
        Map<String, String> fields = new HashMap<String, String>();
        Map<String, String> param = new HashMap<String, String>();
        //设置返回的域
        param.put("fl", "user_id,user_name,user_job,user_title,user_email,user_mobile,user_office_tel,user_micor_tel");
        params.setParams(param);

        SolrUserResult solrUserResult = new SolrUserResult();
        try {
            //查询索引库
            QueryResponse queryResponse = solrCilentService.queryUser(userCore, "user_keyword:*" + keyword + "*", "", params);

            //返回solrUserResult对象
            SolrDocumentList documentList = queryResponse.getResults();
            List<SolrUser> userList = new ArrayList<SolrUser>();
            for (int i = 0; i < documentList.size(); i++) {
                SolrUser solrUser = new SolrUser();
                solrUser.setUserId(documentList.get(i).get("user_id") != null ? documentList.get(i).get("user_id").toString() : null);
                solrUser.setName(documentList.get(i).get("user_name") != null ? documentList.get(i).get("user_name").toString() : null);
                solrUser.setName(documentList.get(i).get("user_name_pingyin") != null ? documentList.get(i).get("user_name").toString() : null);
                solrUser.setJob(documentList.get(i).get("user_job") != null ? documentList.get(i).get("user_job").toString() : null);
                solrUser.setTitle(documentList.get(i).get("user_title") != null ? documentList.get(i).get("user_title").toString() : null);
                solrUser.setEmail(documentList.get(i).get("user_email") != null ? documentList.get(i).get("user_email").toString() : null);
                solrUser.setMobile(documentList.get(i).get("user_mobile") != null ? documentList.get(i).get("user_mobile").toString() : null);
                solrUser.setOfficeTel(documentList.get(i).get("user_office_tel") != null ? documentList.get(i).get("user_office_tel").toString() : null);
                solrUser.setMicorTel(documentList.get(i).get("user_micor_tel") != null ? documentList.get(i).get("user_micor_tel").toString() : null);
                userList.add(solrUser);
            }
            long numFound = documentList.getNumFound();
            solrUserResult.setTotal(numFound);
            solrUserResult.setRows(userList);
            solrUserResult.setMsg("查询用户成功");
            solrUserResult.setRetcode(1);
        } catch (Exception e) {
            solrUserResult.setRows(new ArrayList<SolrUser>());
            solrUserResult.setMsg("查询失败");
            solrUserResult.setRetcode(0);
            e.printStackTrace();
        }
        return solrUserResult;
    }

    @Override
    public SolrUserResult update() {
        return null;
    }

    @Override
    public SolrUserResult delete() {
        return null;
    }

    @Override
    public SolrUserResult insert() {

        SolrUserResult solrUserResult = new SolrUserResult();
        //查询所有用户
        List<SolrUser> userList = itcSolrUserDao.queryUser();
        //根据用户id 获取部门名称
        for (SolrUser solrUser : userList) {
            String dept = getDeptName(solrUser);
            SolrInputDocument solrDocument = new SolrInputDocument();
            solrDocument.addField("id", solrUser.getUserId());
            solrDocument.addField("user_id", solrUser.getUserId());
            solrDocument.addField("user_name", solrUser.getName());
            solrDocument.addField("user_job", solrUser.getJob());
            solrDocument.addField("user_title", solrUser.getTitle());
            solrDocument.addField("user_email", solrUser.getEmail());
            solrDocument.addField("user_mobile", solrUser.getMobile());
            solrDocument.addField("user_office_tel", solrUser.getOfficeTel());
            solrDocument.addField("user_micor_tel", solrUser.getMicorTel());
            solrDocument.addField("user_dept", dept);
            try {
                solrCilentService.addIndex(userCore, solrDocument);
                solrUserResult.setMsg("添加索引成功");
                solrUserResult.setRetcode(SolrUserResult.SUCCESS);
                LOG.info("-------------insert success--------------");
            } catch (Exception e) {
                LOG.info("-------------insert fail--------------");
                solrUserResult.setMsg("添加索引失败");
                solrUserResult.setRetcode(SolrUserResult.FAILURE);
                e.printStackTrace();
            }
        }


        return null;
    }


    /**
     * 获取部门名称
     *
     * @param solrUser
     * @return
     */
    private String getDeptName(SolrUser solrUser) {
        String dept = "";
        String userId = solrUser.getUserId();
        //获取用户所在的所有部门
        List<String> deptCodes = itcSolrUserDao.queryDeptCodeByUserId(userId);
        for (String deptCode : deptCodes) {
            //获取所有父级部门
            List<SolrOrganization> solrOrganizations = itcSolrUserDao.queryUserDeptment(deptCode);
            for (SolrOrganization solrOrganization : solrOrganizations) {
                String parentCode = solrOrganization.getParentCode();
                if ("1".equals(parentCode)) {
                    dept = dept + " " + solrOrganization.getShotName();
                } else {
                    dept = dept + "/" + solrOrganization.getName();
                }
            }
        }
        return dept;
    }
}
