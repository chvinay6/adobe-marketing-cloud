(function(document, $) {
	$(document).on("dialog-ready", function() { //for dialogs
		addValidatorIfSet();
	});
	$(document).on("ready", function() { //for page properties in sites.html
		addValidatorIfSet();
	});
	function addValidatorIfSet() {
		var pathbrowser = $("[data-enable-regex-validation='true']");
		if (pathbrowser.length) {
			$.validator.register({
						selector : "[data-enable-regex-validation='true'] .js-coral-pathbrowser-input",
						validate : function(el) {
							var pathbrowser = $(el).parents("[data-enable-regex-validation='true']");
							if (!pathbrowser.length)
								return;
							var regexAttr = pathbrowser.attr("data-regex");
							if (!regexAttr) return;
							var regex = new RegExp(regexAttr);
							var value = pathbrowser.find(".js-coral-pathbrowser-input").val();

							if (regex.test(value)) {
								return;
							} else {
								var error = pathbrowser.attr("data-regextext");
								if(!error) error = "aemsp_validationDefaultError"
								return Granite.I18n.get(error);
							}
						} 
					});
		}
	}
})(document, Granite.$);