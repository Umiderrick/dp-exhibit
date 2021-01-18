(function (win,$) {
    var vaildation = {
        Init: function (o) {
            $.metadata.setType("attr", "validate");
            $.validator.setDefaults({
                ignore: ":hidden:not(select)",
                highlight: function (a) {
                    if ($(a).closest(".validate").length>0) {
                        $(a).closest(".validate").removeClass("has-success").addClass("has-error")
                    }
                    else {
                        $(a).closest(".form-group").removeClass("has-success").addClass("has-error")
                    }
                },
                success: function (a) {
                	$(".form-control").css("color","black")
                    if ($(a).closest(".validate").length > 0) {
                        $(a).closest(".validate").removeClass("has-error")
                    }
                    else {
                        $(a).closest(".form-group").removeClass("has-error")
                    }
                    //.addClass("has-success")
                },
                errorElement: "span",
                errorPlacement: function (a, b) { if (b.is(":radio") || b.is(":checkbox")) { a.appendTo(b.parent().parent().parent()) } else { a.appendTo(b.parent()) } },
                errorClass: "m-b-none setColor", validClass: "m-b-none setColor"
            });

            this.AddMethod();

            var options = { rules: {}, messages: {} };
            var a = "<i class='fa fa-times-circle'></i> ";

            $(":input",o).not(":submit, :reset, :image,:button, [disabled]").each(function () {
                if (!this.name) return;

                if ($("#"+this.id).metadata().rules == null || $("#"+this.id).metadata().rules == void 0) return;
                options.rules[this.name] = $("#" + this.name).metadata().rules;

                var obj = $("#" + this.id).metadata().messages;
                if (obj==null || obj==undefined) return;
                $.each(obj, function (i, n) {
                    obj[i] = a + n;

                });

                options.messages[this.name] = obj;

            });

            $(o).validate(options);
        },
        AddMethod:function () {
            // 手机号码验证
            $.validator.addMethod("cellphone", function (value, element){
                var length = value.length;
                return this.optional(element) || (length == 11 && /^(1\d{10})$/.test(value));
            }, "请正确填写手机号码");
 
        }
    };

    win.Vaildation = vaildation;
})(window,jQuery);