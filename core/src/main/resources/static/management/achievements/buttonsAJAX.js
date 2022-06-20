function clearAllErrorsSpan() {
    $('.errorSpan').text('');
}

let checkedCh = 0;
function updateCheckBoxCount(chInt){
    let chBox = $('#checkbox' + chInt);
    let deleteBtn = $("#btnDelete");
    chBox.is(":checked") ? checkedCh++ : checkedCh--;
    console.log(checkedCh)
    if(checkedCh === 0) {
        deleteBtn.addClass("disabled");
    } else deleteBtn.removeClass("disabled");
}

$(document).ready(function () {
    let deleteBtn = $("#btnDelete");

    // Activate tooltip
    $('[data-toggle="tooltip"]').tooltip();

    // Select/Deselect checkboxes
    var checkbox = $('table tbody input[type="checkbox"]');
    $("#selectAll").click(function(){
        if(this.checked){
            checkedCh = 0;
            checkbox.each(function(){
                this.checked = true;
                checkedCh++;
            });
            deleteBtn.removeClass("disabled");
        } else{
            checkbox.each(function(){
                checkedCh--;
                this.checked = false;
            });
            deleteBtn.addClass("disabled");
        }
    });
    checkbox.click(function(){
        if(!this.checked){
            $("#selectAll").prop("checked", false);
        }
    });

    $('#btnSearchImage').click(function (){
        let url = "/management/achievement?query=";
        let query = $('#inputSearch').val();
        $.ajax({
            url: url + query,
            type: 'GET',
            success: function(res) {
                window.location.href= url + query;
            }
        });
    });

    //delete button in deleteEcoNewsModal
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
        $('#deleteAchievementModal').modal();
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
        var href = '/management/achievement/deleteAll';
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

    //submit button in addAchievementModal
    $('#submitAddBtn').on('click', function (event) {
        event.preventDefault();
        clearAllErrorsSpan();
        const form = $('#addAchievementForm');

        var formData = form.serializeArray().reduce(function (obj, item) {
            obj[item.name] = item.value;
            return obj;
        }, {});
        var payload = {
            "id": formData.id,
            "translations": getTranslationsFormFormData(formData),
            "achievementCategory": formData.achievementCategory,
            "condition": getConditions(this)
        };
        var multipartFormData = new FormData();
        multipartFormData.append(
            "achievementPostDto", new Blob([JSON.stringify(payload)], {type: "application/json"}));
        multipartFormData.append("file",
            form.find('[name="achievementIcon"]').prop('files')[0]);

        //save request in addAchievementModal
        $.ajax({
            url: '/management/achievement',
            type: 'post',
            dataType: 'json',
            contentType: false,
            processData: false,
            success: function (data) {
                if (Array.isArray(data.errors) && data.errors.length) {
                    data.errors.forEach(function (el) {
                        $(document.getElementById('errorModalSave' + el.fieldName)).text(el.fieldError);
                    })
                } else {
                    location.reload();
                }
            },
            data: multipartFormData
        });
    });

    //edit button on the right in the table
    $('td .edit.eBtn').on('click', function (event) {
        event.preventDefault();
        $("#editAchievementModal").each(function () {
            $(this).find('input.eEdit').val("");
        });
        clearAllErrorsSpan();
        $('#editAchievementModal').modal();
        var href = $(this).attr('href');
        $.get(href, function (achievement, status) {
            $('#id').val(achievement.id);
            achievement.translations.forEach(function (translation, index) {
                $(`#title_${translation.language.id}_${translation.language.code}`)
                    .val(translation.title);
                $(`#description_${translation.language.id}_${translation.language.code}`)
                    .val(translation.description);
                $(`#message_${translation.language.id}_${translation.language.code}`)
                    .val(translation.message);
            });
            $('#achievementCategory').val(achievement.achievementCategory.name);

            const conditionsBlock = $('#editAchievementModal').find('.conditions-block');
            conditionsBlock.children('.condition-block').each(function () {
                removeLastConditionBlock(this)
            });

            for (let conditionKey in achievement.condition) {
                const currentConditionBlock = conditionsBlock.children('.condition-block:last');

                currentConditionBlock.children('[name="conditionKey"]').val(conditionKey);
                currentConditionBlock.children('[name="conditionValue"]').val(achievement.condition[conditionKey]);

                if (conditionsBlock.children('.condition-block').length < Object.keys(achievement.condition).length) {
                    addNewConditionBlock(currentConditionBlock);
                }
            }
        });
    });
    //submit button in editEcoNewsModal
    $('#submitEditBtn').on('click', function (event) {
        event.preventDefault();
        clearAllErrorsSpan();
        const form = $('#editAchievementForm');
        var formData = form.serializeArray().reduce(function (obj, item) {
            obj[item.name] = item.value;
            return obj;
        }, {});
        var payload = {
            "id": formData.id,
            "translations": getTranslationsFormFormData(formData),
            "achievementCategory": formData.achievementCategory,
            "condition": getConditions(this)
        };
        var multipartFormData = new FormData();
        multipartFormData.append(
            "achievementManagementDto", new Blob([JSON.stringify(payload)], {type: "application/json"}));
        multipartFormData.append("file",
            form.find('[name="achievementIcon"]').prop('files')[0]);

        //save request in editAchievementModal
        $.ajax({
            url: '/management/achievement',
            type: 'put',
            dataType: 'json',
            contentType: false,
            processData: false,
            success: function (data) {
                if (Array.isArray(data.errors) && data.errors.length) {
                    data.errors.forEach(function (el) {
                        $(document.getElementById('errorModalUpdate' + el.fieldName)).text(el.fieldError);
                    })
                } else {
                    location.reload();
                }
            },
            data: multipartFormData
        });
    })
});

function getTranslationsFormFormData(formData) {
    var translations = [];
    for (var key in formData) {
        if (key.startsWith("title")) {
            var contentAndLanguage = key.split("_");
            var langId = contentAndLanguage[1];
            var langCode = contentAndLanguage[2];
            var title = formData["title_" + langId + "_" + langCode];
            var description = formData["description_" + langId + "_" + langCode];
            var message = formData["message_" + langId + "_" + langCode];
            translations.push({
                "title": title,
                "description": description,
                "message": message,
                "language": {
                    "code": langCode,
                    "id": langId
                }
            });
        }
    }

    return translations;
}

function getConditions(button) {
    const conditionsBlock = $(button).closest('form').find('div[class*="conditions-block"]');
    let conditions = {};
    conditionsBlock.children('.condition-block').each(function () {
        let key = $(this).find('[name="conditionKey"]').children('option:selected').val();
        conditions[key] = $(this).find('[name="conditionValue"]').val();
    });
    return conditions;
}

function addNewConditionBlock(innerElement) {
    const conditionsBlock = $(innerElement).closest('div[class*="conditions-block"]');
    const newConditionBlock = conditionsBlock.children('.condition-block:first').clone();

    newConditionBlock.children('[name="conditionKey"]').prop("selected", false);
    newConditionBlock.children('[name="conditionValue"]').prop("value", "");

    newConditionBlock.insertAfter(conditionsBlock.children(".condition-block:last"));
}

function removeLastConditionBlock(innerElement) {
    const conditionsBlock = $(innerElement).closest('div[class*="conditions-block"]');
    const conditionsTotal = conditionsBlock.children('.condition-block').length
    if (conditionsTotal > 1) {
        const lastConditionBlock = conditionsBlock.children('.condition-block:last');
        lastConditionBlock.remove();
    }
}