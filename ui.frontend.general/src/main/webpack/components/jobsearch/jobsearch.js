
function handleSuccess(result) {
    var jobLength = result.length;
    if(jobLength < 1 || jobLength == undefined) {
        $('.no-data').show();
    } else {
        $('.no-data').hide();
    }
    $('#wait').hide();
    $('#results').show();
    $('.pagination').show();
    var string1 = JSON.stringify(result);
    var parsed = JSON.parse(string1);

    var s = '';
    var size  = parsed.length/10;
    var count = 0;
    var k = 0;

    for (var j=0; j<Math.floor(size); j++) {
        while (count < 10) {
            count++;
            k++
        }
        count = 0
    }

    for (var i=0; i<jobLength; i++) {
        s+= '<div class="items-list">' + '<div class="item-title">' + parsed[i].title + '<br></div>'
            + '<div class="location">' + parsed[i].city + ', ' + parsed[i].state + '<br></div>'
            + '<a href="' + parsed[i].applyUrl +'" id="apply" role="button" target="_blank" class="btn btn-dark apply" >Apply</a></div>';
    }
    if (document.getElementById("results") !== null) {
        document.getElementById("results").innerHTML = s;
    }
}


 function myFunction() {
        $('#wait').show();
        var se = document.getElementById('name').value;
        $.ajax({
            type : "GET",
            url : "/sling/servlet/default.searchj.json",
            headers : {
                Accept : "application/json; charset=utf-8",
                "Content-Type" : "application/json; charset=utf-8"
            },
            data : {query : se},
            success : function(result) {
                handleSuccess(result);
            },
            error : function(XMLHttpRequest, textStatus, errorThrown) {
            console.log('added log statement to avoid ');
            }
        });
    }

 $(document).keypress(function (event) {
        if (event.keyCode == 13) {
            myFunction();
        }
    });

function search() {
    $('#search').on('click', function () {
        myFunction();
    })
}

$(document).ready(function () {
    $('#results').hide();
    $('.pagination').hide();
    $('.no-data').hide();

    $('#search').on('click', function() {
            myFunction(0);
        })
});
module.exports = {myFunction, handleSuccess, search};
