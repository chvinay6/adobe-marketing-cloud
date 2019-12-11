/***
dependencies:
	$.hurl()
***/
(function ($) {

	"use strict";

	// Constructor
	// TODO:
	// sort option (bool)
	// search option (bool)
	// facet option (bool)
	function RactiveSearchHelper(options) {

		if (typeof options === 'undefined') options = {};

		// TODO: sort and declare right type for variables
		var self = this;
		var ractive = null;
		var searchRequestJSON = {};
		var searchResult = null;

		var $searchContainer = null;
		var $searchResultContainer = null;
		var searchType = null;
		var searchDivision = null;
		var searchUrl = null;
		var sitepath = null;
		var country = null;
		var language = null;
		var $searchboxForm = null;
		var $searchwordInput = null;
		var $searchButton = null;
		var clickEvent = null;
		var facets = null;
		var $facets = null;
		var tabs = null;
		var activeFacets = [];
		var activePaging = 0;
		var searchToken = null;
		var timer = null;
		var scrollToSearchresult = false;
		var numberOfHitsDisplayContainer = null;
		var $searchSortSelector = null;
		var highlightContainer = null;
		var updateUrl = null;
		var debug = false;
		var pageSize = null;
		var firstPageSize = null;
		var searchContainerId = null;
		var searchContainerTemplateId = null;
		var searchwordInputId = null;
		var $searchbox = null;
		var searchSortSelectorId = null;
		var searchResultsId = null;
		var hurlConfigURL = options.hurlConfigURL || "default";

		// private
		var buildSearchRequestJSON = function(){

			searchRequestJSON.sitepath = sitepath;
			searchRequestJSON.country = country;
			searchRequestJSON.language = language;

			searchRequestJSON.pageSize = pageSize;
			searchRequestJSON.firstPageSize = firstPageSize;
			searchRequestJSON.facets = activeFacets;
			searchRequestJSON.page = activePaging;

			searchToken = Helper.getRandomToken();
			searchRequestJSON.token = searchToken;

			searchRequestJSON.searchword = $searchwordInput.val();
			searchRequestJSON.sort = $searchSortSelector.val();

			searchRequestJSON.isMobile = (DeviceSelector.getDeviceType() === "xs");

		};

		var clearPaging = function(){

			activePaging = 0;
			searchRequestJSON.page = activePaging;
		};

		var triggerPictureFill = function(){
			var images = document.getElementById(searchResultsId).getElementsByTagName('img');
			var srcList = [];
			for(var i = 0; i < images.length; i++) {
				srcList.push(images[i]);
			}

			if(srcList.length > 0){
				picturefill({ reevaluate:true, elements: srcList});
			}
		};

		var handleFacetChange = function(element, event){
			var facetId = element.attr("data-facetid");
			var categoryId = element.attr("data-categoryid");

			// remove all active facets with same categoryid
			activeFacets = $.grep(activeFacets, function(e){
				return (e.categoryId !== categoryId);
			});

			if(element.prop("checked")){
				var similiarActiveFacets = $.grep(activeFacets, function(e){
					return ((e.id == facetId) && (e.categoryId == categoryId));
				});

				// add facet to activeFacets if not found
				if(similiarActiveFacets.length <= 0){
					var newFacet = {
						"id": facetId,
						"categoryId": categoryId
					};
					activeFacets.push(newFacet);
				}

			} else {
				// remove facet from activeFacets if found
				if(activeFacets.length > 0){
					activeFacets = $.grep(activeFacets, function(e){
						return !((e.id == facetId) && (e.categoryId == categoryId));
					});
				}

			}
		};

		var loadStateFromURI = function(RactiveSearchHelperConfig){

			// rebuild searchrequestJSON
			searchRequestJSON = JSON.parse(decodeURIComponent(RactiveSearchHelperConfig));

			// rebuild global objects
			console.log(searchRequestJSON);
			if(searchRequestJSON.facets){
				activeFacets = searchRequestJSON.facets;
			}
			else {
				resetFacets();
			}

			if(searchRequestJSON.page){
				activePaging = searchRequestJSON.page;
			}

			// rebuild UI
			$searchwordInput.val(searchRequestJSON.searchword);
			$searchSortSelector.val(searchRequestJSON.sort);

			// Q: Where does initializationDone comme from? A: initOnce custom.js
			$(window).on('ecx.initializationDone', function(){
				submitSearch(event, scrollToSearchresult);
			});
		};

		var processSearchResult = function(data){
			if(data.token) {
				delete data.token;
			}
			ractive.set("searchresult", data);

			updateNumberOfHitsDisplay(data.numberOfHits);
			rebuildUI();
		};

		var rebuildUI = function(){

			// rebuild facets and filter non-existing facets from activeFacets
			facets.find("input:checked").prop("checked", false);
			$.each(activeFacets, function(index, facet) {
				console.log(facet.id);
				var facetInput = facets.find('[data-facetid="'+facet.id+'"][data-categoryid="'+facet.categoryId+'"]');
				if(facetInput.is("*")){
					facetInput.prop("checked", true);
				}
				else {
					activeFacets = $.grep(activeFacets, function(value) {
						return value != facet;
					});
				}
			});

			setDefaultOfCategoryFacets();
		};

		var resetAllFilters = function(){
			resetFacets();
		};

		var resetFacets = function(){
			facets.find("input:radio:checked").each(function(index, el) {
				var element = $(this);
				element.prop("checked", false);
				handleFacetChange(element, null);
			});
			setDefaultOfCategoryFacets();
		};

		var setDefaultOfCategoryFacets = function(){
			// select "all" entry in facet if nothing is set
			facets.find('.facet-category').each(function(index, el) {
				var facetCategory = $(this);
				if(!facetCategory.find('input:radio:checked').is("*")){
					var element = facetCategory.find('input:radio:first');
					element.prop("checked", true);
					handleFacetChange(element, null);
				}
			});
		};

		var submitSearch = function(event, scrollToSearchResult){

			buildSearchRequestJSON();

			if (updateUrl == "true") updateURIHash();

			$.ajax({
				url: searchUrl,
				type: 'POST',
				dataType: 'json',
				data: JSON.stringify(searchRequestJSON),
				contentType: "application/json; charset=utf-8"
			})
			.done(function(data) {
				if(!data.token || searchToken == data.token){

					processSearchResult(data);
					triggerPictureFill();
					updateStyling();
					updateBreakpointSpecificStyling();

					// TODO method update search
					if (Modernizr.touch && $searchwordInput.val()){
						$searchwordInput.blur();
					}else if (Modernizr.touchevents && event && (event.type != "change")){
						document.activeElement.blur(); // closes digital keyboard if opened
					}

					if(scrollToSearchResult) {
						$searchContainer.velocity("scroll", { offset: "-64px", duration: 500, easing: "ease" });
					}
				}
			})
			.fail(function() {
				console.warn("Search pageList: search result error #1");
			});
		};

		var updateBreakpointSpecificStyling = function(){

			ractive.set("breakpoint", DeviceSelector.getDeviceType());
		};

		var updateStyling = function(){

			$searchContainer.find(".button-load").removeClass("is-loading");
			$searchboxForm.removeClass("is-loading");

			if (highlightContainer == "odd" || highlightContainer == "even") {
				var i;
				(highlightContainer == "odd") ? i = 0 : i = 1;
				$searchContainer.find(".items .module").each(function(){
					i = i + 1;
					if (i & 1) $(this).addClass("highlighted");
				});
			}
		};

		var updateURIHash = function(){
			// flexible identify
			var urlConfig = {};
			urlConfig[hurlConfigURL] = encodeURIComponent(JSON.stringify(searchRequestJSON));
			$.hurl("update",urlConfig);
		};

		var updateNumberOfHitsDisplay = function(numberOfHits){

			numberOfHitsDisplayContainer = $searchContainer.find(".search-hits .count");
			Helper.countToNumber(numberOfHitsDisplayContainer,0,numberOfHits,800);
		};

		var highlightText = function(text){

			var searchstring = $searchwordInput.val()
			var searchwords, searchwordsJoined, regex;

			if (searchstring.charAt(0) == '"' && searchstring.slice(-1) == '"') {
				searchwords = searchstring.substring(1,searchstring.length);
				searchwords = searchwords.substring(0,searchwords.length - 1);
				regex = new RegExp(""+searchwords+"(?!([^<]+)?>)", "gi");
			}else{
				searchwords = searchstring.split(" ");
				searchwordsJoined = searchwords.join("|");
				regex = new RegExp(""+searchwordsJoined+"(?!([^<]+)?>)", "gi");
			};

			return text.replace(regex, function(matched) {return "<strong class=\"highlight\">" + matched + "</strong>";});
		};

		var bindOnCompleteEvents = function() {
			// bind events
			// TODO: is enabled / disabled via option/data attribute
			var scrollToSearchresult = (DeviceSelector.getDeviceType() == "xs") ? true : false;

			// TODO: methods events sort / events search / events facets / events load more
			if ($searchSortSelector) {
				$searchSortSelector.on("change", function(event) {
					submitSearch(event, searchRequestJSON.isMobile);
				});
			}
			// searchbox events
			if ($searchbox) {
				$searchboxForm.on('submit', function(event) {
					event.preventDefault();
					$searchboxForm.addClass("is-loading");
					clearPaging();
					resetFacets();
					submitSearch(event, searchRequestJSON.isMobile);
				});
				$searchwordInput.on('autocompleteselectedvalueupdated', function(event) {
					clearPaging();
					resetFacets();
					submitSearch(event, searchRequestJSON.isMobile);
				});
			}
			// if facets
			ractive.on('facet-change', function(event) {
				handleFacetChange($(event.node).find('input[type="radio"]'), event.original);
				clearPaging();
				submitSearch(event.original);
			});

			// load more button events
			// DONE http://learn.ractivejs.org/event-proxies/2
			// CHECK console.log(event) for specific event params in ractive
			ractive.on('load-more', function(event) {
				activePaging++;
				var $this = $(event.node);
				$this.addClass('is-loading');
				submitSearch(event.orginal, false);
				$this.velocity("scroll", { offset: "-124px", duration: 500, easing: "ease" });
			});
			// how you should not do it:
			// if (timerload) {
			// 	clearTimeout(timerload); //cancel the previous timer.
			// 	timerload = null;
			// }
			// var timerload = setTimeout( function(event) {
			// 	$searchContainer.find(".button-load").on(clickEvent, function(event) {
			// 		activePaging++;
			// 		$(this).addClass('is-loading');
			// 		submitSearch(event, false);
			// 		$(this).velocity("scroll", { offset: "-124px", duration: 500, easing: "ease" });
			// 	});
			// },1500);

			$(window).on('ecx.breakpointChange', updateBreakpointSpecificStyling);

		};
		var bindRactive = function(searchContainerId,searchContainerTemplateId,highlightText) {
			var thisRactive = Ractive.extend({
				template: '#'+searchContainerTemplateId,
			});
			ractive = new thisRactive({
				el: '#'+searchContainerId,
				data: {
					breakpoint: DeviceSelector.getDeviceType(),
					highlight: highlightText
				},
				oncomplete: function() {
					bindOnCompleteEvents();
				}
				// TODO: check other ractive events
			});
		};
		var init = function($searchContainerCurrent) {

			$searchContainer = $searchContainerCurrent;

			if($searchContainer.is("*")){

				$searchContainer = $searchContainerCurrent;
				searchContainerId = $searchContainer.attr('id');
				searchContainerTemplateId = searchContainerId+'-ractive';
				searchwordInputId = searchContainerId+'-word';
				searchSortSelectorId = searchContainerId+'-sort';
				searchResultsId = searchContainerId+'-results';

				searchUrl = $searchContainer.attr("data-searchurl");
				sitepath = $searchContainer.attr("data-sitepath");
				country = $searchContainer.attr("data-country");
				language = $searchContainer.attr("data-language");
				searchType = $searchContainer.attr("data-searchType");
				highlightContainer = $searchContainer.attr("data-highlightContainer");
				updateUrl = $searchContainer.attr("data-updateUrl");
				debug = $searchContainer.attr("data-debug");
				pageSize = $searchContainer.attr("data-pageSize");
				firstPageSize = $searchContainer.attr("data-firstPageSize");

				if (Modernizr.touch){
					clickEvent = "touchstart";
				} else {
					clickEvent = "click";
				}

				// bind ractive
				(debug == true) ? Ractive.DEBUG = true : Ractive.DEBUG = false;
				bindRactive(searchContainerId,searchContainerTemplateId,highlightText);

				// searchbox
				// TODO methods init search / init sort / init facets
				$searchbox = $searchContainer.find(".search-box");
				if ($searchbox) {
					$searchboxForm = $searchContainer.find("form");
					$searchwordInput = $searchboxForm.find("#"+searchwordInputId);
					$searchButton = $searchboxForm.find(".submitSearch");
					if (!Modernizr.touch){
						$searchwordInput.attr("autofocus","autofocus").focus();
					}
				}
				// sorting
				$searchSortSelector = $searchContainer.find('#'+searchSortSelectorId);

				// filter
				facets = $searchContainer.find(".filter");
				console.log(facets);

				// load search state if url contains RactiveSearchHelperConfig
				// TODO method init hurl
				var hurl = $("body").data("hurl").link;
				if(hurl && hurl[hurlConfigURL]){
					loadStateFromURI(hurl[hurlConfigURL]);
				}
				else if(searchType == "auto"){
					$(window).on('ecx.initializationDone', function(event){
						submitSearch(event);
					});
				} else if (searchType == "static") {
					$(window).on('ecx.initializationDone', function(event){

							var data = JSON.parse($searchContainer.attr("data-firstData"));

							processSearchResult(data);
							triggerPictureFill();
							updateStyling();
							updateBreakpointSpecificStyling();

							if (Modernizr.touch && $searchwordInput.val()){
								$searchwordInput.blur();
							}else if (Modernizr.touchevents && event && (event.type != "change")){
								document.activeElement.blur(); // closes digital keyboard if opened
							}
					});
				}
			}
		};

		// privileged functions can see private variables/functions
		this.init = function(element){
			var parentElement = (element) ? element : $("body");
			var $searchInstances = parentElement.find('.search.init');
			var i = 0;
			$searchInstances.each(function(){
				i = i + 1;
				init($(this));
				if (i > 1) return console.warn("Ractive: Only one instance is allowed per Page"), false// only one instance
			});
		};

	}

	window.RactiveSearchHelper = new RactiveSearchHelper();

})(jQuery);

// plugins
// http://ractivejs.github.io/ractive-transitions-fade/
(function (global, factory) {
	typeof exports === 'object' && typeof module !== 'undefined' ? module.exports = factory() :
	typeof define === 'function' && define.amd ? define(factory) :
	global.Ractive.transitions.fade = factory();
}(this, function () { 'use strict';

	var DEFAULTS = {
		delay: 0,
		duration: 300,
		easing: 'easyOut'
	};

	function fade(t, params) {
		var targetOpacity;

		params = t.processParams(params, DEFAULTS);

		if (t.isIntro) {
			targetOpacity = t.getStyle('opacity');
			t.setStyle('opacity', 0);
		} else {
			targetOpacity = 0;
		}

		t.animateStyle('opacity', targetOpacity, params).then(t.complete);
	}

	return fade;

}));
