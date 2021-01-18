$(document).ready(function(){
	
	$(".login").click(function(){
		let params = $("#form-login").serialize();
		if(check(params)){
			login(params);
		}
	});
	
	function login(params){
		$.ajax({
			type:"post",
			url:"login",
			async:false,
			data:params,
			success:function(res){
				if(res.status==0){
					sessionStorage.setItem("roles",res.data);
					location.href="index";
				}else{
					alert(res.message[1]);
				}
			},
			error:function(err){
				alert("通信错误:"+err);
			}
		});
	}
	
	/**自动检测参数*/
	function check(params){
		let pms = params.split("&");
		let p;
		for(let i = 0; i < pms.length; i++){
			if(pms[i]!=undefined||pms[i]!=null||pms[i]!=''){
				p = pms[i].split("=");
				if(p[1]==undefined||p[1]==null||p[1]==''){
					let str = $('input[name="'+p[0]+'"]').attr("flag");
					alert(str + "不能为空!");
					return false;
				}
			}
			
		}
		return true;
	}
});
