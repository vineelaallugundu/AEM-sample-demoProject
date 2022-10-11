const options = require('../components/options');

beforeAll(() => {
    global.jQuery = require('jquery');
});

it('should mock checkbox', () => {
    const div = `<div><div class="wholesale-checkbox"> <input class="cmp-form-options__field cmp-form-options__field--checkbox" name="checkbox"
                               value="checked" checked="false" disabled="false" type="checkbox"></input></div></div>`;
    options.checkboxClick();
});

test("compute ready", () => {
     document.body.innerHTML= `<div><div class="wholesale-checkbox"> <input class="cmp-form-options__field cmp-form-options__field--checkbox" name="checkbox"
                           value="checked" checked="false" disabled="false" type="checkbox"></input></div></div>`;
    $(".wholesale-checkbox").trigger("click");
});