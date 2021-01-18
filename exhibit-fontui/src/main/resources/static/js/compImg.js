/**
 * 图片压缩.定宽定高压缩
 * @param {Object} path 
 *   pc端传入的路径可以为相对路径，但是在移动端上必须传入的路径是照相图片储存的绝对路径
 * @param {Object} obj
 *   obj 对象 有 width， height， quality(0-1)
 * @param {Object} callback
 *   回调函数有一个参数，base64的字符串数据
 */
function dealImage(path, obj, callback) {
	let img = new Image();
	img.src = path;
	img.onload = function() {
		let that = this;
		// 默认按比例压缩
		let w = that.width,
			h = that.height;
		//传递的宽、高、放大缩小倍数
		let obw = obj.width,
		    obh = obj.height,
		    mul=0;
		
		//新的 宽高
		let nw,nh;
		
		//定宽进行压缩
		if((obw!=undefined||obw!=null||obw!='')&&(obh==undefined||obh==null||obh=='')){
			mul = obw > w ? 1 : obw / w;
			nw = obw > w ? w : obw;
			nh = obw > w ? h : h * mul;
		}
		
		//定高进行压缩
		if((obh!=undefined||obh!=null||obh!='')&&(obw==undefined||obw==null||obw=='')){
			mul = obh > h ? 1 : obh / h;
			nh = obh > h ? h : obh;
			nw = obh > h ? w : w * mul;
		}
		
		//固定宽高变换
		if((obh!=undefined&&obh!=null&&obh!='')&&(obw!=undefined&&obw!=null&&obw&&'')){
			nw = obw;
			nh = obh;
		}
		
		// 默认图片质量为0.7
		let quality = 0.7;
		//生成canvas
		let canvas = document.createElement('canvas');
		let ctx = canvas.getContext('2d');
		// 创建属性节点
		let anw = document.createAttribute("width");
		anw.nodeValue = nw;
		let anh = document.createAttribute("height");
		anh.nodeValue = nh;
		canvas.setAttributeNode(anw);
		canvas.setAttributeNode(anh);
		
		ctx.drawImage(that, 0, 0, nw, nh);

		// 图像质量
		if(obj.quality && obj.quality <= 1 && obj.quality > 0) {
			quality = obj.quality;
		}
		// quality值越小，所绘制出的图像越模糊
		let base64 = canvas.toDataURL('image/jpeg', quality);
		// 回调函数返回base64的值
		callback(base64);
	}
}
