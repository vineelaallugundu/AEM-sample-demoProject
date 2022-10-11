$(document).ready(function() {
    $('.wholesale-checkbox').click(function() {
		var checkbox = $(this).children('.cmp-form-options__field--checkbox');
        checkbox.attr("checked") ? checkbox.attr("checked", false) : checkbox.attr("checked", 'true');
    })
});