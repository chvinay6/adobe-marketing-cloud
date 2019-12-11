(function ($) {

	"use strict";

	// Constructor
	function ContentCarouselHelper () {
		//default bootstrap carousel with multiple items per viewport
		//data-type: multi for grid items (col-3 * 4 or col-4 * 3)

		var lastDeviceType = null; // save last deviceType (breakpoint) to compare on resize



		this.init = function() {
			var $carousel = $('.carousel');
			//var $this = $(this);
			$carousel.each(function() {

				var $this = $(this);

				if ($this.data('type') == "multi") {

					imagesLoaded($this.find('img'), function() {
						var deviceType = DeviceSelector.getDeviceType();
						var itemCount;

						// define col settings
						switch (deviceType) {
							case 'xs' :
								itemCount = 1;
								break;
							case 'sm' :
								itemCount = 3;
								break;
							case 'md' :
								itemCount = 3;
								break;
							case 'lg' :
								itemCount = 3;
								break;
							default :
								itemCount = 3;
						}

						// if cols' are unequal
						if ($this.hasClass('unequal')) {
							if (itemCount > 1) {
								itemCount = itemCount - 1;
							}
						}

						// do we have more than one item?
						if (itemCount > 1) {

							$this.find('.item').each(function () {
								//var this = $(this).sibling(':first');
								var $_this = $(this);
								var itemChildCount = $_this.children().length;

								// if window size grow
								if (itemChildCount < itemCount) {
									var $next = $_this.next();

									if (!$next.length) { // no .next? go to first
										$next = $_this.siblings(':first');
									}

									// clone next into this item
									$next.children(':first-child').clone().appendTo($_this);

									var itemCountSub = 2;
									if (itemChildCount == 2 && itemCount == 4) {
										itemCountSub = 3;
									}

									if (itemCount > 2) {
										// clone all other items into this
										for (var i = 0; i < itemCount - itemCountSub; i++) {
											$next = $next.next();
											if (!$next.length) { // no .next? go to first
												$next = $_this.siblings(':first');
											}
											// clone them into this item
											$next.children(':first-child').clone().appendTo($_this);
										}
									}

									// if window size slow down
								} else {
									var startCount = itemCount - 1;
									$_this.children(':gt(' + startCount + ')').remove();
								}
							});

						// only one item
						} else {
							$this.find('.item').each(function () {
								// remove other items and only keep one
								$(this).children(':gt(0)').remove();
							});
						}

						// move carousel item back or forward to show the right item in centered position (active)
						// takes only effect on first init when not mobile and on change of deviceType (breakpoint) between mobile and other deviceTypes
						if (lastDeviceType == null || (deviceType != lastDeviceType && (deviceType == "xs" || lastDeviceType == "xs"))) {

							// Mobile
							if (deviceType == "xs" && itemCount == 1 && lastDeviceType != null) {
								var thisItemLength = $this.find('.item').length;

								if (thisItemLength > 2) {
									$this.carousel('next');
								}
							}

							// Other Breakpoints
							else if (itemCount > 1) {
								$this.carousel('prev');
							}
						}

						// save current device type
						lastDeviceType = DeviceSelector.getDeviceType();

						// trigger forward/backward navigation on not active images, if there are 3 images in one view
						if(itemCount == 3) {
							$this.find('.item').each(function() {
								var subItems = $(this).children(".poster");

								subItems.eq(0).addClass("trigger-left");
								subItems.eq(2).addClass("trigger-right");

								// go to prev item
								subItems.eq(0).on("click", function() {
									if(DeviceSelector.getDeviceType() != "xs") {
										$this.find(".carousel-control.left").trigger("click");
									}
								});

								// go to next item
								subItems.eq(2).on("click", function() {
									if(DeviceSelector.getDeviceType() != "xs") {
										$this.find(".carousel-control.right").trigger("click");
									}
								});

							});
						}

					});

				} // end data-type="multi"

				// enable lazyload - lazysizes for picture (data-|srcset) elements
				// <picture><source data-srcset="..."> + .lazyload css class for the <img>
				// see https://github.com/aFarkas/lazysizes
				// is on by default
				// turn it with data-lazyload="false" offset
				//
				// check if data-lazyload is not false and lazySizesConfig variable exist (so plugin is loaded)
				if ($this.data("lazyload") !== "false") {
					//console.log("lazyload init");
					if (lazySizesConfig) {
						//$this.find("img").addClass("lazyload"); //should be done in markup
						var $itemActive = $this.find('.item.active');
						var imageSelector = 'img';

						//$itemActive.find(imageSelector).addClass('lazypreload');

						if ($this.data("type") != "multi") {
							//console.log("not a multi item");
							var $itemActiveNext = $itemActive.next('.item');
							var $itemActivePrev = $itemActive.prev('.item');

							if ($itemActiveNext.length < 1) $itemActiveNext = $this.find('.item:first-child');
							if ($itemActivePrev.length < 1) $itemActivePrev = $this.find('.item:last-child');

							$itemActiveNext.find(imageSelector).addClass('lazypreload');
							$itemActivePrev.find(imageSelector).addClass('lazypreload');
						}

						// bootstrap slid not slide to call in progress
						$this.on('slid.bs.carousel', function (left) {
							//console.log("add preload to prev item");
							$itemActive = $this.find('.item.active');
							$itemActivePrev = $itemActive.prev('.item');
							$itemActivePrev.find(imageSelector).addClass('lazypreload');
						});

						$this.on('slid.bs.carousel', function (right) {
							//console.log("add preload to next item");
							$itemActive = $this.find('.item.active');
							$itemActiveNext = $itemActive.next('.item');
							$itemActiveNext.find(imageSelector).addClass('lazypreload');
						});

					}else{
						console.warn("lazysize plugin not loaded");
					}

				} // end data-lazyload

			});


		};

		this.resize = function () {
			this.init();
		};
	}


	// create global instance of helper class
	window.ContentCarouselHelper = new ContentCarouselHelper();

})(jQuery);
