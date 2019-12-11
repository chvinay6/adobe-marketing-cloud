(function ($) {

	"use strict";

	// Constructor
	function FancyboxHelper (parameter) {

		// private
		var addParamToUrl = function(base, key, value) {
			var sep = (base.indexOf('?') > -1) ? '&' : '?';
			return base + sep + key + '=' + value;
		};


		var setClassForYoutubeLinks = function(parentElement) {
			if ($("html").hasClass('supports-no-touch')) {
				var youtubelinks = parentElement.find("a[href*='//www.youtube']:not([href*='"+window.location.hostname+"']:not(#socialLinks *), a[href*='http://www.youtube']:not([href*='"+window.location.hostname+"']):not(#socialLinks *), a[href*='https://www.youtube']:not([href*='"+window.location.hostname+"']):not(#socialLinks *)");

				youtubelinks.each(function(index, el) {
					var youtubelink = $(this);

					// Prevent (Win7+IE11) from opening link href (if JS is concatenated and uglified)
					youtubelink.on("click", function(e){
						e.preventDefault();
					});

					// Add classes and video data 'youtube' to link
					youtubelink.addClass("fancybox fancybox.iframe youtube").attr("data-fancybox-video","youtube");

					var youtubelinkhref = youtubelink.attr("href");

					if(youtubelinkhref.indexOf("wmode=") == -1){
						youtubelink.attr("href", addParamToUrl(youtubelinkhref, 'wmode', 'transparent'));
					}
					// see more: https://developers.google.com/youtube/player_parameters?hl=en#origin
					if(youtubelinkhref.indexOf("origin=") == -1){
						youtubelink.attr("href", addParamToUrl(youtubelinkhref, 'origin', location.protocol+'//'+window.location.hostname+'/'));
					}
					// FALLBACK: If no aspect ratio param is set: detect aspect ratio
					if(!$(this).attr("data-video-aspect-ratio") || $(this).attr("data-video-aspect-ratio") == ""){

						// Get video dimensions using noembed
						var locationProtocol = location.protocol;
						//var apiEndpoint = locationProtocol + "//www.youtube.com/oembed"; // Youtube does not support JSONP!
						var apiEndpoint = locationProtocol + "//noembed.com/embed";
						var youtubeVideoCode =  $(this).attr("data-video-code");

						// Build AJAX oembed URL
						var oembedURL = apiEndpoint + '?url=' + encodeURIComponent(locationProtocol + "//www.youtube.com/watch?v=" + youtubeVideoCode);

						// Get JSON data
						$.getJSON(oembedURL, function(data){
							var aspectRatioNumber = data.width / data.height;
							if(aspectRatioNumber.toFixed(2) == 1.34){
								var aspectRatioClass = "responsive-4by3";
							}
							else { // Default: 16:9 (aspectRatioNumber.toFixed(2) == 1.78)
								var aspectRatioClass = "responsive-16by9";
							}
							youtubelink.attr("data-video-aspect-ratio", aspectRatioClass);
						});
					}

				});
			}
		};


		/* Currently not in use
		var setClassForLocalVideoLinks = function(parentElement) {
			if ($("html").hasClass('supports-no-touch')) {
				parentElement.find("a[data-video]").addClass("fancybox fancybox.localvideo");
			}
		};
		*/


		var handleGoogleTagmanager = function(status,layertype,url) {
			// is defined about google tagmanager snippet as first child of body tag
			// var dataLayer = dataLayer || [];
			if (window.dataLayer) {
				window.dataLayer.push({'overlayContent': url});
				if(status == "show") {
					window.dataLayer.push({'overlayEvent': layertype+'-'+status});
				}
				if(status == "close") {
					window.dataLayer.push({'overlayEvent': layertype+'-'+status});
				}
				// push event after status update
				window.dataLayer.push({'event': 'overlay'});
				//console.log(window.dataLayer);
			}
		};


		var handleStickyNavigation = function(status) {

			if(status == "openFancybox"){
				$('#permanentHeader').addClass("is-sticky-fancybox"); // While Fancybox is open, navigation is set to sticky
				$('body').addClass("has-overlay");
			}

			if(status == "closeFancybox"){
				$('#permanentHeader').removeClass("is-sticky-fancybox"); // Reset navigation
				$('body').removeClass("has-overlay");
			}
		};


		var handleParentCarousel = function(openerElement, action) {
			var parentCarousel = openerElement.parents(".carousel");

			if(parentCarousel.length){ // Is this part of a carousel?

				if(action == "pause"){
					parentCarousel.carousel('pause');
				}

				if(action == "play"){
					parentCarousel.carousel('cycle');
				}
			}
		};


		// Set Fancybox min-height to avoid iframe collision with navigation
		var iFrameFancyboxMinHeight = function(){
			if($(".fancybox-type-iframe").length) {
				var fancyboxSkin = $(".fancybox-type-iframe").find(".fancybox-skin");
				var fancyboxMinHeight = fancyboxSkin.children(".fancybox-outer").outerHeight(true) + fancyboxSkin.children(".fancybox-title").outerHeight(true) + fancyboxSkin.children(".fancybox-close").outerHeight(true) + parseInt(fancyboxSkin.children(".fancybox-close").css("top"));
				fancyboxSkin.css("min-height", fancyboxMinHeight);
			}
		};


		var initIframeFancybox = function(parentElement){
			var marginTop = 0;
			if ($("body").hasClass("browser-layer-active")) marginTop = marginTop + $("#browserLayerContainerWrapper").height();
			parentElement.find('.fancybox\\.iframe').unbind("click").fancybox({
				padding : 0,
				margin: [marginTop, 0, 0, 0],
				wrapCSS : "ecx",
				autoSize: true,
				autoCenter: true,
				helpers : {
					title: {
						type: 'inside' // float (default), inside, outside, over
					}
				},
				beforeLoad : function() {
					this.wrapCSS  += ' ' + this.element.data('video-aspect-ratio') + ' ' + this.element.data('fancybox-video');
					this.title = "<h3>" + this.element.data('video-headline') + "</h3><p>" + this.element.data('video-subline') + "</p>";
				},
				afterLoad : function(){
					$.extend(this, {
						type    : 'iframe',
						width   : '100%',
						height  : '100%',
						iframe: {
			                preload: false // fixes issue with iframe and IE
			            }
					});
				},
				beforeShow : function() {
					handleStickyNavigation("openFancybox");
					handleParentCarousel(this.element, "pause");
					handleGoogleTagmanager("show", "video", this.href);

					// Close fancybox (on click at background/surrounding area)
					// Onside the video-fancybox the overlay-layer is not reachable/clickable, so we need this as an alternative
					$(".fancybox-skin").add(".fancybox-wrap").on("click", function(){
						$.fancybox.close(true); // close fancybox
					});
				},
				beforeClose : function() {
					handleStickyNavigation("closeFancybox");
					handleParentCarousel(this.element, "play");
					handleGoogleTagmanager("close", "video", this.href);
				},
				onUpdate : function() {
					if (DeviceSelector.getDeviceType() == "xs") {
						$.fancybox.close(true); // close fancybox
					}
					else {
						iFrameFancyboxMinHeight();
					}
				}
			});
		};


		var initImageFancybox = function(parentElement){
			var marginTop = 120;

			if ($("body").hasClass("browser-layer-active")) marginTop = marginTop + $("#browserLayerContainerWrapper").height();
			parentElement.find('.fancybox:not(.fancybox\\.iframe, .fancybox\\.localvideo)').unbind("click").fancybox({
				wrapCSS: "ecx",
				margin: [marginTop, 40, 20, 40],
				padding: 0,
				helpers: {
					title: {
						type: 'inside' // float (default), inside, outside, over
					}
				},
				beforeLoad: function () {
					this.title = "<h3>" + this.element.data('image-headline') + "</h3><p>" + this.element.data('image-subline') + "</p>";
				},
				beforeShow: function () {
					handleStickyNavigation("openFancybox");
					handleGoogleTagmanager("show" ,"image", this.href);

				},
				beforeClose: function () {
					handleStickyNavigation("closeFancybox");
					handleGoogleTagmanager("close" ,"image", this.href);
				},
				afterShow: function () {
					var fancyboxObject = this;
					Helper.addSwipeToFancybox(fancyboxObject.inner);
				},
				onUpdate: function () {
					if (DeviceSelector.getDeviceType() == "xs") {
						$.fancybox.close(true); // close fancybox
					}
				}
			});
		};


		// proof breakpoint and toggle between fancybox and xs-image-zoom functions
		// (called on resize (desktop) and orientation change (mobile): custom.js)
		this.changeImageFancyBoxType = function(){
			var imageFancyboxItems = $(document).find('.fancybox:not(.fancybox\\.iframe, .fancybox\\.localvideo)');

			// desktop/tablet views (lg, md, sm)
			if(DeviceSelector.getDeviceType() != "xs") {
				var body = $('body');
				//var openedZoomItemXS = imageFancyboxItems.filter(".xs-zoom-open");

				//openedZoomItemXS.children("picture").show(); // reset display status
				ImageVideo.closeImageZoomXS(imageFancyboxItems.filter(".xs-zoom-open")); // close opened xs images

				imageFancyboxItems.off("click"); // remove xs-image-zoom click handler
				body.removeClass("xs-image-zoom-enabled"); // remove global class for xs-image-zoom
				initImageFancybox(body); // add fancybox click handler again
			}

			// mobile view (xs)
			else {
				$(document).unbind("click.fb-start"); // remove fancybox click handler
				ImageVideo.imageZoomXS(imageFancyboxItems); // enable XS image zoom
			}
		};


		/* currently not in use
		var initVideoFancybox = function(parentElement){

			parentElement.find('.fancybox\\.localvideo').unbind("click").fancybox({
				padding : 0,
				margin: 0,
				wrapCSS : "ecx",
				autoCenter: true,
				autoSize: false,
				afterLoad : function(currentFancybox, upcomingFancybox){
					$.extend(this, {
						aspectRatio : false,
						type    : 'html',
						width   : '100%',
						height  : '100%',
						content : '<div class="fancybox-image-wrapper"><div class="fancybox-image"><div class="flowplayer"></div></div></div>'
					});
				},
				afterShow : function(){
					var fancyboxObject = this;
					var trigger = fancyboxObject.element;
					var config = trigger.data("video");

					fancyboxObject.inner.find("div.flowplayer").flowplayer({
						playlist: config.playlist,
						ratio: 1/2,
						swf: "static/javascripts/flowplayer.swf",
						script: "static/javascripts/embed.min.js"
					});
				}
			});
		};
		*/

		// privileged functions can see private variables/functions
		this.init = function(element){
			var parentElement = (element) ? element : $("body");

			setClassForYoutubeLinks(parentElement);
			//setClassForLocalVideoLinks(parentElement);

			initIframeFancybox(parentElement);
			// initVideoFancybox(parentElement);
			initImageFancybox(parentElement);

			this.changeImageFancyBoxType();
		};
	}

	// create global instance of helper class
	window.FancyboxHelper = new FancyboxHelper();


})(jQuery);
