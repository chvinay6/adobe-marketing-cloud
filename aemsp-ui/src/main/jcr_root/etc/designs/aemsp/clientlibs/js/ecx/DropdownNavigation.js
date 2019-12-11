(function ($) {

	"use strict"; 

	// Constructor
	function DropdownNavigation (parameter) {

		// private
		var init = function(element){
			var slideOutToggles;
			if((DeviceSelector.getDeviceType() == "md") && (element.is("#mainNavigation"))) {
				element.find("li.slideout > .slideout-toggle, .slideout-menu li.slideout > a").click(function(event) {
					var parent = $(this).parent();
					var currentSlideoutIsOpened = parent.hasClass('open');

					event.preventDefault();

					if(!currentSlideoutIsOpened){
						if($(this).is('a')){
							element.find('.active').removeClass('active');
							parent.addClass('active');
						}else{
							element.find('.open').removeClass('open');
							parent.addClass('open');
						}
					}else{
						element.find('.open').removeClass('open');
					}
					return false;
				});
			}else if(element.is("#subNavigationContainer")){
				slideOutToggles = element.find('.slideout>a>.icon');
				slideOutToggles.click(function(event) {
					var toogleButton = $(this);

					event.preventDefault();

					var parentContainer = toogleButton.closest(".slideout");
					var slideoutTarget = (toogleButton.data("target")) ? $(toogleButton.data("target")) : toogleButton.next(".slideout-menu");

					element.find('.open').not(parentContainer).removeClass('open');
					parentContainer.toggleClass('open');
				});
			}else{
				slideOutToggles = element.find('[data-toggle="slideout"]');
				slideOutToggles.click(function(event) {
					var toogleButton = $(this);

					var parentContainer = toogleButton.closest(".slideout");
					var slideoutTarget = (toogleButton.data("target")) ? $(toogleButton.data("target")) : toogleButton.next(".slideout-menu");

					parentContainer.toggleClass('open');
				});
			}
		};

		// privileged functions can see private variables/functions
		this.init = function(element){
			if(element.is("*")) {
				init(element);
			}
		};

		// public variables/functions
		this.myPublicvariable = "this is public";
	}

	// create global instance of helper class
	window.DropdownNavigation = new DropdownNavigation();


})(jQuery);