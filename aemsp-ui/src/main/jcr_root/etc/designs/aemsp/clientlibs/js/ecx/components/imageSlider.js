var imageSlider = {

    init: function () {

        var _this = this;

        //number of slide to move on click of next/prev buttons
        _this.moveSlides = 1;

        _this.imageSlider = [];

        _this.showSlides = 3;

        _this.resizeObj = '';

        if(DeviceSelector.getDeviceType() == 'xs'){

            //if mobile show only 1 item at time
            _this.showSlides = 1;

        }

        if($('.imageSlider:visible').length){

            imagesLoaded($('.imageSlider:visible img'), function(){

                var ident = 0;

                $.each($('.imageSlider:visible'), function(){

                    var obj = $(this);

                    obj.attr('id', 'imageSlider_'+ident);

                    _this.buildCarousel(obj);

                    ident++;

                });

            });


        }

    },
    buildCarousel: function(obj){

        var _this = this;

        var autoplay = obj.data('auto');
        var pause = obj.data('pause');
        var startSlide = 0;

        var itemCount = obj.find('.carousel-content section article').size();

        obj.find('.carousel-content section article').addClass('show');

        if(itemCount > 2){

            var slideItemWidth = obj.find('.carousel-content section').width()/_this.showSlides;

            var count = _this.imageSlider.length;

            var touchEnabled = false;

            if(DeviceSelector.getDeviceType() != 'lg'){

                touchEnabled = true;

            }

            obj.find('img').attr('draggable','false');

            _this.imageSlider[count] = obj.find('.carousel-content section').bxSlider({
                slideSelector: 'article',
                pager: false,
                minSlides: _this.showSlides,
                maxSlides: _this.showSlides,
                slideWidth: slideItemWidth,
                nextText: '',
                prevText: '',
                slideMargin: 10,

                touchEnabled: touchEnabled,

                auto: autoplay,
                pause: pause,
                startSlide: startSlide,

                autoHover: true,
                moveSlides: _this.moveSlides,
                onSliderLoad: function(){

                    $('.bx-clone figure a').each(function(){

                        $(this).removeAttr('rel').removeClass('fancybox').addClass('fancybox-clone');

                    });

                    $('.bx-clone figure a').click(function(event){
                        event.preventDefault();
                        urlOfImage = $(this).attr('href');
                        $(this).parents('.imageSlider').find('article').not(".bx-clone").has('a[href|="'+urlOfImage+'"]').find('a').trigger('click');
                    });

                },
                onSlideBefore: function(){

                },
                onSlideAfter: function(){

                }

            });

        }else{

            obj.find('.carousel-content section article').addClass('static');

        }

    },
    updateOnResize: function(){

        if($('.imageSlider').length){

            var _this = this;

            for(var i=0;i<_this.imageSlider.length;i++){

                _this.imageSlider[i].destroySlider();

            }

            _this.showSlides = 3;

            if(DeviceSelector.getDeviceType() == 'xs'){

                _this.showSlides = 1;

            }

            setTimeout(function(){

                $.each($('.imageSlider'), function(){

                    var obj = $(this);

                    obj.find('article').removeClass('show');

                    _this.buildCarousel(obj);

                });

            }, 200);

        }

    }

};
