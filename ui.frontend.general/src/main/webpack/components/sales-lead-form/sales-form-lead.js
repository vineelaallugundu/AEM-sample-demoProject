$(document).ready(function() {
    $("#startDate").attr("min", new Date().toISOString().split("T")[0]);

    $('#phone').on('input', function () {
        var number = $(this).val().replace(/[^\d]/g, '');
        if (number.length == 7) {
            number = number.replace(/(\d{3})(\d{4})/, "$1-$2");
        } else if (number.length == 10) {
            number = number.replace(/(\d{3})(\d{3})(\d{4})/, "($1) $2-$3");
        }
        $(this).val(number);
    });
    
    const form = document.querySelector('.cmp-sales-lead form');

    if (form != null) {
        const mail = document.getElementById('email');
    
        dateFormat = function(value, event) {
            if(value === '') {
                $('#endDate').val('');
            }
            const newValue = value.replace(/[^0-9]/g, '').replace(/(\..*)\./g, '$1');
            const dayOrMonth = (index) => index % 2 === 1 && index < 4;
            // on delete key.  
            if (!event.data) {
                return value;
            }
            return newValue.split('').map((v, i) => dayOrMonth(i) ? v + '/' : v).join('');
        }
    
        $('#dummyframe').on('load', function() {
            document.getElementById("myPopupSuccess").classList.remove("d-none");     
            setTimeout(function() {
                document.getElementById("myPopupSuccess").classList.add("d-none");
                $("#myForm")[0].reset();
            }, 5000); 
        });
    
        checkStart = function(value) {
            var today = new Date().setHours(0, 0, 0, 0);
            var userDate = new Date(value).setHours(0, 0, 0, 0);
    
            var presentDay = new Date();
            var dd = String(presentDay.getDate()).padStart(2, '0');
            var mm = String(presentDay.getMonth() + 1).padStart(2, '0'); //since January is 0.
            var yyyy = presentDay.getFullYear();
            presentDay = mm + '/' + dd + '/' + yyyy;
    
            if(userDate < today) {
                var errorMessage = document.getElementById("error-msg3");
                errorMessage.innerHTML="Please select a date after today i.e. "+ presentDay;
                document.getElementById('error-msg3').classList.remove("d-none");   
                $('#startDate').val('');
            } else {
                document.getElementById('error-msg3').classList.add("d-none"); 
            }
            if(!/^\d{1,2}\/\d{1,2}\/\d{4}$/.test(value)) {
                document.getElementById('error-msg').classList.remove("d-none");
                $('#startDate').val('');   
            } else {
                document.getElementById('error-msg').classList.add("d-none");
            }
            var parts = value.split("/");
            var day = parseInt(parts[1], 10);
            var month = parseInt(parts[0], 10);
            var year = parseInt(parts[2], 10);
    
            if(year < 1000 || year > 3000 || month == 0 || month > 12) {
                document.getElementById('error-msg').classList.remove("d-none");
                $('#startDate').val('');
            } else {
                document.getElementById('error-msg').classList.add("d-none");
            }
            var monthLength = [ 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 ];
            // Adjust for leap years
            if(year % 400 == 0 || (year % 100 != 0 && year % 4 == 0))
                monthLength[1] = 29;
            // Check the range of the day
            return day > 0 && day <= monthLength[month - 1];
        }
    
        function validate(event) {
            event.preventDefault();
            hcaptcha.execute();
            setTimeout(function(){ buttonClicked() }, 1000);
            
        }
        
        emailValidation = () => {
            const email = $('#email').val();
            var emailValidation = validateEmail(email);
            if (email === "") {
                // If it isn't, we display an appropriate error message
                mail.setCustomValidity("Please fill out this field");
                return false;
            } else if (emailValidation === false) {
                mail.setCustomValidity("Please enter valid emailId");
                return false;
    
            } else {
                mail.setCustomValidity("");
                return true
            }
        };
    
        var firstInput = true;
        form.addEventListener('input', function(event) {
            if(firstInput){
                window.adobeDataLayer = window.adobeDataLayer || [];
                window.adobeDataLayer.push({
                    event: 'form_start',
                        form: {
                        formDetails: {
                            formStart: {
                                value: 1
                            },
                            formName: 'sales lead'
                        }
                    }
                });
                firstInput = false;
            }
        });
    
    
        email.oninput = function() {
            const regex = /^[a-zA-Z0-9.+_-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,15}$/;
    
            if ($('#email').val() === "") {
                mail.setCustomValidity("Please fill out this field");
            } else if (!regex.test($('#email').val())) {
                mail.setCustomValidity("Please enter valid email");
            } else {
                mail.setCustomValidity("");
            }
        };
    
        emailValidation();
    }
});

validateEmail = (email) => {
    const regex = /^[a-zA-Z0-9.+_-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,15}$/;
    return regex.test(email);
}  

startLoader = (elem) => {
    document.getElementById("site-loader").classList.remove("d-none");
    elem.click();
}

onSubmit = (token) => {
    document.getElementById("site-loader").classList.add("d-none");
    var form = document.getElementsByTagName('form')[0];
    console.log(form);
    var hcapres = $('[name=h-captcha-response]').val();
    if (hcapres != null && form.reportValidity()) {
        document.getElementById("site-loader").classList.remove("d-none");
        $.ajax({
                type: "POST",
                url: '/bin/hcaptcha',
                async: false,
                dataType: 'json',
                data: { 
                    hCaptchaResponse: hcapres,
                },
                success: function(data) {
                    document.getElementById("site-loader").classList.add("d-none");
                    if (form.checkValidity() && data.success) {
                        window.adobeDataLayer = window.adobeDataLayer || [];
                        window.adobeDataLayer.push({
                            event: 'form_complete',
                                form: {
                                formDetails: {
                                    formComplete: {
                                        value: 1
                                    },
                                    formName: 'sales lead'
                                }
                            }
                        });
                        document.getElementById('myForm').submit();
                    } else {
                        console.log('invalid submission');
                    }
                },
                error: function(textStatus, errorThrown) {
                    document.getElementById("site-loader").classList.add("d-none");
                    document.getElementById("myPopupError").classList.remove("d-none");
                    setTimeout(function() {
                        document.getElementById("myPopupError").classList.add("d-none");
                    }, 5000); 
                    console.log(textStatus, errorThrown);
                    return false;
                }
            });
    }
}

showMore = (inputElement) => {
    

    switch(inputElement.id) {
        case "spectrum-leasing": {
            if (inputElement.checked) {
                $("#desired-spectrum").val("CBRS");
                $("#spectrum-leasing-expanded input").each(function() {
                    this.required = true;
                });
                document.getElementById("spectrum-leasing-expanded").classList.remove("d-none");
            } else {
                $("#desired-spectrum").val("");
                $("#spectrum-leasing-expanded input").each(function() {
                    this.required = false;
                });
                document.getElementById("spectrum-leasing-expanded").classList.add("d-none");
            }
            break;
        }
        case "private-network" :
        case "wholesale-wireless" : { 
            if (isDefaultSelected()) {
                /* document.getElementById("unknown-section").classList.remove("d-none");
                $("#unknown-section input").each(function() {
                    this.required = true;
                }); */
            } else {
                /* document.getElementById("unknown-section").classList.add("d-none");
                $("#unknown-section input").each(function() {
                    this.required = false;
                }); */
            }
        }
        break;
        default : {

            
        }
        break;
    }
}

isDefaultSelected = () => {
    var defaultSelected = false;
    checkBoxCounter = 0;
    $(".interests__checkbox").each(function(index, element) {
        switch(element.id) {
            case "private-network" :
            case "wholesale-wireless" : {
                if (element.checked) { defaultSelected = true; }
                else {
                    checkBoxCounter++;
                }
            }
            break;
        }
    });
    if (checkBoxCounter == 2) { defaultSelected = false; }
    return defaultSelected;
}

function isUSAZipCode(str) 
{
  return /^\d{5}(-\d{4})?$/.test(str);
}

function validateInput(input) 
{
  const zipCode = input.value;
  const message = "";
  if (isUSAZipCode(zipCode)) 
  {
    document.getElementById("error-msg-zip").classList.add("d-none");
  } else {
    input.value = "";
    document.getElementById("error-msg-zip").classList.remove("d-none");
  }
}
