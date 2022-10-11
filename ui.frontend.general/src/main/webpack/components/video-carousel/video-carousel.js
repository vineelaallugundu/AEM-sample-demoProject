
playCarouselVideo = (element,event) => {
    if(Array.from(event.srcElement.classList).some(r=> ["custom-menu","custom-menu__share-button", "fa-share"].indexOf(r) >= 0)) {
        event.stopPropagation();
        return;
    }
    if(element.querySelector("video").controls == false || ((event.srcElement.classList.length == 0) || !event.srcElement.classList.contains("video-player"))) {
        if (element.querySelector("video")) {
            element.querySelector("video").controls = true;
            element.querySelector("video").play();
        }
        if (element.querySelector(".play-button")) {
            element.querySelector(".play-button").remove();
        } 
    }
};
videoCarouselShareToggle = (element,event) => {
    var videoSource = element.dataset.videoUuid;
    $.ajax({
        type: "GET",
        url: `/bin/getvideoid?videopath=${videoSource}`,
        async: false,
        dataType: 'text',
        success: function(uuid) {
            navigator.clipboard.writeText(`http://${window.location.hostname}/video?id=${uuid}`);
            
        },
        fail: function(textStatus, errorThrown) {
            console.log("failure fn");
        }
    });
};