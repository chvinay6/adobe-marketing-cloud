(function ($) {

	"use strict";

	function Helper() {

		var rand = function() {
			return Math.random().toString(36).substr(2);
		};
		/*
			element: $()
			countFromNumber: n
			countToNumber: n
			durationNumber: n (ms)
		*/
		this.countToNumber = function(element,countFromNumber,countToNumber,durationNumber) {
			element.addClass("counting").removeClass("counted");
			$({countNumber: countFromNumber}).animate({countNumber: countToNumber+1}, {
				duration: durationNumber,
				easing:'linear',
				step: function() {
					element.text(Math.floor(this.countNumber));
				},
				complete: function() {
					element.addClass("counted").removeClass("counting");
				}
			});
		};

		var getRandomToken = function(){
			return rand() + rand();
		};

		this.getRandomToken = function(){
			return getRandomToken();
		};

		this.getDefaultLanguage = function() {
			return 'en';
		};

		this.getLanguage = function() {
			return $("html").attr("lang");
		};

		// privileged functions can see private variables/functions
		this.addSwipeToCarousel = function(element) {
			var parentElement = (element) ? element : $("body");

			if(!$("html").hasClass('supports-no-touch')){
				parentElement.find(".carousel").swiperight(function() {
					$(this).carousel('prev');
				}).swipeleft(function() {
					$(this).carousel('next');
				});
			}
		};

		this.addSwipeToFancybox = function(element) {
			var parentElement = (element) ? element : $("body");

			if(!$("html").hasClass('supports-no-touch')){
				parentElement.find(".fancybox-image").swiperight(function() {
					$.fancybox.prev();
				}).swipeleft(function() {
					$.fancybox.next();
				});
			}
		};

		this.debounce = function(func, wait, immediate) {
			var timeout;
			return function() {
				var context = this, args = arguments;
				var later = function() {
					timeout = null;
					if (!immediate) func.apply(context, args);
				};
				var callNow = immediate && !timeout;
				clearTimeout(timeout);
				timeout = setTimeout(later, wait);
				if (callNow) func.apply(context, args);
			};
		};

		this.makeTablesResponsive = function(element) {
			var parentElement = (element) ? element : $("body");
			parentElement.find("table").each(function(index, el) {
				var table = $(this);
				if(!table.parent().hasClass('table-responsive')){
					table.wrap("<div class='table-responsive'></div>");
				}
			});
		};

		this.preventTelLinksNoTouch = function(element) {
			var parentElement = (element) ? element : $("body");
			if($("html").hasClass('supports-no-touch')){
				parentElement.find("a[href*='tel:']").addClass("no-link").click(function(e) {
					e.preventDefault();
					return false;
				});
			}
		};

		this.getUrlParam = function( nameVal ) {
			var name = nameVal.replace(/[\[]/,"\\\[").replace(/[\]]/,"\\\]");

			var regexS = "[\\?&]"+name+"=([^&#]*)";
			var regex = new RegExp( regexS );
			var results = regex.exec( window.location.href );

			if ( results == null )
				return "";
			else
				return results[1];
		};

		this.setClassIfIE = function() {
			if (/MSIE (\d+\.\d+);/.test(navigator.userAgent)){ //test for MSIE x.x;
				$("html").addClass('msie');
			}
		};

		this.setClassIfSafari = function() {
			if ( navigator.userAgent.indexOf('Safari') != -1 && navigator.userAgent.indexOf('Chrome') == -1 ){ //test for Safari x.x;
				$("html").addClass('safari');
			}
		};

		this.obfuscateMail = function(element) {
	    var parentElement = (element) ? element : $("body");
			var obfuscatedMailAddress;
	    var unobfuscatedMailAddress;
			var unobfuscatedMailExtend;
			var mailtoLink;
			var mailtoSplit = '?';

			parentElement.find("a.mailobfuscated").each(function(index, element){
				obfuscatedMailAddress = $(this).attr("href").substr(7).split(mailtoSplit);
				var linkTextEqualsMailadress = (obfuscatedMailAddress[0] == $(this).text());
				unobfuscatedMailAddress = obfuscatedMailAddress[0].replace(/[a-zA-Z]/g, function(c){return String.fromCharCode((c<="Z"?90:122)>=(c=c.charCodeAt(0)+13)?c:c-26);});
				mailtoLink = unobfuscatedMailAddress;
				//console.log([obfuscatedMailAddress[0],unobfuscatedMailAddress]);
				// if subject / body etc.
				if (obfuscatedMailAddress[1]) {
					unobfuscatedMailExtend = obfuscatedMailAddress[1].replace(/[a-zA-Z]/g, function(c){return String.fromCharCode((c<="Z"?90:122)>=(c=c.charCodeAt(0)+13)?c:c-26);});
					mailtoLink = mailtoLink + mailtoSplit + unobfuscatedMailExtend;
					//console.log([obfuscatedMailAddress[1],unobfuscatedMailExtend]);
				}
				$(this).attr("href", "mailto:"+mailtoLink);
				if(linkTextEqualsMailadress){
					$(this).text(unobfuscatedMailAddress);
				}
				$(this).removeClass("mailobfuscated"); //important because of possible multiple initialization
			});
		};

		this.openExternalLinksInNewWindow = function(element) {
			var parentElement = (element) ? element : $("body");
			var currentPageDomain = document.location.hostname;
			var externalLinks = {};
			var explicitExternalLinks = {};
			externalLinks = parentElement.find('a[href^="http://"], a[href^="https://"]').not('a[href*="'+currentPageDomain+'"]');
			externalLinks.addClass("breakout")
			explicitExternalLinks = parentElement.find('a.breakout');
			$.extend(externalLinks,explicitExternalLinks);
			// set param to open external links in new window and add hint as data value
			externalLinks.attr('target','_blank');
		};

		this.addOverlayStatus = function() {
			var $container = $('.container');
			var $body = $('body');
			var containerWidth = $container.outerWidth();
			var bodyWidth = $body.outerWidth();
			$container.css('max-width',containerWidth);
			$body.addClass('hasOverlay');
			var bodyOverlayWidth = $body.outerWidth();
			var bodyWidthDiff = Math.floor((bodyWidth - bodyOverlayWidth) / 2);
			$body.css('margin-left',bodyWidthDiff);
		};

		this.removeOverlayStatus = function() {
			var $body = $('body');
			var $container = $('.container');
			$body.removeClass('hasOverlay').css('margin-left','');
			$container.css('max-width','');
		};

		/*** loads fonts via google web font loader and executes style functions after loading of all fonts ***/
		var loadFonts = function(){
			var activeCallback = $.Callbacks();

			window.WebFontConfig = {
				google: { families: [webFonts] },
				active: function () { activeCallback.fire(); }
			};
			$(function() {
				// listen to event when all fonts area loaded and execute functions
				activeCallback.add(function (){
					window.StyleHelper.matchTeaserHeight($('body'));
					window.StyleHelper.styleMasonryLayouts($('body'));
				});
			});

			(function() {
				var wf = document.createElement('script');
				wf.src = ('https:' == document.location.protocol ? 'https' : 'http') + '://ajax.googleapis.com/ajax/libs/webfont/1/webfont.js';
				wf.type = 'text/javascript';
				wf.async = 'true';
				var s = document.getElementsByTagName('script')[0];
				s.parentNode.insertBefore(wf, s);
			})();
		};
		loadFonts();

	}

	window.Helper = new Helper();

})(jQuery);
