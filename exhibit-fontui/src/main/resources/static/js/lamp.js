$(document).ready(function(){
	initTitle();
	initData();
	$(".save").click(function(){
		let param = $("#form1").serialize();
		if(checkParams(param)){
			$.ajax({
				url:"apply/lampapply",
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
		$("#title").text(y+"年"+m+"月灯片提交");
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
