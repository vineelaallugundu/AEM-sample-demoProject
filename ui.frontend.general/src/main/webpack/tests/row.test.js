const row = require('../components/row');

beforeAll(() => {
    global.jQuery = require('jquery');
});

test('row gets desktop height', () => {
    global.innerWidth = 500;
    global.outerWidth = 800;
    window.innerWidth = 769;
    window.outerWidth = 450;
    document.body.innerHTML=
        `<body style="width: 150px;" >
        <div style="width: 150px;">
            <div class="row-wrapper mobile tablet desktop110px" style="width: 150px;"></div>
        </div>
        <div>
            <div class="row-wrapper mobile tablet desktop120px" style="width: 150px;"></div>
        </div>
    </body>`;
    var result = document.querySelectorAll('.row-wrapper');
    var original = $.prototype.width;
    jest.spyOn($.prototype, 'width').mockImplementation(function() {
        if (this[0] === window) {
            return 5;
        } else {
            return original.apply(this, arguments);
        }
    });
    jest.spyOn(document, 'querySelectorAll').mockImplementation((selector) => {
        switch (selector) {
            case '.row-wrapper':
                return result;
        }
    });
    row.initialize();
    expect(document.querySelectorAll).toBeCalledTimes(1);
    expect(window.innerWidth).toBe(769);
});

test('should mock tablet width', () => {
    jest.resetAllMocks();
    jest.restoreAllMocks();
    global.innerWidth = 900;
    window.innerWidth = 577;
    document.body.innerHTML=
        `<body >
        <div>
            <div class="row-wrapper mobile tablet577px desktop" ></div>
        </div>
        <div>
            <div class="row-wrapper mobile tablet577px desktop" ></div>
        </div>
    </body>`;
    var result = document.querySelectorAll('.row-wrapper');
    jest.spyOn(document, 'querySelectorAll').mockImplementation((selector) => {
        switch (selector) {
            case '.row-wrapper':
                return result;
        }
    });
    row.initialize();
    expect(document.querySelectorAll).toBeCalledTimes(1);
    expect(window.innerWidth).toBe(577);
});


test('should mock mobile width', () => {
    jest.restoreAllMocks();
    global.innerWidth = 800;
    window.innerWidth = 400;
    document.body.innerHTML=
        `<body >
        <div>
            <div class="row-wrapper mobile400px tablet desktop" ></div>
        </div>
        <div>
            <div class="row-wrapper mobile400px tablet desktop" ></div>
        </div>
    </body>`;
    var result = document.querySelectorAll('.row-wrapper');
    jest.spyOn(document, 'querySelectorAll').mockImplementation((selector) => {
        switch (selector) {
            case '.row-wrapper':
                return result;
        }
    });
    row.initialize();
    expect(document.querySelectorAll).toBeCalledTimes(1);
    expect(window.innerWidth).toBe(400);
});

test(" also for document ", () => {
    jest.restoreAllMocks();
    var result = $(document).ready();
    jest.spyOn($(document), 'ready').mockImplementation(() => {return result});
});

