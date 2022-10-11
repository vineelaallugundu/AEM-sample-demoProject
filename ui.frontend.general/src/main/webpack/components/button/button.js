
// This foreach may not be necessary. You can set value of input src directly with UUID without JS.
Array.from(document.getElementsByClassName("textToShow")).forEach(element => {
	const video = document.querySelector(`#${element.dataset.modalId} video`);
	element.innerHTML = `${window.location.origin}/video?id=`;
});


copyToClip = (element) => {
	const copyText = document.querySelector(`#${element.dataset.modalId} .textToShow`);
	copyText.select();
	copyText.setSelectionRange(0, 99999); 
	navigator.clipboard.writeText(copyText.value);
}

videoShareToggle = (element, displayType)  => {
	var shareModal = document.querySelector(`#${element.dataset.modalId} .share-modal`);
	var videoSource = element.dataset.videoUuid;

	if (shareModal) {
		shareModal.style.display = displayType;
	}
	
	const modalClose = document.querySelector(`#${element.dataset.modalId} .dish-close`);
	const shareButton = document.querySelector(`#${element.dataset.modalId} .custom-menu__share-button`);

	shareButton.disabled = !shareButton.disabled
	modalClose.disabled = !modalClose.disabled;

	if (displayType == "none") {
		document.querySelector(`#${element.dataset.modalId} .share-modal__link`).innerHTML = `${window.location.origin}/video?id=`;
	} else {
		setCopyLink(element, videoSource);
	}
}
  
setCopyLink = (element, videoSource) => {
	$.ajax({
		type: "GET",
		url: `/bin/getvideoid?videopath=${videoSource}`,
		async: false,
		dataType: 'text',
		success: function(uuid) {
			const shareUrl = document.querySelector(`#${element.dataset.modalId} .share-modal__link`).innerHTML.toString();
			document.querySelector(`#${element.dataset.modalId} .share-modal__link`).innerHTML = shareUrl + uuid;
		},
		fail: function(textStatus, errorThrown) {
			console.log("failure fn");
		}
	});
}

var support = (function () {
	if (!window.DOMParser) {
		return false;
	}
	var parser = new DOMParser();
	try {
		parser.parseFromString('x', 'text/html');
	} catch(err) {
		return false;
	}
	return true;
})();

function stringToHTML(str) {
	// If DOMParser is supported, use it
	if (support) {
		var parser = new DOMParser();
		var doc = parser.parseFromString(str, 'text/html');
		return doc.body;
	}

	// Otherwise, fallback to old-school method
	var dom = document.createElement('div');
	dom.innerHTML = str;
	return dom;
};

const videos = document.querySelectorAll(".button-container--video");

if (videos) {
	Array.from(videos).forEach((video) => {
		const button = video.querySelector(".cmp-button--video");
		const videoFragment = video.querySelector(".dish-modal--video");

		button.addEventListener("click", (event) => {
			const modalID = event.target.id.replace("__button", '__modal');
			document.body.append(stringToHTML(`<div class="dish-overlay fadeIt model-open"></div>`));
			const videoModalFragment = document.getElementById(modalID);
			videoModalFragment.classList.remove("d-none");
			videoModalFragment.querySelector("video").play();
		});
		document.body.append(videoFragment);
	});
}


const videoModals = document.querySelectorAll(".dish-modal--video");

if (videoModals) {
	Array.from(videoModals).forEach((modal) => {
		modal.querySelector(".dish-close").addEventListener("click", () => {
			modal.classList.add("d-none");
			modal.querySelector("video").pause();
			document.querySelector(".dish-overlay").remove();
		});
	});
}

$(document).ready(function() {

	var buttonModal = $('button.button-modal');
	var buttonLink = $('button.button-link');

	// When the user clicks on the button, open the modal
	$(buttonModal).click(function() {
		var cookieConsentForm = $('.cookie-consent');
		if(cookieConsentForm.length > 0) {
			var cookieConsentCheckbox = cookieConsentForm.find('input[name="optout"]');
			if(cookieConsentCheckbox.length > 0) {
				$.cookie('uoOut') == 'true' ? 
				cookieConsentCheckbox.prop('checked', true) : 
				cookieConsentCheckbox.prop('checked', false);
			}
		}
		var modalBox = $(buttonModal).parent().next('.modal-box:first');
		$('.wholsesale-wireless-form').removeClass('d-none');
		$(modalBox).removeClass('d-none');
		$(modalBox).addClass('d-block');
	});

	// When the user clicks on <button> (x), close the modal
	$('.modal-content .dish-close').click(function() {
		var modalBox = $(buttonModal).parent().next('.modal-box');
		$('.form-success').addClass('d-none');
		$('.form-failure').addClass('d-none');
		$(modalBox).removeClass('d-block');
		$(modalBox).addClass('d-none');
	});

	$(buttonLink).click(function() {
		var href = $(this).attr('data-href');
		if(href != undefined && href != '') {
			if(href.startsWith('/content/dam')) {
                window.open($(this).attr('data-href'), '_blank');
            } else if(href.startsWith('/content')) {
                window.open($(this).attr('data-href').concat('.html'), '_blank');
            } else if(href.startsWith('http')) {
				window.open($(this).attr('data-href'), '_blank');
			} else {
				window.open('http://' + $(this).attr('data-href'), '_blank');
			}

		}
	});

});
