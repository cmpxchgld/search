<%--
  Created by IntelliJ IDEA.
  User: 890211
  Date: 2017/9/6
  Time: 8:45
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>solr</title>
    <script type="text/javascript" src="../js/jquery.min.js"></script>
    <!-- 最新版本的 Bootstrap 核心 CSS 文件 -->
    <link rel="stylesheet" href="https://cdn.bootcss.com/bootstrap/3.3.7/css/bootstrap.min.css"
          integrity="sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u" crossorigin="anonymous">
    <!-- 最新的 Bootstrap 核心 JavaScript 文件 -->
    <script src="https://cdn.bootcss.com/bootstrap/3.3.7/js/bootstrap.min.js"
            integrity="sha384-Tc5IQib027qvyjSMfHjOMaLkfuWVxZxUPnCJA7l2mCWNIpG9mGCD8wGNIcPD7Txa"
            crossorigin="anonymous"></script>
    <link rel="stylesheet" type="text/css" href="../css/jquery.fancybox.css?v=2.1.5" media="screen"/>
    <link rel="stylesheet" type="text/css" href="../css/jquery.fancybox-buttons.css?v=1.0.5"/>
    <link rel="stylesheet" type="text/css" href="../css/jquery.fancybox-thumbs.css?v=1.0.7"/>
    <script>
        $(function () {


        });
        var html = "<div>name</div>"

        function search() {

            var keyword = $("#solrSearch").val();
            var kewword = {"keyword": keyword};
            $.ajax({
                url: "/solrsearch/solr/query.do",
                dataType: "json",
                data: {"params": JSON.stringify(kewword)},
                contentType: 'application/json;charset=utf-8',
                success: function (data) {
                    if (data.rows.length == 0) {

                        /* $.each(data, function (i, val) {
                             var html = "<div>" + data[0].name + "</div>";
                         })*/
                    }

                    /*$("#page").show();*/
                },
                error: function (data) {

                }
            });
        };
    </script>
</head>
<body>
<div id="index">
    <div>
        <div style="padding-top: 160px" align="center">
            <div class="row">
                <div style="width: 38%;">
                    <div class="input-group ">
                        <input <%--type="text"--%> class="form-control " placeholder="Search" id="solrSearch">
                        <span class="input-group-btn ">
                        <button class="btn btn-info" type="button" onclick="search()">搜 索</button>
                    </span>
                    </div>
                </div>
            </div>
        </div>
        <table class="ant-table-tbody" style="">

            <c:forEach items="${Qresult.rows}" var="doc" varStatus="status">
                <tr class="ant-table-row  ant-table-row-level-0">
                    <td class=""><span class="ant-table-row-indent indent-level-0" style="padding-left: 0px;"></span>
                        <!-- react-empty: 29 -->
                        <div>
                            <div class="ant-row">
                                <div class=""><span class="icon anticon icon-wordfile1" style="font-size: 22px;"></span><a
                                        href="javascript:pic('${doc.site}${doc.title}')"
                                        style="margin-left: 15px; font-size: 22px; color: rgb(0, 0, 204);">${doc.title}</a>
                                </div>
                            </div>
                            <div class="ant-row">
                                <div class="">
                                    <div class="light-font content" style="padding-top: 8px;">
                                            ${doc.content}
                                    </div>
                                </div>
                            </div>
                            <div class="ant-row">
                                <div class="ant-col-22">
                                    <div style="padding-top: 10px;"><span class="tab">${doc.label}</span></div>
                                </div>
                                <div class="ant-col-2"><a class="icon anticon icon-download"
                                                          href="/search/solr/download.do?fileId=" +${doc.url}}
                                                          style="float: right; font-size: 22px; padding-top: 10px;"></a>
                                </div>
                            </div>
                        </div>
                    </td>
                </tr>
            </c:forEach>
        </table>
    </div>
</div>
</body>
</html>
