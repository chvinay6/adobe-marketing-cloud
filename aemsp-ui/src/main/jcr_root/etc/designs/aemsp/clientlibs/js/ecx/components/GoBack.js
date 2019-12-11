var GoBack = {

    init: function () {

        var _this = this;
        var $goBack = $('.goBack');
        if($goBack.size() > 0){

            _this.bindEvents($goBack);

        }

    },
    bindEvents: function($element){
      var $goBackLink = $element.find("a");
      var referrerDomain = document.referrer.split("/")[2];
      var currentDomain = window.location.href.split("/")[2];
  		if (referrerDomain == currentDomain) {

          $goBackLink.click(function(e){
            window.history.back();
            e.preventDefault();
          });
      }else{

          $goBackLink.hide();
      }
    }
};
