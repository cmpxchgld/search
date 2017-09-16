package solr.service;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;
import org.apache.log4j.Logger;
import org.apache.solr.common.SolrInputDocument;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import solr.bean.SolrUserResult;
import solr.bean.esb.EmpBean;
import solr.solrj.SolrCilentService;
import solr.utils.ParseXmlUtils;
import solr.utils.Pingyin4j;

import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/*.xml"})
public class TestSolrUsersService {

    @Autowired
    private ItcSolrUserService itcSolrUserService;
    @Autowired
    private SolrCilentService solrCilentService;
    @Value("#{solrURLProperties['userCore']}")
    private String userCore;

    private static final Logger LOG = Logger.getLogger(TestSolrUsersService.class);
    @Test
    public void testUploadPng() {

    }

    @Test
    public void test() {
        SolrUserResult insert = itcSolrUserService.insert();
        System.out.println(insert.toString());
    }

    /**
     * 根据员工号获取员工信息
     */
    @Test
    public void testgetEmpByNumCode() {
        try {
            JaxWsDynamicClientFactory clientFactory = JaxWsDynamicClientFactory.newInstance();
            Client client = clientFactory.createClient("http://udx-dev.gdyd.com:8081/services/v2/EmployeeService?wsdl");
            Object[] emp = new Object[0];
            emp = client.invoke("getEmpByNumCode", "300861");
            System.out.println(emp.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据员工名称获取员工信息
     */
    @Test
    public void testgetEmpByName() {
        try {
            JaxWsDynamicClientFactory clientFactory = JaxWsDynamicClientFactory.newInstance();
            Client client = clientFactory.createClient("http://udx-dev.gdyd.com:8081/services/v2/EmployeeService?wsdl");
            Object[] deptsCode = client.invoke("getEmpByName", "曾辉");
            System.out.println(deptsCode.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 测试获取所有员工信息
     */
    @Test
    public void testgetAllEmployee() {
        Date starTime = new Date();
        List<SolrInputDocument> solrInputDocuments = new ArrayList<SolrInputDocument>();
        try {
            JaxWsDynamicClientFactory clientFactory = JaxWsDynamicClientFactory.newInstance();
            Client client = clientFactory.createClient("http://udx-dev.gdyd.com:8081/services/v2/EmployeeService?wsdl");
            Object[] deptsCode = client.invoke("getAllEmployee");
            List<EmpBean> parseBean = ParseXmlUtils.parse(deptsCode[0].toString(), EmpBean.class);
            for (EmpBean empBean : parseBean) {
                String deptPathName = getDeptPathName(client, empBean);
                String employeename = empBean.getEmployeename();
                List<String> hardPinyins = Pingyin4j.getHardPinyins(employeename);
                String pingyin = "";
                for (String s : hardPinyins) {
                    pingyin += s;
                }
                SolrInputDocument solrDocument = new SolrInputDocument();
                solrDocument.addField("id", empBean.getEmployeenumber());
                solrDocument.addField("user_id", empBean.getEmployeenumber());
                solrDocument.addField("user_name", empBean.getEmployeename());
                solrDocument.addField("user_name_pingyin", pingyin);
                solrDocument.addField("user_job", empBean.getEmployeejob());
                solrDocument.addField("user_title", empBean.getEmployeetitle());
                solrDocument.addField("user_email", empBean.getEmail());
                solrDocument.addField("user_mobile", empBean.getMobilephone());
                solrDocument.addField("user_office_tel", empBean.getOfficeTelephone());
                solrDocument.addField("user_micor_tel", empBean.getMicroTelephone());
                solrDocument.addField("user_dept", deptPathName);
                solrInputDocuments.add(solrDocument);
            }
            solrCilentService.addIndexs(userCore, solrInputDocuments);
            LOG.info("-------------insert success--------------");
        } catch (Exception e) {
            LOG.info("-------------insert fail--------------");
            e.printStackTrace();
        }
        Date endTime = new Date();
        System.out.println(starTime + "" + endTime);
    }

    private String getDeptPathName(Client client, EmpBean empBean) throws Exception {

        StringBuffer deptName = new StringBuffer("");
        Object[] deptsCodes = client.invoke("getDeptPathByNumCode", empBean.getEmployeenumber());
        List deptCodePath = getDeptCodePath(deptsCodes);
        for (int i = 0; i < deptCodePath.size(); i++) {
            Object[] deptNamePaths = client.invoke("getDeptNamePathByDeptCodePath", deptCodePath.toArray()[i]);
            String deptNamePath = deptNamePaths[0].toString();
            String deptPaths = deptNamePath.substring(0, deptNamePath.lastIndexOf("/"));
            String deptname = empBean.getShortComName() + deptPaths.substring(deptPaths.indexOf("/"));
            deptName.append(deptname).append(",");
        }
        String substring = deptName.toString().substring(0, deptName.length() - 1);
        return substring;
    }

    /**
     * 测试根据员工号获取部门路径
     */
    @Test
    public void testEip() {
        try {
            JaxWsDynamicClientFactory clientFactory = JaxWsDynamicClientFactory.newInstance();
            Client client = clientFactory.createClient("http://udx-dev.gdyd.com:8081/services/v2/EmployeeService?wsdl");
            Object[] deptsCode = client.invoke("getDeptPathByNumCode", "280825");
            List deptCodePath = getDeptCodePath(deptsCode);
            StringBuffer deptName = new StringBuffer("");
            for (Object code : deptCodePath) {
                Object[] deptNamePaths = client.invoke("getDeptNamePathByDeptCodePath", code);
                String deptNamePath = deptNamePaths[0].toString();
                String substring = deptNamePath.substring(0, deptNamePath.lastIndexOf("/"));
                deptName.append(substring).append(",");
            }
            String substring = deptName.toString().substring(0, deptName.length() - 1);
            System.out.println(deptName.toString().substring(0, deptName.length() - 1));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List getDeptCodePath(Object[] deptsCode) throws DocumentException {
        List<String> retList = new ArrayList<String>();
        for (Object code : deptsCode) {
            Document doc = DocumentHelper.parseText((String) code);
            Element root = doc.getRootElement();
            Element datalist = root.element("datalist");
            for (Iterator it = datalist.elementIterator(); it.hasNext(); ) {
                Element data = (Element) it.next();
                for (Iterator p = data.elementIterator(); p.hasNext(); ) {
                    Element curP = (Element) p.next();
                    String deptPath = curP.getText();
                    retList.add(deptPath);
                }
            }
            /*Document doc = DocumentHelper.parseText(String.valueOf(code));
            Element rootElement = doc.getRootElement();
            Iterator iterator = rootElement.elementIterator("datalist");
            while (iterator.hasNext()) {
                Element next = (Element) iterator.next();
                Iterator data = next.elementIterator("data");
                while (data.hasNext()){
                    Element deptPath = (Element) data.next();
                    String dataPath = deptPath.elementTextTrim("deptPath");
                }
            }

            Object[] deptsName = client.invoke("getDeptNamePathByDeptCodePath", code);
            for (Object name : deptsName) {
                System.out.println(name);
            }*/
        }
        return retList;
    }

    @Test
    public void testEip2() {
        /**
         * Method threw 'java.lang.NoClassDefFoundError' exception.  no jacksonjsonProvider
         */
        String address = "http://udx-dev.gdyd.com:8081/restful/employee/";
        List<Object> list = new ArrayList<Object>();
        /*JacksonJsonProvider jsonProvider = new JacksonJsonProvider();
        list.add(jsonProvider);*/
        WebClient webClient = WebClient.create(address, list);
        String dept = webClient.path("/getPathByEmployeeNum/890211").accept(MediaType.APPLICATION_JSON).get(String.class);
        System.out.println(dept);

    }

}
