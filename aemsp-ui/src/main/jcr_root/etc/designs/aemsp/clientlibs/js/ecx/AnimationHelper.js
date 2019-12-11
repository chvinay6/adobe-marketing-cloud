(function ($) {

	"use strict";

	function AnimationHelper() {

		// private
		var animateAnimationCountUp = function(element) {
			var parentElement = (element) ? element : $("body");

			var options = {
				useEasing : true,
				useGrouping : true,
				separator : '',
				decimal : ',',
				prefix : '',
				suffix : ''
			};

			/*** create animation objects and push it to array ***/
			var animationObjects = [];
			parentElement.find(".animationCountUp [data-animation-countup-start]").each(function(index, el) {
				var elementToAnimate = $(this);
				animationObjects.push(new CountUp(elementToAnimate[0], elementToAnimate.data("animation-countup-start"), elementToAnimate.data("animation-countup-end"), 0, 5, options));
			});

			var handleAllAnimations = function(action){
				$.each(animationObjects, function(index, val) {
					switch(action){
						case "start":
							val.start();
							break;
						case "pause":
							val.pause();
							break;
						case "resume":
							val.resume();
							break;
					}
				});
			};

			$(".animationCountUp").one('enterviewport', function(e){
				handleAllAnimations("start");
				// bind resume function later to not trigger it on first viewport entering
				$(this).bind('enterviewport', function(){
					handleAllAnimations("resume");
				});
			}).bind('leaveviewport', function(){
				handleAllAnimations("pause");
			}).bullseye();
		};

		var animateStickyHeader = function(element, marker){
			var currentBreakpoint = DeviceSelector.getDeviceType();
			var willBeSticky = element.hasClass('will-be-sticky');

			if(!willBeSticky && ((currentBreakpoint == "md") || (currentBreakpoint == "lg")) && !$("body").hasClass('pageEditor')){
				stickElementDependingToMarker(element, marker);
			}
			else if(willBeSticky && ((currentBreakpoint == "xs") || (currentBreakpoint == "sm"))) {
				unstickElementAndMarker(element, marker);
			}
		};

		var bindSlideToggleTrigger = function(elements){

			elements.each(function(index, el) {
				var trigger = $(this);
				var allowedBreakpoints = ["xs", "sm", "md", "lg"];
				var targetSelector = trigger.data("slidetoggletarget");
				var target = trigger;

				// find target
				if(trigger.data("slidetoggleselectorconfig")){
					switch (trigger.data("slidetoggleselectorconfig")){
						case "element":
							target = trigger.find(targetSelector);
							break;
					}
				}
				else {
					target = $(targetSelector);
				}

				// set allowed breakpoints
				if(trigger.data("slidetogglebreakpoints")){
					allowedBreakpoints = trigger.data("slidetogglebreakpoints").split(",");
				}

				// hint layout change to browser
				if(!Modernizr.touch){
					trigger.hover(function() {
						target.css('will-change', 'height');
					}, function() {
						target.css('will-change', '');
					});
				}

				trigger.on('click', function(event) {

					// check if slide should get triggered in current breakpoint and end if not
					var currentBreakpoint = DeviceSelector.getDeviceType();
					if($.inArray(currentBreakpoint, allowedBreakpoints) == -1){
						return false;
					}

					var similiarTriggers = [];
					if(trigger.data("slidetoggle-affectsimiliartriggers") != null){
						similiarTriggers = elements.filter('[data-slidetoggletarget="'+targetSelector+'"]');
					}
					if(trigger.data("slidetoggletargettrigger") != null) {
						var targetTrigger = $(trigger.data("slidetoggletargettrigger"));
					}
					if(target.hasClass('open')){
						if(trigger.data("slidetoggleoverlay") != null){
							$("body").removeClass('has-overlay');
						}

						target.velocity("slideUp", {
							ease: "ease-in-out",
							duration: 700,
							complete: function(){
								target.removeClass('open');
								if (targetTrigger.length > 0) targetTrigger.click();
								// should be a callback param
								if (targetSelector == "#mainNavigationMobile") {
									setTimeout(function() {
										$(targetSelector).find(">ul").css({"overflow-y":"hidden","overflow-x":"hidden"});
									},900);
								}
							}
						});

						if(trigger.data("slidetoggle-affectsimiliartriggers") != null){
							similiarTriggers.removeClass("trigger-open");
						}
						else {
							trigger.removeClass("trigger-open");
						}
					}
					else {
						if(trigger.data("slidetoggleoverlay") != null){
							$("body").addClass('has-overlay');
						}

						target.velocity("slideDown", {
							ease: "ease-in-out",
							duration: 700,
							complete: function(){
								target.addClass('open');
								// should be a callback param
								if (targetSelector == "#mainNavigationMobile") {
									setTimeout(function() {
										$(targetSelector).find(">ul").css({"overflow-y":"auto","overflow-x":"hidden"});
									},900);
								}
							}
						});

						if(trigger.data("slidetoggle-affectsimiliartriggers") != null){
							similiarTriggers.addClass("trigger-open");
						}
						else {
							trigger.addClass("trigger-open");
						}
					}
				});

			});
		};

		var bindSlideDepartmentNavigationToggleTrigger = function(element){
			var trigger = element;
			var target = trigger.closest("ul");

			trigger.on('click', function(event) {
				if(DeviceSelector.getDeviceType() == "xs"){
					event.preventDefault();

					if(target.hasClass('open')){
						target.velocity({ height: 44}, {
							ease: "ease-in-out",
							duration: 700,
							complete: function(){
								target.removeClass('open');
							}
						});
						trigger.removeClass("trigger-open");
					}
					else {
						target.velocity({ height: 195}, {
							ease: "ease-in-out",
							duration: 700,
							complete: function(){
								target.addClass('open');
							}
						});
						trigger.addClass("trigger-open");
					}
				}
			});
		};

		var bindToolboxToggle = function(elements){
			elements.each(function(index, el) {
				var trigger = $(this);

				trigger.on('click', function(event) {
					var target = trigger.parent();
					if(target.hasClass('open')){
						target.velocity({ translateX: "0"}, {
							ease: "ease-in-out",
							duration: 700,
							complete: function(){
								target.removeClass('open');
							}
						});
					}
					else {

						$("#toolbox>div").each(function() {
							target.velocity({ translateX: "0"}, {
								ease: "ease-in-out",
								duration: 0,
								complete: function(){
									target.removeClass('open');
								}
							});
						});

						target.velocity({ translateX: "-350px"}, {
							ease: "ease-in-out",
							duration: 0,
							complete: function(){
								target.addClass('open');
							}
						});
					}
				});
			});
		};

		var resetDepartmentNavigation = function(element){
			var currentBreakpoint = DeviceSelector.getDeviceType();

			if(element.is("*") && (currentBreakpoint != "xs")){
				element.removeClass('open').css("height", "");
				element.find('.trigger-open').removeClass('trigger-open');
			}
		};

		var resetList = function(element){
			var currentBreakpoint = DeviceSelector.getDeviceType();

			if(element.is("*") && (currentBreakpoint != "xs")){
				element.find("ul").removeClass('open').css("display", "");
				element.find('.trigger-open').removeClass('trigger-open');
			}
		};

		var stickElementDependingToMarker = function(element, marker){
			marker.bind('enterviewport', function(e){
				element.removeClass('is-sticky');
				marker.removeClass('sticky-triggered');
			}).bind('leaveviewport', function(){
				element.addClass('is-sticky');
				marker.addClass('sticky-triggered');
			}).bullseye();
			//{offsetTop:"2"}
			element.addClass('will-be-sticky');
		};

		var unstickElementAndMarker = function(element, marker){
			marker.unbind('enterviewport').unbind('leaveviewport').removeClass('is-sticky');
			marker.removeClass('sticky-triggered');
			element.removeClass('is-sticky');
			element.removeClass('will-be-sticky');
		};


		// privileged functions can see private variables/functions
		this.init = function(element){
			var parentElement = (element) ? element : $("body");

			// remove preload class to activate CSS transitions
			parentElement.removeClass('preload');

			//init animations for all visible elements
			animateAnimationCountUp(parentElement);
			animateStickyHeader(parentElement.find("#permanentHeader"), parentElement.find("#collapseableHeader"));
			// set sticky if onload not at top of the page
			if ($("body").scrollTop() > 0) {
				var currentBreakpoint = DeviceSelector.getDeviceType();
				if (currentBreakpoint != "xs" && currentBreakpoint != "sm") $("#permanentHeader").addClass("is-sticky");
				$("#collapseableHeader").addClass("sticky-triggered");
			}
			bindSlideToggleTrigger(parentElement.find(".slideToggleTrigger"));
			bindToolboxToggle(parentElement.find("span.toolboxIcon, span.closeIcon"));
			bindSlideDepartmentNavigationToggleTrigger(parentElement.find("#departmentNavigation > ul > li > ul > li.active > a"));
		};

		this.updateBreakpointSpecificAnimations = function(element){
			var parentElement = (element) ? element : $("body");

			animateStickyHeader(parentElement.find("#permanentHeader"), parentElement.find("#collapseableHeader"));
			// set sticky if onload not at top of the page
			if ($("body").scrollTop() > 0) {
				var currentBreakpoint = DeviceSelector.getDeviceType();
				if (currentBreakpoint != "xs" && currentBreakpoint != "sm") $("#permanentHeader").addClass("is-sticky");
				$("#collapseableHeader").addClass("sticky-triggered");
			}
			resetList(parentElement.find("#globalGateway"));
			resetList(parentElement.find("#sitemap"));
			resetDepartmentNavigation(parentElement.find("#departmentNavigation > ul > li > ul"));
		};
	}

	window.AnimationHelper = new AnimationHelper();

})(jQuery);
