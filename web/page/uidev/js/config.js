var opts = {};
opts.tabs = [	
	{"name":"总则","id":"nav1"},
	{"name":"组件演示","id":"nav2"},
	{"name":"API参考","id":"nav3"},
	{"name":"新版示例","id":"nav4"},
	{"name":"树操作演示","id":"treeDemo"}
];

var treeData = [
{
	"grouptitle" : "总则",
	"initexpand" : true,
	"items" : [
		{"title":"关于本规范", "id":"nav1_11"},
		{"title":"下载","id":"nav1_12"},
		{"title":"历史版本","id":"nav1_13"},
		{"title":"更新日志","id":"nav2_17"}
	]
},
{
	"grouptitle" : "标准页面",
	"initexpand" : true,
	"items" : [
		{"title":"说明", "id":"nav1_21"},
		{"title":"列表页","id":"nav1_22"},
		{"title":"表单页","id":"nav1_23"}
	]
}
];

var treeData4 = [
	{"grouptitle" : "全局变量","id":"nav5_0"},
	{"grouptitle" : "Datagrid","id":"nav5_1"},
	{"grouptitle" : "表单","id":"nav5_2"},
	{"grouptitle" : "Combobox","id":"nav5_3"},
	{"grouptitle" : "通知与对话框","id":"nav5_4"},
	{"grouptitle" : "163邮箱风格框架","id":"nav5_5"},
	{"grouptitle" : "按钮(工具栏)与菜单","id":"nav5_6"},
	{"grouptitle" : "输入框与输入提示","id":"nav5_7"},
	{"grouptitle" : "文件上传/下载","id":"nav5_8"},
	{"grouptitle" : "表单验证","id":"nav5_9"}
];
var treeData2 = [
	{
		"grouptitle" : "概述",
		"initexpand" : true,
		"items" : [
			{"title":"关于本组件库", "id":"nav2_11"},
			{"title":"如何使用","id":"nav2_12"},
			{"title":"页面兼容性","id":"nav2_13"},
			{"title":"事件和消息传递","id":"nav2_14"},
			{"title":"栅格系统","id":"nav2_15"},
			{"title":"表单验证","id":"nav2_16"}			
		]
	},
	{
		"grouptitle" : "框架与布局",
		"initexpand" : false,
		"items" : [
			{"title":"163邮箱风格框架","id":"nav2_21"},
			{"title":"导航选项卡","id":"nav2_22"},
			{"title":"导航树","id":"nav2_23"},
			{"title":"折叠区域","id":"nav2_25"},		
			{"title":"通知与对话框","id":"nav2_26"},
			{"title":"表单","id":"nav2_27"},
			{"title":"页内选项卡","id":"nav2_28"}
		]
	},
	{
		"grouptitle" : "基础组件",
		"initexpand" : false,
		"items" : [
			{"title":"按钮","id":"nav2_31"},
			{"title":"输入框","id":"nav2_32"},
			{"title":"下拉菜单","id":"nav2_33"},
			{"title":"单选/复选框","id":"nav2_34"},
			{"title":"下拉框","id":"nav2_35"},
			{"title":"日期选择器","id":"nav2_36"},
			{"title":"下拉树","id":"nav2_38"},
			{"title":"文件上传","id":"nav2_39"}
		]
	},
	{
		"grouptitle" : "数据表格",
		"initexpand" : false,
		"items" : [
			{"title":"基本操作","id":"nav2_41"},
			{"title":"行列样式1","id":"nav2_42"},
			{"title":"行列样式2","id":"nav2_43"},
			{"title":"数据操作","id":"nav2_44"},
			{"title":"翻页和排序","id":"nav2_45"},
			{"title":"表头搜索","id":"nav2_46"},
			{"title":"综合演示（在线）","id":"nav2_47"}
		]
	}
	,
	{
		"grouptitle" : "实用工具",
		"initexpand" : false,
		"items" : [
			{"title":"下拉菜单","id":"nav2_51"},
			{"title":"按钮/按钮组","id":"nav2_52"}			
		]
	}
];
var treeData5 = [
 	{"grouptitle" : "资产台账","id":"nav6_1"},
 	{"grouptitle" : "其他操作（无资产树）","id":"nav6_2"}
];
opts.tabMapping = {
	"nav1":{
		"cache":true,
		"tree":treeData
	},
	"nav2":{
		"cache":true,
		"tree":treeData2,
		"id" : "iframeComponents"
	},
	"nav3":{
		"cache":true,
		"tree" : treeData4
	},
	"nav4":{
		"cache":true,
		"id":"newdemo",
		"url":"newdemo/about.html"
	},
	"treeDemo":{
		"cache":true,
		"tree":treeData5
	}
};

opts.treeMapping = {
	"nav1_11":"component/aboutui.html",
	"nav1_12":"component/download.html",
	"nav1_14":"component/demo.html",
	"nav1_21":"standard/readme.html",
	"nav1_22":"standard/table.html",
	"nav1_23":"standard/form.html",

	"nav2_11":"component/about.html",
	"nav2_12":"component/howtouse.html",
	"nav2_13":"component/compatibility.html",
	"nav2_14":"component/eventhandler.html",
	"nav2_15":"component/grid.html",
	"nav2_16":"component/validation.html",
	"nav2_17":"component/updatelog.html",

	"nav2_21":"component/oaframe.html",
	"nav2_22":"component/navtab.html",
	"nav2_23":"component/navtree.html",
	"nav2_24":"component/innernav.html",
	"nav2_25":"component/foldable.html",
	"nav2_26":"component/notify.html",
	"nav2_27":"component/form.html",
	"nav2_28":"component/innertab.html",

	"nav2_31":"component/button.html",
	"nav2_32":"component/input.html",
	"nav2_33":"component/menu.html",
	"nav2_34":"component/chkbox.html",
	"nav2_35":"component/combobox.html",
	"nav2_36":"component/datepicker.html",
	"nav2_37":"component/tree.html",
	"nav2_38":"component/combotree.html",
	"nav2_39":"component/fileupload.html",

	"nav2_41":"component/datagrid.html",
	"nav2_42":"component/datagrid2.html",
	"nav2_43":"component/datagrid3.html",
	"nav2_44":"component/datagrid4.html",
	"nav2_45":"component/datagrid5.html",
	"nav2_46":"component/datagrid6.html",
	"nav2_47":"component/datagrid7.html",

	"nav2_51":"component/util_menu.html",
	"nav2_52":"component/util_button.html",

	"nav5_0":"api/global.html",
	"nav5_1":"api/datagrid.html",
	"nav5_2":"api/form.html",
	"nav5_3":"api/combo.html",
	"nav5_4":"api/dialog.html",
	"nav5_5":"api/framework.html",
	"nav5_6":"api/toolbar.html",
	"nav5_7":"api/input.html",
	"nav5_8":"api/upload.html",
	"nav5_9":"api/validate.html",

	"nav6_1":"demo/assetlist.html",
	"nav6_2":"http://www.baidu.com"	
};