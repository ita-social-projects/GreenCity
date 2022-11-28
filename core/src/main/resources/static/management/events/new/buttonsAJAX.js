function clearAllErrorsSpan() {
    $('.errorSpan').text('');
}

function validateTags() {
    const checkedCount = $("input[name='tags[]']:checked").get().length;

    const tagsValid = checkedCount > 0;
    if (!tagsValid) {
        $("#tags-invalid-feedback").css("display", "block");
        $('#tags-form-group').css("border", "1px solid red");
    } else {
        $("#tags-invalid-feedback").css("display", "none");
        $('#tags-form-group').css("border", "none");
    }
    return tagsValid;
}

function validateDescription() {
    const max = 63206;
    const min = 20;
    const numChars = tinymce.activeEditor.plugins.wordcount.body.getCharacterCount();
    const isDescValid = numChars >= min && numChars < max;

    if (!isDescValid) {
        $("#description-invalid-feedback").css("display", "block");
        $('#description-form-group').css("border", "1px solid red");
    } else {
        $("#description-invalid-feedback").css("display", "none");
        $('#description-form-group').css("border", "none");
    }
    return isDescValid;
}

function postEvent(event) {
    clearAllErrorsSpan();

    let form = document.getElementById("addEventsForm")

    const tagsValid = validateTags();
    const descriptionValid = validateDescription();

    if (!form.checkValidity() || !tagsValid || !descriptionValid) {
        form.classList.add('was-validated')
        return false;
    }
    form.classList.add('was-validated')

    let formData = $('#addEventsForm').serializeArray().reduce(function (obj, item) {
            obj[item.name] = item.value;
            return obj;
        },
    );

    const payload = {
        "title": document.getElementById("addTitle").value,
        "description": tinyMCE.activeEditor.getContent(),
        "open": true,
        "tags": $('.tag-checkbox:checkbox:checked').get().map(checkbox => checkbox.value),
        "datesLocations": [{
            "startDate": new Date(formData.startDate).toJSON(),
            "finishDate": new Date(formData.finishDate).toJSON(),
            "coordinates": {
                "latitude": latitude,
                "longitude": longitude
            },
            "onlineLink": formData.onlineLink,
        }],
    };

// Ajax request
    $.ajax({
            url: '/management/events/create',
            type: 'post',
            dataType: 'json',
            contentType: 'application/json',
            success: function (data) {
                if (Array.isArray(data.errors) && data.errors.length) {
                    data.errors.forEach(function (el) {
                        $(document.getElementById('errorModalSave' + el.fieldName)).text(el.fieldError);
                    })
                } else {
                    location.reload();
                }
            },
            data: JSON.stringify(payload)
        }
    );
    location.reload();
}

let map;
let markers = [];
let latitude;
let longitude;

function initMap() {
    const mapCenter = {lat: 49.842957, lng: 24.031111};
    map = new google.maps.Map(document.getElementById("map"), {
        zoom: 12,
        center: mapCenter
    });
    // This event listener will call addMarker() when the map is clicked.
    map.addListener("click", (event) => {
        deleteMarkers();
        addMarker(event.latLng);
        latitude = document.getElementById('latitude').value = event.latLng.lat();
        longitude = document.getElementById('longitude').value = event.latLng.lng();
    });
    // Adds a marker at the center of the map.
    addMarker(mapCenter);
}

// Adds a marker to the map and push to the array.
function addMarker(location) {
    let marker = new google.maps.Marker({
        position: location,
        map: map,
    });
    markers.push(marker);
    var requestOptions = {
        method: 'GET',
    };
}

function deleteMarkers() {
    for (let i = 0; i < markers.length; i++) {
        markers[i].setMap(null);
    }
    markers = [];
}

$('#submitAddBtn').on('click', function (event) {
    let form = document.getElementById("addEventsForm");
    if (form.checkValidity() === false) {
        event.preventDefault();
        event.stopPropagation();
    }
    form.classList.add("was-validated");
});

$(document).ready(function () {

    // Activate tooltip
    $('[data-toggle="tooltip"]').tooltip();

    // Select/Deselect checkboxes
    let checkbox = $('table tbody input[type="checkbox"]');
    $("#selectAll").on('click', function () {
        if (this.checked) {
            checkbox.each(function () {
                if (!this.disabled) {
                    this.checked = true;
                }
            });
        } else {
            checkbox.each(function () {
                this.checked = false;
            });
        }
    });
    checkbox.on('click', function () {
        if (!this.checked) {
            $("#selectAll").prop("checked", false);
        }
    });

    // Add place button (popup)
    $('#addPlaceModalBtn').on('click', function (event) {
        clearAllErrorsSpan();
        clearEditModal();
        $('#submitAddBtn').val("Add");
        $('.modal-title').text("Add Place");
    });

    // Ajax request
    $('td .delete.eDelBtn').on('click', function (event) {
        event.preventDefault();
        $('#deletePlaceModal').modal();
        let href = $(this).attr('href');
        $('#deleteOneSubmit').attr('href', href);
    });

    //delete в deletePlaceModal
    $('#deleteOneSubmit').on('click', function (event) {
        event.preventDefault();
        let href = $(this).attr('href');
        $.ajax({
            url: href,
            type: 'delete',
            dataType: 'json',
            contentType: 'application/json',
            success: function (data) {
                location.reload();
            },
        });
    });
    //delete в deleteAllSelectedModal
    $('#deleteAllSubmit').on('click', function (event) {
        event.preventDefault();
        let payload = [];
        checkbox.each(function () {
            if (this.checked) {
                payload.push(this.value);
            }
        })
    });
});
