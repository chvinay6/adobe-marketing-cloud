(function ($) {

	"use strict";

	// Constructor
	function DataHelper () {


    // private
    var $element = $('body');

    this.getCurrentPageUrl = function(){
      return $element.data('url');
    };

  }

  // create global instance of helper class
  window.DataHelper = new DataHelper();


})(jQuery);
