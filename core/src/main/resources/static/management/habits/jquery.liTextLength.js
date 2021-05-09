/*
 * jQuery liTextLength v 2.0
 *
 * Copyright 2012, Linnik Yura | LI MASS CODE | http://masscode.ru
 * http://masscode.ru/index.php/k2/item/24-litextlength
 * Free to use
 * 
 * October 2012
 */
 
jQuery.fn.liTextLength = function(options){
	// настройки по умолчанию
	var o = jQuery.extend({
		length: 150,									//Видимое кол-во символов
		afterLength: '...',								//Текст после видимого содержания		
		fullText:true,									//Добавить ссылку для отображения скрытого текста
		moreText: '<br>полный&nbsp;текст',				//Текст ссылки до показа скрытого содержания
		lessText: '<br>скрыть&nbsp;полный&nbsp;текст'	//Текст ссылки после показа скрытого содержания
	},options);
	return this.each(function(){
		var 
		$el = $(this),
		elText = $.trim($el.text()),
		elLength = elText.length;
		if(elLength > o.length){ 
			var 
			textSlice = $.trim(elText.substr(0,o.length)),
			textSliced = $.trim(elText.substr(o.length));
			if(textSlice.length < o.length){
				var 
				textVisible = textSlice,
				textHidden = $.trim(elText.substr(o.length));
			}else{	
				var 
				arrSlice = textSlice.split(' '),
				popped = arrSlice.pop(),
				textVisible = arrSlice.join(' ') + ' ',
				textHidden = popped + textSliced  + ' ';
			};
			var 
			$elTextHidden = $('<span>').addClass('elTextHidden').html(textHidden),
			$afterLength = $('<span>').addClass('afterLength').html(o.afterLength + ' '),
			$more = $('<span>').addClass('more').html(o.moreText);
			$el.text(textVisible).append($afterLength).append($elTextHidden);
			var displayStyle = $elTextHidden.css('display');
			$elTextHidden.hide();
			if(o.fullText){
				$el.append($more);
				$more.click(function(){
					if($elTextHidden.is(':hidden')){
						$elTextHidden.css({display:displayStyle})	;
						$more.html(o.lessText);
						$afterLength.hide();
					}else{
						$elTextHidden.hide();
						$more.html(o.moreText);
						$afterLength.show();
					};
					return false;
				});
			}else{
				$elTextHidden.remove();
			};
		};
	});
};