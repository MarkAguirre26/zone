$(document).ready(function () {


// Select the parent <div> containing the buttons
    const buttonsContainer = document.querySelector('#button-collections');

// Loop through all the buttons within the container
    buttonsContainer.querySelectorAll('button').forEach(button => {
        button.addEventListener('click', (event) => {
            // console.log(`Button clicked: ${event.target.id}`);
            // Add additional logic here as needed
            processEvent(event.target.id, $("#I7").val());
        });
    });

    processEvent("currentState", "");




});



function showLoadingDialog() {
    document.getElementById("loading-dialog").classList.remove("hidden");
}

// Function to hide the loading dialog
function hideLoadingDialog() {
    document.getElementById("loading-dialog").classList.add("hidden");
}



function processEvent(event, draft) {
    showLoadingDialog();
    $.ajax({
        url: '/api/process-request',
        method: 'GET',  // Use GET method
        data: {
            event: event,
            draft: draft
        },
        success: function (data) {
             // console.log('Success:', data);


            $("#C11").text(Math.trunc(data[0]) || ""); // data[0] corresponds to C11
            $("#D11").text(Math.trunc(data[1]) || ""); // data[1] corresponds to D11
            $("#E11").text(Math.trunc(data[2]) || ""); // data[2] corresponds to E11
            $("#F11").text(Math.trunc(data[3]) || ""); // data[3] corresponds to F11
            $("#G11").text(Math.trunc(data[4]) || ""); // data[4] corresponds to G11
            $("#H11").text(Math.trunc(data[5]) || ""); // data[5] corresponds to H11
            $("#I11").text(Math.trunc(data[6]) || ""); // data[6] corresponds to I11
            $("#J11").text(Math.trunc(data[7]) || ""); // data[7] corresponds to J11
            $("#K11").text(Math.trunc(data[8]) || ""); // data[8] corresponds to K11
            $("#L11").text(Math.trunc(data[9]) || ""); // data[9] corresponds to L11
            $("#M11").text(Math.trunc(data[10]) || ""); // data[10] corresponds to M11
            $("#N11").text(Math.trunc(data[11]) || ""); // data[11] corresponds to N11
            $("#O11").text(Math.trunc(data[12]) || ""); // data[12] corresponds to O11
            $("#P11").text(Math.trunc(data[13]) || ""); // data[13] corresponds to P11
            // $("#L18").val(data[14] || ""); // data[14] corresponds to L18
            // $("#M18").val(data[15] || ""); // data[15] corresponds to M18
            $("#wandL").text(Math.trunc(data[14]) +"/"+Math.trunc(data[15]) );

            $("#L22").val(Math.trunc(data[16]) || ""); // data[16] corresponds to L22
            $("#I7").val(Math.trunc(data[17]) || "");
            $("#I18").text(data[18] || ""); // data[17] corresponds to I18
            $("#I19").text(data[19] || ""); // data[18] corresponds to I19
            $("#I20").text(data[20] || ""); // data[19] corresponds to I20
            $("#I21").text(data[21] || ""); // data[20] corresponds to I21
            $("#I22").text(data[22] || ""); // data[21] corresponds to I22
            $("#I23").text(data[23] || ""); // data[22] corresponds to I23
            $("#I24").text(data[24] || ""); // data[23] corresponds to I24
            $("#I25").text(data[25] || ""); // data[24] corresponds to I25
            // $("#I7").text(Math.trunc(data[25]) || ""); // data[13] corresponds to I7
            hideLoadingDialog();
        },
        error: function (xhr, status, error) {
            console.error('Error:', status, error);
        }
    });

}