(function ($) {

	'use strict';

	function Header() {

		// private
		/*var bindShowLoginArea = function() {
			$('.loginAreaButton.loggedIn').click(function() {
				$(this).parent().toggleClass('active');
				$('.loginArea').slideToggle('slow');
			});
		};*/
		var mobileNavigationOverlay = function() {
			var $mobileNavCollapse = $('#mobileNavigation-collapse');
			$mobileNavCollapse.on('show.bs.collapse', function () {
				Helper.addOverlayStatus();
			});
			$mobileNavCollapse.on('hide.bs.collapse', function () {
				Helper.removeOverlayStatus();
			});
		};
		// privileged functions can see private variables/functions
		this.init = function(){
			mobileNavigationOverlay();
		};

	}

	window.Header = new Header();

})(jQuery);
