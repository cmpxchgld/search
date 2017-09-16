/*
	ITC组件库 
	最后更新时间：2014/2/19
	参考地址：http://10.0.250.52/uidev
*/

var itcui = {};
itcui.combo_displayed = false;
function ITC_GetAbsPos(obj)
{
	var o = $(obj);
	var obj_x = o.offset().left;
	var obj_y = o.offset().top;
	while(o.attr("parentNode"))
	{
		o = o.parent();
		obj_x += o.offset().left;
		obj_y += o.offset().top;
	}
	var p = {"left":obj_x,"top":obj_y};
	return p;
}

function ITC_Len(str){
	var len = str.length;
	var reLen = 0; 
	    for (var i = 0; i < len; i++) {        
	        if (str.charCodeAt(i) < 27 || str.charCodeAt(i) > 126) { 
	            // 全角    
	            reLen += 2; 
	        } else { 
	            reLen++; 
	        } 
	    } 
	return reLen;  
}

function ITC_Substr(str, startp, endp) {
    var i=0; c = 0; unicode=0; rstr = '';
    var len = str.length;
    var sblen = ITC_Len(str);

    if (startp < 0) {
        startp = sblen + startp;
    }

    if (endp < 1) {
        endp = sblen + endp;// - ((str.charCodeAt(len-1) < 127) ? 1 : 2);
    }
    // 寻找起点
    for(i = 0; i < len; i++) {
        if (c >= startp) {
            break;
        }
	    var unicode = str.charCodeAt(i);
		if (unicode < 127) {
			c += 1;
		} else {
			c += 2;
		}
	}
	// 开始取
	for(i = i; i < len; i++) {
	    var unicode = str.charCodeAt(i);
		if (unicode < 127) {
			c += 1;
		} else {
			c += 2;
		}
		rstr += str.charAt(i);
		if (c >= endp) {
		    break;
		}
	}
	return rstr;
}

function isArray(o) {
  return Object.prototype.toString.call(o) === '[object Array]'; 
}

function ITC_AddValidationStyle(label, element)
{
	//获取被验证元素的绝对位置 因为某些表单布局在文本框后面没有空间 直接用相对位置无法插入
	var pos = ITC_GetAbsPos(element);
	//这里的长度还要加上文本框的长度
	var objWidth = parseInt($(element).css("width"));
	//注意这里如果用display:none会被后续代码覆盖，因而无效，用remove会导致信息无法刷新
	label.children("label").css("width","0px");
	label.children("label").css("overflow","hidden");
	//为标签加上惊叹号图标
	label.addClass('itcui_icon_warn_mid').css({
		left: pos.left+ 6 +objWidth,
		top: pos.top+2
	});
	//这里的placement如果不改 也就是默认显示在图标上面 会出现对齐问题
	var myTooltip = $(label).tooltip({"title":"info","placement":"right"});
	myTooltip.on("shown.bs.tooltip",function(e){
		var info = $(this).children("label").html();
		//错误验证信息第二次出现不会重新触发errorPlacement 因而只能动态刷新内容
		$(this).next("div").find(".tooltip-inner").html(info);
	});
	label.insertAfter(element);
}

function ITC_GetStyler(){
	return window.parent.document.ITC_Styler||ITC_Styler||null;
}

function _parent(){
	if(window.parent.document==document){
		return window;
	}
	else{
		return window.parent;
	}
}

(function($){
	$.fn.extend({ 
		/*
			导航树（只支持两层）
			data : [{"grouptitle":"组标题","items":[{"title":"项标题","action":"点击后的动作"}],"initexpand":true}]
			opts:
				expandOnlyOne - 每次只能展开一层
				width - 树宽度
				treeId - 树Id，如果需要在一个页面产生多个导航树需要修改这个
		*/
		ITCUI_NavTree:function(action,data,opts){
			var expandedNode = null;
			var treeWidth = 200;
			var treeName = "itc_navtree";
			var _this = $(this);
			var eventList = {};
			var expandOnlyOne = false;


			this.initTree = function(data,opts){
				var treeHtml = '<div class="mainframe_navtree" id="' + treeName + '">';
				for(var j=0;j<data.length;j++){
					treeGroup = data[j];
					var grpId = treeName + "_" + j;
					treeHtml += '<div class="itcui_navtree_grouptitle" id="' + grpId + '">' + treeGroup["grouptitle"] + '</div>';
					var subItemid = grpId + "_subitem";
					treeHtml += '<div id="' + subItemid + '" class="navtree_subitem"';
					if(!treeGroup["initexpand"]){
						treeHtml += ' style="display:none"';
					}
					treeHtml += '>';
					//tree sub item
					if(treeGroup["items"]){
						for(var i=0;i<treeGroup['items'].length;i++){
							var treeitem = treeGroup['items'][i];
							var itemId = treeitem.id || treeName + "_item_" + j + "_" + i;
							treeHtml += '<div class="itcui_navtree_item" id="' + itemId + '">';
							treeHtml += treeitem["title"] + "</div>";
							if(treeitem["click"]){
								eventList[itemId] = treeitem["click"];
							}
						}
					}
					treeHtml += '</div>';//end of subitem
				}
				treeHtml += '</div>';//end of tree
				_this.html(treeHtml);
			};

			this.addEvents = function(data,opts){
				//鼠标点击时对子项高亮
				$("#" + treeName + " .itcui_navtree_item").click(function(){
					$("#" + treeName + " .itcui_navtree_item").removeClass("itcui_navtree_item_selected");
					$(this).addClass("itcui_navtree_item_selected");
					var evt = eventList[this.id];
					if(_event_handler){
						_event_handler.triggerEvent("navTreeItemClick",this.id);
					}
					if(evt){
						if(typeof(evt)=="function"){
							evt(this.id);
						}
						else{
							eval(evt);
						}
					}
				});

				//树折叠效果
				$("#" + treeName + " .itcui_navtree_grouptitle").click(function(){
					var box_id = this.id + "_subitem";
					var is_fold = $("#" + box_id).css("display")=="none"?true:false;
					//收起其他的选项卡
					if(expandOnlyOne){
						$("#" + expandedNode).removeClass("itcui_navtree_grouptitle_expand");
						$("#" + expandedNode + "_subitem").slideUp();
					}
					if(is_fold)
					{
						expandedNode = this.id;
						$("#" + box_id).slideDown();
						$(this).addClass("itcui_navtree_grouptitle_expand");
					}
					else
					{
						$("#" + box_id).slideUp();
						$(this).removeClass("itcui_navtree_grouptitle_expand");
					}
				});
			};

			if(action=="init"){
				if(opts){
					if(opts["width"] && opts["width"]>100){
						treeWidth = opts["width"];
					}
					if(opts["expandOnlyOne"]){
						expandOnlyOne = true;
					}
				}			
				this.initTree(data,opts);
				this.addEvents(data,opts);
			}
		},

		/**
			可折叠栏目
		*/
		ITCUI_Foldable:function(action,opts){
			var title = "无标题栏目";
			var _this = $(this);

			this.initFoldable = function(){
				if(_this.attr("grouptitle")){
					title = _this.attr("grouptitle");
				}
				if(opts){
					if(opts["grouptitle"]){
						title = opts["grouptitle"];
					}
				}
				var prepHtml = '<div class="itcui_frm_grp_title" style="width:100%">';
				prepHtml += '<span class="itcui_frm_grp_title_arrow';
				if(_this.css("display")!="none"){
					prepHtml += '  itcui_frm_grp_title_arrow_expand';
				}
				prepHtml += '"></span>';
				prepHtml += '<span class="itcui_frm_grp_title_txt">' + title + '</span>';
				prepHtml += '</div>';
				_this.before(prepHtml);
				var fTitle = _this.prev(".itcui_frm_grp_title");
				if(_this.css("float")){
					fTitle.css("float",_this.css("float"));
				}
			};

			this.addEvents = function(){
				var grpTitle = _this.prev(".itcui_frm_grp_title");
				grpTitle.click(function(e){
					var mainPart = $(this).next("div");
					if(mainPart.css('display')=="none"){
						grpTitle.children(".itcui_frm_grp_title_arrow").addClass("itcui_frm_grp_title_arrow_expand");
						mainPart.slideDown();
					}
					else{
						mainPart.slideUp();	
						grpTitle.children(".itcui_frm_grp_title_arrow").removeClass("itcui_frm_grp_title_arrow_expand");
					}
				});
			};

			this.fold = function(){
				var grpTitle = _this.prev(".itcui_frm_grp_title");
				grpTitle.children(".itcui_frm_grp_title_arrow").removeClass("itcui_frm_grp_title_arrow_expand");
				_this.slideUp();
			}

			this.expand = function(){
				var grpTitle = _this.prev(".itcui_frm_grp_title");
				grpTitle.children(".itcui_frm_grp_title_arrow").addClass("itcui_frm_grp_title_arrow_expand");
				_this.slideDown();
			}

			if(!action){
				this.initFoldable();
				this.addEvents();
			}
			else if(action=="fold"){
				this.fold();
			}
			else if(action=="expand"){
				this.expand();
			}
		},

		ITCUI_ComboBox:function(action,opts){
			//_this指针用于函数调用
			//__this用于事件中参数传递
			//这两个都是指向原始select的指针
			var displayItemCount = 6;
			var multiSelect = false;
			var maxStrLength = 12;
			var width = 150;
			var _this = $(this);

			//getVal=true时返回显示值 false返回实际值（option的value）
			getMultiSelected = function(getVal,__this){
				var mul = __this.data("multiSelectedVal");
				var rStr = "";
				var i = 0;				
				__this.children("option").each(function(){
					var sel = false;
					var itemValue = !getVal?$(this).attr("value"):$(this).html();
					var initChecked = $(this).attr("multichecked");
					if(mul == null){
						if(initChecked){
							sel = true;
						}
					}
					else{
						if(mul[i]){
							sel = true;
						}
					}
					if(sel){
						rStr += itemValue + " ";
					}
					i++;
				});
				return rStr;
			};

			initComboBox = function(){				
				width = parseInt(_this.css("width"));
				var maxLen = _this.attr("maxlength") || maxStrLength;
				_this.data("maxLen",maxLen);
				//获取初始化已选择的内容
				var comboLabel = _this.children("option:selected").text();
				if(_this.attr("multiselect")){
					multiSelect = true;
					comboLabel = getMultiSelected(true,_this);
					if(ITC_Len(comboLabel)>maxLen){
						comboLabel = ITC_Substr(comboLabel,0,maxLen) + "...";
					}
				}
				_this.data("multiSelect", multiSelect);
				_this.data("dataMapping", []);
				_this.data("multiSelectedVal", null);
				_this.data("comboWidth",width);
				var comboHtml = "<div class='itcui_combo' style='width:" + width + "px'>";
				comboHtml += "<span class='itcui_combo_text'>" + comboLabel + "</span><span class='itcui_combo_arrow_wrap'><b class='itcui_combo_arrow'></b></span>";
				comboHtml += "</div>";
				_this.css("display","none");
				_this.before(comboHtml);
			};

			

			updateComboText = function(__this){
				var mul = __this.data("multiSelect");
				var maxLen = __this.data('maxLen');
				var txt = "";
				if(mul){
					txt = getMultiSelected(true,__this);
					if(ITC_Len(txt)>maxLen){
						txt = ITC_Substr(txt,0,maxLen) + "...";
					}
				}
				else{
					txt = __this.find(":selected").first().html();					
				}
				__this.prev(".itcui_combo").children(".itcui_combo_text").html(txt);
			};

			initMultiSelectMapping = function(){
				var multiSelectedVal = {};
				var multiMapping = {};
				var i = 0;		
				_this.children("option").each(function(){
					var itemValue = $(this).attr("value");
					var initChecked = $(this).attr("multichecked");
					multiMapping[itemValue] = i;
					if(initChecked){
						multiSelectedVal[i] = true;
					}
					else{
						multiSelectedVal[i] = false;	
					}
					i++;
				});
				_this.data("multiSelectedVal",multiSelectedVal);
				_this.data("multiMapping",multiMapping);
			};
			
			display = function(__this){				
				//生成元素项
				var listHtml = "";
				//必须先数出来有多少项才能确定宽度
				var itemCount = 0;
				var itemWidth = parseInt(__this.data("comboWidth"));
				itemWidth = itemWidth<100?100:itemWidth;
				__this.children("option").each(function(){
					itemCount ++;
				});
				if(itemCount>displayItemCount){
					itemWidth -= 18;//留出一个滚动条的宽度
				}				
				var wrapHeight = 25 * (itemCount>displayItemCount?displayItemCount:itemCount) + 16;
				listHtml += "<div id='itc_combo_wrap' class='itcui_dropdown_menu' style='padding-top:6px;padding-bottom:6px;height:" + wrapHeight + "px;";
				if(itemCount>displayItemCount){
					listHtml += "overflow-y:scroll";
				}					
				listHtml += "'>";
				var multiSelect = __this.data("multiSelect");
				var multiSelectedVal = __this.data("multiSelectedVal");
				var dataMapping = __this.data("dataMapping");				
				var i = 0;
				__this.children("option").each(function(){
					var itemName = $(this).html();
					var itemValue = $(this).attr("value");					
					if(multiSelect==false){
						dataMapping.push([itemValue,itemName]);
						listHtml += "<div id='itcui_combo_item_" + i + "' class='itcui_dropdown_item' style='width:" + itemWidth + "px'>";
						listHtml += "<span class='itcui_dropdown_text'>" + itemName + "</span>";
						listHtml += "</div>";
					}
					else{
						listHtml += "<div id='itcui_combo_item_" + i + "' class='itcui_dropdown_item' style='width:" + itemWidth + "px'>";
						if(!multiSelectedVal[i]){
							listHtml += '<input class="itcui_dropdown_checkbox" type="checkbox" id="itcui_combo_chkbox_' + i + '">';
						}
						else{
							listHtml += '<input class="itcui_dropdown_checkbox" type="checkbox" id="itcui_combo_chkbox_' + i + '" checked>';
						}
						listHtml += '<label for="itcui_combo_chkbox_' + i + '">' + itemName + '</label>';
						listHtml += "</div>";
					}
					i++;
				});
				listHtml += "</div>";
				__this.data("dataMapping",dataMapping);
				__this.after(listHtml);
			};
			

			doSingleSelect = function(__this,sVal){
				__this.val(sVal);
				//这里需要更改原始select的值 否则显示文本和post会出错
				__this.children("[selected]").removeAttr("selected");
				__this.children("[value='" + sVal + "']").attr("selected","selected");
				updateComboText(__this);
			};

			doMultiSelect = function(__this,sVal){
				var multiSelectedVal = __this.data("multiSelectedVal");
				var multiMapping = __this.data("multiMapping");
				for(var k in sVal){
					var n = multiMapping[k];
					if(sVal[k]){
						multiSelectedVal[n] = true;
					}
					else{
						multiSelectedVal[n] = false;
					}
				}
				__this.data("multiSelectedVal",multiSelectedVal);
				updateComboText(__this);
			};

			changeSelect = function(){
				var mul = _this.data("multiSelect");
				if(mul){
					doMultiSelect(_this,opts);
				}
				else{
					doSingleSelect(_this,opts);
				}
			};

			addSingleChoiceEvent = function(){
				$("#itc_combo_wrap .itcui_dropdown_item").click(function(e){
					var __this = $(this).parent("#itc_combo_wrap").prev("select");
					var dataMapping = __this.data("dataMapping");
					var id = this.id;
					var num = parseInt(id.substr(17));
					doSingleSelect(__this,dataMapping[num][0]);
					$("#itc_combo_wrap").remove();	
					itcui.combo_displayed = false;				
				});
			};

			addMultiChoiceEvent = function(){
				$("#itc_combo_wrap input").iCheck({
				    checkboxClass: 'icheckbox_flat-blue',
				    radioClass: 'iradio_flat-blue'
				});
				$("#itc_combo_wrap .itcui_dropdown_item").click(function(e){
					e.stopPropagation();
					//修改选项卡状态
					var id = this.id;
					var num = parseInt(id.substr(17));
					$("#itcui_combo_chkbox_" + num).iCheck('toggle');					
				});
				$('#itc_combo_wrap .itcui_dropdown_checkbox').on('ifChanged', function(event){
					var id = this.id;
					var num = parseInt(id.substr(19));					
					var __this = $(this).parents("#itc_combo_wrap").prev("select");
					var multiSelectedVal = __this.data("multiSelectedVal");
					multiSelectedVal[num] = !multiSelectedVal[num];
					__this.data("multiSelectedVal", multiSelectedVal);
					updateComboText(__this);
				});
			};

			addEvents = function(){
				_this.prev(".itcui_combo").click(function(e){
					e.stopPropagation();
					var inputPtr = $(this).next("select");
					if(itcui.combo_displayed==false){
						display(inputPtr);
						if(multiSelect){
							addMultiChoiceEvent(inputPtr);
						}
						else{
							addSingleChoiceEvent(inputPtr);
						}
						itcui.combo_displayed = true;
						
					}
					else{
						$("#itc_combo_wrap").remove();
						itcui.combo_displayed = false;
					}
				});
				$("body").click(function(e){
					if(itcui.combo_displayed==true){
						$("#itc_combo_wrap").remove();
						itcui.combo_displayed = false;
					}
				});
			};

			
			if(!action){
				initComboBox();
				if(multiSelect==true){
					initMultiSelectMapping();
				}
				addEvents();
			}
			else if(action=="getSelected"){
				return getMultiSelected(false,_this);
			}
			else if(action=="select"){
				changeSelect();
			}
		},

		/*
			此翻页器依赖EasyUi的Pagination，仅提供样式
		*/
		ITCUI_Pagination:function(action,arg,opts){
			var _this = $(this);
			var _pagination = $(_this.datagrid('getPager'));
			var options = _pagination.pagination("options");
			var PG_STEP = 2;
			var NO_SEL_TEXT = "";//可以写"请选择xxxx"

			bindMenuPagerEvents = function(){
				//绑定菜单翻页器的事件
				$(".itcui_pagination_menu li").click(function(e){
					var __this = $(this);
					var num = __this.attr("turnto");
					turnTo(__this.parents(".itcui_pagination_container"),num);
					//TODO:ie8下手工消失 bug!
					
				});
				$(".itcui_pagination_psize li").click(function(e){
					var pgSize = $(this).children("a").html().replace(/<.*>/,"");
					var container = $(this).parents(".itcui_pagination_container");
					var targetPager = container.data("targetPager");
					//TODO:多pagination的异常
					if(!targetPager){
						return;
					}
					targetPager.pagination({"pageSize":pgSize}).pagination("select",1);
					_this = container.data('ptrGrid');
					targetDiv = container.data('targetDiv');
					_this.ITCUI_Pagination("create",targetDiv);
				});
			};

			turnTo = function(itcPagination,pageNumber){
				var style = "TIMSS";
				var pagination = itcPagination.data("targetPager");
				pagination.pagination("select",pageNumber);
				adjustButtons(itcPagination);
				//刷新当前页数
				var wrap = itcPagination.find(".itcui_pg_selector_wrap");
				var pgNumHtml = wrap.html();
				pgNumHtml = pgNumHtml.replace(/>\d+\//,">" + pageNumber + "/");
				wrap.html(pgNumHtml);
				//刷新翻页菜单
				var opts = pagination.pagination("options");
				var currPage = parseInt(opts.pageNumber);
				var maxPage = Math.ceil(opts.total/opts.pageSize) || 1;//没有任何数据时也显示1页 2014.3.4
				if(style!="TIMSS"){
					//标准翻页风格 只显示几页加首位链接
					var pStart = -1;
					var pEnd = -1;
					if(maxPage<=1 + 2*PG_STEP){
						pStart = 1;
						pEnd = maxPage;
					}
					else if(currPage-PG_STEP>1&&currPage+PG_STEP<maxPage){
						//两头都不着边界 取p-2 p-1 p p+1 p+2
						pStart = currPage - PG_STEP;
						pEnd = currPage + PG_STEP;
					}
					else if(currPage-PG_STEP<=1){
						//左边不够长
						pStart = 1;
						pEnd = 1 + 2*PG_STEP;
					}
					else if(currPage+PG_STEP>=maxPage){
						//右边不够长
						pEnd = maxPage;
						pStart = maxPage - 1 - 2*PG_STEP;
					}
					var liHtml = "";
					for(var i=pStart;i<=pEnd;i++){
						liHtml += "<li turnto=" + i + "><a class='menuitem'>" + i +"/" + maxPage + "</a></li>";
					}
					liHtml += "<li class='divider'></li>";
					liHtml += "<li turnto=1><a class='menuitem'>首页</a></li>";
					liHtml += "<li turnto=" + maxPage + "><a class='menuitem'>末页</a></li>";
				}
				else{
					var pgCnt = maxPage>8?8:maxPage;
					$("div .itcui_pagination_menu").css({
						"height":8+pgCnt*25 + "px",
						"overflow-y":"auto",
						"overflow-x":"hidden"
					});
					for(var i=1;i<=maxPage;i++){
						liHtml += "<li turnto=" + i + "><a class='menuitem'>" + i +"/" + maxPage + "</a></li>";
					}
				}
				setTimeout('$("div .itcui_pagination_menu").html(\"' + liHtml + '\");bindMenuPagerEvents();',200);				
			};



			createPagination = function(){
				$(arg).css({"height":"34px"});
				$(arg).addClass("itcui_pagination_container");
				var btnHtml = "";
				var pgSizeList = options.pageList;
				//设置菜单
				btnHtml += "<div class='dropdown' style='float:right'><span data-toggle='dropdown' class='itcui_pg_btn itcui_pg_setting'></span>";
				btnHtml += "<ul class='dropdown-menu' role='menu' aria-labelledby='dLabel'>";
				btnHtml += "<li class='dropdown-submenu pull-left'><a>分页设置</a><ul class='dropdown-menu itcui_pagination_psize'>";
				for(var i=0;i<pgSizeList.length;i++){
					btnHtml += "<li><a class='menuitem'><span class='itcui_option_point_wrap'>";
					if(pgSizeList[i]==options.pageSize){
						btnHtml += "<span class='itcui_option_point'></span>";
					}
					btnHtml += "</span>" + pgSizeList[i] + "</a></li>";
				}
				btnHtml += "</ul></li></ul>";
				if(ITC_GetStyler){
					
				}
				btnHtml += "</div>";
				//前后翻页按钮
				btnHtml += "<span class='itcui_pg_btn itcui_pg_next'></span>";
				btnHtml += "<span class='itcui_pg_btn itcui_pg_prev'></span>";				
				//菜单式翻页器
				var totalPage = Math.ceil(options.total/options.pageSize);
				btnHtml += "<div class='dropdown bbox' style='float:right'><div class='itcui_pg_selector_wrap' data-toggle='dropdown'><span style='float:left'>";
				btnHtml += options.pageNumber + "/" + totalPage;
				btnHtml += "</span><span class='itcui_pg_selector_icon'></span></div>";
				btnHtml += "<ul class='itcui_pagination_menu dropdown-menu' role='menu' aria-labelledby='dLabel'>";
				var pStart = 1;
				var pEnd = totalPage>5?5:totalPage;
				for(var i=pStart;i<=pEnd;i++){
					btnHtml += "<li turnto=" + i + "><a class='menuitem'>" + i +"/" + totalPage + "</a></li>";
				}
				btnHtml += "<li class='divider'></li>";
				btnHtml += "<li turnto=1><a class='menuitem'>首页</a></li>";
				btnHtml += "<li turnto=" + totalPage + "><a class='menuitem'>末页</a></li>";
				btnHtml += "</ul></div>";
				$(arg).html(btnHtml);
				_pagination.css("display","none");
				bindMenuPagerEvents();				
			};

			adjustButtons = function(tgtITCPager){				
				targetITCPager = tgtITCPager==null?$(arg):tgtITCPager;
				var opt = tgtITCPager==null?options:targetITCPager.data("targetPager").pagination("options");
				if(opt.pageNumber>1){
					targetITCPager.find(".itcui_pg_prev_disable").removeClass("itcui_pg_prev_disable").addClass("itcui_pg_prev");
				}
				else{
					targetITCPager.find(".itcui_pg_prev").addClass("itcui_pg_prev_disable").removeClass("itcui_pg_prev");
				}

				if(opt.pageNumber*opt.pageSize<opt.total){
					targetITCPager.find(".itcui_pg_next_disable").removeClass("itcui_pg_next_disable").addClass("itcui_pg_next");
				}
				else{
					targetITCPager.find(".itcui_pg_next").addClass("itcui_pg_next_disable").removeClass("itcui_pg_next");
				}
			};

			addEvents = function(){				
				var pgBtn = $(arg).find(".itcui_pg_btn");
				$(arg).data("targetPager",_pagination);
				$(arg).data("targetDiv",arg);
				$(arg).data('ptrGrid',_this);
				pgBtn.click(function(e){
					var __this = $(this);
					if(!__this.hasClass("itcui_pg_setting")){						
						var targetPager = __this.parent().data("targetPager");	
						var opt = targetPager.pagination("options");
						if(__this.hasClass("itcui_pg_prev")){
							turnTo($(arg),parseInt(opt.pageNumber) - 1);
						}
						if(__this.hasClass("itcui_pg_next")){
							turnTo($(arg),parseInt(opt.pageNumber) + 1);
						}
					}
				});
			};

			if(action=="create"){
				createPagination();
				adjustButtons();
				addEvents();
			}

		},

		/*
		 * 扩展输入框
		 */
		ITCUI_Input : function(opt){
			var _this = $(this);			
			var options = opt || {};
			var _parent = _this.parent();
			
			init = function(){
				var icon = options["icon"] || _this.attr("icon") || null;
				var placeholder = options["placeholder"] || _this.attr("placeholder") || null;
				var onlyLabel = options["onlylabel"] || _this.attr("onlylabel") || null;
				var inputWidth = "100%";
				if(icon){				
					$("<span class='itcui_input_icon'></span>").appendTo(_parent).addClass(icon).css({
						"float":"left",
						"margin-left" : "2px",
						"vertical-align" : "middle"
					});
					//调整图标的位置
					var iconSpan = _parent.children(".itcui_input_icon");
					var wrapHeight = parseInt(_parent.css("height"));
					var iconHeight = parseInt(iconSpan.css("height"));
					iconSpan.css("margin-top",(wrapHeight - iconHeight)/2-1);
					//重新计算input的大小
					var wrapWidth = parseInt(_parent.css("width"));
					var iconWidth = parseInt(iconSpan.css("width"));
					inputWidth = (wrapWidth - iconWidth - 12) + "px";
					
				}
				if(placeholder){
					//新版jquery判断ie6-ie8
					if(!$.support.leadingWhitespace){
						$("<span class='itcui_placeholder'>" + placeholder +  "</span>").prependTo(_parent).addClass("itcui_placeholder").css({
							"width" : inputWidth
						});
						_parent.children("input").css("display","none").blur(function(){
							var _this = $(this);
							if(!_this.val()){
								_this.css("display","none");
								_this.prev("span").css("display","block");
							}
						});
						_parent.children(".itcui_placeholder").click(function(){
							var _this = $(this);
							_this.css("display","none");
							_this.next("input").css("display","block").focus();
						});
					}
				}
				if(placeholder||icon){
					_this.css({
						"float":"left",
						"width" : inputWidth,
						"border-width" : "0px",
						"outline":"none",
						"margin-left":"4px",
						"font-size":"12px",
						"margin-top":"2px"
					});
					_parent.addClass("form-control-style");
				}
				if(onlyLabel){
					addLabel();
				}
			};
			
			addLabel = function(){
				_parent.addClass("borderless").find("input,.itcui_input_icon").css("display","none");
				var text = _this.val();
				$("<span class='itcui_onlylabel'>" + text + "<span>").appendTo(_parent);
			};
			
			removeLabel = function(){
				
			};
			
			if(!opt || typeof(opt)=="object"){
				init();
			}
			else if(typeof(opt)=="string"){
				if(opt=="label"){
					addLabel();
				}
			}
		},
		
		/*
			带有树的下拉框
		*/
		ITCUI_ComboTree : function(action,opt,args){
			var width = 150;
			var _this = $(this);


			initComboBox = function(){				
				width = parseInt(_this.css("width"));
				var wrapWidth = _this.attr("treewidth");
				var wrapHeight = _this.attr("treeheight");
				_this.data("wrapWidth",wrapWidth);
				_this.data("wrapHeight",wrapHeight);
				_this.data("opt",opt);
				//获取初始化已选择的内容
				var comboLabel = _this.children("option:selected").text();
				if(opt["checkbox"]){
					multiSelect = true;
				}
				else{
					multiSelect = false;	
				}
				_this.data("multiSelect", multiSelect);
				var comboHtml = "<div class='itcui_combo' style='width:" + width + "px'>";
				comboHtml += "<span class='itcui_combo_text'>" + comboLabel + "</span><span class='itcui_combo_arrow_wrap'><b class='itcui_combo_arrow'></b></span>";
				comboHtml += "</div>";
				_this.css("display","none");
				_this.before(comboHtml);
				_this.prev(".itcui_combo").click(function(e){
					e.stopPropagation();
					__this = $(this).next("select");
					popUp(__this);
				});
				setInitText(_this);
			};

			popUp = function(__this){
				var wrap = __this.next("div");
				if(wrap.hasClass("itcui_dropdown_menu")){
					wrap.css("display","block");
				}
				else{
					var wrapWidth = __this.data("wrapWidth");
					var wrapHeight = __this.data("wrapHeight");
					var opt = __this.data("opt");
					var wrapHtml = "<div class='itcui_dropdown_menu' style='padding-top:6px;padding-bottom:6px;height:" + wrapHeight + ";width:" + wrapWidth + "'><div class='itcui_combotree' style='width:100%'></div>";
					wrapHtml += "</div>";
					__this.after(wrapHtml);
					opt["onCheck"] = function(e){
						var __this = $(this).parent().prev("select");
					};
					//创建树
					var tree = __this.next('.itcui_dropdown_menu').find('.itcui_combotree').tree(opt);
					bindEvent(__this);					
					//初始选择 仅用于单选树
					var initId = __this.data("initSelected");
					if(initId){
						var targetNode = tree.tree("find",initId);
						tree.tree("select",targetNode.target);
					}
				}

			};

			bindEvent = function(__this){
				var tree = __this.next('.itcui_dropdown_menu').find('.itcui_combotree');
				var multiSelect = __this.data("multiSelect");
				$("body").click(function(e){
					var wrap = $(".itcui_dropdown_menu");
					wrap.css("display","none");
				});
				//点击树时不消失
				$(tree).click(function(e){
					e.stopPropagation();
				});		
			};

			initTree = function(){

			};

			getTree = function(){

			};

			/*
				遍历树节点，获取满足某条件的节点列表
				parent - 父节点
				resultList - 返回的结果列表
				property - 要获取的属性
				needCondition - 满足true的条件，如checked 				
			*/
			visitNode = function(parent,resultList,property,needCondition){
				if(!parent){
					return;
				}
				for(var i=0;i<parent.length;i++){
					node = parent[i];
					if(node[needCondition]){
						resultList.push(node[property]);
					}
					visitNode(node["children"],resultList,property,needCondition);
				}
				
			};		

			setInitText = function(__this){
				var multiSelect = __this.data("multiSelect");
				var opt = __this.data("opt");
				if(!opt["data"]){
					return;
				}
				var resultList = [];
				if(multiSelect){
					visitNode(opt["data"],resultList,"text","checked");
				}
				else{
					var resultIdList = [];
					visitNode(opt["data"],resultList,"text","selected");	
					visitNode(opt["data"],resultIdList,"id","selected");	
					//如果找到，还要标记一下，因为单选树没有默认选择的功能
					if(resultList.length>0){
						__this.data("initSelected",resultIdList[0]);
					}
				}
				__this.prev(".itcui_combo").find(".itcui_combo_text").html(resultList.join(" "));
			};

			
			getSelectedText = function(__this){
				var multiSelect = __this.data("multiSelect");
				var tree = __this.next('.itcui_dropdown_menu').find('.itcui_combotree');
				if(multiSelect){
					var nodes = tree.tree("getChecked");
					var nodesName = [];
					for(var i=0;i<nodes.length;i++){
						var node = nodes[i];
						nodesName.push(node.text);
					}
					return nodesName.join(" ");
				}
				else{
					var node = tree.tree("getSelected");
					if(node){
						return node.text;
					}
				}
			};

			if(action=="create"){
				initComboBox();
			}
			else if(action=="getTree"){

			}
			else if(action=="setText"){

			}
		},

		/*
			基于BS Dropdown的加强版 支持多级菜单、任意位置弹出、图标、单选/复选、取值
		*/
		ITCUI_Menu : function(action,options){
			var _this = $(this);

			init = function(){
				
			};

			getValue = function(k){

			};
		},

		ITCUI_FileUpload : function(options){
			var _this = $(this);
			if(!_this.attr("id")){
				//uploadify必须有一个ID
				return;
			}
			if(!options && typeof(options)!="object"){
				return;
			}
			var realId = _this.attr("id") + "_real";
			var queueId = _this.attr("id") + "_queue";
			var uploadClickAct = "$('#" + realId + "').uploadify('upload','*')"; 
			$("<div></div>").appendTo(_this).attr("id", realId);
			$("<span class='start_upload'>开始上传</span>").appendTo(_this).addClass("itcui_link").css({
				"height":"30px",
				"line-height":"30px",
				"vertical-align":"middle",
				"float":"left",
				"display":"none"
			}).attr("onclick",uploadClickAct);
			$("<div></div>").appendTo(_this).attr("id",queueId).css({
				"width":"100%","clear":"both"
			});
			options.queueID = queueId;
			options.swf = options.swf || "js/uploadify.swf";
			options.buttonText = options.buttonText || "选择文件";
			options.buttonClass = options.buttonClass || "itcui_link";
			options.auto = options.auto || false;
			options.width = options.width || "72";
			options.itemTemplate = options.itemTemplate || '<div class="itcui_upload_wrap" fileID="${fileID}">' + 
				'<div class="itcui_upload_icon_demo ml8">' +
				'</div>' +
				'<div class="itcui_upload_detail_wrap ml4">' +
					'<div class="itcui_upload_filename">' +
						'<span>${fileName}</span> <a onclick="$(\'#${instanceID}\').uploadify(\'cancel\',\'${fileID}\')" class="itcui_upload_cancel itcui_link fr">取消</a>' +
					'</div>' +
					'<div style="clear:both" class="itcui_upload_progress_line">' +
						'<div class="itcui_upload_progress_txt" style="margin-left:-4px">' +
							'<span class="fl">${fileSize}</span>' +
						'</div>' +
					'</div>' +
				'</div> ' +
			'</div>';

			options.onCancel = options.onCancel || function(file){
				var _this = $(this);
				$("#" + _this[0].settings.queueID).children("[fileID='" + file.id + "']").remove();
				if(_this[0].queueData.queueLength==0){
					_this[0].wrapper.parent().find(".start_upload").css("display","none");
				}
			};
			
			options.onUploadSuccess = options.onUploadSuccess || function(file, data, response){
				if(!data){
					return;
				}
				if(typeof(data)!="object"){
					data = eval("(" + data + ")");
				}
				var _this = $(this);
				var fileWrap = $("#" + _this[0].settings.queueID).children("[fileID='" + file.id + "']");
				fileWrap.find(".itcui_upload_progess_wrap").remove();
				if(data.status==1){
					fileWrap.find(".itcui_upload_progress_txt").append('<span class="itcui_upload_finish">完成</span>');
					fileWrap.find(".itcui_upload_cancel").remove();
				}
				else{
					fileWrap.find(".itcui_upload_progress_txt").append('<span class="itcui_upload_fail">' + data.msg + '</span>');
				}
			};
			
			options.onUploadError = options.onUploadError || function(file, errorCode, errorMsg, errorString){
				var _this = $(this);
				var fileWrap = $("#" + _this[0].settings.queueID).children("[fileID='" + file.id + "']");
				fileWrap.find(".itcui_upload_progess_wrap").remove();
				fileWrap.find(".itcui_upload_progress_txt").append('<span class="itcui_upload_fail">上传失败</span>');
			};
			
			options.onUploadProgress = options.onUploadProgress || function(file, bytesUploaded, bytesTotal, totalBytesUploaded, totalBytesTotal){
				var _this = $(this);
				var progressWidth = (bytesTotal==0)?46:parseInt(bytesUploaded/bytesTotal*46);
				$("#" + _this[0].settings.queueID).children("[fileID='" + file.id + "']").
					find(".itcui_upload_progress_bar").css("width",progressWidth + "px");
			};
			
			options.onUploadStart = function(file){
				var _this = $(this);
				var progressHtml = '<div class="itcui_upload_progess_wrap"><div class="itcui_upload_progress_bar" style="width:0px"></div></div>';
				$("#" + _this[0].settings.queueID).children("[fileID='" + file.id + "']").find(".itcui_upload_progress_txt").prepend(progressHtml);
			};
			
			options.onInit = function(){
				$("#" + $(this).attr("id")).css("float","left");
			};
			
			options.onSelect = function(file){
				var _this = $(this);
				_this[0].wrapper.parent().find(".start_upload").css("display","block");
			};
			
			$("#" + realId).uploadify(options);
			
			
		}
	});
})(jQuery);

/*
	修复部分
*/
(function($){
	$.fn.extend({
		
		/**
		 * 具有延迟加载功能的日期选择器
		 * @param optTextBox 控制文本框的选项，包括图标等，用于初始化ITCUI_Input
		 * @param optDatePicker 控制日期选择器的选项
		 * 
		 * 这两个选项都可以为空
		 */
		ITCUI_LazyLoadPicker : function(optTextBox,optDatePicker){
			var optText = optTextBox || {};
			var optPicker = optDatePicker || {};
			var _this = $(this);
			optPicker.language = 'ch';
			optPicker.autoclose = optPicker.autoclose || true;
			optPicker.forceParse = true;
			var placeHolder = optText.placeholder || "";
			optText.inputId = optText.inputId || ("itc_dp" + $(".itcui_btn_calander").length);
			_this.addClass("input-group input-group-sm");
			
			$("<input id='" + optText.inputId + "' type='text' icon='itcui_btn_calander' style='width:10px'></input>").attr("placeholder",placeHolder).appendTo(_this);
			var ipt = $(_this.children(":input"));
			ipt.ITCUI_Input();
			var icon = ipt.next("span");
			_this.children("input").data("opts",optPicker);
			_this.children("input").data("inited",false);
			_this.children("input").click(function(){
				var __this = $(this);
				var inited = __this.data("inited");
				var opts = __this.data("opts");
				if(!inited){
					__this.data("inited",true);
					__this.datetimepicker(opts).datetimepicker("show");
				}
			});
			//保证点图标也能弹出来
			icon.click(function(e){
				var __this = $(this);
				var ipt = __this.prev("input");
				var inited = ipt.data("inited");				
				if(!inited){
					var opts = ipt.data("opts");
					ipt.datetimepicker(opts).datetimepicker("show");
					ipt.data("inited",true);
				}
				else{
					ipt.datetimepicker("show");
				}
			});
		},
		
		ITCUI_FixTableChkBox : function(){
			var t = $(this).prev(".datagrid-view2");
			//表头部分
			t.find(".datagrid-header").find(".datagrid-header-check > input").iCheck({
				checkboxClass: 'icheckbox_flat-blue',
				radioClass: 'iradio_flat-blue'
			}).on('ifChecked', function(event){
				var datagrid = $(this).parents(".datagrid-view2").next("table");
				datagrid.datagrid("selectAll");
			}).on('ifUnchecked', function(event){
				var datagrid = $(this).parents(".datagrid-view2").next("table");
				datagrid.datagrid("unselectAll");
			});
			//数据行部分
			t.find(".datagrid-body").find(".datagrid-cell-check > input").iCheck({
				checkboxClass: 'icheckbox_flat-blue',
				radioClass: 'iradio_flat-blue'
			}).on('ifChecked', function(event){
				var datagrid = $(this).parents(".datagrid-view2").next("table");
				var rownum = $(this).parents(".datagrid-row").attr("datagrid-row-index");
				datagrid.datagrid("selectRow",rownum);
			}).on('ifUnchecked', function(event){
				var datagrid = $(this).parents(".datagrid-view2").next("table");
				var rownum = $(this).parents(".datagrid-row").attr("datagrid-row-index");
				datagrid.datagrid("unselectRow",rownum);
			});
			
		},

		ITCUI_DataGrid_LineEdit : function(action,editrow){
			var _this = $(this);
			var options = _this.datagrid("options");
			var oldData = _this.datagrid("getData");
			var rows = oldData["rows"];
			var columns = options.columns;
			if(!options){
				return;
			}

			//找出所有可以编辑的列
			var colMapping = {};
			for(var i=0;i<columns.length;i++){
				var col = columns[i];
				for(var j=0;j<col.length;j++){
					var _col = col[j];
					if(_col.edit){
						colMapping[_col.field] = _col;
					}
				}
			}

			startEdit = function(){				
				//找到真实的表格
				var realTable = _this.prev(".datagrid-view2").find(".datagrid-btable");
				var needComboBox = false;
				var datetimeOption = {};
				var spinnerOption = {};
				var needDateTime = false;
				var needSpinner = false;
				_this.data("itc_editrow",editrow);
				if(editrow=="all"){
					realTable.addClass("itcui_dg_edit");
				}
				realTable.find("tr").each(function(){
					var lnNum = parseInt($(this).attr("datagrid-row-index"));
					$(this).children("td").each(function(){
						if(lnNum!=editrow && editrow!="all"){
							return;
						}
						var td = $(this);
						var fieldId = td.attr("field");
						if(colMapping[fieldId]){
							var col = colMapping[fieldId];
							var ctrlType = col["control"];		
							var wrapDiv = td.children("div");
							var initVal = wrapDiv.html();
							var repHtml = initVal;
							var dataType = col["type"];
							if(ctrlType=="textbox"){
								//替换为文本框
								repHtml = '<div class="input-group-sm"><input type="text" class="form-control input-group-sm" style="width:100%" value="' + initVal + '"></div>';
							}
							else if(ctrlType=="combobox"){
								//替换为单选下拉框
								initVal = rows[lnNum][fieldId];
								repHtml = '<select style="width:100%">';
								for(var k in col["itc_option"]["data"]){
									var v = col["itc_option"]["data"][k];
									if(k!=initVal){
										repHtml += '<option value="' + k +'">' + v + '</option>';
									}
									else{
										repHtml += '<option value="' + k +'" selected="selected">' + v + '</option>';	
									}
								}
								needComboBox = true;
								repHtml += '</select>';
								
							}
							else if(dataType=="date"||dataType=="datetime"){
								//替换为日期选择器
								repHtml = '<div class="input-group-sm"><input type="text" class="itcui_grid_datetime form-control input-group-sm" style="width:100%" value="' + initVal + '"></div>';
								initVal = rows[lnNum][fieldId];
								wrapDiv.html(repHtml);
								if(needDateTime==false){
									needDateTime = true;
									datetimeOption["language"] = "ch";
									datetimeOption["startView"] = 2;
									datetimeOption["autoclose"] = true;
									if(dataType=="date"){
										datetimeOption["format"] = col["itc_option"]["date-format"] || "yyyy-mm-dd";
										datetimeOption["minView"] = 2;
									}
									else if(dataType=="datetime"){
										datetimeOption["format"] = col["itc_option"]["date-format"] || "yyyy-mm-dd hh:ii";			
										datetimeOption["minView"] = 0;
									}

								}
							}
							else if(ctrlType=="spinner"){
								repHtml = '<input class="itcui_grid_spinner" style="width:100%" value="' + initVal + '"></input>';
								if(!needSpinner){
									spinnerOption = col["itc_option"];
									spinnerOption["height"] = 24;
								}
								needSpinner = true;

							}
							wrapDiv.html(repHtml);						
						}
					});
					
				});
				//应用控件样式
				if(needComboBox){
					realTable.find("select").each(function(){
						$(this).ITCUI_ComboBox();
					});
				}
				if(needDateTime){
					realTable.find(".itcui_grid_datetime").each(function(){
						$(this).click(function(){
							var _this = $(this);
							if(!_this.data("datetimeInited")){
								_this.data("datetimeInited",true);
								_this.datetimepicker(datetimeOption);
								_this.datetimepicker("show");
							}
						});						
					});
				}
				if(needSpinner){
					realTable.find(".itcui_grid_spinner").each(function(){
						$(this).numberspinner(spinnerOption);
					});
				}
			};

			endEdit = function(){
				var realTable = _this.prev(".datagrid-view2").find(".datagrid-btable");
				var editrow = _this.data("itc_editrow");
				if(!editrow){
					return;
				}
				realTable.find("tr").each(function(){					
					var lnNum = parseInt($(this).attr("datagrid-row-index"));
					$(this).children("td").each(function(){
						if(!lnNum==editrow && editrow!="all"){
							return;
						}
						var td = $(this);
						var fieldId = td.attr("field");
						if(colMapping[fieldId]){
							var col = colMapping[fieldId];
							var ctrlType = col["control"];	
							var dataType = col["type"];	
							var wrapDiv = td.children("div");
							var newVal = null;
							if(ctrlType=="textbox"){
								newVal = wrapDiv.find("input").val();
							}
							else if(ctrlType=="combobox"){
								newVal = wrapDiv.find("select").val();
							}
							else if(dataType=="date"||dataType=="datetime"){
								newVal = wrapDiv.find("input").val();
							}
							else if(ctrlType=="spinner"){
								newVal = wrapDiv.find(".itcui_grid_spinner").numberspinner("getValue");
							}
							if(newVal!=null){
								rows[lnNum][fieldId] = newVal;
							}
						}
					});
				});
				_this.data("itc_editrow",null);
				_this.datagrid("loadData",oldData);
			};

			if(action=="start"){
				startEdit();
				_this.data("itc_status","edit");
			}
			else if(action=="end"){
				if(_this.data("itc_status")=="edit"){
					endEdit();
					_this.data("itc_status","normal");
				}
			}
		}
	});
})(jQuery);
