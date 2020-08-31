function clearAllErrorsSpan() {
    $('.errorSpan').text('');
}

$(document).ready(function () {
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
    //Кнопка delete справа в таблиці
    $('td .delete.eDelBtn').on('click', function (event) {
        event.preventDefault();
        $('#deleteTipsAndTricksModal').modal();
        var href = $(this).attr('href');
        $('#deleteOneSubmit').attr('href', href);
    });
    //Кнопка addTipsAndTricks зверху таблиці
    $('#addTipsAndTricksModalBtn').on('click', function (event) {
        clearAllErrorsSpan();
    });
    //Кнопка delete в deleteTipsAndTricksModal
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
    //Кнопка delete в deleteAllSelectedModal
    $('#deleteAllSubmit').on('click', function (event) {
        event.preventDefault();
        var checkbox = $('table tbody input[type="checkbox"]');
        var payload = [];
        checkbox.each(function () {
            if (this.checked) {
                payload.push(this.value);
            }
        });
        var href = '/management/tipsandtricks/deleteAll';
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
    //Кнопка submit в модальній формі Add
    $('#submitAddBtn').on('click', function (event) {
        event.preventDefault();
        clearAllErrorsSpan();
        var formData = $('#addTipsAndTricksForm').serializeArray().reduce(function (obj, item) {
            obj[item.name] = item.value;
            return obj;
        }, {});
        var payload = {
            "id": formData.id,
            "title": formData.title,
            "text": formData.text,
            "image": formData.image,
            "source": formData.source,
            "tags": [],
            "file": formData.file
        };
        for (var key in formData) {
            if (key.startsWith("tags") && formData["tags"].trim().length !== 0) {
                var tag = formData["tags"].toString().split(/[\s,]+/);
                for (var i = 0; i < tag.length; i++) {
                    payload.tags.push(tag[i]);
                }
            }
        }
        //запит save у модальній формі add
        $.ajax({
            url: '/management/tipsandtricks/',
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
        });
    });
    //Кнопка edit справа в таблиці
    $('td .edit.eBtn').on('click', function (event) {
        event.preventDefault();
        $("#editTipsAndTricksModal").each(function () {
            $(this).find('input.eEdit').val("");
        });
        clearAllErrorsSpan();
        $('#editTipsAndTricksModal').modal();
        var href = $(this).attr('href');
        $.get(href, function (tipsandtricks, status) {
            $('#id').val(tipsandtricks.id);
            $('#title').val(tipsandtricks.title);
            $('#text').val(tipsandtricks.text);
            $('#emailAuthor').val(tipsandtricks.emailAuthor);
            $('#imagePath').val(tipsandtricks.imagePath);
            $('#source').val(tipsandtricks.source);
            $('#tags').val(tipsandtricks.tags);
            $('#file').val(tipsandtricks.file);
        });
    });
    //Кнопка submit в модальній формі Edit
    $('#submitEditBtn').on('click', function (event) {
        event.preventDefault();
        clearAllErrorsSpan();
        var formData = $('#editTipsAndTricksForm').serializeArray().reduce(function (obj, item) {
            obj[item.name] = item.value;
            return obj;
        }, {});
        var returnData = {
            "id": formData.id,
            "title": formData.title,
            "text": formData.text,
            "emailAuthor": formData.emailAuthor,
            "imagePath": formData.imagePath,
            "source": formData.source,
            "tags": [],
            "file": formData.file
        }
        for (var key in formData) {
            if (key.startsWith("tags") && $("#" + key).val().trim().length !== 0) {
                var tag = $("#" + key).val().split(/[\s,]+/);
                for (var i = 0; i < tag.length; i++) {
                    returnData.tags.push(tag[i]);
                }
            }
        }
        //запит save у модальній формі update
        $.ajax({
            url: '/management/tipsandtricks/',
            type: 'put',
            dataType: 'json',
            contentType: 'application/json',
            success: function (data) {
                if (Array.isArray(data.errors) && data.errors.length) {
                    data.errors.forEach(function (el) {
                        $(document.getElementById('errorModalUpdate' + el.fieldName)).text(el.fieldError);
                    })
                } else {
                    location.reload();
                }
            },
            data: JSON.stringify(returnData)
        });
    })
});
