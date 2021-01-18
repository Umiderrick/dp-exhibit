/**
 * Created by yimao on 2016-06-15.
 */

(function(win, $) {
	var pwd = {
		init : function() {
			var obj = this;
			
			win.Vaildation.Init('#form');
			$(".form-group .validate").removeClass("has-error");
			$(".form-group .validate span").remove();
			$("#btnOK").click(obj.pwd);
		},
		pwd : function() {
			var oldPwd=$('#txtOldPwd').val(),
			newPwd = $('#txtNewPwd').val(), 
			checkPwd = $('#txtCheckPwd').val();
			if (!$('#form').valid()) return;
			if(newPwd!=checkPwd)
			{
				layer.alert("密码不一致!", {closeBtn : 0,skin : 'layui-layer-molv',icon : 2});
			}
	 
			win.Config.Ajax(win.Config.Domain+win.Config.Services.pwdUpdate, {
				oldPwd:oldPwd,
				pwd:newPwd
			}, function (result) {	 
				if (result.status == 0) {
					layer.alert(result.message[0], {
						closeBtn : 0,
						skin : 'layui-layer-molv',
						icon : 1
					}); 
				 
				} else if (result.status==-7){
					window.location.href =window.Config.Domain;
				} else {
					layer.alert(result.message[0], {
						closeBtn : 0,
						skin : 'layui-layer-molv',
						icon : 2
					});
				}
				
			}); 
		}
	};

	win.Pwd = pwd;
})(window, jQuery);
