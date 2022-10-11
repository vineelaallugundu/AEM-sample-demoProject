(function(document, $) {
    "use strict";
    // when dialog gets injected
    $(document).on("foundation-contentloaded", function(e) {
        // if there is already an inital value make sure the according target element becomes visible
        if(($(".type-showhide-button-type").length>0)){

                 setTimeout(function () {
                    showHideHandler($(".cq-dialog-dropdown-showhide-multival"),e.target);
                 }, 300);
        }
        else{

             showHideHandler($(".cq-dialog-dropdown-showhide-multival"),e.target);
        }
    });

    $(document).on("change", ".cq-dialog-dropdown-showhide-multival", function(e) {

        showHideHandler($(this));
    });

    function showHideHandler(el) {

        el.each(function(i, element) {
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
        })

    }

    function showHide(component, element) {
        // get the selector to find the target elements. its stored as data-.. attribute
        var target = $(element).data("cq-dialog-dropdown-showhide-target");
        var $target = $(target);
        var elementIndex = $(element).closest('hide action-showhide-target').index();

        if (target) {
            var value;
            if (typeof component.value !== "undefined") {
                value = component.value;
             } else if (typeof component.getValue === "function") {
                value = component.getValue();
            }
            $target.each(function(index) {
                    $(this).not(".hide").addClass("hide");
                    $(this).filter(function() {
                        return $(this).data('showhidetargetvalue').replace(/ /g, '').split(',').includes(value);
                    }).removeClass("hide");

            });
        }
    }

})(document, Granite.$);