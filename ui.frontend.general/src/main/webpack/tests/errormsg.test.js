const errorMsg = require('../components/errormsg');

beforeAll(() => {
    global.jQuery = require('jquery');
});

test("back", () => {
    errorMsg.back();
});