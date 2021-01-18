$(document).ready(function(){
	initTitle();
	initData();
	$(".save").click(function(){
		let param = $("#form1").serialize();
		if(checkParams(param)){
			$.ajax({
				url:"apply/fapply",
				type:"post",
				async:false,
				data:param,
				success:function(res){
					if(res.status==0){
						alert("申请提交成功!");
						location.href="index";
					}else {
						alert(res.message[0])
					}
				},
				error:function(err){
					
				}
			})
		}
	});
	
	//动态判断参数是否完整
	function checkParams(params){
		let pms = params.split("&");
		for(let i=0;i<pms.length;i++){
			let p = pms[i].split("=");
			if(p[0]=="pic7"||p[0]=="area"||p[0]=="areaCounty"){
				continue;
			}
			if(p[1]==undefined||p[1]==null||p[1]==''){
				var msg = $('*[name="'+p[0]+'"]').attr("placeholder");
				alert(msg+"不能为空!");
				return false;
			}
		}
		return true;
	}
	
	//初始化柜台信息
	function initData(){
		$.ajax({
			url:"counterInfo",
			type:"get",
			async:false,
			success:function(res){
				if(res.data!=undefined||res.data!=null||res.data!=''){
					for(var k in res.data[0]){
						var node = $('input[name="'+k+'"]');
						var val = res.data[0][k];
						node.prev().text(val);
						node.val(val);
					}
				}
			},
			error:function(err){
				alert("通信错误");
				location.href="index";
			}
		})
	}
	
	//获取月份信息
	function initTitle(){
		let dt = new Date();
		let y = dt.getFullYear();
		let m = dt.getMonth()+1;
		let d = dt.getDate();
		if(m<10){
			m = 0+""+m;
		}
		if(d > 25 && m < 12){
			m++;
		}
		$.ajax({
                        url: "https://eapi.drplant.com.cn/display/display-series/seriesProportion",    // 提交到controller的url路径
                        type: "get",    // 提交方式
                        data: {"countercode": sessionStorage.getItem("counterCode"), "time":y+'-'+m},  // data为String类型，必须为 Key/Value 格式。
                        dataType: "json",    // 服务器端返回的数据类型
                        success: function (res) {    // 请求成功后的回调函数，其中的参数data为controller返回的map,也就是说,@ResponseBody将返回的map转化为JSON格式的数据，然后通过data这个参数取JSON数据中的值
                                if (res.status === 0) {
                                        var x = res.data;
                                        var i =1
                                        var str ='';
                                        for (var key in x) {
                                                str+=(i+"、"+ key + "占比"+ x[key]+"。")
                                                i++;
                                                if(i==4){
                                                        break;
                                                }
                                        }
                                        $('#seriesRank').text(str)
                                } else {
                                        console.log(res.message)
                                }
                        },
                });
		$("#title").text(y+"年"+m+"月陈列申请");
	}
	

});

//上传图片
function previewPic(pic){
	let nextNode = $(pic).next();
	let pclass = nextNode.attr("name");
	let li = $("."+pclass);
	li.empty();
	let picture = pic.files[0];

	let reader = new FileReader();
    reader.readAsDataURL(picture);   
    reader.onload = function(e){
		let srcBase64=this.result;
			dealImage(srcBase64,{width:600},function(base64){
	    		$.ajax({
	    			url:"apply/uploadpic",
	    			type:"post",
	    			data:{img:base64,picType:pclass},
	    			async:true,
	    			success:function(res){
	    				if(res.status==0){
	    					li.html('<li class="weui-uploader__file weui-uploader__file_status" style="background-image:url('
	    							+base64
	    							+')">'
	    							+'<div class="weui-uploader__file-content">'
	    							+' <i class="weui-icon-success"></i>'
	    							+'</div></li>');
							nextNode.val(res.data);
							//显示大图查看
							$(".weui-uploader__files li").click(function(){
								var picSrc = $(this).css("background-image");
								$(".weui-gallery__img").css("background-image", picSrc);
								$(".weui-gallery").addClass("gallery-show");
							});
							//隐藏大图查看
							$(".weui-gallery__img, .weui-gallery__opr").click(function(){
								$(".weui-gallery").removeClass("gallery-show");
							});
	    				}else{
	    					alert(res.message[0]);
						}

	    			},
	    			error:function(err){
	    				alert("通讯错误!"+err);
	    			}
	    		});
			})
     }
}


//检测满意度数值
function checkSatisfy(e){
	let satisfy = $(e).val();
	satisfy = Math.floor(satisfy);
	if(satisfy>10){
		$(e).val(10);
		return;
	}
	if(satisfy<0){
		$(e).val(0);
		return;
	}
	$(e).val(satisfy);
}

//textarea检测字数
function checkSuggest(e,maxLen){
	let ta = $(e);
	let len = ta.val().length;
	$("#suggest-len").text(len+"/"+maxLen);
	if(len > maxLen){
		$("#suggest-len").text(maxLen+"/"+maxLen);
		ta.val(ta.val().substring(0, maxLen)); //就去掉多余的字
	}
}
