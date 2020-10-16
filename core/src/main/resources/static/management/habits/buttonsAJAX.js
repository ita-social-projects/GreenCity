var languages;
$.get('/factoftheday/languages', function (data) {
    languages = data;
});

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
        $('#deleteHabitModal').modal();
        var href = $(this).attr('href');
        $('#deleteOneSubmit').attr('href', href);
    });
    //Кнопка delete в deleteFactOfTheDayModal
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
            },
        });
    });
    //Кнопка delete в deleteAllSelectedModal
    $('#deleteAllSubmit').on('click', function (event) {
        event.preventDefault();
        var payload = [];
        checkbox.each(function () {
            if (this.checked) {
                payload.push(this.value);
            }
        })
        var href = '/management/habit/deleteAll';
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





    //Кнопка addFactOftheDay зверху таблиці
    $('#addFactOfTheDayModalBtn').on('click', function (event) {
        clearAllErrorsSpan();
    });
    //Кнопка submit в модальній формі Add
    $('#submitAddBtn').on('click', function (event) {
        event.preventDefault();
        clearAllErrorsSpan();
        var formData = $('#addHabitForm').serializeArray().reduce(function (obj, item) {
            obj[item.name] = item.value;
            return obj;
        }, {});
        var returnData = {
            "id": formData.id,
            "image": formData.image,
            "habitTranslations": []

        }
        for (var key in formData) {
            if(key.startsWith("contentName") | key.startsWith("contentDescr") | key.startsWith("contentHabItem")) {
                var lang, name, description, habitItem;

                lang = key.slice(key.length - 2);
                if (key.startsWith("contentName")) {
                    name = formData["contentName" + lang]
                }

                if (key.startsWith("contentName")) {
                    name = formData["contentName" + lang]
                }
                if (key.startsWith("contentDescr")) {
                    description = formData["contentDescr" + lang]
                }
                if (key.startsWith("contentHabItem")) {
                    habitItem = formData["contentHabItem" + lang]
                }
                if(name != null && description != null && habitItem != null) {
                    returnData.habitTranslations.push(
                        {
                            "name": name,
                            "description": description,
                            "habitItem": habitItem,
                            "languageCode": lang
                        }
                    );
                    name = null; description = null; habitItem = null;
                }
            }
        }
        var result = new FormData();
        result.append("habitDto", new Blob([JSON.stringify(returnData)], {type: "application/json"}));
        var file = document.getElementById("fileCreate").files[0];
        result.append("file", file);
        //request save in modal add
        $.ajax({
            url: '/management/habits/save',
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






    //edit button
    $('td .edit.eBtn').on('click', function (event) {
        event.preventDefault();
        $("#editHabitModal").each(function () {
            $(this).find('input.eEdit').val("");
        });
        clearAllErrorsSpan();
        $('#editHabitModal').modal();
        var href = $(this).attr('href');
        $.get(href, function (habit, status) {
            $('#id').val(habit.id);
            $('#image').val(habit.image);
            $('#file').val(habit.file);
            habit.habitTranslations.forEach(function (translation, index) {
                $('#contentName' + translation.languageCode).val(translation.name);
                $('#contentHabItem' + translation.languageCode).val(translation.habitItem);
                $('#contentDescr' + translation.languageCode).val(translation.description);
            })
        });
    });
    //submit button in modal edit
    $('#submitEditBtn').on('click', function (event) {
        event.preventDefault();
        clearAllErrorsSpan();
        var formData = $('#editHabitForm').serializeArray().reduce(function (obj, item) {
            obj[item.name] = item.value;
            return obj;
        }, {});
        var returnData = {
            "id": formData.id,
            "image": formData.image,
            "habitTranslations": []

        }
        for (var key in formData) {
            if(key.startsWith("contentName") | key.startsWith("contentDescr") | key.startsWith("contentHabItem")) {
                var lang, name, description, habitItem;

                lang = key.slice(key.length - 2);

                if (key.startsWith("contentName")) {
                    name = formData["contentName" + lang]
                }
                if (key.startsWith("contentDescr")) {
                    description = formData["contentDescr" + lang]
                }
                if (key.startsWith("contentHabItem")) {
                    habitItem = formData["contentHabItem" + lang]
                }
                if(name != null && description != null && habitItem != null) {
                    returnData.habitTranslations.push(
                        {
                            "name": name,
                            "description": description,
                            "habitItem": habitItem,
                            "languageCode": lang
                        }
                    );
                    name = null; description = null; habitItem = null;
                }
            }
        }
        var result = new FormData();
        result.append("habitDto", new Blob([JSON.stringify(returnData)], {type: "application/json"}));
        var file = document.getElementById("file").files[0];
        result.append("file", file);

        //save request in edit modal update
        $.ajax({
            url: '/management/habits/update',
            type: 'put',
            dataType: 'json',
            contentType: false,
            processData: false,
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
