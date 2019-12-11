var forms = {

	init: function () {

		var _this = this;
		var forms = $(".module.form").find(".contentForm");

		if (forms.length) {

			_this.selectFields = forms.find("select");
			_this.datepickerContainer = forms.find(".datepicker");
			_this.inputTextFields = forms.find(".textItem input:text");
			_this.textareaFields = forms.find("textarea");

			if(_this.selectFields.length) {
				_this.selectBoxes(_this.selectFields);
			}

			if(_this.datepickerContainer.length) {
				_this.datePicker(_this.datepickerContainer);
			}

			if(_this.inputTextFields.length) {
				//_this.validateMinLength(_this.inputTextFields, 2);
				_this.validateMaxLength(_this.inputTextFields);
			}

			if(_this.inputTextFields.length) {
				//_this.validateMinLength(_this.textareaFields, 20);
				_this.validateMaxLength(_this.textareaFields);
			}

			_this.initFormSubmit(forms);
		}

	},
	selectBoxes: function(selectFields) {

		// Calls the selectBoxIt method
		selectFields.selectBoxIt({

			// Full options list here:
			// http://gregfranko.com/jquery.selectBoxIt.js/index.html#OptionsAPI

			// Don't detect and set width automatically (does not work if width is set to a percent value like: 100%)
			autoWidth: false,

			// Hides the first select box option from appearing when the drop down is opened
			showFirstOption: false,

			// Uses the jQuery 'fadeIn' effect when opening the drop down
			showEffect: "fadeIn",

			// Sets the jQuery 'fadeIn' effect speed to 400 milleseconds
			showEffectSpeed: 400,

			// Uses the jQuery 'fadeOut' effect when closing the drop down
			hideEffect: "fadeOut",

			// Sets the jQuery 'fadeOut' effect speed to 400 milleseconds
			hideEffectSpeed: 400,

			// Set a custom down arrow icon by adding new CSS class(s)
			downArrowIcon: "icon-chevron-down"

		});

	},
	datePicker: function(datepickerContainer) {

		datepickerContainer.each(function(){
			var thisDatepickerContainer = $(this);
			var thisInput = thisDatepickerContainer.children("input");
			var numberOfMonths = 1;

			// desktop: show 2 month in a row; mobile/tablet: show a single month only
			if(DeviceSelector.getDeviceType() == "md" || DeviceSelector.getDeviceType() == "lg") {
				numberOfMonths = 2;
			}

			thisDatepickerContainer.datepicker({
				dateFormat: "dd.mm.yy",  // results in: "01.01.2016"
				numberOfMonths: numberOfMonths,  // number of month to show at once
				altField: thisInput, // sets input field as action area
				firstDay: 1, // first day of a week to show (sunday = 0, monday = 1 ...)
				dayNamesMin: [ "So", "Mo", "Di", "Mi", "Do", "Fr", "Sa" ],
				monthNames: [ "Januar", "Februar", "März", "April", "Mai", "Juni", "Juli", "August", "September", "Oktober", "November", "Dezember" ],
				beforeShowDay: $.datepicker.noWeekends // disable weekend days
			});

			// don't show preselected date
			datepickerContainer.datepicker("setDate", "");

			var datepickerItem = thisDatepickerContainer.children(".ui-datepicker");

			// hide datepicker onload of page
			datepickerItem.hide();

			// if calendar icon gets clicked: trigger focus to show calendar
			thisDatepickerContainer.on("click", function(){
				thisInput.trigger("focus");
			});

			// on input field focus: show calendar
			thisInput.on("focus", function(){
				datepickerItem.fadeIn(function(){

					thisInput.addClass("highlight"); // used for styling effects

					// close datepicker on click outside
					// - checks also for children elements
					// - is executed only once: event listener will be destroyed after the first click outside
					// - uses namespace for click event, to make sure no other events will be removed
					var thisTimestamp = String(new Date().getTime()); // used to build namespace
					$(document).on("click."+thisTimestamp, function(e) {
						// if click is outside (target is not the layer or its children)
						if (e.target != thisDatepickerContainer[0] && !thisDatepickerContainer.has(e.target).length && !$(e.target).hasClass("ui-datepicker-next") && !$(e.target).hasClass("ui-datepicker-prev")) {
							closeDatepicker();
						}
					});

					// on select another day
					datepickerContainer.on("mousedown", "a.ui-state-default", function(){
						closeDatepicker();
					});

					function closeDatepicker(){
						datepickerItem.fadeOut(function(){
							thisInput.removeClass("highlight");
						});
						$(document).off('click.'+thisTimestamp); // remove click listener
					}
				});

			});

		});

	},
	validateMinLength: function(formFields, minLength) {

		formFields.on("focusout", function() {
			var thisFormItem = $(this).parents(".formItem");
			var length = $(this).val().length;

			if (minLength > length) {
				thisFormItem.addClass("minLengthError");
			}
			else {
				thisFormItem.removeClass("minLengthError");
			}

		});

	},
	validateMaxLength: function(formFields) {

		formFields.on("keyup", function() {
			var thisFormItem = $(this).parents(".formItem");
			var maxLength = $(this).attr("maxlength");
			var length = maxLength - $(this).val().length;

			if (length <= 0) {
				var valueLimited = $(this).val().substr(0, maxLength);
				$(this).val(valueLimited);
				length = maxLength - valueLimited.length;
				thisFormItem.addClass("counterError");
			}

			thisFormItem.attr("data-count", length);

		});

		formFields.on("focusout", function() {
			$(this).parents(".formItem").attr("data-count", "");
		});

	},


	// The following functions provide ajax requests and
	// client side error handling for the contact form

	initFormSubmit: function(forms) {
		var _this = this;

		forms.each(function(){
			var thisForm = $(this);

			// send the post on click
			thisForm.find("button").click(function() {
				_this.sendMessageForm(thisForm);
			});

		});

	},
	sendMessageForm: function(thisForm) {
		var _this = this;
		var thisFormId = thisForm.attr("id");
		var thisFormFields = thisForm.find('input:visible, textarea:visible, select');

		// setup request model including anti forgery token
		var requestModel = {};
		requestModel["requestVerificationToken"] = $('input[name="__RequestVerificationToken"]').val();
		requestModel["ModuleId"] = thisForm.data("module-id");
		requestModel[thisFormId] = {};

		thisFormFields.each(function () {
			var thisField = $(this);

			if (thisField.is("input") && (thisField.attr("type") == "checkbox" || thisField.attr("type") == "radio")) {
				requestModel[thisFormId][thisField.attr("id")] = thisField.is(':checked');
			}
			else {
				requestModel[thisFormId][thisField.attr("id")] = thisField.val();
			}
		});

		// debug output:
		//console.log(JSON.stringify(requestModel, null, '\t'));

		var dataDict = $.toDictionary(requestModel);

		// if a "google recaptcha" object exists, we need to pass it to the POST data
		if(typeof grecaptcha !== "undefined"){
			dataDict.push({name: "g-recaptcha-response", value: grecaptcha.getResponse()});
		}

		// run ajax call to execute post
		$.ajax({
			url: thisForm.data("form-url"),
			async: false,
			type: "POST",
			data: dataDict,
			beforeSend: function () {
				_this.removeErrors(thisForm);
			},
			success: function(data) {

				// hide contact form in case the POST succeeded.
				thisForm.hide();
				var formHeadlines = thisForm.parents(".module.form").find(".headlines");
				formHeadlines.find("h3").text(data);
				formHeadlines.find("p").hide();

				// In case we want a redirection after all...
				// window.location.href = window.UrlContactFormSendSuccess;
			},
			error: function (xhr) {
				switch (xhr.status) {
					// action in case of model validation (data annotation) errors (400 == Bad request)
					case 400:
						if(typeof grecaptcha !== "undefined") {
							grecaptcha.reset();
						}

						$.each(jQuery.parseJSON(xhr.responseText),
							function (id, error) {
								_this.addErrors(thisForm, id, error);
							});
						break;

					// action in case of other technical errors
					default:
						// ???
						break;
				}
			}
		});
	},
	addErrors: function (thisForm, fieldId, error) {

		// recaptcha
		if(typeof grecaptcha !== "undefined") {
			if (fieldId === "g-recaptcha") {
				// set global error
				thisForm.find("." + fieldId).parents("div.formItem")
					.addClass("error")
					.prepend('<span class="errorMessage">' + error.join(", ") + '</span>');
			}
		}

		// global form error
		else if (fieldId === thisForm.attr("id")){
			var formHeadlines = thisForm.parents(".module.form").find(".headlines");
			formHeadlines.find("p").hide();
			formHeadlines.append('<p class="errorMessage">' + error.join(", ") + '</p>');
		}

		// single field error
		else {
			var realId = fieldId.replace("contactFormDto.", "");

			thisForm.find("#" + realId).parents("div.formItem")
				.addClass("error")
				.find("label").append('<span class="errorMessage"> – ' + error.join(", ") + '</span>');
		}

	},
	removeErrors: function (thisForm) {
		var formHeadlines = thisForm.parents(".module.form").find(".headlines");
		formHeadlines.find("p").show();
		formHeadlines.find("p.errorMessage").remove();

		thisForm.find(".formItem").each(function() {
			$(this).removeClass("error");
			$(this).find(".errorMessage").remove();
		});
	},

	// On page resize:
	// set number of month to show in datapicker (depending on current breakpoint)
	updateOnResize: function () {
		var _this = this;

		if(typeof _this.datepickerContainer !== "undefined"){
			var numberOfMonths = 1;

			// desktop: show 2 month in a row; mobile/tablet: show 1 month only
			if(DeviceSelector.getDeviceType() == "md" || DeviceSelector.getDeviceType() == "lg") {
				numberOfMonths = 2;
			}

			_this.datepickerContainer.each(function() {
				$(this).datepicker("option", "numberOfMonths", numberOfMonths);
				$(this).datepicker("refresh");
			});
		}
	}
};
