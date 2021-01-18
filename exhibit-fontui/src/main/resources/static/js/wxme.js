$(document).ready(function(){
	initData();
	
	function initData(){
		let counterCode = sessionStorage.getItem("counterCode");
		if(counterCode==undefined||counterCode==null||counterCode==''){
			initCountsData();
		}else{
			initCounterInfo();
		}
	}
	
	
	function initCounterInfo(){
		$.ajax({
			url:"counterInfo",
			type:"get",
			async:false,
			success:function(res){
				if(res.data==undefined||res.data==null||res.data==''){
					alert("无数据!");
				}else{
					dealData(res.data);
				}
			},
			error:function(err){
				alert("通信错误");
			}
		})
	}
	
	function initCountsData(){
		$.ajax({
			url:"getCounters",
			type:"get",
			async:false,
			success:function(res){
				if(res.data==undefined||res.data==null||res.data==''){
					$("#counters").html("无数据!");
				}else{
					dealData(res.data);
				}
			},
			error:function(err){
				alert("通信错误:"+err);
			}
		})
	}
	
	function dealData(data){
		let list = '';
		for(var i=0; i<data.length; i++){
			list+='<div class="iblock">'
					+'<div class="weui-cells">'
					+	'<div class="weui-cell">'
					+		'<div class="weui-cell__hd">用户姓名：</div>'
					+		'<div class="weui-cell__bd">'
					+			'<span name="contact">'+data[i].contact+'</span>'
					+		'</div>'
					+	'</div>'
					+	'<div class="weui-cell">'
					+		'<div class="weui-cell__hd">手机号码：</div>'
					+		'<div class="weui-cell__bd">'
					+			'<span name="mobile">'+data[i].mobile+","+data[i].phone+'</span>'
					+		'</div>'
					+	'</div>'
					+	'<div class="weui-cell">'
					+		'<div class="weui-cell__hd">柜台号码：</div>'
					+		'<div class="weui-cell__bd">'
					+			'<span name="counterCode">'+data[i].counterCode+'</span>'
					+		'</div>'
					+	'</div>'
					+	'<div class="weui-cell">'
					+		'<div class="weui-cell__hd">柜台名称：</div>'
					+		'<div class="weui-cell__bd">'
					+			'<span name="counterName">'+data[i].counterName+'</span>'
					+		'</div>'
					+	'</div>'
					+	'<div class="weui-cell">'
					+		'<div class="weui-cell__hd">柜台地址：</div>'
					+		'<div class="weui-cell__bd">'
					+			'<span name="address">'+data[i].address+'</span>'
					+		'</div>'
					+	'</div>'
					+'</div>'
			+'</div>';
		}
		$("#counters").html(list);
	}
});
