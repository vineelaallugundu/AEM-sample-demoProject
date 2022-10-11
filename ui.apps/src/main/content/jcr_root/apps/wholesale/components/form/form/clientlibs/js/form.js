$(document).ready(function() {
	var cookieConsentForm = $('.cookie-consent');
    if(cookieConsentForm.length > 0) {
        var submit = cookieConsentForm.find('button[type="submit"]');
        cookieConsentForm.find('button[type="submit"]').click(function(event) {
            event.preventDefault();
			var cookieConsent = $('.cookie-consent').find('input[name="optout"]');
            if(cookieConsent.length > 0) {
                if(cookieConsent.attr('checked') == 'checked') {
					$.removeCookie("uoOut");
                    $('.form-success').removeClass('d-none');
                    $('.cookie-consent').addClass('d-none');
                } else {
                    var date = new Date();
                    date = date.setFullYear(date.getFullYear()+10);
                    $.cookie("uoOut", "true", { expires: date });
                    $('.form-success').removeClass('d-none');
                    $('.cookie-consent').addClass('d-none');
                }
            }
        })
    }
})