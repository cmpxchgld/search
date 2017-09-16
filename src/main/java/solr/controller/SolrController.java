package solr.controller;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import solr.bean.QResult;
import solr.bean.RPic;
import solr.bean.SolrPage;
import solr.bean.SolrUserResult;
import solr.dataimport.IAttachmentManager;
import solr.service.FileUpload;
import solr.service.ItcSolrUserService;
import solr.service.Timss4SolrService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 890213
 * @version 1.0
 * @date 2017/8/11 15:34
 */
@Controller
@RequestMapping("/solr")
public class SolrController {
    @Autowired
    Timss4SolrService timss4SolrService;
    @Autowired
    ItcSolrUserService solrUserService;
    @Autowired
    FileUpload fileUpload;
    @Autowired
    IAttachmentManager attachManager;
    @Value("#{solrURLProperties['picPath']}")
    private String picPath;

    @Value("#{solrURLProperties['localPath']}")
    private String localPath;

    @RequestMapping(value = "/queryUser.do")
    public @ResponseBody
    SolrUserResult queryUser(HttpServletRequest request) {
        int start = 0;
        int rows = 20;
        String startStr = request.getParameter("start");
        if (StringUtils.isNotEmpty(startStr)) {
            start = Integer.parseInt(startStr);
        }
        String rowsStr = request.getParameter("rows");
        if (StringUtils.isNotEmpty(rowsStr)) {
            rows = Integer.parseInt(rowsStr);
        }
        String keyword = request.getParameter("keyword").replaceAll(" ", "");
        SolrUserResult query = solrUserService.query(keyword, start, rows);
        return query;
    }


    @RequestMapping(value = "/query.do", method = RequestMethod.POST)
    public @ResponseBody
    QResult query(@RequestBody SolrPage page) {
        if (page == null)
            return null;
        String keyword = page.getParams().get("keyword");
        return timss4SolrService.query(keyword, null, null, 0, 50);
    }

    @RequestMapping(value = "/query.do", method = RequestMethod.GET)
    @CrossOrigin(origins = "*", maxAge = 3600)
    public @ResponseBody
        //@RequestBody SolrPage page
    QResult query(HttpServletRequest request, HttpServletResponse response) {

        int start = 0;
        int rows = 50;
        String startStr = request.getParameter("start");
        if (StringUtils.isNotEmpty(startStr)) {
            start = Integer.parseInt(startStr);
        }
        String rowsStr = request.getParameter("rows");
        if (StringUtils.isNotEmpty(rowsStr)) {
            rows = Integer.parseInt(rowsStr);
        }

        Map<String, String[]> parameterMap = request.getParameterMap();
        SolrPage page = new SolrPage();
        String params = parameterMap.get("params")[0];

        HashMap hashMap = JSON.parseObject(params, HashMap.class);
        if (page == null) {
            page = new SolrPage();
        }
       /* if (hashMap == null) {
            return null;
        }
        page.setParams(hashMap);*/
        if (hashMap != null) {
            page.setParams(hashMap);
        } else if (page == null && StringUtils.isBlank(params)) {
            return null;
        }
        QResult query = new QResult();
        String keyword = page.getParams().get("keyword");
        if (StringUtils.isBlank(keyword)) {
            query.setCode(QResult.SUCCESS);
            query.setRetcode(QResult.SUCCESS);
            query.setRows(new ArrayList<QResult.Doc>());
            query.setMsg("搜索结果为空");
            return query;
        }
        query = timss4SolrService.query(keyword, null, null, start, rows);
        response.setHeader("Access-Control-Allow-Origin", "*");
        return query;

    }

    @RequestMapping("/upload.do")
    public void fileUpload(@RequestParam(value = "file", required = false) MultipartFile[] files,
                           HttpServletRequest request) {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        String site = multipartRequest.getParameterValues("site") != null
                ? multipartRequest.getParameterValues("site")[0] : null;
        String user = multipartRequest.getParameterValues("user") != null
                ? multipartRequest.getParameterValues("user")[0] : null;
        String label = multipartRequest.getParameterValues("label") != null
                ? multipartRequest.getParameterValues("label")[0] : null;
        String userid = multipartRequest.getParameterValues("userid") != null
                ? multipartRequest.getParameterValues("userid")[0] : null;
        fileUpload.upload(files, site, userid, user, label);
    }

    @RequestMapping("/download.do")
    public void download(HttpServletRequest request, HttpServletResponse response) throws IOException {
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        //获取下载文件
        String fileId = URLDecoder.decode(request.getParameter("fileId"), "UTF-8");
        File file = new File(fileId);
        if (!file.exists()) return;
        //MultipartFile multfile = new MultipartFile()
        //获取文件的长度
        long fileLength = file.length();
        //设置文件输出类型
        //response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        response.setContentType("application/x-download;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        String fileName = fileId.split("/")[(fileId.split("/")).length - 1];
        String userAgent = request.getHeader("User-Agent").toLowerCase();
     /*   byte[] bytes = null;
        if (userAgent.indexOf("msie") > -1 || userAgent.indexOf("rv:11") > -1) {
            bytes = fileName.getBytes("GBK");
        } else {
            bytes = fileName.getBytes("UTF-8");
        }
        fileName = new String(bytes, "iso-8859-1");
        response.setHeader("Content-disposition", String.format("attachment; filename=\"%s\"", fileName));*/

        if (userAgent == null || userAgent.indexOf("msie") > 0) {
            response.addHeader("Content-Disposition", "attachment;filename=\"" + URLEncoder.encode(fileName, "UTF8").replace("+", "%20") + "\"");
        } else {
            response.addHeader("Content-Disposition", "attachment;filename*=UTF-8''" + URLEncoder.encode(fileName, "UTF8").replace("+", "%20"));
        }
        //设置输出长度
        response.setHeader("Content-Length", String.valueOf(fileLength));
        //获取输入流
        bis = new BufferedInputStream(new FileInputStream(fileId));
        //输出流
        bos = new BufferedOutputStream(response.getOutputStream());
        byte[] buff = new byte[2048];
        int bytesRead;
        while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
            bos.write(buff, 0, bytesRead);
        }
        //关闭流
        bis.close();
        bos.close();
    }

    @RequestMapping("/callback.do")
    public void callBack(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String id = String.valueOf(request.getParameter("id"));
        int status = Integer.valueOf(String.valueOf(request.getParameter("status")));
        String args = String.valueOf(request.getParameter("args"));
        attachManager.callBack2ReceiveNotify(id, status, args);
    }

    @RequestMapping(value = "/getpic.do", method = RequestMethod.GET)
    @CrossOrigin(origins = "*", maxAge = 3600)
    public @ResponseBody
        //@RequestBody
    RPic test(String params) {
        Map<String, String> param = JSON.parseObject(params, Map.class);
        RPic result = new RPic();
        if (param == null) {
            result.setCode(RPic.FAILURE);
            result.setRetcode(RPic.FAILURE);
            return result;
        }
        String fileName = param.get("fileName");
        List<String> images = new ArrayList<String>();
        //如果是图片,直接返回地址
        if (fileName.endsWith(".png") || fileName.endsWith(".jpg")) {
            images.add(localPath + "search/pic/" + fileName);
            result.setRetcode(RPic.SUCCESS);
            result.setCode(RPic.SUCCESS);
            result.setPic(new ArrayList<Map<String, String>>());
            result.setImages(images);
            return result;
        }
        File file = new File(picPath + fileName);
        if (!file.exists()) {
            result.setCode(RPic.FAILURE);
            result.setRetcode(RPic.FAILURE);
            return result;
        }

        int total = file.list().length;
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        for (int i = 1; i <= total; i++) {
            Map<String, String> map = new HashMap<>();
            // /webapps/search/pic
            //picPath
            //map.put("href", "http://10.0.250.57:8081/search/pic/" + fileName + "/" + i + ".png");
            map.put("href", "../pic" + "/" + i + ".png");
            map.put("title", fileName + " " + i + "/" + total);
            list.add(map);
            //eip列表展示
            images.add(localPath + "/search/pic/" + fileName + "/" + i + ".png");
        }
        result.setRetcode(RPic.SUCCESS);
        result.setCode(RPic.SUCCESS);
        result.setPic(list);
        //eip列表展示
        result.setImages(images);
        return result;
    }

    @RequestMapping(value = "/getpic.do", method = RequestMethod.POST)
    @ResponseBody
    public RPic test(@RequestBody Map<String, String> param) {
        RPic result = new RPic();
        if (param == null) {
            result.setCode(0);
            return result;
        }
        String fileName = (String) param.get("fileName");
        List list = new ArrayList();
        if (fileName.endsWith(".png") || fileName.endsWith(".jpg")) {
            Map map = new HashMap();
            map.put("href", "../pic/" + fileName);
            map.put("title", fileName);
            list.add(map);
            result .setPic(list);
            return result;
        }
        File file = new File(this.picPath + fileName);
        if (!(file.exists())) {
            result.setCode(0);
            return result;
        }
        int total = file.list().length;
        for (int i = 1; i <= total; ++i) {
            Map map = new HashMap();
            map.put("href", "../pic/" + fileName + "/" + i + ".png");
            map.put("title", fileName + " " + i + "/" + total);
            list.add(map);
        }
        result.setCode(1);
        result.setPic(list);
        return result;
    }
}
