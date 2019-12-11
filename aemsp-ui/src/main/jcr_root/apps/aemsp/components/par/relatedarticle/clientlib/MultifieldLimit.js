(function($, $document) {
	"use strict";
	$(document).on("dialog-ready", function() {
		var $limit = $('.js-coral-NumberInput-input[name="./limit"]');
		if ($limit.length) {  	//check if dialog has limit Numberfield
			$(".coral-Form").css("top", "50px");
			$(".coral-Multifield").css({"max-height":"496px", "overflow": "hidden", "overflow-y":"auto"});
			$(".js-coral-Multifield-add").hide();
			adaptMultiField($limit);
			$limit.change(function() {
				adaptMultiField($(this));
			});
		}
	});
	
	function adaptMultiField(limitField) {
		var value = $(limitField).val();
		var $itemList = $("li.js-coral-Multifield-input");
		var currentLength = $itemList.length;
		if(!currentLength) currentLength = 0;
		while(currentLength < value) {
			$(".js-coral-Multifield-add").trigger("click");
			currentLength++;
		} 
		while(currentLength > value) {
			var toRemove = $itemList.get(currentLength-1);
			$(toRemove).find(".js-coral-Multifield-remove").trigger("click");
			currentLength--;
		}
		$(".js-coral-Multifield-remove").hide();
		$(".js-coral-Multifield-move").hide();
		$("li.js-coral-Multifield-input").css("padding-right", "0px");
	}
})($, $(document));