$(document).ready(function() {
	initCountCode();
	initMonth();
	checkSub();
	getguide();
	checkPassStatus();
	$(".logout").click(function() {
		loginout();
	});

	//获取柜台
	function initCountCode() {
		//localStorage
		let roles = sessionStorage.getItem("roles");
		if (roles == null || roles == undefined || roles == '') {
			location.href = "wxlogin";
			return;
		}
		$("#countCode").empty();
		$.ajax({
			url : "getCounters",
			type : "get",
			async : false,
			success : function(res) {
				if (res.status == 0) {
					let lcounter = sessionStorage.getItem("counterCode");
					if (res.data.length == 1) {
						let counterCode = res.data[0].counterCode;
						$("#currc").html("已选柜台:" + counterCode);
						sessionStorage.setItem("counterCode", counterCode);
						checkSub();
						getguide();
						checkPassStatus();
					} else {
						let cts = "<select id='cts'><option value='0'>--选择柜台号--</option>";
						for (let i = 0; i < res.data.length; i++) {
							if (lcounter == res.data[i].counterCode) {
								cts += '<option value="' + res.data[i].counterCode + '" selected = "selected">' + res.data[i].counterName + '</option>';
								continue;
							}
							cts += '<option value="' + res.data[i].counterCode + '">' + res.data[i].counterName + '</option>';
						}
						cts += "</select>";
						$("#countCode").html(cts);
						$("#cts").select2();
					}
				}
			},
			error : function(err) {
				console.log(err)
			}
		});
	}

	//经理选择柜台
	$("#countCode select").change(function(res) {
		$("#message-box").text('');
		let ccode = $(this).val();
		if (ccode == 0) {
			sessionStorage.removeItem("counterCode");
			$(".sub").attr("href", "javascript:void(0)");
			$("#message-box").text("未选择柜台");
			$("#currc").text("");
			$("#message-box").show();
			getguide();
			checkPassStatus();
			return;
		}
		$.ajax({
			url : "changecounter",
			type : "post",
			data : {
				counterCode : ccode
			},
			async : false,
			success : function(res) {
				if (res.status != 0) {
					alert(res.message[1])
				}
				sessionStorage.setItem("counterCode", ccode);
				$("#currc").html("已选柜台:" + ccode);
				checkSub();
				getguide();
				checkPassStatus();
			},
			error : function(err) {}
		});
	});

	//检测时候是否有 叉叉
	function checkPassStatus() {
		$.ajax({
			url : "getunpass",
			type : "get",
			async : false,
			success : function(res) {
				if (res.status == 0) {
					if( (res.data != undefined || res.data != null || res.data != '') ) {
						$(".tips").text(res.data);
						$(".tips").show();
					} else {
						$(".tips").text(res.data);
						$(".tips").hide();
					}
				} else {
					alert("请重新登陆");
				}

			},
			error : function(err) {
				alert("请重新登陆");
			}
		})
	}
	function getDate() {
		let retDate ='';
		$.ajax({
			url : "getFiscaleDate",
			type : "get",
			async : false,
			success : function(res) {
				retDate = res.data;
			}
		});
		return retDate;
	}

	//获取月份信息
	function initMonth() {
		let showd = getDate();
		$("#month").text(showd);
	}

	//是否已经提交

	function checkSub() {
		$("#message-box").text('');
		let ct = sessionStorage.getItem("counterCode");
		if (ct == undefined || ct == null || ct == '') {
			$("#message-box").text("未选择柜台");
			console.log("未选择柜台");
			$(".sub").attr("href", "javascript:void(0)");
		}
		$.ajax({
			url : "issub",
			type : "get",
			async : false,
			success : function(res) {
				if (res.status == 0) {
					if (res.data == false) {
						$("#message-box").text("已经提交过了");
						$(".sub").attr("href", "javascript:void(0)");
					} else {
						$(".sub").attr("href", "wxapply");
					}
				}
			},
			error : function(err) {}
		});
	}


	function getguide() {
		$.ajax({
			url : "getguide",
			type : "get",
			async : true,
			success : function(res) {
				if (res.status == 0) {
					$(".readbutton").attr("href", "viewPDF?url=" + res.data.accUrl);
				} else if (res.status == -1) {
					console.log(res.message)
				} else if (res.status == -7) {
					console.log("未选择柜台")
				}
			},
			error : function(err) {}
		});
	}
	//退出登录
	function loginout() {
		$.ajax({
			url : "loginout",
			type : "get",
			async : false,
			success : function(res) {
				if (res.status == 0) {
					sessionStorage.clear();
					location.href = "wxlogin";
				}
			},
			error : function(err) {}
		});
	}
});