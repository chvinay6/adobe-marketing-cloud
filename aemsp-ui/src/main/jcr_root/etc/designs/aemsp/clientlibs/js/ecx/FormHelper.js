(function ($) {

	"use strict";

	// Constructor
	function FormHelper () {

		var uniqueId = 0;


		/*************************************************/
		/***			 CONFIGURATIONS 			   ***/
		/*************************************************/
		var _formCallbacks = {
			'Bereich.Funktionsbeschreibung': function(form){
				// ...
			}
		};

		var _parsyleyConfiguration = {
    		excluded: 'input[class=no-validate], input[type=button], input[type=submit], input[type=reset], input[type=hidden], [disabled], :hidden *'
		};



		/*************************************************/
		/***			 HELPER FUNCTIONS 			   ***/
		/*************************************************/
		var getUniqueId = function(){
			uniqueId++;
			return uniqueId;
		};

		var callCallbackFunctionIfExisting = function(form, typeOfCallback, ajaxData){
			var nameOfDataAttribute = typeOfCallback + "callback";
			if(form.data(nameOfDataAttribute)){
				_formCallbacks[form.data(nameOfDataAttribute)](form, ajaxData);
			}

		};

		var bindDependentVisibilityElements = function(form){
			form.find("[data-hide-on-click]").each(function(index, element){
				var useForm = $(this).data("useform") ? $($(this).data("useform")) : form;
				var dependendElement = $(this);
				var dependendElementTriggerSelector = dependendElement.data("hide-on-click");
				var dependendElementTrigger = useForm.find(dependendElementTriggerSelector);
				if(dependendElementTrigger.is("*")){
					dependendElementTrigger.click(function(event) {
						dependendElement.hide();
						return false;
					});
				}
			});
			form.find("[data-hide-on-selected]").each(function(index, element){
				var useForm = $(this).data("useform") ? $($(this).data("useform")) : form;
				var dependendElement = $(this);
				var dependendElementTriggerSelector = dependendElement.data("hide-on-selected");
				var dependendElementTrigger = useForm.find(dependendElementTriggerSelector);
				if(dependendElementTrigger.is("*")){
					dependendElementTrigger.change(function(event) {
						var activeTriggerElement = $(this);
						if(activeTriggerElement.is(":checked")){
							dependendElement.hide();
						}
						else {
							dependendElement.show();
						}
					});
				}
			});
			form.find("[data-show-on-click]").each(function(index, element){
				var useForm = $(this).data("useform") ? $($(this).data("useform")) : form;
				var dependendElement = $(this);
				var dependendElementTriggerSelector = dependendElement.data("show-on-click");
				var dependendElementTrigger = useForm.find(dependendElementTriggerSelector);
				if(dependendElementTrigger.is("*")){
					dependendElementTrigger.click(function(event) {
						dependendElement.show();
						return false;
					});
				}
			});
			form.find("[data-show-other-on-click]").each(function(index, element){
				var useForm = $(this).data("useform") ? $($(this).data("useform")) : form;
				var dependendElementTrigger = $(this);
				var dependendElementSelector = dependendElementTrigger.data("show-other-on-click");
				var dependendElement = ($(this).data("useform") == dependendElementTrigger.data("show-other-on-click")) ? useForm : useForm.find(dependendElementSelector);

				dependendElementTrigger.click(function(event) {
					dependendElement.show();
					if(dependendElementTrigger.data("show-other-on-click-scrolldown")){
						$.scrollTo(dependendElement, 1000, {'axis':'y'});
					}
					return false;
				});
			});
			form.find("[data-show-on-selected]").each(function(index, element){
				var useForm = $(this).data("useform") ? $($(this).data("useform")) : form;
				var dependendElement = $(this);
				var dependendElementTriggerSelector = dependendElement.data("show-on-selected");
				var dependendElementTrigger = useForm.find(dependendElementTriggerSelector);
				if(dependendElementTrigger.is("*")){
					dependendElementTrigger.change(function(event) {
						var activeTriggerElement = $(this);
						var formFieldType = getTypeOfField(activeTriggerElement);
						var showDependentElement = false;

						switch (formFieldType){
							case "checkbox":
							case "radio":
								if(activeTriggerElement.is(":checked")){
									showDependentElement = true;
								}
								//if()
								break;
							case "select":
								var optionToBeChosen = activeTriggerElement.children("option[value="+dependendElement.data("show-on-selected-value")+"]");
								if(optionToBeChosen.is(":checked")){
									showDependentElement = true;
								}
								break;
						}

						if(showDependentElement){
							dependendElement.show();
						}
						else {
							dependendElement.hide();
						}
					});
				}
			});
		};

		var bindDependentSelectionElements = function(form){
			form.find("[data-select-on-selected]").each(function(index, element){
				var useForm = $(this).data("useform") ? $($(this).data("useform")) : form;
				var dependendElement = $(this);
				var dependendElementTriggerSelector = dependendElement.data("select-on-selected");
				var dependendElementTrigger = useForm.find(dependendElementTriggerSelector);
				if(dependendElementTrigger.is("*")){
					var formFieldType = getTypeOfField(dependendElementTrigger);

					switch (formFieldType){
						case "checkbox":
							dependendElementTrigger.click(function(event) {
								var activeTriggerElement = $(this);
								if(activeTriggerElement.is(":checked")){
									dependendElement.prop("checked", true).trigger("change");
								}
								else {
									dependendElement.prop("checked", false).trigger("change");
								}
							});
							break;
						case "radio":
							dependendElementTrigger.click(function(event) {
								dependendElement.prop("checked", true).trigger("change");
							});
							break;
					}
				}
			});
		};

		var bindDependentValueElements = function(form){
			form.find("[data-update-by-value]").each(function(index, element){
				var useForm = $(this).data("useform") ? $($(this).data("useform")) : form;
				var dependendElement = $(this);
				var dependendElementTriggerSelector = dependendElement.data("update-by-value");
				var dependendElementTrigger = useForm.find(dependendElementTriggerSelector);
				if(dependendElementTrigger.is("*")){
					dependendElementTrigger.change(function(event) {
						var activeTriggerElement = $(this);
						var value = getValueOfInputElement(activeTriggerElement, form);
						if(dependendElement.data("update-format")){
							value = formatString(value, dependendElement.data("update-format"));
						}
						dependendElement.text(value);
					});
				}
			});
			form.find("[data-update-by-textvalue]").each(function(index, element){
				var useForm = $(this).data("useform") ? $($(this).data("useform")) : form;
				var dependendElement = $(this);
				var dependendElementTriggerSelector = dependendElement.data("update-by-textvalue");
				var dependendElementTrigger = useForm.find(dependendElementTriggerSelector);
				if(dependendElementTrigger.is("*")){
					dependendElementTrigger.change(function(event) {
						var activeTriggerElement = $(this);
						dependendElement.text(getTextValueOfInputElement(activeTriggerElement, form));
					});
				}
			});
			form.find("[data-set-value-on-click-by]").each(function(index, element){
				var useForm = $(this).data("useform") ? $($(this).data("useform")) : form;
				var dependendElement = $(this);
				var dependendElementTriggerSelector = dependendElement.data("set-value-on-click-by");
				var dependendElementTrigger = useForm.find(dependendElementTriggerSelector);
				if(dependendElementTrigger.is("*")){
					dependendElementTrigger.click(function(event) {
						var activeTriggerElement = $(this);
						dependendElement.val(activeTriggerElement.data("value"));
					});
				}
			});
		};

		var formatString = function(value, format){
			var valueFormatted = value;

			switch (format){
				case "iban":
					valueFormatted = "";
					for (var i = 0, len = value.length; i < len; i++) {
						valueFormatted += ((i > 0) && ((i % 4) === 0)) ? (" " + value[i]) : value[i];
					}

					break;
				case "amount":
					valueFormatted = $.formatNumber(value, {format:"#,###.00", locale:"de"});
					break;
			}
			return valueFormatted;
		};

		var isBlockValid = function(form, blockId){
			if (true === form.parsley().validate('block' + blockId)){
				return true;
			}
			return false;
		};

		var formatMessages = function(messages) {
			var messagesFormated = "";
			$.each(messages, function(index, message){
				messagesFormated += message+"<br />";
			});
			return messagesFormated;
		};

		var getTypeOfField = function(formFieldInput){
			var type = formFieldInput.first().attr('type');

			if(formFieldInput.hasClass('datepicker')){
				type = "date";
			}
			if(formFieldInput.hasClass('sliderGroup')){
				type = "sliderGroup";
			}

			if((typeof type == "undefined") && formFieldInput.first().is("select")){
				type = "select";
			}

			return type;
		};

		var getNameOfInputElement = function(formFieldInput, form, useRequiredForCalculationName){
			var formFieldType = getTypeOfField(formFieldInput);
			var value = null;

			switch (formFieldType){
				case "checkbox":
					var checkedCheckbox = formFieldInput.filter(":checked").first();
					value = (useRequiredForCalculationName && (checkedCheckbox.data("required-for-calculation-name") !== undefined)) ? checkedCheckbox.data("required-for-calculation-name") : checkedCheckbox.attr("name");
					break;
				case "radio":
					var checkedRadio = form.find('[name="'+formFieldInput.attr("name")+'"]:checked');
					value = (useRequiredForCalculationName && (checkedRadio.data("required-for-calculation-name") !== undefined)) ? checkedRadio.data("required-for-calculation-name") : checkedRadio.attr("name");
					break;
				case "date":
				case "email":
				case "hidden":
				case "number":
				case "text":
				case "select":
				case "sliderGroup":
					value = (useRequiredForCalculationName && (formFieldInput.data("required-for-calculation-name") !== undefined)) ? formFieldInput.data("required-for-calculation-name") : formFieldInput.attr("name");
					break;
			}

			return value;
		};

		var getValueOfInputElement = function(formFieldInput, form, useRequiredForCalculationValue){
			var formFieldType = getTypeOfField(formFieldInput);
			var value = null;

			switch (formFieldType){
				case "checkbox":
					var checkedCheckbox = formFieldInput.filter(":checked").first();
					value = (useRequiredForCalculationValue && (checkedCheckbox.data("required-for-calculation-value") !== undefined)) ? checkedCheckbox.data("required-for-calculation-value") : checkedCheckbox.val();
					break;
				case "radio":
					var checkedRadio = form.find('[name="'+formFieldInput.attr("name")+'"]:checked');
					value = (useRequiredForCalculationValue && (checkedRadio.data("required-for-calculation-value") !== undefined)) ? checkedRadio.data("required-for-calculation-value") : checkedRadio.val();
					break;
				case "date":
				case "email":
				case "hidden":
				case "number":
				case "text":
				case "select":
					value = (useRequiredForCalculationValue && (formFieldInput.data("required-for-calculation-value") !== undefined)) ? formFieldInput.data("required-for-calculation-value") : formFieldInput.val();
					break;
				case "sliderGroup":
					var checkedRadioButtonId = formFieldInput.find("input[type=radio]:checked").first().attr("id");
					value = formFieldInput.find('[data-slider-amountboxvalue-for="'+ checkedRadioButtonId +'"]').text();
					break;
			}

			return value;
		};

		var getTextValueOfInputElement = function(formFieldInput, form){
			var formFieldType = getTypeOfField(formFieldInput);
			var textValue = "";
			var chosenElement;
			var labelOfChosenElement;

			switch (formFieldType){
				//todo checkboxes
				case "checkbox":  // only works if just one checkbox is chosen
					chosenElement = formFieldInput.filter(":checked").first();
					labelOfChosenElement = form.find("label[for="+chosenElement.attr("id")+"]");
					textValue = labelOfChosenElement.text();
					break;
				case "radio":
					chosenElement = form.find('[name="'+formFieldInput.attr("name")+'"]:checked');
					labelOfChosenElement = form.find("label[for="+chosenElement.attr("id")+"]");
					textValue = labelOfChosenElement.text();
					break;
				case "select":
					textValue = formFieldInput.children("option:selected").text();
					break;
			}

			return textValue;
		};

		var setFieldValue = function(formFieldInput, formFieldType, fieldConfig){
			switch (formFieldType){
				case "radio":
					// not implemented
					break;
				case "date":
					var dateSplitted = fieldConfig.defaultValue.split("-");
					formFieldInput.datepicker("setDate", new Date(dateSplitted[0], dateSplitted[1] - 1, dateSplitted[2]));
					formFieldInput.trigger("change");
					break;
				case "email":
				case "number":
				case "text":
					formFieldInput.val(fieldConfig.defaultValue);
					formFieldInput.trigger("change");
					break;
				case "select":
					var optionsString = "";
					$.each(fieldConfig.options, function(index, option){
						optionsString += '<option value="'+ option.value +'" '+ ((option.selected) ? 'selected="selected"' : '') +'>'+ option.text +'</option>';
					});
					formFieldInput.first().html(optionsString);
					break;
			}
		};

		var setFieldValidators = function(formFieldInput, formFieldType, fieldConfig){
			var dateSplitted;
			switch (formFieldType){
				case "radio":
					// not implemented
					break;
				case "date":
					if(fieldConfig.min){
						dateSplitted = fieldConfig.min.split("-");
						formFieldInput.datepicker( "option", "minDate", new Date(dateSplitted[0], dateSplitted[1] - 1, dateSplitted[2]));
					}
					if(fieldConfig.max){
						dateSplitted = fieldConfig.max.split("-");
						formFieldInput.datepicker( "option", "maxDate", new Date(dateSplitted[0], dateSplitted[1] - 1, dateSplitted[2]));
					}
					break;
				case "email":
				case "number":
				case "text":
					if(fieldConfig.min){
						formFieldInput.attr("min", fieldConfig.min);
					}
					if(fieldConfig.max){
						formFieldInput.attr("max", fieldConfig.max);
					}
					if(fieldConfig.regex){
						formFieldInput.attr("pattern", fieldConfig.regex);
					}
					break;
				case "select":
					// not implemented
					break;
			}
		};

		/*************************************************/
		/***			 MAIN FUNCTIONS 			   ***/
		/*************************************************/
		var bindAjaxHandling = function(jqueryFormElement, sendAsJSON){
			if(jqueryFormElement.hasClass('sendFormByAjax')){

				// save reference to message boxes
				var processMessageBox =  jqueryFormElement.parent().children("div.processMessageBox").first();
				var successMessageBox =  jqueryFormElement.parent().children("div.successMessageBox").first();
				var errorMessageBox =  jqueryFormElement.find("div.errorMessageBox");

				var formSessionId = jqueryFormElement.data("formsessionid") ? jqueryFormElement.data("formsessionid") : null;

				jqueryFormElement.bind('submit', function(event) {
					event.preventDefault();

					var url = jqueryFormElement.attr("action");
					var submitButton = jqueryFormElement.find('button[type="submit"]');
					var requestBody;

					if(!submitButton.hasClass('disabled')){
						submitButton.addClass('disabled');

						$.blockUI({
							message: processMessageBox,
							css: {
								border: 'none',
								width: 'auto'
							},
							baseZ: 9000
						});

						if(sendAsJSON){
							var fields = jqueryFormElement.find(":input").serializeArray();

							//get initialization data
							requestBody = {
								_charset_: "utf-8",
								formSessionId: formSessionId,
								action: "submit",
								fields: fields
							};
						}

						$.ajax({
							type: "POST",
							url: (sendAsJSON) ? url + "?_charset_=utf-8" : url,
							data: (sendAsJSON) ? JSON.stringify(requestBody) : (jqueryFormElement.find(":input").serialize() + "&formSessionId=" + formSessionId),
							contentType: (sendAsJSON) ? "application/json" : "application/x-www-form-urlencoded; charset=UTF-8",
							error: function(data){
								successMessageBox.hide();
								errorMessageBox.children(".message").html("Der Request ist fehlgeschlagen.<br />Bitte versuchen Sie es erneut.");
								errorMessageBox.show();
								submitButton.removeClass('disabled');

								$.unblockUI();
							},
							success: function(data){
								$.unblockUI();
								if(data.success){
									$.blockUI({
										message: successMessageBox,
										css: {
											border: 'none',
											width: 'auto'
										},
										baseZ: 9000
									});

									var ajaxData = data;

									setTimeout(function(){
										if($(window).data('blockUI.isBlocked')){
											$.unblockUI();
											jqueryFormElement[0].reset();
											jqueryFormElement.parsley().reset();
											callCallbackFunctionIfExisting(jqueryFormElement, "success", ajaxData);
										}
									}, 2000);

									$(".blockOverlay").click(function() {
										$.unblockUI();
										jqueryFormElement[0].reset();
										jqueryFormElement.parsley().reset();
										callCallbackFunctionIfExisting(jqueryFormElement, "success", ajaxData);
									});
								}
								else {
									$.unblockUI();

									errorMessageBox.children(".message").html(formatMessages(data.messages));
									errorMessageBox.show();

									callCallbackFunctionIfExisting(jqueryFormElement, "error");
								}
								submitButton.removeClass('disabled');
							}
						});
					}

					return false;
				});
			}
		};

		var bindFileUpload = function(jqueryElement) {
			jqueryElement.find('input[type="file"]').change(function(event) {
				var path = $(this).val();
				var cuttingPosition = (path.lastIndexOf("\\") >= 0) ? path.lastIndexOf("\\")+1 : 0;
				path = path.substr(cuttingPosition);
				$(this).closest(".form-group").children('.fileupload-chosenfile').text(path);
			});
		};

		var bindMultiFileUploadSubmit = function(jqueryElement, data){
			var parent = jqueryElement.parent();
			parent.find('#'+parent.find('.fileInput').attr('id')).fileupload('send', {files: data.originalFiles})
			.success(function (result, textStatus, jqXHR){
		    	jqueryElement.find('.errors-list').hide();
		    	jqueryElement.find('li.template-upload').remove();
		    })
		    .error(function (jqXHR, textStatus, errorThrown){
		    	jqueryElement.parent().find('.errors-list').show();
		    })
		    .complete(function (result, textStatus, jqXHR){
		    	//jqueryElement.find('.fileUpload-progress').addClass('hidden');
		    });
		};

		var bindMultiFileUpload = function(jqueryElement){
			var formSessionId = jqueryElement.data("formsessionid") ? jqueryElement.data("formsessionid") : null;
			var lang = window.fileupload[Helper.getLanguage()];

			if(!lang){
				lang = window.fileupload[Helper.getDefaultLanguage()];
			}

			jqueryElement.find('.fileInput').each(function(index, element){
				var jqueryFileInput = $(this);
				var filesContainer = ($("[data-uploadtemplate-for="+ jqueryFileInput.attr("id") +"]").is("*")) ? $("[data-uploadtemplate-for="+ jqueryFileInput.attr("id") +"]") : $('.fileUploads ul.template-files');
				var filesContainerWrapper = filesContainer.parent();

				jqueryFileInput.fileupload({
					// Uncomment the following to send cross-domain cookies (and there has to be set something else):
			        //xhrFields: {withCredentials: true},
					url: jqueryFileInput.data("fileuploadurl"),
					acceptFileTypes: jqueryFileInput.data("acceptfiletypes") ? (new RegExp(jqueryFileInput.data("acceptfiletypes"), "i")) : undefined,
					maxNumberOfFiles: jqueryFileInput.data("maxnumberoffiles") ? jqueryFileInput.data("maxnumberoffiles") : undefined,
					autoUpload: jqueryFileInput.data("autoupload") ? jqueryFileInput.data("autoupload") : false,
					formData: {
						_charset_: "utf-8",
						formSessionId: formSessionId
					},
					filesContainer: filesContainer,
				    uploadTemplateId: null,
				    downloadTemplateId: null,
				    maxFileSize: jqueryFileInput.data("maxfilesize") ? jqueryFileInput.data("maxfilesize") : 6000000,
				    uploadTemplate: function (o){
				        var rows = $();
				        $.each(o.files, function (index, file) {
				            var row = $('<li class="template-upload clearfix">' +
				                '<span class="icon-WR_icons_doc docIcon"></span>' +
				                '<span class="fileName"></span><span class="size"></span><span class="error"></span>' +
				                '<button class="fileCancel cancel"><span class="icon-WR_icons_close closeIcon"></span>'+lang.messages.deleteText+'</span></button>' +
				                '</li>');
				            row.find('.fileName').text(file.name);
				            row.find('.size').text(o.formatFileSize(file.size));
				            rows = rows.add(row);
				        });
						filesContainerWrapper
						.find(".fileUpload-start")
							.removeClass('disabled');
				        return rows;
				    },
				    downloadTemplate: function (o){
				        var rows = $();
				        $.each(o.files, function (index, file) {
				        	var fileDeleteData = "data-data='{\"formSessionId\":\""+formSessionId+"\", \"fileName\":\""+file.name+"\"}'";
				            var row = $('<li class="template-download clearfix">' +
				                '<span class="icon-WR_icons_doc docIcon"></span>' +

				                '<span class="fileName"></span> <div class="uploaded">('+lang.messages.uploadedText+')</a><span class="size"></span><span class="error"></span>' +
				                '<button class="fileCancel delete" '+ fileDeleteData +' data-type="'+file.deleteType+'" data-url="'+file.deleteUrl+'"><i class="glyphicon glyphicon-trash"></i><span class="icon-WR_icons_close closeIcon"></span>'+window.fileupload[Helper.getLanguage()].messages.deleteText+'</button>'+
				                '</li>');
				            row.find('.fileName').text(file.name);
				            row.find('.size').text(o.formatFileSize(file.size));
				            if (file.error){
				                row.find('.error').text(file.error);
				            }
				            rows = rows.add(row);
				        });
				        return rows;
				    },
				    stop: function(){
				    	jqueryElement.find('.fileUpload-progress').addClass('hidden');
				    },
				    change: function(e, data) {
				    	var submitBtn = filesContainerWrapper.find(".fileUpload-start");
			    		if(jqueryFileInput.data("autoupload")){
			    			submitBtn.hide();
			    		}else{
							submitBtn
								.removeClass('disabled')
								.unbind('click')
								.click(function(){
									bindMultiFileUploadSubmit(filesContainerWrapper, data);
								});
			    		}
				    	if(filesContainerWrapper.hasClass("hidden")) {
				    		filesContainerWrapper.removeClass("hidden");
				    	}
				    },
					destroyed: function(e, data){
						var fileInput = $(e.target);
						fileInput.data("numberOfUploadedFiles", fileInput.data("numberOfUploadedFiles") - 1);
						var items = filesContainerWrapper.find('li.template-upload, li.template-download');
				    	if(items.length===0){
			    			filesContainerWrapper.addClass("hidden");
			    		}
						filesContainerWrapper.find('.fileUpload-progress').addClass('hidden');
						return true;
					},
				    send: function(e, data){
						filesContainerWrapper.find('.fileUpload-progress').removeClass('hidden');
						filesContainerWrapper.find(".fileUpload-start").addClass('disabled');
				    	callCallbackFunctionIfExisting(jqueryElement, "fileuploadstart", data);
				    },
				    fail: function(e, data){
						if(data._response.jqXHR.responseJSON.messages) {
							var uploadFileRow = data.filesContainer.find("li.in");
							uploadFileRow.addClass('failed');
							uploadFileRow.find(".error").text(data._response.jqXHR.responseJSON.messages);
						}
				    },
				    always: function(e, data){
				    	callCallbackFunctionIfExisting(jqueryElement, "fileuploaddone", data);
				    },
				    destroy: function(e, data){
						filesContainerWrapper.find('.fileUpload-progress').removeClass('hidden');
						if (e.isDefaultPrevented()) {
							return false;
						}
						var that = $(this).data('blueimp-fileupload') ||
						$(this).data('fileupload'),
						removeNode = function () {
							that._transition(data.context).done(
								function () {
									$(this).remove();
									that._trigger('destroyed', e, data);
								}
							);
						};
						if (data.url){
							data.dataType = data.dataType || that.options.dataType;
							$.ajax(data).done(removeNode).fail(function () {
							that._trigger('destroyfailed', e, data);
						});
						} else {
							removeNode();
						}
				    },
				    processfail: function(e, data) {
						var currentFile = data.files[data.index];
						if (data.files.error && currentFile.error) {
							var uploadFileRow = data.filesContainer.find("li.processing");
							uploadFileRow.addClass('failed');
							uploadFileRow.find(".error").text(currentFile.error);
						}
				    },
				    processdone: function(e, data) {
				    	var fileInput = $(e.target);
				    	var numberOfUploadedFiles = fileInput.data("numberOfUploadedFiles") || 0;
				    	filesContainerWrapper.find('ul li.template-upload .cancel').unbind('click').click(function(){
				    		var itemsUp = filesContainerWrapper.find('li.template-upload').length;
				    		var itemsDown = filesContainerWrapper.find('li.template-download').length;
				    		var name = $(this).parent().find('.fileName').text();
					    	if(itemsUp-1===0&&itemsDown===0){
				    			filesContainerWrapper
					    			.addClass("hidden");
				    		}else if(itemsUp-1===0){
				    			filesContainerWrapper
				    				.find(".fileUpload-start")
				    				.addClass('disabled');
				    		}
							for(var i = 0; i<data.originalFiles.length;i++){
								if(data.originalFiles[i].name==name){
									data.originalFiles.splice(i, 1);
									break;
								}
							}
				    	});
				    	fileInput.data("numberOfUploadedFiles", numberOfUploadedFiles+1);
				    },
					messages: lang.messages
				});
			});
		};

		/*** define blocks and only validate these because sitecore has a form around all body elements ***/
		var initSitecoreClientValidation = function(jqueryElement){
			jqueryElement.find('[role="form"]').each(function(index, el) {
				var realForm = $(this);
				var trigger = realForm.find(".validation-trigger");
				var uniqueFormId = getUniqueId();
				realForm.data("form-id", uniqueFormId);
				realForm.find(':input').each(function(index, el) {
					$(this).attr("data-parsley-group", "block"+uniqueFormId);
				});

				// prevent onclick trigger by element attribute without check
				trigger.each(function(index, el){
					if(this.onclick){
						$(this).data('onclick', this.onclick);

						this.onclick = function(event) {
							if(isBlockValid(jqueryElement, uniqueFormId)){
								// destroy validation because a second sitecore form on page would be validated invalid
								jqueryElement.parsley().destroy();
								// call orignal onclick action
								$(this).data('onclick').call(this, event || window.event);
							}
							else {
								return false;
							}
						};
					}
				});

				// prevent all other onclick events without check
				trigger.click(function(event) {
					if(isBlockValid(jqueryElement, uniqueFormId)){
						// destroy validation because a second sitecore form on page would be validated invalid
						jqueryElement.parsley().destroy();
					}
					else {
						return false;
					}
				});
			});

		};

		var initSpecialForms = function(jqueryElement){
			// formulare
			if(jqueryElement.is("#formid")){
				// do something
			}
		};


		var initGeneral = function(jqueryElement){
			var locale = $("html").attr("lang") || "en";
			ParsleyHelper.setLocalization(locale);

			// init selectboxes
			jqueryElement.find('select').bind({
				'create': function(ev, obj){
					var width = obj.selectbox.width()-obj.dropdown.find('>.selectboxit-arrow-container').outerWidth()-10;
					obj.dropdown.find('>.selectboxit-text').css({'max-width':width});
				}
			}).selectBoxIt({
				autoWidth: false,
				showEffect: "slideDown",
				showEffectSpeed: 500,
				hideEffect: "slideUp",
				hideEffectSpeed: 500
			});

			// init special forms
			initSpecialForms(jqueryElement);
		};


		var initStandardForms = function(jqueryElement){
			initSitecoreClientValidation(jqueryElement);
			jqueryElement.parsley(_parsyleyConfiguration);
			//bindAjaxHandling(jqueryElement, false);
			bindFileUpload(jqueryElement);
			//bindMultiFileUpload(jqueryElement);
			//bindDependentVisibilityElements(jqueryElement);
			//bindDependentSelectionElements(jqueryElement);
			//bindDependentValueElements(jqueryElement);
			initGeneral(jqueryElement);
			ParsleyHelper.subscribeToParsleyFunctions(jqueryElement);
		};


		// privileged functions can see private variables/functions
		this.init = function(jqueryElement){
			if(jqueryElement.is("*")){
				jqueryElement.each(function(index, element){
					var jqueryFormElement = $(this);
					initStandardForms(jqueryFormElement);
				});
			}
		};


		// public variables/functions
		//this.myPublicvariable = "this is public";
	}

	// create global instance of helper class
	window.FormHelper = new FormHelper();


})(jQuery);
