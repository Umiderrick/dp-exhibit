(function() {
	var config = {
		Services : {
			'login' : '/login.do', // 登录接口 
			'loginOut' : '/logout.do', // 退出登录  
			'menuInit' : '/menuinit.do', //菜单
			'getPMenu' : '/get/pmenu.do', //父菜单tree
			'getCMenu' : '/get/cmenu.do', //子菜单
			'authUserInfo' : '/authuser/userinfo.do', //登录成功，获取登陆者的信息
			'queryApplys' : '/apply/list.do',
			'queryApplydetail' : '/apply/detail.do', //查询门店
			'queryGuide' : '/guide/list.do', //门店明细
			'queryGuidedetail' : '/guide/detail.do', //查询门店
			'queryPass' : '/apply/pass.do',
			'queryRefuse' : '/apply/refuse.do',
			'queryUpload' : '/guide/uploadPDF.do',
			'getallCounters':'/counter/getallcounters.do',
			'saveGuide':'/guide/saveGuide.do',
		},
		Domain : '/exhibit-adm',
		GetVfPrefix : 'https://api.drplant.com.cn/dp-vfs_t/api/',

		Ajax : function(url, data, type, success) {
			$.ajax({
				url : url,
				data : data,
				async : true,
				type : type,
				dataType : 'json',
				success : success,
				error : function(jqXHR, textStatus, errorThrown) {
					if (textStatus == 'error') {
						alert('网络通信失败，请稍后重试！');
					} else {
						alert(errorThrown + ',' + textStatus);
					}
				}
			});

		},
		//电话验证
		IsMobile : function(mobile) {
			if (mobile.length == 0) {
				return false;
			}
			if (mobile.length != 11) {
				return false;
			}

			var myreg = /^(((1[3-9][0-9]{1})|159|153)+\d{8})$/;
			if (!myreg.test(mobile)) {
				return false;
			}
			return true;
		},
		//邮箱验证
		IsEmail : function(email) {
			var re = /^(\w-*\.*)+@(\w-?)+(\.\w{2,})+$/;
			if (re.test(email)) {
				return true;
			} else {
				return false;
			}
		},
		ValidateIdCard : function(idCard) {
			//15位和18位身份证号码的正则表达式
			var regIdCard = /^(^[1-9]\d{7}((0\d)|(1[0-2]))(([0|1|2]\d)|3[0-1])\d{3}$)|(^[1-9]\d{5}[1-9]\d{3}((0\d)|(1[0-2]))(([0|1|2]\d)|3[0-1])((\d{4})|\d{3}[Xx])$)$/;

			//如果通过该验证，说明身份证格式正确，但准确性还需计算
			if (regIdCard.test(idCard)) {
				if (idCard.length == 18) {
					var idCardWi = new Array(7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2); //将前17位加权因子保存在数组里
					var idCardY = new Array(1, 0, 10, 9, 8, 7, 6, 5, 4, 3, 2); //这是除以11后，可能产生的11位余数、验证码，也保存成数组
					var idCardWiSum = 0; //用来保存前17位各自乖以加权因子后的总和
					for (var i = 0; i < 17; i++) {
						idCardWiSum += idCard.substring(i, i + 1) * idCardWi[i];
					}

					var idCardMod = idCardWiSum % 11; //计算出校验码所在数组的位置
					var idCardLast = idCard.substring(17); //得到最后一位身份证号码

					//如果等于2，则说明校验码是10，身份证号码最后一位应该是X
					if (idCardMod == 2) {
						if (idCardLast == "X" || idCardLast == "x") {
							return true;
						} else {
							return false;
						}
					} else {
						//用计算出的验证码与最后一位身份证号码匹配，如果一致，说明通过，否则是无效的身份证号码
						if (idCardLast == idCardY[idCardMod]) {
							return true;
						} else {
							return false;
						}
					}
				}
			} else {
				return false;
			}
		},
		//获取URL中的参数值
		GetQueryString : function(name) {
			var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i");
			var r = window.location.search.substr(1).match(reg); //获取url中"?"符后的字符串并正则匹配
			var context = "";
			if (r != null)
				context = r[2];
			reg = null;
			r = null;
			return context == null || context == "" || context == "undefined" ? "" : context;
		},

		getQuery : function(obj) {
			var group = [];
			$(":input", obj).not(":submit, :reset, :image,:button, [disabled]").each(function() {
				if (!this.name) return;

				if ($(this)[0].type == "checkbox" && $(this)[0].checked == false) return;
				if ($(this)[0].type == "radio" && $(this)[0].checked == false) return;
				if ($(this).val() == null || $(this).val() == "") return;
				if ($(this).attr("op") == undefined) return;
				if ($(this).attr("fieldtype") == undefined) return;

				group.push({
					op : $(this).attr("op"),
					field : this.name,
					value : ($(this)[0].type == "checkbox" || $(this)[0].type == "radio") ? ($(this)[0].checked == true ? 1 : 0) : $(this).val(),
					type : $(this).attr("fieldtype")
				});
			});
			var queryJson = "";
			if (group.length > 0) {
				queryJson = JSON.stringify(group);
			}

			return queryJson;
		},
		//获取URL中的参数值
		InitSelect : function(obj, info, idname, valuename) {
			var data = new Array();
			data.push('<option value="">请选择</option>');
			if (info != null) {
				if (info.length > 0) {
					$.each(info, function(index, value) {
						var s = '';
						$.each(value, function(i, n) {
							if (i == idname)
								s = '<option value="' + n + '">'
							if (i == valuename)
								s = s + n + '</option>';
						});
						if (s != "") {
							data.push(s);
						}
					});
				}
			}
			obj.empty().append(data.join(''));
		},
		initSelect : function(id, params, url, initval) {
			var obj = this;
			// 获取Select列表内容
			obj.Ajax(obj.Domain + url, params,'GET', function(result) {
				if (result.status == 0) {
					$('#' + id).select2({
						data : result.data.rows
					});
					if (!initval) {
						$('#' + id).val('').trigger("change");
					}
				} else if (result.status == -7) {
					window.location.href = obj.Domain;
				} else {
					layer.alert(result.message[0], {
						closeBtn : 0,
						skin : 'layui-layer-molv',
						icon : 2
					});
				}
			});
		},
		/**日期格式化*/
		dateFormat : function(date) {
			Y = date.getFullYear() + '-';
			M = (date.getMonth() + 1 < 10 ? '0' + (date.getMonth() + 1) : date
					.getMonth() + 1)
				+ '-';
			D = date.getDate().toString().length == 1 ? '0' + date.getDate()
				: date.getDate();
			return Y + M + D;
		},
		/**时间格式化*/
		timeFormat : function(time) {
			Y = time.getFullYear() + '-';
			M = (time.getMonth() + 1 < 10 ? '0' + (time.getMonth() + 1) : time
					.getMonth() + 1)
				+ '-';
			D = time.getDate().toString().length == 1 ? '0' + time.getDate()
				: time.getDate();
			H = time.getHours() < 10 ? ' 0' + (time.getHours()) : ' '
			+ time.getHours();
			Mi = ':'
			+ (time.getMinutes() < 10 ? '0' + (time.getMinutes())
				: time.getMinutes());
			S = ':'
			+ (time.getSeconds() < 10 ? '0' + (time.getSeconds())
				: time.getSeconds());
			return Y + M + D + H + Mi + S;
		},
		clearSelect : function(id) {
			document.getElementById(id).options[0].selected = true;
			$('#' + id).trigger("change");
		},
		dateAdd : function(part, value) {
			var d = new Date();
			value *= 1;
			if (isNaN(value)) {
				value = 0;
			}
			switch (part) {
			case 'y':
				d.setFullYear(d.getFullYear() + value);
				return d;
			case 'm':
				d.setMonth(d.getMonth() + value);
				return d;
			case 'd':
				d.setDate(d.getDate() + value);
				return d;
			case 'h':
				d.setHours(d.getHours() + value);
				return d;
			case 'n':
				d.setMinutes(d.getMinutes() + value);
				return d;
			case 's':
				d.setSeconds(d.getSeconds() + value);
				return d;
			default:
				return d;
			}
		}
	};
	window.Config = config;
})();
