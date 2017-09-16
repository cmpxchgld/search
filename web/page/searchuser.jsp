<%--
  Created by IntelliJ IDEA.
  User: 890211
  Date: 2017/9/9
  Time: 17:51
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>通讯录</title>
    <script src="../js/jquery.min.js"></script>
    <!-- 最新版本的 Bootstrap 核心 CSS 文件 -->
    <link rel="stylesheet" href="https://cdn.bootcss.com/bootstrap/3.3.7/css/bootstrap.min.css"
          integrity="sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u" crossorigin="anonymous">
    <!-- 最新的 Bootstrap 核心 JavaScript 文件 -->
    <script src="https://cdn.bootcss.com/bootstrap/3.3.7/js/bootstrap.min.js"
            integrity="sha384-Tc5IQib027qvyjSMfHjOMaLkfuWVxZxUPnCJA7l2mCWNIpG9mGCD8wGNIcPD7Txa"
            crossorigin="anonymous"></script>
    <script>
        $(document).ready(function () {
            $("#template").hide();
            $("#search").keydown(function (e) {
                if (e.keyCode == 13) {
                    search();
                }
            });
           $("#datas").hide();
        });

        function search() {
            var start = 0;
            var rows = 20;
            var keyword = $("#search").val();
            if (keyword == "") {
                console.log("请输入查询内容");
                $("tr[id='ready']").each(function () {
                    $(this).remove();
                });
                $("#datas").hide();
                $("#page").hide();
                $("#")
                alert("请输入查询内容");
            } else {
                /*$("#template").show();*/
                $.ajax({
                    url: "/solrsearch/solr/queryUser.do",
                    dataType: "json",
                    data: {"keyword": keyword, "start": start, "rows": rows},
                    contentType: 'application/json;charset=utf-8',
                    success: function (data) {
                        $("tr[id='ready']").each(function () {
                            $(this).remove();
                        });

                        var rows = data.rows;
                        if (rows.length == 0) {
                            $("#template").hide();
                            $("#datas").hide();
                            $("#nosearch").show();
                            $("#page").hide();
                            $("#nosearch").html("没有查询到结果");
                        } else {
                            $("#page").show();
                            $("#template").show();
                            $("#nosearch").hide();
                            $("#datas").show();
                            $.each(rows, function (i, n) {
                                var row = $("#template").clone();
                                row.find("#userName").text(n.name);
                                row.find("#userId").text(n.userId);
                                /* row.find("#userDept").text(n.job);*/
                                row.find("#userJob").text(n.job);
                                row.find("#officeTel").text(n.officeTel);
                                row.find("#mobile").text(n.mobile);
                                row.find("#email-href").attr("href", "mailto:" + n.email);
                                /*$("#email-href").att("href",n.email);*/
                                row.attr("id", "ready");//改变绑定好数据的行的id
                                row.appendTo("#datas");//添加到模板的容器中
                            });
                            $("#page").html("");
                            $("#page").append("总条数为:",data.total);
                            $("#template").hide();
                            console.log(data);
                        }
                    },
                    error: function () {
                        console.log("error");
                    },
                });
            }
        };

        function searchPrevious() {
            var prev = (parseInt($("#href").html()) - 1) * 15 - 15;
            start = prev;
            search(prev);
        };

        function searchNext() {
            var next = (parseInt($("#href").html()) * 15) + 1;
            start = next;
            search(next);
        };

        function searchCurrent() {
            var current = parseInt($("#href").html() - 1) * 15 + 1
        /*    if (current == 1) {
                start = 0;
            } else {
                start = current;
            }*/
            search(current);
        };
    </script>

</head>
<body>
<div>
    <div align="center">
        <div class="row" style="width: 50%;padding-top: 10%" id="search-input">
            <div class="col-lg-6" style="width: 100%">
                <div class="input-group" align="center">
                    <input type="text" class="form-control" placeholder="Search for..." id="search">
                    <span class="input-group-btn">
                        <button class="btn btn-info" type="button" onclick="search()">搜 索</button>
                    </span>
                </div><!-- /input-group -->
            </div><!-- /.col-lg-6 -->
        </div>
    </div>
    <div id="nosearch" align="center"></div>
    <div align="center" style="padding-top: 20px">
        <table class="table table-striped table-hover" style="width: 70%" id="datas">
            <tr>
                <th>姓名</th>
                <th>工号</th>
                <th>部门</th>
                <th>职务</th>
                <th>工作电话</th>
                <th>手机</th>
                <th>发短息</th>
                <th>发邮件</th>
            </tr>

            <tr id="template">
                <td id="userName"></td>
                <td id="userId"></td>
                <td id="userDept"></td>
                <td id="userJob"></td>
                <td id="officeTel"></td>
                <td id="mobile"></td>
                <td id="sendMessage"></td>
                <td id="email">
                    <a href="" id="email-href">
                        <span class="glyphicon glyphicon-envelope"/>
                    </a>
                </td>
            </tr>
        </table>
    </div>
    <div id="page" align="center">
        <%--<nav aria-label="Page navigation">
            <ul class="pagination">
                <li>
                    <a href="javascript:searchPrevious()" aria-label="Previous">
                        <span aria-hidden="true">&laquo;</span>
                    </a>
                </li>
                <li id="pageTemplate"><a href="javascript:searchCurrent()" style="background: #46b8da" id="href">1</a>
                </li>
                <li>
                    <a href="javascript:searchNext()" aria-label="Next">
                        <span aria-hidden="true">&raquo;</span>
                    </a>
                </li>
            </ul>
        </nav>--%>

    </div>
</div>
</body>
</html>
