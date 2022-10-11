$(document).ready(function() {

    const urlWebsite = window.location.origin;

    window.onkeydown = function( event ) {
        const key = event.key;
        if (key === "Escape"){
            document.querySelector(".search-toggle").classList.remove("d-none");
            document.querySelector(".primary-nav > ul").classList.remove("d-none");
            document.querySelector(".wholesale-search").classList.add("d-none");
        }
        if (key === "Enter"){
            result = document.getElementById('inputbox').value;
            if (result && result.trim().length > 0) {
                console.log("ENTER search now-->"+result);
                const searchPage = urlWebsite + '/content/wholesale/us/en/search-result.html?search=';
                window.open(searchPage+result, '_blank');
            }else if (result.trim().length === 0 || result === null || result === undefined){
                      const searchPage = urlWebsite + '/content/wholesale/us/en/404.html';
                      window.location.href = searchPage;
                    }
        }
    };

    if(document.querySelector(".search-toggle")) {
        document.querySelector(".search-toggle").addEventListener("click", (event) => {
            event.target.classList.add("d-none");
            document.querySelector(".primary-nav > ul").classList.add("d-none");
            document.querySelector(".wholesale-search").classList.remove("d-none");
            document.querySelector(".wholesale-search").style.display = "inline-block";
        });
    }

    $('.search-toggle').click(function () {
        $('#inputbox').focus();
    });

    $('[id^=clickSearch]').on('click', function(e) {
        if (document.getElementById('inputbox')) {
        result = document.getElementById('inputbox').value;
        if (result && result.trim().length > 0) {
            const searchPage = urlWebsite + '/content/wholesale/us/en/search-result.html?search=';
            window.open(searchPage + result, '_blank');
        }else if (result.trim().length === 0 || result === null || result === undefined){
                 const searchPage = urlWebsite + '/content/wholesale/us/en/404.html';
                 window.location.href = searchPage;
                 }
    }
    });

    if (document.querySelector(".cmp-search__input")) {
        document.querySelector(".cmp-search__input").addEventListener("blur", (event) => {
            if (event.relatedTarget == null) {
                document.querySelector(".search-toggle").classList.remove("d-none");
                document.querySelector(".primary-nav > ul").classList.remove("d-none");
                document.querySelector(".wholesale-search").classList.add("d-none");
                console.log("inside cmp-search__input Event stop");
            }
        });
    }
});
