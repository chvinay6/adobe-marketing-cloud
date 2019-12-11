var toTopButton = {

    init: function () {

        if($('.to-top').children('a').length){

           	this.action();

        }

    },
    action: function(){

        var _this = this;

        // click event to scroll to top (smooth)
        $('.to-top').children('a').on("click", function(){
            $('html, body').animate({scrollTop : 0}, 1000);
            return false;
        });

    }

}
