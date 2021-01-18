$(document).ready(function() {
	initBaseData();
	let theDate = initDate();
	if (theDate == undefined || theDate == null || theDate == '') {
		theDate = "0";
	}
	initMonthData(theDate);

	$("#appdate").change(function() {
		let d = $(this).val();
		initMonthData(d);
	});
	//初始化本月信息
	function initMonthData(theD) {
		let issue = theD.split("-");
        $("#count").val(1);
		$("#title").text(issue[0] + "年" + issue[1] + "月陈列数据");
		$.ajax({
			url : "apply/curmonthresult",
			type : "get",
			async : false,
			data : {
				time : theD
			},
			success : function(res) {
				if (res.data == undefined || res.data == null || res.data == '') {
					alert("该月无数据!");
					$("#rebutton").hide();
				} else {
					initNodeData(res);
				}
			},
			error : function(err) {
				alert("通信错误:"+err);
				location.href = "index";
			}
		})
		 $.ajax({
                        url: "https://eapi.drplant.com.cn/display/display-series/seriesProportion",    // 提交到controller的url路径
                        type: "get",    // 提交方式
                        data: {"countercode": sessionStorage.getItem("counterCode"), "time":issue[0]+'-'+issue[1]},  // data为String类型，必须为 Key/Value 格式。
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
		
	}

	//初始化数据
	function initNodeData(res) {

		//显示标题
		$("#appId").val(res.data.id);
		$("span[name='counterCode']").text(res.data.counterCode);
		$("span[name='counterName']").text(res.data.counter.counterName);
		$("span[name='area']").text(res.data.counter.area);
		$("span[name='areaCounty']").text(res.data.counter.areaCounty);
		$("span[name='receiveTime']").text(res.data.receiveTime.slice(0,10));
		$("span[name='satisfy']").text(res.data.satisfy);
		$("span[name='showcaseQty']").text(res.data.showcaseQty);
		$("span[name='suggest']").html(res.data.suggest);
		$("span[name='auditsuggest']").html(res.data.auditsuggest);

		//isPass
		//count 0第一次 1复审
		let audit = $("#audit");
		audit.empty();
		let at = "<li class='weui-uploader__file'><div class='weui-uploader__input-box'><input class='weui-uploader__input' type='file' accept='image/*' onchange='previewPic(this);'/>" +
			"<input type='hidden' name='pic1'  placeholder='门头整理'></div></li>";
		let bt = "<li class='weui-uploader__file'><div class='weui-uploader__input-box'><input class='weui-uploader__input' type='file' accept='image/*' onchange='previewPic(this);'/>" +
			"<input type='hidden' name='pic2'  placeholder='主推台'></div></li>";
		let ct = "<li class='weui-uploader__file'><div class='weui-uploader__input-box'><input class='weui-uploader__input' type='file' accept='image/*' onchange='previewPic(this);'/>" +
			"<input type='hidden' name='pic3'  placeholder='第一高柜'></div></li>";
		let ct2 = "<li class='weui-uploader__file'><div class='weui-uploader__input-box'><input class='weui-uploader__input' type='file' accept='image/*' onchange='previewPic(this);'/>" +
			"<input type='hidden' name='pic8'  placeholder='第二高柜'></div></li>";
		let ct3 = "<li class='weui-uploader__file'><div class='weui-uploader__input-box'><input class='weui-uploader__input' type='file' accept='image/*' onchange='previewPic(this);'/>" +
			"<input type='hidden' name='pic9'  placeholder='第三高柜'></div></li>";
		let dt = "<li class='weui-uploader__file'><div class='weui-uploader__input-box'><input class='weui-uploader__input' type='file' accept='image/*' onchange='previewPic(this);'/>" +
			"<input type='hidden' name='pic4'  placeholder='集中售卖区'></div></li>";
		let et = "<li class='weui-uploader__file'><div class='weui-uploader__input-box'><input class='weui-uploader__input' type='file' accept='image/*' onchange='previewPic(this);'/>" +
			"<input type='hidden' name='pic5'  placeholder='面膜/排行榜/植萃美眼'></div></li>";
		let ft = "<li class='weui-uploader__file'><div class='weui-uploader__input-box'><input class='weui-uploader__input' type='file' accept='image/*' onchange='previewPic(this);'/>" +
			"<input type='hidden' name='pic6'  placeholder='护理区'></div></li>";
		let gt = "<li class='weui-uploader__file'><div class='weui-uploader__input-box'><input class='weui-uploader__input' type='file' accept='image/*' onchange='previewPic(this);'/>" +
			"<input type='hidden' name='pic7'  placeholder='工服'></div></li>";
		if ((res.data != null || res.data != undefined || res.data != null)
			&& (res.data.isPass == false || res.data.isPass == null || res.data.isPass == undefined || res.data.isPass == '')) {
			if (res.data.count == 1 && res.data.isPass == false && res.data.active == false) {
				audit.append('<div class="weui-flex__item">'
					+ '<label for="cs" style="vertical-align: middle;">结果:</label>'
					+ '<i class="weui-icon-cancel" id="cs" style="font-size: 16px;">未通过</i>'
					+ '</div>');
				$(".pic1").after(at)
				$(".pic2").after(bt)
				$(".pic3").after(ct)
				$(".pic8").after(ct2)
				$(".pic9").after(ct3)
				$(".pic4").after(dt)
				$(".pic5").after(et)
				$(".pic6").after(ft)
				$(".pic7").after(gt)
			} else if (res.data.count == 1 && res.data.isPass == false && res.data.active == true) {
				audit.append('<div class="weui-flex__item">'
					+ '<label for="cs" style="vertical-align: middle;">结果:</label>'
					+ '<i class="weui-icon-cancel" id="cs" style="font-size: 16px;">未通过</i>'
					+ '</div>');
				$("#rebutton").hide();
			} else if (res.data.count == 0 && res.data.isPass == false) {
				audit.append('<div class="weui-flex__item">'
					+ '<label for="cs" style="vertical-align: middle;">结果:</label>'
					+ '<i class="weui-icon-waiting" id="cs" style="font-size: 16px;">审核中</i>'
					+ '<button class="weui-btn weui-btn_primary" id="cancelAudit">撤回本次提交</button>'
					+ '</div>');
				$("#rebutton").hide();
			}
		}

		//第一次审核通过
		if (res.data.isPass == true && res.data.count == 1) {
			audit.append('<div class="weui-flex__item">'
				+ '<label for="cs" style="vertical-align: middle;">结果:</label>'
				+ '<i class="weui-icon-success" id="cs" style="font-size: 16px;">通过</i>'
				+ '</div>');
			$("#rebutton").hide();
		}

		for (let i = 0; i < res.data.piclist.length; i++) {
			switch (i + 1) {
			case 1:
				$(".pic1").css({
					"background-image" : "url(" + res.data.piclist[i].url + ")"
				});
				break;

			case 2:
				$(".pic2").css({
					"background-image" : "url(" + res.data.piclist[i].url + ")"
				});
				break;

			case 3:
				$(".pic3").css({
					"background-image" : "url(" + res.data.piclist[i].url + ")"
				});
				break;

			case 4:
				$(".pic4").css({
					"background-image" : "url(" + res.data.piclist[i].url + ")"
				});
				break;

			case 5:
				$(".pic5").css({
					"background-image" : "url(" + res.data.piclist[i].url + ")"
				});
				break;
			case 6:
				$(".pic6").css({
					"background-image" : "url(" + res.data.piclist[i].url + ")"
				});
				break;
			case 7:
				$(".pic7").css({
					"background-image" : "url(" + res.data.piclist[i].url + ")"
				});
				break;
			case 8:
				$(".pic8").css({
					"background-image" : "url(" + res.data.piclist[i].url + ")"
				});
				break;
			case 9:
				$(".pic9").css({
					"background-image" : "url(" + res.data.piclist[i].url + ")"
				});
				break;
			}
		}
		//隐藏大图查看
		$(".weui-gallery__img,.weui-gallery__opr").click(function() {
			$(".weui-gallery").removeClass("gallery-show");
		});
		//显示大图查看
		$(".pic").click(function() {
			let picSrc = $(this).css("background-image");
			$(".weui-gallery__img").css("background-image", picSrc);
			$(".weui-gallery").addClass("gallery-show");
		});
	}

	$("#cancelAudit").click(function() {
		let appMonth = theDate;
		$.ajax({
			url : "apply/cancelAudit",
			type : "post",
			async : false,
			data : {
				appmonth : appMonth
			},
			success : function(res) {
				if (res.status == 0) {
					if (res.data) {
						alert("撤回成功，重新提交");
						location.href = "index";
					} else {
						alert("已被审核，无法撤回");
					}
				} else {
					alert("通信错误");
				}

			},
			error : function(err) {
				alert("通信错误"+err);
			}
		})
	});
	//long类型转时间
	function longToDate(time) {
		let y = time.getFullYear();
		let m = time.getMonth() + 1;
		let d = time.getDate();
		if (m < 10) {
			m = '0' + m;
		}
		if (d < 10) {
			d = '0' + d;
		}
		return y + "/" + m + "/" + d;
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
	function initBaseData() {
		let showd = getDate();
		$("#title").text(showd + "月陈列数据");
	}

	//初始化时间
	function initDate() {
		let showd = getDate();
		let issue = showd.split("-");
		let theTime;
		let dt ='';
		let y = issue[0];
		let m = parseInt(issue[1]); // 234
		let appdate = $("#appdate");
		appdate.empty();
		let mt;
		for (let i = 1; i <= m; i++) {
			if (i < 10) {
				mt = '0' + i;
			} else {
				mt = i;
			}
			var ttime = y + '-' + mt;
			if (i == m) {
				theTime = ttime;
				dt += '<option value="' + ttime + '" selected="selected">' + ttime + '</option>'; //selected="selected"
			} else {
				dt += '<option value="' + ttime + '">' + ttime + '</option>'; //selected="selected"
			}

		}
		appdate.html(dt);
		return theTime;
	}

});
function previewPic(pic) {
	let that = $(pic).parent().parent().prev();
	let picture = pic.files[0];
	let nextNode = $(pic).next();
	let pclass = nextNode.attr("name");
	let xxx =$("#count").val();
	let ii =Number(xxx);
    ii++;
	let reader = new FileReader();
	reader.readAsDataURL(picture);
	reader.onload = function() {
		let pbase64 = this.result;
		dealImage(pbase64, {
			width : 600
		}, function(base64) {
			$.ajax({
				url : "apply/uploadpic",
				type : "post",
				data : {
					img : base64,
                    picType : pclass,
                    ct:ii
				},
				async : true,
				success : function(res) {
					if (res.status == 0) {
						that.css({
							"background-image" : "url(" + base64 + ")"
						});
						nextNode.val(res.data);
						//隐藏大图查看
						$(".weui-gallery__img,.weui-gallery__opr").click(function() {
							$(".weui-gallery").removeClass("gallery-show");
						});
						//显示大图查看
						$(".pic").click(function() {
							var picSrc = $(this).css("background-image");
							$(".weui-gallery__img").css("background-image", picSrc);
							$(".weui-gallery").addClass("gallery-show");
						});
					}else{
						alert(res.message[1]);
					}

				},
				error : function(err) {
					alert("通讯错误!"+err);
				}
			});
		})
	}
}