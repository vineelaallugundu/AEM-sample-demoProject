function checkboxClick() {
    var checkbox = $('.wholesale-checkbox').children('.cmp-form-options__field--checkbox');
    checkbox.attr("checked") ? checkbox.attr("checked", false) : checkbox.attr("checked", 'true');
}

$(document).ready(function() {
    $('.wholesale-checkbox').click(function() {
         checkboxClick();
    });
});


module.exports = {checkboxClick};


