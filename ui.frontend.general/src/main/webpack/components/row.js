function initialize() {
    var elements = document.querySelectorAll('.row-wrapper');
    for(const element of elements) {
        var cssClassName = element.className;
        var cssArray = cssClassName.split(' ');
        var desktopHeight = '';
        var tabletHeight = '';
        var mobileHeight = '';
        for (const value of cssArray) {
            if(value != undefined && value.indexOf('desktop') != -1) {
                desktopHeight = value.substring(7);
                if(window.innerWidth >= 768 && desktopHeight != '') {
                    $('.row .row-wrapper').height(desktopHeight);
                }
            } else if(value != undefined && value.indexOf('tablet') != -1) {
                tabletHeight = value.substring(6);
                if((window.innerWidth < 768 && window.innerWidth >=576) && tabletHeight != '') {
                    $('.row .row-wrapper').height(tabletHeight);
                }
            } else if(value != undefined && value.indexOf('mobile') != -1) {
                mobileHeight = value.substring(6);
                if(window.innerWidth < 576 && mobileHeight != '') {
                    $('.row .row-wrapper').height(mobileHeight);
                }
            }
        }
    }
}

$(document).ready(function() {
    initialize();
});

module.exports = {initialize};
