/**
 * Created by yimao on 2016-06-15.
 */
(function (win, $) {
	var menu_list = [];		
	 function menuCircle (value){
     	 
             if (value.childern.length > 0) {
                 menu_list.push('<li class="treeview"><a href="#"><i class="' +
                     value.node.icon+'"></i><span class="menu-label">'+
                     value.node.name+'</span><i class="fa fa-angle-left pull-right"></i></a>');

                 menu_list.push('<ul class="treeview-menu">');
                 $.each(value.childern, function (i, v) { 
                	 
            	 if(v.childern.length>0)
        		 {
            		 menuCircle(v);
        		 }
            	 else
        		 {                		 
            		 menu_list.push('<li class="treeview b"><a href="#" data-url="' + v.node.hrefUrl +
        				 ' " data-index="' + (index+1).toString() + i.toString() +
        				 '" data-mid="'+v.node.id+'"><i class="fa"></i>'+v.node.name+'</a></li>');                 	
        		 }
                 });
                 menu_list.push('</ul></li>');
             } else {  
               menu_list.push('<li class="treeview"><a  href="#" data-url="' + value.node.hrefUrl + 
                     '"><i class="'+
                     value.node.icon+'"></i><span class="menu-label">'+
                     value.node.name+'</span><i class="fa fa-angle-left pull-right"></i></a></li>');
             }
         	
     };
	
 
    var index = {
        init:function () {
          
        	 this.initMenu();  
        	 this.userInfo();
    		 $('#pwd').click(function () {
                 var url = $(this).data("url");
                 $('.main-iframe').attr('src', url);

             }); 
    		 
    		 $('#loginOut').click(this.loginOut);
           
        },
        initMenu:function (menu) {
          
        	 win.Config.Ajax(win.Config.Domain+win.Config.Services.menuInit, {},'GET', function (result) {
                 
                	 if (result.status == 0) {
						var menu = result.data;	                    
						$.each(menu,function (index, value) {
							menuCircle(value);	 
						});
	                    $('#sidebar-menu')
	                    .append(menu_list.join(''));
	                    
	                    $('.b').click(function () {
	                    	$('.b').css("background-color","#2C3B41");
	                    	$(this).css("background-color","#607B8B");
	                        var url = $(this).children("a").data("url");
	                        $('.main-iframe').attr('src', url);
	                    }); 
	                    function NavToggle() {
	                        $(".menu-minimalize").trigger("click")
	                    }
	                    function SmoothlyMenu() {
	                        $("body").hasClass("menu-mini") ? $("body").hasClass("main-sidebar") ? ($("#sidebar-menu").hide(), setTimeout(function () { $("#sidebar-menu").fadeIn(500) }, 300)) : $("#sidebar-menu").removeAttr("style") : ($("#sidebar-menu").hide(), setTimeout(function () { $("#sidebar-menu").fadeIn(500) }, 100))
	                    }
	                    function localStorageSupport() {
	                        return "localStorage" in window && null !== window.localStorage
	                    }

	                    function a() {
	                        var a = $("body > #container").height() - 61; $(".sidebard-panel").css("min-height", a + "px")
	                    }


	                    var flag = "";
	                     
	                    $(".sidebar-container").slimScroll({ height: "100%", railOpacity: .4, wheelStep: 10 }),
	                    $(function () {
	                        $(".menu-collapse").slimScroll({ height: "100%", railOpacity: .9, alwaysVisible: !1 })
	                    })
	                    ,
	                    $(".menu-minimalize").click(function () {

	                        $("body").toggleClass("menu-mini")
	                        , SmoothlyMenu()
	                    })
	                    ,
	                    a(),
	                    $(window).bind("load resize click scroll", function () {
	                        $("body").hasClass("body-small") || a()
	                    }),
	                    $(window).scroll(function () {
	                        $(window).scrollTop() > 0 && !$("body").hasClass("fixed-nav") ? $("#right-sidebar").addClass("sidebar-top") : $("#right-sidebar").removeClass("sidebar-top")
	                    }), 
	                    $("#sidebar-menu>li").click(function () {
	                        if (flag == "ok") { flag = ""; return;}

	                        $("body").hasClass("menu-mini") && NavToggle()
	                        
	                    })
	                    ,
	                    $("#sidebar-menu>li li a").click(function () {                       
	                        if ($(window).width() < 769 )                        
	                        {
	                            NavToggle();
	                            flag="ok"
	                        }
	                    })
	                    , $(".menu-close").click(NavToggle),

	                    $(window).bind("load resize", function () {
	                        $(this).width() < 769 && ($("body").addClass("menu-mini"), $(".menu-static-side").fadeIn()),
	                        $(this).width() >= 769 && $("body").hasClass("menu-mini") && NavToggle()
	                    });


	                    if ($(window).width() < 769) {
	                        !$("body").hasClass("menu-mini") && NavToggle()
	                    }
                	 
                 } else if (result.status == -7) {
                     win.location.href = win.Config.domain;
                 } else {
                     layer.alert(result.message[0], { icon: 5 });
                 }
             }); 
        },       
        loginOut:function(){
        	win.Config.Ajax(win.Config.Domain+win.Config.Services.loginOut, { 
			},'GET', function (result) {	 				 
					window.location.href =window.Config.Domain; 
			}); 
        },
        userInfo:function(){
        	win.Config.Ajax(win.Config.Domain+win.Config.Services.authUserInfo, { 
			},'GET', function (result) {	 				 
					if(result.status==0){
						document.getElementById("lbl").innerText=result.data.loginName;
					}
					else if(result.status==-7){
						window.location.href =window.Config.Domain;
					}
			}); 
        }
    };

    win.Index = index;

})(window,jQuery);