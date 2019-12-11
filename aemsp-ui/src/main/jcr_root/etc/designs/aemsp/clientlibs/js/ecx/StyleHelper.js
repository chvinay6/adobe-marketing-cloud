(function ($) {

	"use strict";

	// Constructor
	function StyleHelper (parameter) {

		var cq = false;

		// private
		var addClearfixForTeaserRow = function(teaserRow, breakpoint){
			var columnSizeSum = 0;

			teaserRow.children(":not(.clearfix, .text)").each(function(index, el) {
				var teaser = $(this);
				var classArray = teaser.attr("class").split(" ");

				if(Helper.isCQ()){
					classArray = teaser.children(":first").attr("class").split(" ");
				}
				var columnSize = getColumnSizeFromClassArray(classArray, breakpoint);

				if(columnSizeSum + columnSize > 12) {

					if(!Helper.isSitecoreEdit()){
						teaser.before('<div class="clearfix visible-'+ breakpoint +'-block"></div>');
					}

					columnSizeSum = 0;
				}
				columnSizeSum += columnSize;
			});
		};

		var getColumnSizeFromClassArray = function(classArray, breakpoint){
			var columnSize = 0;

			if(!breakpoint){
				return 0;
			}

			$.each(classArray, function(index, val) {
				if(val.search("col-"+breakpoint+"-") != -1){
					columnSize = parseInt(val.substr(7));
					return false;
				}
			});

			if(columnSize === 0){
				columnSize = getColumnSizeFromClassArray(classArray, getSmallerBreakpoint(breakpoint));
			}

			return columnSize;
		};

		var getSmallerBreakpoint = function(breakpoint){
			if(breakpoint == "lg") {
				return "md";
			}
			if(breakpoint == "md") {
				return "sm";
			}
			if(breakpoint == "sm") {
				return "xs";
			}
			return false;
		};

		var getTeaserListSeperatedByVisibleRows = function(row){
			var elementsInRow = row.children();
			var seperatorIndizesArray = [];

			elementsInRow.each(function(index, el) {
				var jqueryElement = $(this);
				if(jqueryElement.is('.clearfix:visible')){
					seperatorIndizesArray.push(index);
				}
			});

			if(seperatorIndizesArray.length > 0){
				var seperatedTeaserList = [];
				var startingIndex = 0;
				$.each(seperatorIndizesArray, function(index, val) {
					seperatedTeaserList.push(elementsInRow.slice(startingIndex, val));
					startingIndex = val;
				});
				return seperatedTeaserList;
			}

			return [elementsInRow];
		};


		// privileged

		/*** add clearfix divs before teaser that would not fit in line to secure clear breaking ***/
		this.addClearfixForTeaserRows = function(element){
			var parentElement = (element) ? element : $("body");

			parentElement.find('.row-teaser:not(.masonry)').each(function(index, el) {
				var teaserRow = $(this);

				addClearfixForTeaserRow(teaserRow, "lg");
				addClearfixForTeaserRow(teaserRow, "md");
				addClearfixForTeaserRow(teaserRow, "sm");
				addClearfixForTeaserRow(teaserRow, "xs");
			});
		};

		this.matchTeaserHeight = function(element){
			var parentElement = (element) ? element : $("body");
			var currentDeviceType = DeviceSelector.getDeviceType();

			parentElement.find(".row-teaser:not(.masonry)").each(function(index, el) {
				var row = $(this);

				var teaserListSeperatedByVisibleRows = getTeaserListSeperatedByVisibleRows(row);

				$.each(teaserListSeperatedByVisibleRows, function(index, visibleRow) {

					var maxHeight = 0;
					var childs = visibleRow.children();
					if($(childs[0]).attr("class").indexOf('col-')!=-1){
						visibleRow = childs;
					}

					visibleRow.not('.clearfix').each(function(index, el) {
						var teaser = $(this);
						var teaserHeight = teaser.find(".inner:first").innerHeight("auto").outerHeight();

						maxHeight = (teaserHeight > maxHeight) ? teaserHeight : maxHeight;
					});

					visibleRow.not('.clearfix').each(function(index, el) {
						var teaser = $(this);
						var childs = teaser;

						if(Helper.isCQ()){
							childs = teaser.children(":first");
						}

						var classArray = childs.attr("class").split(" ");
						var columnSize = getColumnSizeFromClassArray(classArray, currentDeviceType);

						if(columnSize < 12){
							teaser.find(".inner:first").innerHeight(maxHeight);
						}
					});

				});
			});
		};

		this.styleMasonryLayouts = function(element){
			var parentElement = (element) ? element : $("body");

			parentElement.find(".row-teaser.masonry").each(function(index, el) {
				var teaserRow = $(this);
				window.imagesLoaded(teaserRow, function(){
					teaserRow.isotope({
						itemSelector : (Helper.isCQ()) ? '.textImageTeaser > *, .newsListTeaser > *, .downloadListTeaser > *' : '.textImageTeaser, .newsListTeaser, .downloadListTeaser',
						layoutMode: 'masonry'
					});
				});
			});
		};

		//loadFonts();
	}

	// create global instance of helper class
	window.StyleHelper = new StyleHelper();


})(jQuery);
