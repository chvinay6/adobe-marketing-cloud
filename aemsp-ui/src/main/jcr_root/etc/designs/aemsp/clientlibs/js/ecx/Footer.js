(function ($) {

	"use strict"; 

	function Footer() {

		// private
		var bindscrollToTop = function() {
			$("footer .scrollToTop").click(function() {
				$.scrollTo(0, 1000, {'axis':'y'});
				return false;
			});
		};

		// privileged functions can see private variables/functions
		this.init = function(){
			//bindscrollToTop(); // no scrollToTop-Button yet
		};

	}

	window.Footer = new Footer();


})(jQuery);