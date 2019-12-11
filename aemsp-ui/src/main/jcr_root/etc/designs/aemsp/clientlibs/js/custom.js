(function ($) {

	"use strict";

	var initFunctions = {

		initOnce: function(){
			var initArea = $("body");
			//$.hurl(); // manage uri hash
			/*** functions called on window resize and orientation change ***/
			var debouncedResizeFunction = Helper.debounce(function() {
				DeviceSelector.init();
				footerNavigation.init();
			}, 250);

			if(Modernizr.touch){
				$(window).on('orientationchange', debouncedResizeFunction);
			}
			else {
				$(window).on('resize', debouncedResizeFunction);
			}

			DeviceSelector.init();

			Header.init();
			Footer.init();
			footerNavigation.init();
			toTopButton.init();

			/*   content manipulation   */
			/****************************/

			initFunctions.init();

			$(window).trigger('ecx.initializationDone');
		},


		init: function(area){
			var initArea = (area) ? area : $("body");

			/*   missing browser features   */
			/********************************/
			// if($.fn.placeholder) {
			// 	initArea.find('input[type="text"]').placeholder();
			// }
			// initArea.find('select.selectboxit').selectBoxIt({
			// 	autoWidth: false,
			// 	showFirstOption: false
			// });

			/*   content manipulation   */
			/****************************/


			/*   responsive   */
			/******************/
			// Helper.makeTablesResponsive();
			// Helper.preventTelLinksNoTouch(initArea);

			/*   security   */
			/****************/
			//Helper.obfuscateMail(initArea);

			/*   user interface adaptions   */
			/********************************/
			//Helper.addSwipeToCarousel(initArea);
			/*   general appearance   */
			/**************************/
			GoBack.init();
			ActionBar.init();

			// var truncate = initArea.find('.truncate');
			// if(truncate.length){
			// 	truncate.dotdotdot();
			// }

			//AnimationHelper.init(initArea);
			Helper.openExternalLinksInNewWindow(initArea);

			/*   components/widgets   */
			/**************************/


			//FormHelper.init(initArea.find('form'));
			//$('[data-toggle="tooltip"]').tooltip();
			ListPage.init($('main'));
			SearchPage.init($('aside'));
			//ReCaptchaHelper.init();
		}

	};

	var InitHelper = {
		initialize: initFunctions.init
	};

	window.InitHelper = InitHelper;


	$(document).ready(function(){

		initFunctions.initOnce();
	});


})(jQuery);
