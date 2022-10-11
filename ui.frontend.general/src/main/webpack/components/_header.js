var rtime;
var timeout = false;
var delta = 200;
var clicked = true;
var countVar = 1;
var countContrastVar = 1;

function toggleMenu(classExists) {
    if (!classExists){
        $('#left-nav').addClass('display');
        $('#top-nav').addClass('slide-right');
    } else {
        $('#left-nav').removeClass('display');
        $('#top-nav').removeClass('slide-right');
    }
}

function checkNavState() {
    let classExists = false;
    if (document.getElementById('left-nav')) {
        var classList = document.getElementById('left-nav').className.split(/\s+/);
        for (var i = 0; i < classList.length; i++) {
            if (classList[i] === 'display') {
                classExists = true;
            }
        }
    }
    return classExists;
}

function resizeEnd() {
    if (new Date() - rtime < delta) {
        setTimeout(resizeEnd, delta);
    } else {
        timeout = false;
        if (window.innerWidth >= 992) {
            if (checkNavState()) {
                toggleMenu(true);
            }
            nullCheck("#top-nav__spacer", () => { document.querySelector("#top-nav__spacer").classList.remove("d-none"); });
        } else {
            nullCheck("#top-nav__spacer", () => { document.querySelector("#top-nav__spacer").classList.add("d-none"); });
            
        }
        setTopNavSpacing();
    }
}

setTopNavSpacing = () => {
    if (document.querySelector("#top-nav") && document.querySelector("#top-nav__spacer")) {
        document.querySelector("#top-nav__spacer").style.height = `${document.querySelector("#top-nav").offsetHeight}px`;
    }
}

nullCheck = (selectorString, callBackFunction) => {
    if (document.querySelector(selectorString)) {
        callBackFunction();
    }
}

function initialize() {

    const expirationDuration = 1000 * 60 * 60 * 24;  //24 Hours
    const currentTime = new Date().getTime();
    const prevChanged = localStorage.getItem('lasttimeupdated');

    const notYetChanged = prevChanged == undefined;
    const prevChangedExpired = prevChanged != undefined && currentTime - prevChanged > expirationDuration;

    if (notYetChanged || prevChangedExpired) {
        localStorage.removeItem('accessibility');
    }

    const accessibilityClass = localStorage.getItem('accessibility');
    if (accessibilityClass == null) {
        $('html').addClass("contrast-1 font-size-1");
    }
    if(accessibilityClass !== ''){
        $('html').addClass(accessibilityClass);
    }

    window.addEventListener('resize', function() {
        rtime = new Date();
        if (timeout === false) {
            timeout = true;
            setTimeout(resizeEnd, delta);
        }
    }, true);
}

$(document).ready(function() {
    if (document.querySelector("#menu-button")) {
        document.querySelector("#menu-button").addEventListener("click", () => { toggleMenu(checkNavState()) })
    }
    setTopNavSpacing();
    initialize();
});

/* function showDiv() {
    if (clicked) {
        $('#kb-menu').addClass('active');
        document.querySelectorAll(".popup").forEach(a=>a.style.display = "inline");
        clicked = false;
    } else {
        $('#kb-menu').removeClass('active');
        document.querySelectorAll(".popup").forEach(a=>a.style.display = "none");
        clicked = true;
    }
} */

/* function increaseFontSize() {
    if (countVar < 5) {
        countVar++;
    }
    var elements = document.getElementsByTagName('html');

    for (var i=0; i<elements.length; i++) {
        var nameOfClass = elements[i].className.substring(0, elements[i].className.length-1);
        if (countVar <= 5) {
            elements[i].className = nameOfClass + countVar;
        }
        localStorage.setItem('accessibility', elements[i].className);
        const currentTime = new Date().getTime();
        localStorage.setItem('lasttimeupdated', currentTime);
    }
}

function decreaseFontSize() {
    if (countVar > 1) {
        countVar--;
    }
    var elements = document.getElementsByTagName('html');

    for (var i=0; i<elements.length; i++) {
        var nameOfClass = elements[i].className.substring(0, elements[i].className.length-1);
        if (countVar < 5) {
            elements[i].className = nameOfClass + countVar;
        }
        localStorage.setItem('accessibility', elements[i].className);
        const currentTime = new Date().getTime();
        localStorage.setItem('lasttimeupdated', currentTime);
    }
}

function increaseContrastValue() {
    if (countContrastVar < 5) {
        countContrastVar++;
    }
    var elements = document.getElementsByTagName('html');
    var nameOfClass = elements[0].className;
    var parts = nameOfClass.split(" ");

    for (var i=0; i<parts.length; i++) {
        var match = /contrast/.exec(parts[i]);
        if (match) {
            if (countContrastVar <= 5) {
                var updateContrast = parts[i].substring(0, parts[i].length-1 );
                parts[i] = updateContrast + countContrastVar;
                contrastUpdated = parts.join(" ");
                elements[0].className = contrastUpdated;
            }
            localStorage.setItem('accessibility', elements[i].className);
            const currentTime = new Date().getTime();
            localStorage.setItem('lasttimeupdated', currentTime);
        }

    }

}

function decreaseContrastValue() {
    if (countContrastVar > 1) {
        countContrastVar--;
    }
    var elements = document.getElementsByTagName('html');
    var nameOfClass = elements[0].className;
    var parts = nameOfClass.split(" ");

    for (var i=0; i<parts.length; i++) {
        var match = /contrast/.exec(parts[i]);
        if (match) {
            if (countContrastVar < 5) {
                var valueToUpdate = parts[i].substring(0, parts[i].length-1 );
                parts[i] = valueToUpdate + countContrastVar;
                contrastUpdated = parts.join(" ");
                elements[0].className = contrastUpdated;
            }
            localStorage.setItem('accessibility', elements[i].className);
            const currentTime = new Date().getTime();
            localStorage.setItem('lasttimeupdated', currentTime);
        }

    }

} */

function goHome() {
        window.location.href = "/";
}

module.exports = {initialize, goHome, toggleMenu, checkNavState, resizeEnd};
