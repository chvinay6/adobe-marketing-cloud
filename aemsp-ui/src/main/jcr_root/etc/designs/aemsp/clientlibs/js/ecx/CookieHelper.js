(function ($) {

	"use strict";

	// Constructor
	function CookieHelper () {

		/* privileged functions can see private variables/functions */

		/*
		*  ### Cookie Helper ###
		*
		*  Vars:
		*  name                = [Default: null, required] Name of this cookie, must be an unique identifier (no cookie with same name).
		*  prefix              = [Default: null, required] Prefix of CSS IDs and classes, used in HTML for this cookie (like a namespace). Must be unique!
		*  setBodyClass        = [true or false, default: false, optional] If set, a class will be added to the <body>: (class="PREFIX-layer-active"). Useful for styling reasons.
		*  proofBrowserSupport = [true or false, default: false, optional] If set, users browser will be proofed with list of supported browsers. Cookie will only be used, if browser is NOT supported.
		*  expireAccepted      = [integer of days, default: 365 (one year), optional] Days for cookie to expire. Only used, if cookie is accepted by user (via button: id="#PREFIXLayerAcceptBtn")
		*
		*  Hints:
		*  - If no expire date is set, cookie becomes a session cookie (expires every session).
		*  - Closing buttons "x" do not have an expire date, so they are always session cookies.
		*
		*  Examples (initialize for every cookie):
		*  CookieHelper.init("uprBa", "browser", true, true);        (used as note if users browser is not supported)
		*  CookieHelper.init("uprCa", "cookie", false, false, 365);  (shows layer to accept cookies)
		*
		*/
		this.init = function(name, prefix, setBodyClass, proofBrowserSupport, expireAccepted){

			// name and prefix are required!
			if(name && prefix){

				// if set: is browser is supported?
				if(proofBrowserSupport){
					var isBrowserSupported = BrowserDetect.proofBrowserSupport();
				}

				// set cookie only, if no browser proofing is enabled or browser is not supported.
				if(!proofBrowserSupport || !isBrowserSupported){
					if(!expireAccepted){ expireAccepted = 365; }
					var cookieName = name; // Name of Cookie (example: "uprCa")
					var cookiePrefix = prefix; // Namespace of Cookie, used as prefix for IDs and Classes (examples: "cookie" or "browser")
					var acceptDaysToExpire = expireAccepted; // Set cookie live time (days, default: 365)
					var $cookieLayerContainerWrapper = $('#' + cookiePrefix + 'LayerContainerWrapper');
					var cookieValue = getCookieValue(cookieName);

					if ($cookieLayerContainerWrapper && cookieValue == 'not set') {
						if(setBodyClass){ // true or false
							var bodyClass = prefix + "-layer-active";
							$("body").addClass(bodyClass);
						}
						$cookieLayerContainerWrapper.fadeIn(); // display: none default
						bindClickElement('#' + cookiePrefix + 'LayerAcceptBtn', $cookieLayerContainerWrapper, bodyClass, {name: cookieName, value: 2, option: {expires: acceptDaysToExpire, path: '/'}});
						bindClickElement('#' + cookiePrefix + 'LayerDeclineBtn', $cookieLayerContainerWrapper, bodyClass, {name: cookieName, value: 1, option: {path: '/'}});
						bindClickElement('#' + cookiePrefix + 'LayerCloseBtn', $cookieLayerContainerWrapper, bodyClass, {name: cookieName, value: 1, option: {path: '/'}});
					}
				}
			}
			else {
				if(window.console) { console.warn("Please specify at least cookies name and prefix parameters! CookieHelper.previewInit(name, prefix)"); }
			}
		};

		var getCookieValue = function(thisCookieName){
			var cookieValue = $.cookie(thisCookieName);
	        if(!cookieValue) {
	            cookieValue = 'not set';
	        }
	        return cookieValue;
		};

		var bindClickElement = function(element, $cookieLayerContainerWrapper, bodyClass, cookie){
			var $element = $(element);
			if ($element) $element.unbind('click');
			if ($element) $element.bind('click',function(){
	            $.cookie(cookie['name'],cookie['value'],cookie['option']); // expire session
				if(bodyClass){
					$("body").removeClass(bodyClass);
				}
	            $cookieLayerContainerWrapper.fadeOut()
	        });
		};


		// for development only
		this.previewInit = function(name, prefix) {

			if(name && prefix) {
				var cookieName = name;
				var cookiePrefix = prefix;
				var $statuscookieLayer = $('#' + cookiePrefix + 'StatusCookieLayer');
				var $removecookieLayer = $('#' + cookiePrefix + 'RemoveCookieLayer');
				var $reloadcookieLayer = $('#' + cookiePrefix + 'ReloadCookieLayer');

				if ($statuscookieLayer) {
					$statuscookieLayer.html(getCookieValue(cookieName))
					window.setInterval(function () {
						$statuscookieLayer.html(getCookieValue(cookieName));
					}, 1000);

					if ($removecookieLayer) $removecookieLayer.click(function () {
						if (!$.cookie(cookieName)) {
							alert('cookie is not set');
						}
						$.removeCookie(cookieName);
						$statuscookieLayer.html(getCookieValue(cookieName));
					});
				}

				if ($reloadcookieLayer) $reloadcookieLayer.click(function () {
					location.reload();
				});
			}
			else {
				if(window.console) { console.warn("Please specify at least cookies name and prefix parameters! CookieHelper.previewInit(name, prefix)"); }
			}
		};


		// Detect browser name and version via userAgent
		var BrowserDetect = {
			init: function () {
				this.browser = this.searchString(this.dataBrowser) || "Other";
				this.version = this.searchVersion(navigator.userAgent) || this.searchVersion(navigator.appVersion) || "Unknown";
			},
			searchString: function (data) {
				for (var i = 0; i < data.length; i++) {
					var dataString = data[i].string;
					this.versionSearchString = data[i].subString;

					if (dataString.indexOf(data[i].subString) !== -1) {
						return data[i].identity;
					}
				}
			},
			searchVersion: function (dataString) {
				var index = dataString.indexOf(this.versionSearchString);
				if (index === -1) {
					return;
				}

				var rv = dataString.indexOf("rv:");
				if (this.versionSearchString === "Trident" && rv !== -1) {
					return parseFloat(dataString.substring(rv + 3));
				} else {
					return parseFloat(dataString.substring(index + this.versionSearchString.length + 1));
				}
			},
			proofBrowserSupport: function (){

				// if we can't determine browser or version...
				if(this.browser == "Other" || this.version == "Unknown"){
					if(window.console) { console.warn("Browser is not in list or browser version is unknown."); }
					return false; // ...we act like this is an unsupported browser
				}

				for (var s = 0; s < this.dataBrowserSupported.length; s++) {
					var supportedBrowser = this.dataBrowserSupported[s].browser;
					var supportedVersion = this.dataBrowserSupported[s].version;

					// search for a matching browser to check
					if (this.browser == supportedBrowser) {

						// Is this browser version supported?
						if (this.version < supportedVersion) {
							return false; // unsupported browser
						}
						else {
							return true; // supported browser
						}
					}
				}

				if(window.console) { console.warn("Supported browser: No match found."); }
				return false; // no match found, we act like this is an unsupported browser
			},

			// List of browsers
			dataBrowser: [
				{string: navigator.userAgent, subString: "Edge", identity: "MS Edge"},
				{string: navigator.userAgent, subString: "Chrome", identity: "Chrome"},
				{string: navigator.userAgent, subString: "MSIE", identity: "Explorer"},
				{string: navigator.userAgent, subString: "Trident", identity: "Explorer"},
				{string: navigator.userAgent, subString: "Firefox", identity: "Firefox"},
				{string: navigator.userAgent, subString: "Safari", identity: "Safari"},
				{string: navigator.userAgent, subString: "Opera", identity: "Opera"}
			],

			// List of supported browsers and its version. Every version below is not supported!
			dataBrowserSupported: [
				{browser: "MS Edge", version: 12},
				{browser: "Chrome", version: 46},
				{browser: "Explorer", version: 11},
				{browser: "Firefox", version: 42},
				{browser: "Safari", version: 9},
				{browser: "Opera", version: 33}
			]
		};

		BrowserDetect.init();

	}

	// create global instance of helper class
	window.CookieHelper = new CookieHelper();

})(jQuery);
