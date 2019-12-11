(function ($) {

	"use strict"; 

	function DeviceSelector () {

		// private
		var media;

		// privileged functions can see private variables/functions
		this.init = function(){
			media = $(".deviceSelector:visible").data("devicetype");

			$("html")
				.removeClass("device-xs")
				.removeClass("device-sm")
				.removeClass("device-md")
				.removeClass("device-lg")
				.addClass("device-" + media);
		};

		this.getDeviceType = function(){
    		return media;
    	};

	}

	window.DeviceSelector = new DeviceSelector();

})(jQuery);