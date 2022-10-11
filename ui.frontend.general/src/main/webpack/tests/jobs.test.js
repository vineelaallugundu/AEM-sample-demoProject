const jobs = require('../components/_jobs');

beforeAll(() => {
    global.jQuery = require('jquery');
});

it('should check ajax', () => {
    document.body.innerHTML=
        `<div className="buttons">
            <a href="javascript:void(0)" id="next">Next</a>
            <div id="results" role="alert"></div>
        </div>`;
    jobs.loadCurrentPage();
});


test("test length zero for jobs", () => {
    document.body.innerHTML=
        `<div className="buttons">
            <a href="javascript:void(0)" id="next">Next</a>
            <div id="results" role="alert"></div>
        </div>`;
    result = "";
    jobs.handleSuccess(result);
    expect(result.length).toBe(0);
});

test("check click of next", () => {
        document.body.innerHTML =
        `<div className="buttons">
            <a href="javascript:void(0)" id="next">Next</a>
        </div>`;
    jobs.navClick();
    expect(document.getElementById('next').value).toBeUndefined();
});

test("compute ready", () => {
    _this = `<div className="buttons">
            <a href="javascript:void(0)" id="next">Next</a>
        </div>`;
     $("a").trigger("click");
    jobs.computePage();
    expect(document.getElementById('Next')).toBeNull();
});


test("starting initialize", () => {
    jobs.starting();
});