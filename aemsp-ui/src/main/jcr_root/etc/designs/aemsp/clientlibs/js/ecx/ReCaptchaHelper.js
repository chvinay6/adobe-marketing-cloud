(function ($) {

	"use strict";

	// Constructor
	function ReCaptchaHelper () {

		var reCaptchaLang = 'en';
		var reCaptchaSitekey = '6LeJxRoTAAAAADPS6ICVYqwIxoDEjhVLAzIAyjY1'; // 6LeJxRoTAAAAADPS6ICVYqwIxoDEjhVLAzIAyjY1 for localhost
		var reCaptchaRequestUrl = 'recaptcha.html'; // recaptcha.html for serve/ruby
		var reCaptchaSitepath = null // aem project path
		var reCaptchaGroupClass = '.recaptcha-group';
		var reCaptchaIncludeClass = '.recaptcha-include';
		var reCaptchaErrorClass = '.recaptcha-error';
		var reCaptchaResponseClass = '.recaptcha-response';
		var reCaptchaLoadCounter = 10;

		this.init = function() {
			// module
			var $reCaptchaGroups = $('body').find(reCaptchaGroupClass);
			if ($reCaptchaGroups) {
				var reCaptchaGroupId,reCaptchaIncludeId,reCaptchaErrorId, reCaptchaResponseId;
				var $reCaptchaGroup, $reCaptchaInclude, $reCaptchaError, $reCaptchaResponse;

				$reCaptchaGroups.each( function() {

					$reCaptchaGroup = $(this);
					$reCaptchaInclude = $reCaptchaGroup.find(reCaptchaIncludeClass);
					$reCaptchaError = $reCaptchaGroup.find(reCaptchaErrorClass);
					$reCaptchaResponse = $reCaptchaGroup.find(reCaptchaResponseClass);
					reCaptchaGroupId = $reCaptchaGroup.attr('id');
					reCaptchaIncludeId = $reCaptchaInclude.attr('id');
					reCaptchaErrorId = $reCaptchaError.attr('id');
					reCaptchaResponseId = $reCaptchaResponse.attr('id');
					reCaptchaSitekey = $reCaptchaInclude.data('sitekey') || reCaptchaSitekey;
					reCaptchaRequestUrl = $reCaptchaInclude.data('response') || reCaptchaRequestUrl;
					reCaptchaLang = $reCaptchaInclude.data('lang') || reCaptchaLang;
					reCaptchaSitepath = $reCaptchaInclude.data('sitepath');

					$.getScript("https://www.google.com/recaptcha/api.js?onload="+reCaptchaGroupId+"&render=explicit&hl="+reCaptchaLang)
					.done(function() {
						var runOnloadLogic = function() {
							if(reCaptchaLoadCounter == 0) { // stop recursion if reCaptchaLoadCounter is counted down
								printApiFail();
								return false;
							}else{
								reCaptchaLoadCounter--;
								var timer = null;
								if (timer) {
									clearTimeout(timer); //cancel the previous timer.
									timer = null;
								}
								timer = setTimeout( function() {
									if (typeof grecaptcha != 'undefined') {
										var onloadFunction = onloadCallback(reCaptchaIncludeId,reCaptchaSitekey,reCaptchaResponseId,reCaptchaErrorId,reCaptchaSitepath);
										eval("var "+reCaptchaGroupId+" = onloadFunction");
									}else{
										console.warn("grecaptcha: retrying to load captcha")
										runOnloadLogic();
									}
								},500);
							}
						}
						// recursion
						runOnloadLogic();
					})
					.fail(function() {
						printApiFail();
					});
					return false; // only one recaptcha per site is allow (only one callback is allowed)
				});
			}
		};
    var onloadCallback = function(reCaptchaIncludeId,reCaptchaSitekey,reCaptchaResponseId,reCaptchaErrorId,reCaptchaSitepath) {
			//console.log(reCaptchaIncludeId,reCaptchaSitekey,reCaptchaResponseId,reCaptchaErrorId);
      grecaptcha.render(reCaptchaIncludeId, {
				'log' : console.info('grecaptcha: captcha request complete'),
        'sitekey' : reCaptchaSitekey,
        'callback' : function(response) {
          $.get(reCaptchaRequestUrl, {
						'sitepath' : reCaptchaSitepath,
            response: response,
						log: console.info('grecaptcha: captcha done')
          },
            function (data) {
              reCaptchaValidate(data,$('#'+reCaptchaResponseId),$('#'+reCaptchaErrorId));
            }
          );
        }
      });
    };
    var reCaptchaValidate = function(data,$reCaptchaResponseField,$reCaptchaErrorField) {
      $reCaptchaErrorField.html(""); // reset
      (data == "true") ? $reCaptchaResponseField.val("true") : $reCaptchaResponseField.val("");
    };
		var printApiFail = function() {
			console.warn("grecaptcha: captcha request failed");
		}
  }

	// create global instance of helper class
	window.ReCaptchaHelper = new ReCaptchaHelper();


})(jQuery);
