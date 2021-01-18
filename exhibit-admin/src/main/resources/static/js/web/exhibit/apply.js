(function(win, $) {
	function clear() {
		$(".form-group .validate").removeClass("has-error");
		$(".form-group .validate span").remove();
	}
	var applys = {
		init : function() {
			var obj = this;
			
			$('.full-height-scroll').slimScroll({
				height : '100%'
			});

    	    clear();
        	// 绑定按钮点击事件
			$('#query').click(function(){
				$('#name').val($.trim($('#name').val()));
				$('#code').val($.trim($('#code').val()));
				// 设置参数
				$('#table').bootstrapTable('refreshOptions',{pageNumber:1,pageSize:10});
			});
			$('#dl1').click(function(){
				download(1)
			});
			$('#dl2').click(function(){
				download(0)
			});
			window.operateEvents = {
			    'click #toCreate': function (e, value, row, index) {
			    	layer.open({
		 	            type: 2,
		 	            offset:['20px'], 
		 	            title: '申请详情',
		 	            area: ['90%', '100%'],
		 	            fix: false, //不固定
		 	            maxmin: false,
		 	            content: 'applydetail?id='+row.id,
		 	            btn: ['通过','退回','关闭'],
		 	            yes: function (index, layero) {
		 	            	var auditsuggest = layer.getChildFrame('.w-e-text', index);
		 	            	var mark =layer.getChildFrame("#marks").val();
		 	            	win.Config.Ajax(win.Config.Domain+win.Config.Services.queryPass, {
		 	            		id:row.id,
		 	            		mark:mark,
		 	            		suggest:auditsuggest.html()
								},'POST', function (result) {
								if (result.status == 0) {
									layer.close(index);
									$('#table').bootstrapTable('refresh');
								} else {
									layer.alert(result.message[0],{closeBtn : 0,skin : 'layui-layer-molv',icon : 2});
								}
							});
		 	            },btn2: function(index, layero){
		 	            	var auditsuggest = layer.getChildFrame('.w-e-text', index);
		 	            	var mark =layer.getChildFrame("#marks").val();
		 	            	if(auditsuggest.html()==''){
		 	            		layer.alert('审核意见不能为空！',{closeBtn : 0,skin : 'layui-layer-molv'});
		 	            		return false;
		 	            	 }
		 	            	win.Config.Ajax(win.Config.Domain+win.Config.Services.queryRefuse, {
		 	            		id:row.id,
		 	            		mark:mark,
		 	            		suggest:auditsuggest.html()
								},'POST', function (result) {
								if (result.status == 0) {
									layer.close(index);
									$('#table').bootstrapTable('refresh');
								} else {
									layer.alert(result.message[0],{closeBtn : 0,skin : 'layui-layer-molv',icon : 2});
								}
							});
		 	            	return false; 
		 	                //按钮【按钮二】的回调
		 	            },btn3: function(index, layero){
		 	             //按钮【按钮三】的回调
		 	            },
		 	            success: function(layero, index){
		 	            	var auditsuggest = layer.getChildFrame('.w-e-text', index);
		 	            	auditsuggest.attr('contenteditable', true);
		 	            }
		 	        });
			    	
			    }
			};
			obj.initTable();
		},
		initTable : function() {
			$('#table').bootstrapTable(
			{
				columns : [{ 
					field : 'counterCode',
					title : '柜台号',
					halign : 'center'
				},{
					field : 'counterName',
					title : '柜台名称',
					halign : 'center'
				},{
					field : 'isPass',
					title : '审核状态',
					halign : 'center',
					formatter:function(value,row,index){
						if(row.count==0){
							return '待审核';
						}else if(!value){
							return '已退回';
						}else if(value){
							return '已通过';
						}
					}
				},{
					field : 'upTime',
					title : '提交日期',
					halign : 'center',
				},{
					field : 'lastupdate',
					title : '最终修改时间',
					halign : 'center',	
					formatter:function(row){
						return datetimeFormat_1(row);
					}
				},{
					field : 'verifyUser',
					title : '审核人',
					halign : 'center'
				},{
					field : 'appMonth',
					title : '审核月份',
					halign : 'center'
				},{
					field : 'count',
					title : '审核次数',
					halign : 'center'
				},{ 
					field : 'Button',
					title : '操作',
					halign : 'center',
					events : operateEvents,// 给按钮注册事件
					formatter : function(value,row,index) {
						if(row.active == 0){
							return [
								'<a href="javascript:void(0)"><font color="red">无操作</font></a>|'+
								'<a href="javascript:cancel('+row.id+')"><font color="blue">撤销</font></a>'
								].join('');
						}else{
							return[
								'<a id="toCreate" href="javascript:void(0)"><font color="blue">审核</font></a>'
								].join('');
						}
					}
				}],
				idField : 'id',
				classes : 'table table-bordered table-hover table-striped',
				toolbar : '#toolbar',
				pagination : true,
				sidePagination : 'server',
				pageSize : 10,
				singleSelect : true,
				clickToSelect : true,
				pageList : [ 10, 25, 50, 100 ],
				paginationPreText : '上一页',
				paginationNextText : '下一页',
				locale : 'zh-cn',
				ajax : function(params) {
					var row = params.data.limit, index = params.data.offset
							/ params.data.limit + 1;
					if (params.data.q === 1) {
						index = 1;
					}
					var appmonth =$("#datepicker").val();
					win.Config.Ajax(win.Config.Domain+win.Config.Services.queryApplys, {
						name:$('#name').val(),
						code:$('#code').val(),
						index:index,
						row:row,
						appmonth:appmonth
						},'GET', function (result) {
						if (result.status == 0) {
							if (result.data.total == 0) {
								params.success({
									total : 0,
									rows : []
								});
							} else {
								params.success(result.data);
							}
						}  else if (result.status==-7){
							 if (window.top!=null && window.top.document.URL!=document.URL){    
							        window.top.location= win.Config.Domain;     
							   }
						} else {
							layer.alert(result.message[0],{closeBtn : 0,skin : 'layui-layer-molv',icon : 2});
						}
					});
				},
				onDblClickRow: function (row, $element) {
					layer.open({
		 	            type: 2,
		 	            offset:['20px'], 
		 	            title: '申请详情',
		 	            area: ['90%', '60%'],
		 	            fix: false, //不固定
		 	            maxmin: false,
		 	            content: 'applydetail?id='+row.id,
		 	            btn: ['关闭'],
		 	            yes: function (index, layero) {
		 	            	layer.close(index);
		 	            },
					    success: function(layero, index){
					    	var suggest = layer.getChildFrame('#suggest', index);
					    	var auditsuggest = layer.getChildFrame('.w-e-text', index);
					    	var upldpdf = layer.getChildFrame('#upldpdf', index);
		 	            	auditsuggest.attr('contenteditable', false);
						    suggest.attr("readonly",true);
						    upldpdf.hide();
						}
		 	        });
		        }
			});
		}
	};
	win.Apply = applys;
	 function download(i){
		 	var dt =$("#datepicker").val();
		 	if(dt.length == 0){
		 		alert("选择导出月份")
			}else{
				var url =win.Config.Domain+"apply/getExcel/"+i+"/"+dt+".do";
				window.location.href=url;
			}
		}
	function datetimeFormat_1(longTypeDate){  
	    var datetimeType = "";  
	    var date = new Date();  
	    date.setTime(longTypeDate);  
	    datetimeType+= date.getFullYear();   //年  
	    datetimeType+= "-" + getMonth(date); //月   
	    datetimeType += "-" + getDay(date);   //日  
	    datetimeType+= "&nbsp;&nbsp;" + getHours(date);   //时  
	    datetimeType+= ":" + getMinutes(date);      //分
	    datetimeType+= ":" + getSeconds(date);      //分
	    return datetimeType;
	} 
	//返回 01-12 的月份值   
	function getMonth(date){  
	    var month = "";  
	    month = date.getMonth() + 1; //getMonth()得到的月份是0-11  
	    if(month<10){  
	        month = "0" + month;  
	    }  
	    return month;  
	}  
	//返回01-30的日期  
	function getDay(date){  
	    var day = "";  
	    day = date.getDate();  
	    if(day<10){  
	        day = "0" + day;  
	    }  
	    return day;  
	}
	//返回小时
	function getHours(date){
	    var hours = "";
	    hours = date.getHours();
	    if(hours<10){  
	        hours = "0" + hours;  
	    }  
	    return hours;  
	}
	//返回分
	function getMinutes(date){
	    var minute = "";
	    minute = date.getMinutes();
	    if(minute<10){  
	        minute = "0" + minute;  
	    }  
	    return minute;  
	}
	//返回秒
	function getSeconds(date){
	    var second = "";
	    second = date.getSeconds();
	    if(second<10){  
	        second = "0" + second;  
	    }  
	    return second;  
	}
})(window, jQuery);
