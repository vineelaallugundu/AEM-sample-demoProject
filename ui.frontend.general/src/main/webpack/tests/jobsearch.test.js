const jobs = require('../components/jobsearch/jobsearch');

beforeAll(() => {
    global.jQuery = require('jquery');
});

it('should mock ajax inner width', () => {
    document.body.innerHTML=
        `<div class="jobs-form">
        <div class="jobs-container">
            <input type="text" id="name" class="form-control form-input" placeholder="Search for opportunities">
            <em id="search" class="fas fa-search"></em>
                    <div class="no-data" style="display: none;">No results found</div>
                    <div class="pagination" style="display: none;"></div>
                    <div id="results" role="alert"></div>
        </div>
    </div>`;
    var result = document.getElementById('name').value;
    const ajaxSpy = jest.spyOn($, "ajax");
    jobs.myFunction();
});

test("test handle success: ", () => {
    document.body.innerHTML=
        `<div class="jobs-form">
        <div class="jobs-container">
            <input type="text" id="name" class="form-control form-input" placeholder="Search for opportunities">
            <em id="search" class="fas fa-search"></em>
                    <div class="no-data" style="display: none;">No results found</div>
                    <div class="pagination" style="display: none;"></div>
                    <div id="results" role="alert"></div>
        </div>
    </div>`;
    result = [
        {
            "title": "DevOps Engineer",
            "city": "Littleton",
            "state": "CO",
            "applyUrl": "https://attract-careers1-dish.icims.com/jobs/52702/login"
        },
        {
            "title": "DevOps Engineer",
            "city": "Littleton",
            "state": "CO",
            "applyUrl": "https://attract-careers1-dish.icims.com/jobs/51403/login"
        },
        {
            "title": "Sr. 5G Automation Engineer- Wireless Systems",
            "city": "Littleton",
            "state": "CO",
            "applyUrl": "https://attract-careers1-dish.icims.com/jobs/54058/login"
        },
        {
            "title": "Cloud Engineer",
            "city": "Littleton",
            "state": "CO",
            "applyUrl": "https://attract-careers1-dish.icims.com/jobs/54555/login"
        },
        {
            "title": "VMware Engineer",
            "city": "Littleton",
            "state": "CO",
            "applyUrl": "https://attract-careers1-dish.icims.com/jobs/52539/login"
        },
        {
            "title": "VMware Engineer",
            "city": "Cheyenne",
            "state": "WY",
            "applyUrl": "https://attract-careers1-dish.icims.com/jobs/52545/login"
        },
        {
            "title": "SRE Engineer",
            "city": "Littleton",
            "state": "CO",
            "applyUrl": "https://attract-careers1-dish.icims.com/jobs/54430/login"
        },
        {
            "title": "Voice Traffic Engineer",
            "city": "Littleton",
            "state": "CO",
            "applyUrl": "https://attract-careers1-dish.icims.com/jobs/51393/login"
        },
        {
            "title": "Senior Transport Engineer",
            "city": "Littleton",
            "state": "CO",
            "applyUrl": "https://attract-careers1-dish.icims.com/jobs/51384/login"
        },
        {
            "title": "Principal Systems Engineer",
            "city": "Herndon",
            "state": "VA",
            "applyUrl": "https://attract-careers1-dish.icims.com/jobs/54533/login"
        }
    ];
    // const logSpy = jest.spyOn(console, "log");
    jobs.handleSuccess(result);
    expect(result.length).toBe(10);
});

test("test length zero for jobs", () => {
    document.body.innerHTML=
        `<div class="jobs-form">
        <div class="jobs-container">
            <input type="text" id="name" class="form-control form-input" placeholder="Search for opportunities">
            <em id="search" class="fas fa-search"></em>
                    <div class="no-data" style="display: none;">No results found</div>
                    <div class="pagination" style="display: none;"></div>
                    <div id="results" role="alert"></div>
        </div>
    </div>`;
    result = "";
    jobs.handleSuccess(result);
    expect(result.length).toBe(0);
});

test("search ready", () => {
    $("em").trigger("click");
    jobs.search();
});
