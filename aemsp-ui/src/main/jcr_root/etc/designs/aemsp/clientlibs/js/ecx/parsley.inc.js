// ParsleyConfig definition if not already set
window.ParsleyConfig = window.ParsleyConfig || {};

window.ParsleyConfig = $.extend(true, window.ParsleyConfig, {
	validators: {
		conditionalrequired: {
			fn: function (value, requirements) { 
				// if requirements[0] value does not meet requirements[1] expectation, field is required
				if (requirements[1] == $(requirements[0]).val() && '' === value){
					return false;
				}

				return true;
			},
			priority: 32
		},
		/*** REQUIRED VALUE UNTIL ELEMENT SET ***/
		requiredvalueuntilelementset: {
			fn: function (value, requiredElementName) { 
				if(value.length !== 0){
					return true;
				}

				var requiredElements = $("form [name=" + requiredElementName + "]");
				var requiredElementHasValue = false;

				// check radio and checkbox groups
				if(requiredElements.length > 1){
					var checkedElements = requiredElements.filter(":checked");
					if(checkedElements.is("*")){
						return true;
					}
				}
				// check single inputs
				else if(requiredElements.length == 1) {
					if(requiredElements.val()){
						return true;
					}
				}

				return false;
			},
			priority: 32
		},
		/*** REQUIRED JQUERY FILE UPLOAD ***/
		requiredjqueryfileupload: {
			fn: function (value, selector) { 
				var fileInput = $(selector);

				var numberOfFiles = fileInput.data("numberOfUploadedFiles");
				if(numberOfFiles > 0){
					return true;
				}

				return false;
			},
			priority: 32
		}
	},
	i18n: {
		en: {
			requiredvalueuntilelementset: 'One of the values is required',
			requiredjqueryfileupload: 'File is required'
		},
		de: {
			requiredvalueuntilelementset: 'Geben Sie zumindest einen der beiden Werte ein',
			requiredjqueryfileupload: 'Datei ist erforderlich'
		}
	}
});