function clearAllErrorsSpan() {
    $('.errorSpan').text('');
}

function sendAjaxDeleteRequest(href, payload) {
    payload = payload !== undefined ? payload : {};
    $.ajax({
        url: href,
        type: 'delete',
        dataType: 'json',
        contentType: 'application/json',
        success: function () {
            location.reload();
        },
        data: JSON.stringify(payload)
    });
}

function sendAjaxPostRequest(payload) {
    $.ajax({
        url: '/management/advices/',
        type: 'post',
        dataType: 'json',
        contentType: 'application/json',
        success: function (data) {
            if (Array.isArray(data.errors) && data.errors.length) {
                data.errors.forEach(function (el) {
                    console.log(el.fieldName);
                    $(document.getElementById('errorModalSave' + el.fieldName)).text(el.fieldError);
                })
            } else {
                location.reload();
            }
        },
        data: JSON.stringify(payload)
    });
}

function sendAjaxPutRequest(payload, adviceId) {
    $.ajax({
        url: `/management/advices/${adviceId}`,
        type: 'put',
        dataType: 'json',
        contentType: 'application/json',
        success: function (data) {
            if (Array.isArray(data.errors) && data.errors.length) {
                data.errors.forEach(function (el) {
                    console.log(el.fieldName);
                    $(document.getElementById('errorModalUpdate' + el.fieldName)).text(el.fieldError);
                })
            } else {
                location.reload();
            }
        },
        data: JSON.stringify(payload)
    });
}

function getCheckedValues(checkbox) {
    var payload = [];
    checkbox.each(function () {
        if (this.checked) {
            payload.push(parseInt(this.value));
        }
    });

    return payload;
}

function getDataFromForm(formId) {
    return $(formId).serializeArray().reduce(function (obj, item) {
        obj[item.name] = item.value;
        return obj;
    }, {});
}

function getTranslationsFormFormData(formData) {
    var translations = [];
    for (var key in formData) {
        if (key.startsWith("content")) {
            var contentAndLanguage = key.split("_");
            var langId = contentAndLanguage[1];
            var langCode = contentAndLanguage[2];
            var content = formData["content_" + langId + "_" + langCode];
            translations.push({
                "content": content,
                "language": {
                    "code": langCode,
                    "id": langId
                }
            });
        }
    }

    return translations;
}

function composePayloadFromFormData(formData) {
    return {
        "habit": {
            id: formData.habit
        },
        "translations": getTranslationsFormFormData(formData)
    };
}

$(document).ready(function () {
    $('[data-toggle="tooltip"]').tooltip();

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

    $('td .edit.eBtn').on('click', function (event) {
        event.preventDefault();
        $("#editAdviceModal").each(function () {
            $(this).find('input.eEdit').val("");
        });
        clearAllErrorsSpan();
        $('#editAdviceModal').modal();
        var href = $(this).attr('href');
        $.get(href, function (advice, status) {
            $('#id').val(advice.id);
            $('#habit').val(advice.habit.id);
            advice.translations.forEach(function (translation, index) {
                $(`#content_${translation.language.id}_${translation.language.code}`)
                    .val(translation.content);
            })
        });
    });

    $('#addAdviceModalBtn').on('click', function (event) {
        clearAllErrorsSpan();
    });

    $('td .delete.eDelBtn').on('click', function (event) {
        event.preventDefault();
        $('#deleteAdviceModal').modal();
        var href = $(this).attr('href');
        $('#deleteOneSubmit').attr('href', href);
    });

    $('#deleteOneSubmit').on('click', function (event) {
        event.preventDefault();
        var href = $(this).attr('href');
        sendAjaxDeleteRequest(href);
    });

    $('#deleteAllSubmit').on('click', function (event) {
        event.preventDefault();
        var href = '/management/advices/deleteAll';
        var payload = getCheckedValues(checkbox);
        sendAjaxDeleteRequest(href, payload);
    });

    $('#submitAddBtn').on('click', function (event) {
        event.preventDefault();
        clearAllErrorsSpan();
        var formData = getDataFromForm('#addAdviceForm');
        var payload = composePayloadFromFormData(formData);
        console.log(payload);
        sendAjaxPostRequest(payload);
    });

    $('#submitEditBtn').on('click', function (event) {
        event.preventDefault();
        clearAllErrorsSpan();
        var formData = getDataFromForm('#editAdviceForm');
        var adviceId = formData.id;
        var payload = composePayloadFromFormData(formData);
        sendAjaxPutRequest(payload, adviceId);
    })
});

