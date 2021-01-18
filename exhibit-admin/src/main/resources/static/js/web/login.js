/**
 * Created by yimao on 2016-06-15.
 */

(function(window, $) {
	var login = {
		init : function() {
			this.loginOn();
		},
		loginOn : function() {
			$('#defaultForm')
					.bootstrapValidator({
						fields : {
							username : {
								validators : {
									notEmpty : {
										message : '用户名称不能为空！'
									}
								}
							},
							password : {
								// message:'密码不能为空！',
								validators : {
									notEmpty : {
										message : '密码不能为空！'
									}
								}
							}
						}
					})
					.on(
					'success.form.bv',
					function(e) {
						e.preventDefault();

						var $form = $(e.target);

						$.ajax({

							type : "POST",
							url : Config.Domain+Config.Services.login,
							data : $form.serialize(),
							dataType : "json",
							success : function(result) {
								if (result.status == 0) {
									window.location.href = Config.Domain
											+ "/index";

								} else {
									$('#error').html(result.message[0]);
								}
							}
						});

					});
		}
	};

	window.Login = login;
})(window, jQuery);
