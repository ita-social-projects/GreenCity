function clearAllErrorsSpan(){
    $('.errorSpan').text('');
}

$(document).ready(function() {
    // Activate tooltip
    $('[data-toggle="tooltip"]').tooltip();

    // Select/Deselect checkboxes
    var checkbox = $('table tbody input[type="checkbox"]');
    $("#selectAll").click(function () {
        if (this.checked) {
            checkbox.each(function () {
                this.checked = true;
            });
        } else {
            checkbox.each(function () {
                this.checked = false;
            });
        }
    });
    checkbox.click(function () {
        if (!this.checked) {
            $("#selectAll").prop("checked", false);
        }
    });

    //delete button in deleteSocialNetworkImagesModal
    $('#deleteOneSubmit').on('click', function (event) {
        event.preventDefault();
        var href = $(this).attr('href');
        $.ajax({
            url: href,
            type: 'delete',
            dataType: 'json',
            contentType: 'application/json',
            success: function (data) {
                location.reload();
            }
        });
    });

    //delete button on the right in the table
    $('td .delete.eDelBtn').on('click', function (event) {
        event.preventDefault();
        $('#deleteSocialNetworkImagesModal').modal();
        var href = $(this).attr('href');
        $('#deleteOneSubmit').attr('href', href);
    });

    //delete button in deleteAllSelectedModal
    $('#deleteAllSubmit').on('click', function (event) {
        event.preventDefault();
        var checkbox = $('table tbody input[type="checkbox"]');
        var payload = [];
        checkbox.each(function () {
            if (this.checked) {
                payload.push(this.value);
            }
        });
        var href = '/management/socialnetworkimages/deleteAll';
        $.ajax({
            url: href,
            type: 'delete',
            dataType: 'json',
            contentType: 'application/json',
            success: function (data) {
                location.reload();
            },
            data: JSON.stringify(payload)
        });
    });

    //submit button in addSocialNetworkImagesModal
    $('#submitAddBtn').on('click', function (event) {
        event.preventDefault();
        clearAllErrorsSpan();
        var formData = $('#addSocialNetworkImagesForm').serializeArray().reduce(function (obj, item) {
            obj[item.name] = item.value;
            return obj;
        }, {});
        var payload = {
            "id": formData.id,
            "hostPath": formData.hostPath,
            "imagePath": formData.imagePath
        };
        var result = new FormData();
        result.append("socialNetworkImageRequestDTO", new Blob([JSON.stringify(payload)], {type: "application/json"}));
        var file = document.getElementById("creationFile").files[0];
        result.append("file", file);
        //save request in addEcoNewsModal
        $.ajax({
            url: '/management/socialnetworkimages/',
            type: 'post',
            contentType: false,
            processData: false,
            dataType: "json",
            cache: false,
            success: function (data) {
                if (Array.isArray(data.errors) && data.errors.length) {
                    data.errors.forEach(function (el) {
                        $(document.getElementById('errorModalSave' + el.fieldName)).text(el.fieldError);
                    })
                } else {
                    location.reload();
                }
            },
            data: result
        });
    });

    //edit button on the right in the table
    $('td .edit.eBtn').on('click', function (event) {
        event.preventDefault();
        $("#editSocialNetworkImagesModal").each(function () {
            $(this).find('input.eEdit').val("");
        });
        clearAllErrorsSpan();
        $('#editSocialNetworkImagesModal').modal();
        var href = $(this).attr('href');
        $.get(href, function (socialnetworkimage, status) {
            $('#id').val(socialnetworkimage.id);
            $('#hostPath').val(socialnetworkimage.hostPath);
            $('#imagePath').val(socialnetworkimage.imagePath);
            $('#file').val(econews.file);
        });
    });
    //submit button in editEcoNewsModal
    $('#submitEditBtn').on('click', function (event) {
        event.preventDefault();
        clearAllErrorsSpan();
        var formData = $('#editSocialNetworkImagesForm').serializeArray().reduce(function (obj, item) {
            obj[item.name] = item.value;
            return obj;
        }, {});
        var returnData = {
            "id": formData.id,
            "hostPath": formData.hostPath,
            "imagePath": formData.imagePath
        };

        var result = new FormData();
        result.append("socialNetworkImageResponseDTO", new Blob([JSON.stringify(returnData)], {type: "application/json"}));
        var file = document.getElementById("file").files[0];
        result.append("file", file);
        //save request in editEcoNewsModal
        $.ajax({
            url: '/management/socialnetworkimages/',
            type: 'put',
            contentType: false,
            processData: false,
            dataType: "json",
            cache: false,
            success: function (data) {
                if (Array.isArray(data.errors) && data.errors.length) {
                    data.errors.forEach(function (el) {
                        $(document.getElementById('errorModalUpdate' + el.fieldName)).text(el.fieldError);
                    })
                } else {
                    location.reload();
                }
            },
            data: result
        });
    })

});
