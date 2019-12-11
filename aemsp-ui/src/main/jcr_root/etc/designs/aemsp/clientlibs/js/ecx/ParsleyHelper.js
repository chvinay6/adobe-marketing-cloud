(function ($) {

	"use strict"; 

	// Constructor
	function ParsleyHelper (parameter) {

		// private functions
		var updateSummaryErrors = function(success, fieldInstance){
			var field = fieldInstance.$element;
			var fieldWrapper = field.closest('div.form-group');
			var fieldLabelElement = fieldWrapper.children("label, span.label").first();
			var fieldLabel = fieldLabelElement.text() || "";
			var summaryErrors = fieldWrapper.closest('form, div[role="form"]').children(".summary-errors");

			// fill error summary
			var arrErrorMsg = ParsleyUI.getErrorsMessages(fieldInstance);
			var errorMsg = arrErrorMsg.join(', ');
			var existingErrorEntry = summaryErrors.children('li[data-fieldname="'+field.attr("name")+'"]');

			if(existingErrorEntry.is("*")){
				if(success){
					existingErrorEntry.remove();
				}
				else {
					existingErrorEntry.children('.field-error').text(errorMsg);
				}
			}
			else {
				if(!success){
					summaryErrors.append('<li data-fieldname="'+field.attr("name")+'"><span class="field-name">'+fieldLabel+'</span><span class="field-error">'+errorMsg+'</span></li>');
				}
			}
			
			var showSummaryErrors = (summaryErrors.first().children('li').length > 0);
			if(showSummaryErrors){
				summaryErrors.show();
			}
			else {
				summaryErrors.hide();
			}
		};

		var subscribeToParsleyFunctions = function(form){
			if(form.is("*")){

				// set general class on form if validation was successfull/failed
				form.parsley().subscribe('parsley:form:validated', function(formInstance){
					var form = formInstance.$element;

					if (form.parsley().isValid()){
						form.addClass('parsley-valid').removeClass('parsley-invalid');
					}
					else{
						form.addClass('parsley-invalid').removeClass('parsley-valid');
					}
				});

				// set error class on wrapper
				form.parsley().subscribe('parsley:field:error', function(fieldInstance){
					var field = fieldInstance.$element;
					var fieldWrapper = field.closest('div.form-group');

					// set error class on wrapper
					fieldWrapper.removeClass('has-success').addClass('has-error');

					// fill error summary
					updateSummaryErrors(false, fieldInstance);
				});

				// set success class on wrapper
				form.parsley().subscribe('parsley:field:success', function(fieldInstance){
					var field = fieldInstance.$element;
					var fieldWrapper = field.closest('div.form-group');

					// set success class on wrapper
					fieldWrapper.removeClass('has-error').addClass('has-success');

					// fill error summary
					updateSummaryErrors(true, fieldInstance);
				});


			}
		};

		// privileged functions can see private variables/functions
		this.setLocalization = function(locale){
			window.ParsleyValidator.setLocale(locale);
		};
		this.subscribeToParsleyFunctions = function(form){
			subscribeToParsleyFunctions(form);
		};

	}

	// create global instance of helper class
	window.ParsleyHelper = new ParsleyHelper();


})(jQuery);