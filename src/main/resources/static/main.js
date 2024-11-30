$(document).ready(function () {


// Select the parent <div> containing the buttons
    const buttonsContainer = document.querySelector('#button-collections');

// Loop through all the buttons within the container
    buttonsContainer.querySelectorAll('button').forEach(button => {
        button.addEventListener('click', (event) => {
            // console.log(`Button clicked: ${event.target.id}`);
            // Add additional logic here as needed
            cardButtonClicked(event.target.id);
        });
    });

    const draftNumber = $("#draftNumber");
    let draftNumberValue = localStorage.getItem("draftNumber");
    draftNumber.val(draftNumberValue);


    const captureNumber = $("#captureNumber");
    let captureNumberValue = localStorage.getItem("captureNumber");
    captureNumber.val(captureNumberValue);
    console.log(captureNumber);


    getDefaultData();
    clearDivsBackgroundColor();


});


function executeClicked() {


}

function clearDivsBackgroundColor() {
    const parentDiv = document.querySelector('.flex.flex-wrap.justify-center.space-x-1');
    const divsWithIdContainingDiv = Array.from(parentDiv.querySelectorAll('div[id*="div-"]'));

    divsWithIdContainingDiv.forEach(div => {
        div.addClass("bg-white");
        div.removeClass("bg-green-100");
    })
}

function winLoseClicked(e) {
    const captureNumber = $("#captureNumber");

    let captureNumberValue = captureNumber.val();


    if (e === "w") {
        captureNumberValue++;
    } else if (e === "l") {
        captureNumberValue--;
    } else if (e === "reset") {
        captureNumberValue = 0;
    }

    localStorage.setItem("captureNumber", captureNumberValue);
    captureNumber.val(captureNumberValue);
}

function cardButtonClicked(cardButtonCardId) {
    // Data to be sent in the AJAX request
    if (cardButtonCardId.includes("reset") ||
        cardButtonCardId.includes("execute") ||
        cardButtonCardId.includes("btn-w") ||
        cardButtonCardId.includes("btn-l")) {
        return;
    }

// jQuery AJAX call
    // Prepare data as an object
    const dataParams = {
        "div-1": $("#div-1").text(),
        "div-2": $("#div-2").text(),
        "div-3": $("#div-3").text(),
        "div-4": $("#div-4").text(),
        "div-5": $("#div-5").text(),
        "div-6": $("#div-6").text(),
        "div-7": $("#div-7").text(),
        "div-8": $("#div-8").text(),
        "div-9": $("#div-9").text(),
        "div-10": $("#div-10").text(),
        "div-11": $("#div-11").text(),
        "div-12": $("#div-12").text(),
        "div-13": $("#div-13").text(),
        "cardId": cardButtonCardId
    };

// Make the AJAX POST call
    $.ajax({
        url: '/api/cardClicked',  // Your endpoint URL
        method: 'POST',  // Use POST method
        // contentType: 'application/json',  // Specify that we're sending JSON data
        data: dataParams,  // Convert the object to a JSON string
        success: function (data) {
            console.log('Success:', data);
            for (let i = 0; i < data.length; i++) {
                let id = data[i].id;
                let content = data[i].content;
                if (id.includes("div-")) {
                    const div = $("#" + id);
                    div.text(content);
                    div.removeClass("bg-green-100");

                }


            }


            const div = $("#" + cardButtonCardId.replace("btn", "div").replace("-n", ""));
            div.addClass("bg-green-100");


        },
        error: function (xhr, status, error) {
            console.error('Error:', status, error);
        }
    });


}


function getDefaultData() {
    const draftNumber = $("#draftNumber");
    localStorage.setItem("draftNumber", draftNumber.val());

    $.ajax({
        url: '/api/default',
        method: 'GET',  // Use GET method
        data: {
            draft: draftNumber.val()
        },
        success: function (data) {
            // console.log('Success:', data);
            //divElement
            let divElement = data.divElement;
            for (let i = 0; i < divElement.length; i++) {
                let id = divElement[i].id;
                let content = divElement[i].content;
                if (id.includes("div-")) {
                    const div = $("#" + id);
                    div.text(content);
                    div.removeClass("bg-green-100");

                }
            }
            //     zoneResponse
            let zoneResponse = data.zoneResponse;

            $("#shenro").text(zoneResponse.shenro);
            $("#lucky").text(zoneResponse.lucky);
            $("#cloud").text(zoneResponse.cloud);
            $("#sss").text(zoneResponse.sss);
            $("#everythingUnderTheSun").text(zoneResponse.everythingUnderTheSun);
            $("#bluesky").text(zoneResponse.bluesky);
            $("#redsea").text(zoneResponse.redsea);
            $("#even").text(zoneResponse.even);
            draftNumber.val(zoneResponse.draftNumber);

             // winLoseClicked("reset");

        },
        error: function (xhr, status, error) {
            console.error('Error:', status, error);
        }
    });

}