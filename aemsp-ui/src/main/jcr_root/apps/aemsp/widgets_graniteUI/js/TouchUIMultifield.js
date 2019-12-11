(function () {
    var DATA_EAEM_NESTED = "data-eaem-nested",
        CFFW = ".coral-Form-fieldwrapper",
        THUMBNAIL_IMG_CLASS = "cq-FileUpload-thumbnail-img",
        SEP_SUFFIX = "-",
        SEL_FILE_UPLOAD = ".coral-FileUpload",
        SEL_FILE_REFERENCE = ".cq-FileUpload-filereference",
        SEL_FILE_NAME = ".cq-FileUpload-filename",
        SEL_FILE_MOVEFROM = ".cq-FileUpload-filemovefrom",
        EAEM_IMAGE_REQ_FIELD_ID_PREFIX = "eaem-img-required-",
        FILE_UPLOAD_CLEAR = ".cq-FileUpload-clear",
        DATA_ATTR_FILE_UPLOAD = "fileUpload",
        FILE_UPLOAD_SEL = ".coral-FileUpload-input",
        FILE_NAME = "./slideName",
        COMPONENT = "aemsp/components/par/imageslider",
        FIELD_ERROR_EL = $("<span class='coral-Form-fielderror coral-Icon coral-Icon--alert " +
                            "coral-Icon--sizeS' data-init='quicktip' data-quicktip-type='error'>" +
                        "</span>");
 
    function getStringBeforeAtSign(str){
        if(_.isEmpty(str)){
            return str;
        }
 
        if(str.indexOf("@") != -1){
            str = str.substring(0, str.indexOf("@"));
        }
 
        return str;
    }
 
    function getStringAfterAtSign(str){
        if(_.isEmpty(str)){
            return str;
        }
 
        return (str.indexOf("@") != -1) ? str.substring(str.indexOf("@")) : "";
    }
 
    function getStringAfterLastSlash(str){
        if(!str || (str.indexOf("/") == -1)){
            return "";
        }
 
        return str.substr(str.lastIndexOf("/") + 1);
    }
 
    function getStringBeforeLastSlash(str){
        if(!str || (str.indexOf("/") == -1)){
            return "";
        }
 
        return str.substr(0, str.lastIndexOf("/"));
    }
 
    function removeFirstDot(str){
        if(str.indexOf(".") != 0){
            return str;
        }
 
        return str.substr(1);
    }
 
    function modifyJcrContent(url){
        return url.replace(new RegExp("^" + Granite.HTTP.getContextPath()), "")
                .replace("_jcr_content", "jcr:content");
    }
 
    function isSelectOne($field) {
        return !_.isEmpty($field) && ($field.prop("type") === "select-one");
    }
 
    function setSelectOne($field, value) {
        var select = $field.closest(".coral-Select").data("select");
 
        if (select) {
            select.setValue(value);
        }
    }
 
    function isCheckbox($field) {
        return !_.isEmpty($field) && ($field.prop("type") === "checkbox");
    }
 
    function setCheckBox($field, value) {
        $field.prop("checked", $field.attr("value") === value);
    }
 
    function setWidgetValue($field, value) {
        if (_.isEmpty($field)) {
            return;
        }
 
        if (isSelectOne($field)) {
            setSelectOne($field, value);
        } else if (isCheckbox($field)) {
            setCheckBox($field, value);
        } else {
            $field.val(value);
        }
    }
 
    /**
     * Removes multifield number suffix and returns just the fileRefName
     * Input: paintingRef-1, Output: paintingRef
     *
     * @param fileRefName
     * @returns {*}
     */
    function getJustName(fileRefName){
        if(!fileRefName || (fileRefName.indexOf(SEP_SUFFIX) == -1)){
            return fileRefName;
        }
 
        var value = fileRefName.substring(0, fileRefName.lastIndexOf(SEP_SUFFIX));
 
        if(fileRefName.lastIndexOf(SEP_SUFFIX) + SEP_SUFFIX.length + 1 == fileRefName.length){
            return value;
        }
 
        return value + fileRefName.substring(fileRefName.lastIndexOf(SEP_SUFFIX) + SEP_SUFFIX.length + 1);
    }
 
    function getMultiFieldNames($multifields){
        var mNames = {}, mName;
 
        $multifields.each(function (i, multifield) {
            mName = $(multifield).children("[name$='@Delete']").attr("name");
            mName = mName.substring(0, mName.indexOf("@"));
            mName = mName.substring(2);
            mNames[mName] = $(multifield);
        });
 
        return mNames;
    }
 
    function buildMultiField(data, $multifield, mName){
        if(_.isEmpty(mName) || _.isEmpty(data)){
            return;
        }
 
        _.each(data, function(value, key){
            if(key == "jcr:primaryType" || key == 'doNotDelete'){
                return;
            }
 
            $multifield.find(".js-coral-Multifield-add").click();
 
            _.each(value, function(fValue, fKey){
                if(fKey == "jcr:primaryType" || _.isObject(fValue)){
                    return;
                }
 
                var $field = $multifield.find("[name='./" + fKey + "']").last();
 
                if(_.isEmpty($field)){
                    return;
                }
 
                setWidgetValue($field, fValue);
            });
        });
    }
 
    function buildImageField($multifield, mName, isUserClick){
        $multifield.find(".coral-FileUpload:last").each(function () {
            var $element = $(this), widget = $element.data("fileUpload"),
                resourceURL = $element.parents("form.cq-dialog").attr("action"),
                counter = $multifield.find(SEL_FILE_UPLOAD).length;
 
            if (!widget) {
                return;
            }
 
            var fuf = new Granite.FileUploadField(widget, resourceURL);
            
            $element.find(".cq-FileUpload-clear").replaceWith("<br />");
            if (!isUserClick) {
            	addThumbnail(fuf, mName, counter);
            }
        });
    }
 
    function addThumbnail(imageField, mName, counter){
        var $element = imageField.widget.$element,
            $thumbnail = $element.find("." + THUMBNAIL_IMG_CLASS),
            thumbnailDom;
 
        $thumbnail.empty();
 
        $.ajax({
            url: imageField.resourceURL + ".2.json",
            cache: false
        }).done(handler);
 
        function handler(data){
            var fName = getJustName(getStringAfterLastSlash(imageField.fieldNames.fileName)),
                fRef = getJustName(getStringAfterLastSlash(imageField.fieldNames.fileReference));
 
            if(isFileNotFilled(data, counter, fRef)){
                return;
            }
 
            var fileName = data[mName][counter][fName],
                fileRef = data[mName][counter][fRef];
 
            if (!fileRef) {
                return;
            }
 
            if (imageField._hasImageMimeType()) {
                imageField._appendThumbnail(fileRef, $thumbnail);
            }
            
            $element.find(".cq-FileUpload-clear").replaceWith("<br />");
 
            var $fileName = $element.find("[name=\"" + imageField.fieldNames.fileName + "\"]"),
                $fileRef = $element.find("[name=\"" + imageField.fieldNames.fileReference + "\"]");
 
            $fileRef.val(fileRef);
            $fileName.val(fileName);
        }
 
        function isFileNotFilled(data, counter, fRef){
            return _.isEmpty(data[mName])
                    || _.isEmpty(data[mName][counter])
                    || _.isEmpty(data[mName][counter][fRef])
        }
    }
 
    //reads multifield data from server, creates the nested composite multifields and fills them
    function addDataInFields() {
        $(document).on("dialog-ready", function() {
            var $multifields = $("[" + DATA_EAEM_NESTED + "]");
 
            if(_.isEmpty($multifields)){
                return;
            }
 
            workaroundFileInputPositioning($multifields);
 
            var mNames = getMultiFieldNames($multifields),
                $form = $(".cq-dialog"),
                actionUrl = $form.attr("action") + ".infinity.json";
 
            $.ajax(actionUrl).done(postProcess);
 
            function postProcess(data){
                _.each(mNames, function($multifield, mName){
                    $multifield.on("click", ".js-coral-Multifield-add", function (event) {
                        
                    	buildImageField($multifield, mName, event.hasOwnProperty('originalEvent'));
                        workaroundFileInputPositioning($multifields);
                        checkFileRequired();
                    });
 
                    buildMultiField(data[mName], $multifield, mName);
                });
            }
        });
    }
 
    function workaroundFileInputPositioning($multifields){
    	//to workaround the .coral-FileUpload-input positioning issue
    	var test = $multifields.find(".coral-Multifield-list").children();
    	_.each(test, function(field) {
    		var fileInputSelector = "input[type='file']";
    		if ($(field).find(".inputfix").length == 0) {
    			$(field).find(fileInputSelector).css("width" ,"auto");
    			$(field).find(fileInputSelector).addClass("inputfix");
    		}
    	});
    }
 
    function collectImageFields($form, $fieldSet, counter){
        var $fields = $fieldSet.children().children(CFFW).not(function(index, ele){
            return $(ele).find(SEL_FILE_UPLOAD).length == 0;
        });
 
        $fields.each(function (j, field) {
            var $field = $(field),
                $widget = $field.find(SEL_FILE_UPLOAD).data("fileUpload");
 
            if(!$widget){
                return;
            }
 
            var prefix = $fieldSet.data("name") + "/" + (counter + 1) + "/",
 
                $fileRef = $widget.$element.find(SEL_FILE_REFERENCE),
                refPath = prefix + getJustName($fileRef.attr("name")),
 
                $fileName = $widget.$element.find(SEL_FILE_NAME),
                namePath = prefix + getJustName($fileName.attr("name")),
 
                $fileMoveRef = $widget.$element.find(SEL_FILE_MOVEFROM),
                moveSuffix =   $widget.inputElement.attr("name") + "/" + new Date().getTime()
                                        + SEP_SUFFIX + $fileName.val(),
                moveFromPath =  moveSuffix + "@MoveFrom";
 
            $('<input />').attr('type', 'hidden').attr('name', refPath)
                .attr('value', $fileRef.val() || ($form.attr("action") + removeFirstDot(moveSuffix)))
                .appendTo($form);
 
            $('<input />').attr('type', 'hidden').attr('name', namePath)
                .attr('value', $fileName.val()).appendTo($form);
 
            $('<input />').attr('type', 'hidden').attr('name', moveFromPath)
                .attr('value', modifyJcrContent($fileMoveRef.val())).appendTo($form);
 
            $field.remove();
        });
    }
 
    function collectNonImageFields($form, $fieldSet, counter){
        var $fields = $fieldSet.children().children(CFFW).not(function(index, ele){
            return $(ele).find(SEL_FILE_UPLOAD).length > 0;
        });
 
        $fields.each(function (j, field) {
            fillValue($form, $fieldSet.data("name"), $(field).find("[name]"), (counter + 1));
        });
    }
 
    function fillValue($form, fieldSetName, $field, counter){
        var name = $field.attr("name"), value;
 
        if (!name) {
            return;
        }
 
        //strip ./
        if (name.indexOf("./") == 0) {
            name = name.substring(2);
        }
 
        value = $field.val();
 
        if (isCheckbox($field)) {
            value = $field.prop("checked") ? $field.val() : "";
        }
 
        //remove the field, so that individual values are not POSTed
        $field.remove();
 
        $('<input />').attr('type', 'hidden')
            .attr('name', fieldSetName + "/" + counter + "/" + name)
            .attr('value', value)
            .appendTo($form);
    }
 
    //collect data from widgets in multifield and POST them to CRX
    function collectDataFromFields(){
        $(document).on("click", ".cq-dialog-submit", function () {
            var $multifields = $("[" + DATA_EAEM_NESTED + "]");
 
            if(_.isEmpty($multifields)){
                return;
            }
 
            var $form = $(this).closest("form.foundation-form"),
                $fieldSets;
            
            var $fileName = $(SEL_FILE_NAME), fileReqId = EAEM_IMAGE_REQ_FIELD_ID_PREFIX + getStringAfterLastSlash($fileName.attr("name"));

            var invalidFileUploadExists = false;
            $fileName.each(function(j, filename) {
                fnReqId = EAEM_IMAGE_REQ_FIELD_ID_PREFIX + getStringAfterLastSlash($(filename).attr("name"));
                $fnReqInvisibleField = $("#" + fnReqId);
                if (!$fnReqInvisibleField.checkValidity()) {
                	invalidFileUploadExists = true;
                }
            });
            
            var invalidInputExists = false;
            var requiredTextfields = $multifields.find('.coral-Textfield[aria-required="true"]');
            $(requiredTextfields).each(function(j, tf) {
            	if(!$(tf).checkValidity()) {
            		invalidInputExists = true;
            	}
            });
            
            if(invalidFileUploadExists || invalidInputExists) {
            	return;
            }
 
            $multifields.each(function(i, multifield){
                $fieldSets = $(multifield).find("[class='coral-Form-fieldset']");
 
                $fieldSets.each(function (counter, fieldSet) {
                    collectNonImageFields($form, $(fieldSet), counter);
 
                    collectImageFields($form, $(fieldSet), counter);
                });
            });
        });
    }
 
    function overrideGranite_refreshThumbnail(){
        var prototype = Granite.FileUploadField.prototype,
            ootbFunc = prototype._refreshThumbnail;
 
        prototype._refreshThumbnail = function() {
            var $imageMulti = this.widget.$element.closest("[" + DATA_EAEM_NESTED + "]");
 
            if (!_.isEmpty($imageMulti)) {
                return;
            }
 
            return ootbFunc.call(this);
        }
    }
 
    function overrideGranite_computeFieldNames(){
        var prototype = Granite.FileUploadField.prototype,
            ootbFunc = prototype._computeFieldNames;
 
        prototype._computeFieldNames = function(){
            ootbFunc.call(this);
 
            var $imageMulti = this.widget.$element.closest("[" + DATA_EAEM_NESTED + "]");
 
            if(_.isEmpty($imageMulti)){
                return;
            }
 
            var fieldNames = {},
                fileFieldName = $imageMulti.find("input[type=file]").attr("name"),
                counter = $imageMulti.find(SEL_FILE_UPLOAD).length;
            
               
            var fName = $imageMulti.find(".cq-FileUpload-filename");
            var regex = /\d+(?!.*-)/;
            var arr = Array.apply(null, Array(counter+1)).map(Number.prototype.valueOf,0);

            _.each(fName, function(name, j) {
              var nameIndex = regex.exec($(name).attr("name"))[0];
              arr[nameIndex] = 1;
            });

            var realCounter = 0;
            for (var i = 1; i < counter; i++) {
              if (arr[i] == 0) {
                realCounter = i;
                break;
              }
            }

            counter = realCounter;
 
            _.each(this.fieldNames, function(value, key){
                if(value.indexOf("./jcr:") == 0){
                    fieldNames[key] = value;
                }else if(key == "tempFileName" || key == "tempFileDelete"){
                    value = value.substring(0, value.indexOf(".sftmp")) + getStringAfterAtSign(value);
                    fieldNames[key] = fileFieldName + removeFirstDot(getStringBeforeAtSign(value))
                                        + SEP_SUFFIX + counter + ".sftmp" + getStringAfterAtSign(value);
                }else{
                    fieldNames[key] = getStringBeforeAtSign(value) + SEP_SUFFIX
                                                    + counter + getStringAfterAtSign(value);
                }
            });
 
            this.fieldNames = fieldNames;
 
            this._tempFilePath = getStringBeforeLastSlash(this._tempFilePath);
            this._tempFilePath = getStringBeforeLastSlash(this._tempFilePath) + removeFirstDot(fieldNames.tempFileName);
        }
    }
 
    function performOverrides(){
        overrideGranite_computeFieldNames();
        overrideGranite_refreshThumbnail();
    }
    
    function checkFileRequired(){
        var $fileUpload = $(SEL_FILE_UPLOAD);
        
        $fileUpload.each(function(j, fileupload) {
            var $fileName = $(SEL_FILE_NAME).eq(j);
            var fileReqId = EAEM_IMAGE_REQ_FIELD_ID_PREFIX + getStringAfterLastSlash($fileName.attr("name"));
            var editable = Granite.author.DialogFrame.currentDialog.editable;
             
            if((editable.type !== COMPONENT) || _.isEmpty($fileName)){
             return;
            }
            
            if ($(fileupload).find($("input[id^=" + EAEM_IMAGE_REQ_FIELD_ID_PREFIX + "]")).length == 0) {
            	$(fileupload).append("<input type=text style='display:none' id='" + fileReqId + "'/>");
  
             	var cuiFileUpload = $(fileupload).data(DATA_ATTR_FILE_UPLOAD),
                 	$fileReqInvisibleField = $("#" + fileReqId);
  
             	addValidatorIfRequiredSet(editable, cuiFileUpload, $fileReqInvisibleField);
        	}
         });
    }
    
    function addValidatorIfRequiredSet(editable, cuiFileUpload, $fileReqInvisibleField){
        var $fileUploadInput = cuiFileUpload.$element.find(FILE_UPLOAD_SEL);
 
        if ($fileUploadInput.attr("aria-required") !== "true") {
            return;
        }
 
        //user can either drop or upload an image; with required set to true
        //validator with selector: ".coral-FileUpload-input"
        //in /libs/granite/ui/components/foundation/clientlibs/foundation.js
        //always checks if there is a file queued for upload, so workaround it by removing
        //the required attribute on file upload input
        $fileUploadInput.removeAttr( "aria-required" );
 
        cuiFileUpload.$element.find(FILE_UPLOAD_CLEAR).on("click tap", function (e) {
            performRequiredCheck($fileReqInvisibleField, '');
        });
 
        cuiFileUpload.$element.on("fileuploadsuccess", function (event) {
            performRequiredCheck($fileReqInvisibleField, event.item.file.name);
        });
 
        cuiFileUpload.$element.on("assetselected", function (event) {
            performRequiredCheck($fileReqInvisibleField, event.path);
        });
 
        addValidator($fileReqInvisibleField);
 
        initRequiredField($fileReqInvisibleField, editable.path);
    }
    
    function initRequiredField($fileReqInvisibleField, path){
        var fileName = getStringAfterLastSlash(FILE_NAME);
        var regex = /\d+(?!.*-)/;
        var pathElementExt = "/slides/" + regex.exec($fileReqInvisibleField.selector);
        $.ajax(path + pathElementExt + ".json").done(function(data){
            var value = data[fileName];
 
            if(_.isEmpty(value)){
                value = data["slideReference"];
 
                if(_.isEmpty(value)){
                    return;
                }
 
                value = getStringAfterLastSlash(value);
            }
 
            $fileReqInvisibleField.val(value);
        })
    }
    
    function performRequiredCheck($fileReqInvisibleField, value){
        $fileReqInvisibleField.val(value);
        $fileReqInvisibleField.checkValidity();
        $fileReqInvisibleField.updateErrorUI();
    }
 
    function addValidator($fileReqInvisibleField){
        $.validator.register({
            selector: "#" + $fileReqInvisibleField.attr("id"),
 
            validate: validate ,
 
            show: show ,
 
            clear: clear
        });
 
        function validate($fileReqInvisibleField) {
            if ($fileReqInvisibleField.siblings(".cq-FileUpload-filename").attr("value") == 'false' 
            	&& $fileReqInvisibleField.siblings(".cq-FileUpload-filereference").attr("value") == 'false') {
                return "Drop or upload an image";
            }
 
            return null;
        }
 
        function show($fileReqInvisibleField, message) {
            var $fileUploadField = $fileReqInvisibleField.closest(SEL_FILE_UPLOAD),
                arrow = $fileUploadField.closest("form").hasClass("coral-Form--vertical") ? "right" : "top",
                $error = $fileUploadField.nextAll(".coral-Form-fielderror");
           
            var test = $fileUploadField.children(".cq-FileUpload-thumbnail");
            
            if (!_.isEmpty($error)) {
                
                return;
            }

            $(test).css("border-color", "red");
             
            FIELD_ERROR_EL.clone()
                .attr("data-quicktip-arrow", arrow)
                .attr("data-quicktip-content", message)
                .css("display", "none")
                .insertAfter($fileUploadField);
        }
 
        function clear($fileReqInvisibleField) {
            var $fileUploadField = $fileReqInvisibleField.closest(SEL_FILE_UPLOAD);
            var thumbnails = $fileUploadField.children(".cq-FileUpload-thumbnail");
            $(thumbnails).css("border-color", "black");
            $fileUploadField.nextAll(".coral-Form-fielderror").remove();
        }
    }
 
    function getStringAfterLastSlash(str){
        if(!str || (str.indexOf("/") == -1)){
            return "";
        }
 
        return str.substr(str.lastIndexOf("/") + 1);
    }
 
    $(document).ready(function () {
        addDataInFields();
        collectDataFromFields();
    });
 
    performOverrides();
})();