var ActionBar = {

    init: function () {

        var _this = this;

        if($('.actionBar').size() > 0){

            _this.triggerPrint();

        }

    },
    triggerPrint: function(){

      $(".actionBar .print").click(function(){
      window.print();

    });

    }
};
