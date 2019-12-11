(function (document, $) {
    var rel_preview_enabled_path_browser = "[data-enable-path-browser-preview='true'] .js-coral-pathbrowser-button";
    var rel_path_browser_item = ".coral-Pathbrowser-picker[data-enable-path-browser-preview=\"true\"] .coral-ColumnView-item";
    var rel_path_browser_preview = ".path-browser-preview";
    var rel_path_browser_preview_image = rel_path_browser_preview + " .path-browser-preview-image";
    var rel_active_item = ".coral-ColumnView-column.is-active .coral-ColumnView-item.is-active";
    var preview_image_column_css = "preview_image_column";
    var rel_preview_enabled_confirm_button = "[data-enable-path-browser-preview='true'] .js-coral-pathbrowser-confirm";
    var previewUrl;
    
    $(document).on("click", rel_preview_enabled_path_browser, function (e) {
    	
        var target = $(e.target);
        var pathBrowser = target.closest("[data-enable-path-browser-preview='true']");
        previewUrl = pathBrowser.data("previewUrl") || "$path.thumb.319.319.png";
        if (pathBrowser.length) {
            var $pathBrowser = pathBrowser.data("pathBrowser");
            var $pathBrowserPicker = $pathBrowser.$picker;
            $pathBrowserPicker.attr("data-enable-path-browser-preview", "true");
            var $pickerPanel = $pathBrowserPicker.find(".coral-Pathbrowser-pickerPanel");  
	        var $pickerColumnView = $pickerPanel.find(".coral-ColumnView");
	            
	        if (!$pickerColumnView.hasClass(preview_image_column_css)){
	  	
	        	$pickerColumnView.addClass(preview_image_column_css);
	            $pickerColumnView.css({
	                width: "75%"
	            });
	            var $preview = $("<div class='" + rel_path_browser_preview.substr(1) + "'><div class='path-browser-preview-text'></div><div class='path-browser-preview-image'></div></div>");
	            $preview.css({
	                width: "25%",
	                height: "100%",
	                float: "right"
	            });
	            $pickerPanel.append($preview);
	            
	            if($(rel_active_item).data('value'))
	            {
	            	setImagePreview();
	            }
            }
        }
    });
    
 
    $(document).on("click", rel_path_browser_item, function (e) {
    	setImagePreview();
    });
    
    function setImagePreview()
    {
    	 var activeItem = $(rel_active_item);
         var path = activeItem.data('value');
         var previewImage = $(rel_path_browser_preview_image);
         previewImage.find("img").remove();
         if (path) {
             var previewThumbnail = previewUrl.replace("$path", path);
             previewImage.append("<img src='" + previewThumbnail + "' style='display:block; margin: 20px auto; max-width:319px; max-height:319px'>");
         }
    }
})(document, Granite.$);