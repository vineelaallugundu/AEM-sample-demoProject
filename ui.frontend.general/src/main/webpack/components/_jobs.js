let currentPage = 0;
let _this;

function handleSuccess(result) {
    $('#wait').hide();
    $('#prev').show();
    $('#next').show();
    var string1 = JSON.stringify(result);
    var parsed = JSON.parse(string1);
    var joblistLength = parsed.length;
    var s = '';
    for (var i=0; i < joblistLength; i++) {
        s+= '<div class="items-list">'
            + '<div class="cf-button neutral-filled float-right"><a href="' + parsed[i].applyUrl +'" id="apply" role="button" target="_blank" class="" >Apply</a></div>'
            + '<div class="item-title">' + parsed[i].title + '<br></div>'
            + '<div class="location">' + parsed[i].city + ', ' + parsed[i].state + '<br></div></div>';
    }
    if (document.getElementById("results") !== null) {
        document.getElementById("results").innerHTML = s;
    }
}

function loadCurrentPage(){
const queryString = window.location.search;
const hrefString = window.location.href;
const jobForm = document.querySelector(".jobs-form");
if ((!(hrefString.indexOf('/search-result') > 0) && (jobForm))) {
    $.ajax({
        type : "GET",
        url : "/sling/servlet/default.jobs.json",
        headers : {
            Accept : "application/json; charset=utf-8",
            "Content-Type" : "application/json; charset=utf-8"
        },
        data : { pageNum : currentPage },
        success : function(result) {
            handleSuccess(result);
        }
    });
    } else if(((hrefString.indexOf('/search-result') > 0) && (queryString.length === 8)) ||
     ((hrefString.indexOf('/search-result') > 0) && (queryString.length === 0))){
            $('#wait').hide();
            const urlWebsite = window.location.origin;
            const searchPage = urlWebsite + '/content/wholesale/us/en/404.html'
            window.location.href = searchPage;
    }
}

function computePage() {
    $('#wait').show();
    currentPage = (_this.attr('id')=='next') ? currentPage + 10 : currentPage - 10;
    loadCurrentPage();
    if(currentPage === 0) {
        $('#prev').addClass('disabled');
    }
    if(currentPage > 0) {
        $('#prev').removeClass('disabled');
    }
}

function navClick() {
    $("#next, #prev").click(function(){
        _this = $(this);
        computePage();
    });
}

function starting() {
    $('#prev').hide();
    $('#next').hide();
    $('#wait').show();
    if(currentPage === 0) {
        $('#prev').addClass('disabled');
    }
    loadCurrentPage();
    navClick();
}

$(document).ready(function() {
    starting();
});

module.exports = {loadCurrentPage, handleSuccess, computePage, navClick, starting};