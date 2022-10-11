$(document).ready(function() {
    const queryString = window.location.search;
    const hrefString = window.location.href;
    const urlParams = new URLSearchParams(queryString);
    const search = urlParams.get('search');
//START of Ajax .....
    $('#wait').show();
    var se = search;
    if ((se) && (queryString.length>8)) {
        $.ajax({
            type: "GET",
            url: "/content/wholesale/us/en/search-result.dishsearchresults.json",
            headers: {
                Accept: "application/json; charset=utf-8",
                "Content-Type": "application/json; charset=utf-8"
            },
            data: {fulltext: se},
            success: function (result) {
                var jobLength = result.length;
                var a = '';
                if (jobLength < 1 || jobLength == undefined) {

                    $('.no-data').show();
                    if (document.getElementById("nosearchtext") !== null) {
                        document.getElementById("nosearchtext").innerHTML = 'No results for " ' + decodeURI(decodeURI(se)) + ' " ';
                    }
                    if (document.getElementById("nosearchpara") !== null) {
                        document.getElementById("nosearchpara").innerHTML = "Sorry we couldn't find what you're looking for. Try looking in these content pages.";
                    }
                    a = '<div class="items-list"><div class="cf-button neutral-filled float-right">' +
                        '<a href="/content/wholesale/us/en/leadership.html" role="button" target="_blank" class="">Go To</a></div><div class="item-gmt">Leadership<br></div></div>' +
                        '<div class="items-list"><div class="cf-button neutral-filled float-right">' +
                        '<a href="/content/wholesale/us/en/home/about-us.html" role="button" target="_blank" class="">Go To</a></div><div class="item-gmt">About-Us<br></div></div></div>';
                    if (document.getElementById("nosearchresultsubstitute") !== null) {
                        document.getElementById("nosearchresultsubstitute").innerHTML = a;
                    }
                } else {
                    $('.no-data').hide();
                    if (document.getElementById("searchtext") !== null) {
                        document.getElementById("searchtext").innerHTML = 'Search results for " ' + se + ' " ';
                    }
                    if (document.getElementById("searchcount") !== null) {
                        document.getElementById("searchcount").innerHTML = jobLength + ' results. ';
                    }
                }
                $('#wait').hide();
                $('#results').show();
                $('.pagination').show();
                var string1 = JSON.stringify(result);
                var parsed = JSON.parse(string1);
                var s = '';
                for (var i = 0; i < jobLength; i++) {
                    s += '<div class="items-list">'
                        + '<div class="cf-button neutral-filled float-right"><a href="' + parsed[i].url + '" id="apply" role="button" target="_blank" class="" >Go To</a></div>'
                        + '<div class="item-gmt">' + parsed[i].title + '<br></div></div>';
                }
                if (document.getElementById("resultsview") !== null) {
                    document.getElementById("resultsview").innerHTML = s;
                }
            }
        });
    } else if(((hrefString.indexOf('/search-result') > 0) && ((queryString.length === 8)))  && (queryString.trim().length === 0 || queryString ===null || queryString === undefined )) {
    $('#wait').hide();
    const urlWebsite = window.location.origin;
    const searchPage = urlWebsite + '/content/wholesale/us/en/404.html';
    window.location.href = searchPage;
    }
});
