/**
 *
 * Extends checkbox component and provides show/hide capability for other
 * dialog components based on checkbox value. Supports validation toggle
 * based on visibility of components - prevents validation on hidden components.
 *
 * USAGE:
 * 1. create checkbox component and add class [class="cq-dialog-checkbox-showhide"]
 * 2. add data attribute to checkbox component with selector for elements that will be shown/hidden [showhide-target=".someSelectorClass" ]
 * 3. add class to target element that will be shown/hidden [class="someSelectorClass"]
 * 4. add conditional value of checkbox to target element [showhide-target-value="some-checkbox-value"]
 *
 * Made by RJ Spiker: http://aempodcast.com/2016/javascript/simple-touch-ui-dialog-extensions-aem/
 */
(function(document, $) {
    "use strict";

    /**
     * Listen for dialog injection with checkbox toggle
     */
    $(document).on("foundation-contentloaded", function(e) {
        $(".cq-dialog-checkbox-showhide").each(function() {
            showHide($(this));
        });
    });

    /**
     * Listen for checkbox toggle change
     */
    $(document).on("change", ".cq-dialog-checkbox-showhide", function(e) {
        showHide($(this));
    });

    /**
     * Show/hide target element depending on toggle state
     */
    function showHide(el) {
        var target = el.data("showhideTarget"),
            value = el.prop("checked") ? el.val() : "";

        // hide all targets by default
        $(target).not(".hide").addClass("hide");

        // show any targets with a matching target value
        $(target).filter("[data-showhide-target-value=\"" + value + "\"]").removeClass("hide");
    }
})(document, Granite.$);