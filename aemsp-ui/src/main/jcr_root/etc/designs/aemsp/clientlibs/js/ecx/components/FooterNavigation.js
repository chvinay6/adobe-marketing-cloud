var footerNavigation = {

    init: function () {

        var _this = this;

        if($('.module-footerNavigation').size())  {

            var currentBreakpoint = DeviceSelector.getDeviceType();

			if(currentBreakpoint == "xs") {
				_this.toggleFooterNav();
			}

        }

    },
    toggleFooterNav: function(){

        $(".module-footerNavigation .footer-linklist").hide();

		$(".module-footerNavigation h2").click(function(){

            var _this = $(this);

            _this.toggleClass('open');
			_this.next('.footer-linklist').slideToggle();

		});

    }

};
