function clearAllErrorsSpan(){
    $('.errorSpan').text('');
}

$(document).ready(function(){
    $('[data-toggle="tooltip"]').tooltip();

    var checkbox = $('table tbody input[type="checkbox"]');
    $("#selectAll").click(function(){
        if(this.checked){
            checkbox.each(function(){
                this.checked = true;
            });
        } else{
            checkbox.each(function(){
                this.checked = false;
            });
        }
    });
    checkbox.click(function(){
        if(!this.checked){
            $("#selectAll").prop("checked", false);
        }
    });
    $('td .edit.eBtn').on('click',function(event){
        event.preventDefault();
        $("#editAdviceModal").each(function(){
            $(this).find('input.eEdit').val("");
        });
        clearAllErrorsSpan();
        $('#editAdviceModal').modal();
        var href = $(this).attr('href');
        $.get(href, function (advice,status){
            $('#id').val(advice.id);
            $('#habit').val(advice.habit.id);
            advice.translations.forEach(function (translation, index){
                $(`#content_${translation.language.id}_${translation.language.code}`)
                    .val(translation.content);
            })
        });
    });
    $('#addAdviceModalBtn').on('click',function(event){
        clearAllErrorsSpan();
    });
    $('td .delete.eDelBtn').on('click',function(event){
        event.preventDefault();
        $('#deleteAdviceModal').modal();
        var href = $(this).attr('href');
        $('#deleteOneSubmit').attr('href',href);
    });
    $('#deleteOneSubmit').on('click',function(event){
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
    $('#deleteAllSubmit').on('click',function(event){
        event.preventDefault();
        var payload=[];
        checkbox.each(function (){
            if(this.checked){
                payload.push(parseInt(this.value));
            }
        });
        var href = '/management/advices/deleteAll';
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
    $('#submitAddBtn').on('click',function(event) {
        event.preventDefault();
        clearAllErrorsSpan();
        var formData = $('#addAdviceForm').serializeArray().reduce(function (obj, item) {
            obj[item.name] = item.value;
            return obj;
        }, {});
        var payload = {
            "habit": {
                id: formData.habit
            },
            "translations": []
        };
        for (var key in formData) {
            if (key.startsWith("content")) {
                var contentAndLanguage = key.split("_");
                var langId = contentAndLanguage[1];
                var langCode = contentAndLanguage[2];
                var content = formData["content_" + langId + "_" + langCode];
                payload.translations.push({
                   "content": content,
                    "language": {
                       "code": langCode,
                        "id": langId
                    }
                });
            }
        }
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
    });

    //Кнопка submit в модальній формі Edit
    $('#submitEditBtn').on('click',function(event){
        event.preventDefault();
        clearAllErrorsSpan();
        var formData = $('#editAdviceForm').serializeArray().reduce(function(obj, item) {
            obj[item.name] = item.value;
            return obj;
        }, {});
        var adviceId = formData.id;
        var payload = {
            "habit": {
                id: formData.habit
            },
            "translations": []
        };
        for (var key in formData) {
            if (key.startsWith("content")) {
                var contentAndLanguage = key.split("_");
                var langId = contentAndLanguage[1];
                var langCode = contentAndLanguage[2];
                var content = formData["content_" + langId + "_" + langCode];
                payload.translations.push({
                    "content": content,
                    "language": {
                        "code": langCode,
                        "id": langId
                    }
                });
            }
        }
        //запит save у модальній формі update
        $.ajax({
            url: `/management/advices/${adviceId}`,
            type: 'put',
            dataType: 'json',
            contentType: 'application/json',
            success: function (data) {
                if(Array.isArray(data.errors) && data.errors.length){
                    data.errors.forEach(function(el){
                        console.log(el.fieldName);
                        $(document.getElementById('errorModalUpdate'+el.fieldName)).text(el.fieldError);
                    })
                }
                else{
                    location.reload();
                }
            },
            data: JSON.stringify(payload)
        });
    })
});

