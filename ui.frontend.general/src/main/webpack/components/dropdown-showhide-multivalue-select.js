function showHide(component, element) {
    // get the selector to find the target elements. its stored as data-.. attribute
    var target = $(element).data("cq-dialog-dropdown-showhide-target");
    var $target = $(target);

    if (target) {
        var value;
        if (typeof component.value !== "undefined") {
            value = component.value;
        } else if (typeof component.getValue === "function") {
            value = component.getValue();
        }
        $target.each(function() {
            $(this).not(".hide").addClass("hide");
            $(this).filter(function() {
                return $(this).data('showhidetargetvalue').replace(/ /g, '').split(',').includes(value);
            }).removeClass("hide");

        });
    }
}

function handlerCondition(element) {
    var hideElemExist = $(element).closest('.type-showhide-button-type').hasClass('hide');

    if(!hideElemExist){
        if ($(element).is("coral-select")) {
            // handle Coral3 base drop-down
            Coral.commons.ready(element, function(component) {
                showHide(component, element);
                component.on("change", function() {
                    showHide(component, element);
                });
            });
        } else {
            // handle Coral2 based drop-down
            var component = $(element).data("select");
            if (component) {
                showHide(component, element);
            }
        }
    }
}

function showHideHandler(el) {
    $(el).each(function(i, element) {
        handlerCondition(element);
    });

}

function condition() {
    if(($(".type-showhide-button-type").length>0)){
        setTimeout(function () {
            showHideHandler($(".cq-dialog-dropdown-showhide-multival"));
        }, 300);
    }
}

function firstCall() {
    $(document).on("foundation-contentloaded", function() {
        // if there is already an inital value make sure the according target element becomes visible
        condition();
    });
}

(function() {
    "use strict";
    // when dialog gets injected
    firstCall();
})(document, $);


module.exports = {showHide, showHideHandler, handlerCondition, condition};

