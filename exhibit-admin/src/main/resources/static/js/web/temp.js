/**
 * Created by yimao on 2016-06-15.
 */
(function (win, $) {
    var temp = {
        init: function () {
            var s = win.Config.GetQueryString('s'),
                o = win.Config.GetQueryString('o'),
                m = win.Config.GetQueryString('m'),
                n = win.Config.GetQueryString('n'),
                id = win.Config.GetQueryString('id'),
                uc = win.Config.GetQueryString('uc'),
                su = win.Config.GetQueryString('su'),
                iu = win.Config.GetQueryString('iu'),
                sid = win.Config.GetQueryString('sid'),
                un  = win.Config.GetQueryString('un');


            if (s === ''){
                win.location.href = win.Config.Domain;
            }
           
            win.Config.CreateCookie('session', s);
            win.Config.CreateCookie('o', o);
            win.Config.CreateCookie('m', m);
            win.Config.CreateCookie('n', n);
            win.Config.CreateCookie('id', id);
            win.Config.CreateCookie('uc', uc);
            win.Config.CreateCookie('su', win.decodeURIComponent(su));
            win.Config.CreateCookie('iu', win.decodeURIComponent(iu));
            win.Config.CreateCookie('sid', sid);
            win.Config.CreateCookie('un', win.decodeURIComponent(un));

            win.location.href = "index.html";
        }
           
    };

    win.Temp = temp;

})(window,jQuery);