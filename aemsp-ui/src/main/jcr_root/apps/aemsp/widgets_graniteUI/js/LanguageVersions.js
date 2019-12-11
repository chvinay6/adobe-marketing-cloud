(function () {
    var DATA_LANG_PICKER = "data-language-picker",
        CFFW = ".coral-Form-fieldwrapper",
        LANG_PAGE = "aemsp/components/page/languagepage",
        CONTENT_NODE = "jcr:content",
        LANG_PROP_PREFIX = "lang_";
    
    function handler() {
    	var $langSection = $("[" + DATA_LANG_PICKER + "]");
    	 
        if(_.isEmpty($langSection)){
            return;
        }
        
        var regex = /\/content\/[^\/]*\/[^\/]*/;
        var result = regex.exec(MSM.MSMCommons.getPageContentUrl());
        var languageCodes = [];

        var currentLocaleRegex = /\/content\/[^\/]*\/[^\/]*\/([^\/]*)\//;
        var currentLocale = currentLocaleRegex.exec(MSM.MSMCommons.getPageContentUrl())[1];
        var allLanguages = Granite.I18n.getLanguages();

        // get language roots (available languages)
        $.get(result[0] + ".2.json").done(function(data) {
        	for (var i in data) {
        		if (typeof data[i] == 'object') {
        			if (typeof data[i][CONTENT_NODE] !== 'undefined') {
        				if (data[i][CONTENT_NODE]["sling:resourceType"] == LANG_PAGE) {
        					languageCodes.push(i);
        				}
        			}
        		}
        	}
        	
        	// remove current language
        	var currentLangIndex = languageCodes.indexOf(currentLocale);
        	if (currentLangIndex > -1) {
        		languageCodes.splice(currentLangIndex, 1);
        	}
        	
        	// if no others languages are available don't show the section
        	if (languageCodes.length < 1) {
        		$langSection.css("display", "none");
        	}
          
        	// create pathbrowser fields and fill them if values exist
        	var noEditorRegex = /\/content\/.*/;
        	var noEditorResult = noEditorRegex.exec(MSM.MSMCommons.getPageContentUrl());
        	$.get(noEditorResult[0] + ".1.json").done(function(data) {
        		for (var i = 0; i < languageCodes.length; i++) {
        			var fieldValue = data[LANG_PROP_PREFIX + languageCodes[i]];
        			$langSection.append(
                  		'<div class="coral-Form-Fieldwrapper">' 
                          + '<label class="coral-Form-fieldlabel">' + Granite.I18n.get((allLanguages[languageCodes[i]]).title) + '</label>'
                          + '<span class="coral-Form-field coral-PathBrowser" data-init="pathbrowser" data-root-path="' + result[0] + '/' + languageCodes[i] + '"'
                          + ' data-option-loader="granite.ui.pathBrowser.pages.hierarchyNotFile"'
                          + ' data-picker-src="/libs/wcm/core/content/common/pathbrowser/column.html/content?predicate=hierarchyNotFile"' 
                          + ' data-picker-title="' + Granite.I18n.get("Select Path") + '" data-crumb-root="' + Granite.I18n.get("Content Root") + '"'
                          + ' data-picker-multiselect="false" data-root-path-valid-selection="true">'
                          + '<span class="coral-InputGroup coral-InputGroup--block">'
                          + '<input class="coral-InputGroup-input coral-Textfield js-coral-pathbrowser-input" type="text" name="./' + LANG_PROP_PREFIX + languageCodes[i] + '" autocomplete="off"'
                          + ' value="' + ((typeof fieldValue) == 'undefined' ? '' : fieldValue) +'" data-validation="">'
                          + '<span class="coral-InputGroup-button"><button class="coral-Button coral-Button--square js-coral-pathbrowser-button" type="button" title="'+ Granite.I18n.get("Browse") + '">'
                          + '<i class="coral-Icon coral-Icon--sizeS coral-Icon--folderSearch"></i></button></span></span>');
        			var widget = $langSection.find(".coral-PathBrowser");
        			widget.pathBrowser();
        		}
        	});
        });
    }
 
    $(document).ready(function () {
    	$(document).on("dialog-ready", handler);	// Page properties dialog
    	handler();      // Edit properties page
    });
 
})();