(function(win, $) {
	function clear() {
		$(".form-group .validate").removeClass("has-error");
		$(".form-group .validate span").remove();
	}
	var guide = {
		init : function() {
			var obj = this;
			$('.full-height-scroll').slimScroll({
				height : '100%'
			});

    	    clear();
        	// 绑定按钮点击事件
			$('#query').click(function(){
				$('#name').val($.trim($('#name').val()));
				// 设置参数
				$('#table').bootstrapTable('refreshOptions',{pageNumber:1,pageSize:10});
			});
			 
			obj.initTable();
		},
		initTable : function() {
			$('#table').bootstrapTable(
			{
				columns : [{ 
					field : 'name',
					title : '指导名称',
					halign : 'center'
				},{
					field : 'useMonth',
					title : '指导月份',
					halign : 'center'
				},{
					field : 'counterCode',
					title : '指定柜台',
					halign : 'center'
				},{
					field : 'accUrl',
					title : '图片缩略',
					halign : 'center'
				},{
					field : 'is_use',
					title : '是否使用',
					halign : 'center',
					formatter:function(value,row,index){
						if(row.is_use){
							return '是';
						}else{
							return '否';
						}
					}
				},{
					field : 'id',
					title : '操作',
					halign : 'center',
					formatter:function(value,row){
						return[
							'<a  href="javascript:deleteGuide('+value+')" color="blue">删除</a>'
						].join('');
					}
				}],
				idField : 'counterCode',
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
					
					win.Config.Ajax(win.Config.Domain+win.Config.Services.queryGuide, {
						name:$('#name').val(),
						index:index,
						row:row
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
					window.open(row.accUrl);
		        },
			});
		}
	};
	win.Guide = guide;
})(window, jQuery);