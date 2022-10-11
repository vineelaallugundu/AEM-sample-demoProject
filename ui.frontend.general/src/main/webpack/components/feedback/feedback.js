$(document).ready(function(){
    $("#leaveFeedackButton").click(function(e) {
        $('#feedbackBlock').toggle();
    });
    $("#feedbackSubmit").click(function(e) {
        var hcapres = $('[name=h-captcha-response]').val();
        if (hcapres != null) {
			$.ajax({
                type: "POST",
                url: '/bin/hcaptcha',
                async: false,
                dataType: 'json',
                data: { 
                    hCaptchaResponse: hcapres,
                },
                success: function(data) {
                    console.log("Success function");
                    if (data.success == true) {
                        nop = true;
                    } else {
                        nop = false;
                    }
                },
                fail: function(textStatus, errorThrown) {
                    console.log(textStatus, errorThrown);
                }
            });
        }

        if(nop){
            e.preventDefault();
            $.ajax({
                type: "POST",
                url: "/bin/feedback",
                data: { 
                    feedbackData: $("#feedbackText").val(),
                    url: window.document.location.href
                },
                success: function(result) {
                    console.log('Feedback received successfully!');
                    location.reload();
                },
                error: function(result) {
                    console.log('error');
                }
            });
        }
    });
});    