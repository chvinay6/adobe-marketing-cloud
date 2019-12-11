(function ($) {

	"use strict";

	// Constructor
	function ContentManipulationHelper (parameter) {

		/*** copy countries to country selector mobile ***/
		this.addCountrySelectorMobile = function(element){
			var parentElement = (element) ? element : $("body");

			var countrySelectorEntries = parentElement.find("#countrySelector .countrybox");
			var target = parentElement.find("#countrySelectorMobile");
			var countrySelectorMobileEntries = "";

			countrySelectorEntries.each(function(index, el) {
				var entry = $(this);
				var link = entry.children('a');
				var cssClass = entry.hasClass('active') ? "active" : "";

				var languages = entry.find('div.languages a');
				if(languages.length > 0) {
					var languagesMobile = "";
					languages.each(function(index, el) {
						var language = $(this);
						var cssClass = language.hasClass('active') ? "active" : "";
						languagesMobile += '<li class="'+cssClass+'"><a href="'+language.attr("href")+'">'+language.text()+'</a></li>';
					});

					countrySelectorMobileEntries += '<li class="slideout '+cssClass+'"><a role="menuitem" href="'+link.attr("href")+'">'+link.text()+' <span class="icon" aria-hidden="true"></span></a><span data-toggle="slideout" class="icon slideout-toggle" aria-hidden="true"></span><ul role="menu" class="slideout-menu">'+languagesMobile+'</ul></li>';
				}
				else {
					countrySelectorMobileEntries += '<li class="'+cssClass+'"><a role="menuitem" href="'+link.attr("href")+'">'+link.text()+'</a></li>';
				}
			});

			target.html(countrySelectorMobileEntries);
		};

		/*** copy languages to language selector mobile ***/
		this.addLanguageSelectorMobile = function(element){
			var parentElement = (element) ? element : $("body");

			var languageSelectorEntries = parentElement.find("#languageSelector ul.dropdown-menu").children();
			var target = parentElement.find("#languageSelectorMobile");
			var languageSelectorMobileEntries = "";

			languageSelectorEntries.each(function(index, el) {
				var entry = $(this);
				var link = entry.children('a');
				var cssClass = entry.hasClass('active') ? "active" : "";

				languageSelectorMobileEntries += '<li class="'+cssClass+'"><a role="menuitem" href="'+link.attr("href")+'">'+link.text()+'</a></li>';
			});

			target.html(languageSelectorMobileEntries);
		};

		/*** copy teaser from subnavigation column of contentpage to additional content column for non-lg breakpoints ***/
		this.cloneContentPageContent = function(element){
			var parentElement = (element) ? element : $("body");

			if($("body").hasClass('contentPage') || $("body").hasClass('searchResultPage')){
				var subNavigationColumnContent = element.find('.subNavigationColumn > div.subNavigationColumn-content > *');
				var additionalContentColumnContent = $(".additionalContentColumn-content:first");
				if(subNavigationColumnContent.is("*") && additionalContentColumnContent.is("*")) {
					subNavigationColumnContent.each(function(index, el) {
						var clonedElement = $(this).clone();
						clonedElement.addClass('hidden-lg');
						additionalContentColumnContent.append(clonedElement);
					});
				}
			}
		};

		/*** there exists two versions of main navigation for lg and md, but in smaller versions main navigation is always the same ***/
		/*** so remove class for version2 in sm and smaller ***/
		this.setMainNavigationClass = function() {
			var mainNavElement = $("#mainNavigationContainerWrapper");
			if ( (DeviceSelector.getDeviceType() == "lg") || (DeviceSelector.getDeviceType() == "md") ) {
				if (mainNavElement.hasClass("mainNavigationVersion2Mobile")) {
					mainNavElement.removeClass("mainNavigationVersion2Mobile");
					mainNavElement.addClass("mainNavigationVersion2");
				}
			}
			else if ( (DeviceSelector.getDeviceType() == "sm") || (DeviceSelector.getDeviceType() == "xs") ){
				if (mainNavElement.hasClass("mainNavigationVersion2")) {
					mainNavElement.removeClass("mainNavigationVersion2");
					mainNavElement.addClass("mainNavigationVersion2Mobile");
				}
			}
		};

		// remove titles on hover
		// mouseenter & mouseleave
		this.titleSubpress = function($elements) {
			$elements.each(function() {
				$(this).hover(function() {
					var $this = $(this);
					$this.attr("subTitle", $this.attr("title"));
					$this.removeAttr("title");
				},function(){
					var $this = $(this);
					$this.attr("title", $this.attr("subTitle"));
					$this.removeAttr("subTitle");
				});
			});
		}
		// public variables/functions
		//this.myPublicvariable = "this is public";
	}

	// create global instance of helper class
	window.ContentManipulationHelper = new ContentManipulationHelper();


})(jQuery);
