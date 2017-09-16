<%--
  Created by IntelliJ IDEA.
  User: 890211
  Date: 2017/9/6
  Time: 14:43
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>

    <title>Title</title>
    <script src="../react/jquery.min.js"></script>
    <link rel="stylesheet" type="text/css" href="../css/search.css"/>
    <script>
        $(document).ready(function () {
            // search();
            $("#search").hide();
            $("#groupsearch").show();
            $("#solrSearch").keydown(function (e) {
                if (e.keyCode == 13) {
                    search();
                }
            });
            $("#solr").keydown(function (e) {
                var keyword = $("#solr").val();
                $("#solrSearch").val(keyword);
                if (e.keyCode == 13) {
                    search();
                }
            });
        });

        function search(value) {
            $("#list").html("");
            var keyword = "";
            if (value = 'solrSearch') {
                keyword = $("#solrSearch").val();
            } else if (value == '') {
                keyword = $("#solr").val();
            }
            if (keyword == "") {
                $("#search").hide();
                $("#groupsearch").show();
                window.location.reload();
            } else {
                keyword = {"keyword": keyword};
                $.ajax({
                    url: "/solrsearch/solr/query.do",
                    dataType: "json",
                    data: {"params": JSON.stringify(keyword)},
                    contentType: 'application/json;charset=utf-8',
                    success: function (data) {
                        $("#search").show();
                        $("#groupsearch").hide();
                        if (data.total == 0) {
                            $("#list").append("<li><div id='noResult' class='noResult'>" +
                                "<span class='noSearch-png'><img src='../images/noSearch.png'></span>"
                                + "<span class='noSearch-font'>" + '暂无数据' + "</span>"
                                + "</div>"
                                + "</li>");
                        } else {
                            $.each(data.rows, function (i, val) {
                                $("#list").append("<li id=" + i + ">"
                                    + "<div id=title class='title'>"
                                    + "<a href=/solrsearch/solr/getpic.do?fileName=" + data.rows[i].site + data.rows[i].title+"></a></div>"
                                    + "<div id=detil class=detil> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
                                    + data.rows[i].content + "</div>"
                                    + "<div id=tag-war class='lable-download'>"
                                    + "<span id=tag class='label'>" + data.rows[i].label + "</span>"
                                    + "<span id=tag class='download'>"
                                    + "<a href=/solrsearch/solr/download.do?fileId="
                                    + data.rows[i].url + "><img src=../images/download.png></a></span></div>"
                                    + "<div id=tag-war>"
                                    + "<span id=tag>"
                                    + "</span>"
                                    + "</div>"
                                    + "</li>"
                                    + "<hr/>"
                                );
                            });
                            $("#pagination").append("<li id='page'>"

                                + "</li>"
                            );
                            $("#search").show();
                            $("#groupsearch").hide();
                            $("li").mouseenter(function () {
                                $("li")[this.id].style.background = "rgb(240,240,240)"
                            });
                            $("li").mouseleave(function () {
                                $("li")[this.id].style.background = "white"
                            });
                        }
                    },
                    error: function (data) {

                    }

                });
            }
        };


    </script>
</head>
<body>

<div id="index">
    <div class="search" id="search">
        <div class="row">
            <div class="input-group ">
                    <span class="span1">
                         <input type="text" class="input" id="solrSearch" placeholder="请输入关键字查询">
                        <input type="button" value="搜  索" class="button" onclick="search('solrSearch')">
                    </span>
            </div>
        </div>
    </div>
    <div id="listdiv" class="listdiv">
        <ul id="list" class="searchlist"></ul>
    </div>
    <div id="page-layout" class="page-layout">
        <div id="pagination" class="pagination">
        </div>
    </div>
    <div class="input-group-search" id="groupsearch">
        <span class="span1">
             <input type="text" class="input" id="solr" placeholder="请输入关键字查询">
        <%-- </span>
        <span class="span2">--%>
            <input type="button" value="搜  索" class="button" onclick="search('solr')">
        </span>
    </div>

</div>
</body>
</html>
