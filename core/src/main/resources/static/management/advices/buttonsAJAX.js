var languages;
$.get('/advice/languages',function (data){
    languages=data;
});

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
            $('#habit').val(advice.habit);
            advice.adviceTranslations.forEach(function (translation,index){
                $('#content'+translation.language.code).val(translation.content);
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
                payload.push(this.value);
            }
        })
        var href = '/management/advice/deleteAll';
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
        console.log(formData);
        var habit = {
            id: formData.habit
        };
        var payload = {
            "habit": habit,
            "translations": []
        };
        for (var key in formData) {
            if (key.startsWith("content")) {
                var contentAndLanguage = key.split(" ");
                var langId = contentAndLanguage[1];
                var langCode = contentAndLanguage[2];
                var content = formData["content " + langId + " " + langCode];
                payload.translations.push({
                   "content": content,
                    "language": {
                       "code": langCode,
                        "id": langId
                    }
                });
            }
        }
        console.log(payload);
        $.ajax({
            url: 'management/advice/',
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

    //Кнопка submit в модальній формі Edit
    $('#submitEditBtn').on('click',function(event){
        event.preventDefault();
        clearAllErrorsSpan();
        var formData = $('#editAdviceForm').serializeArray().reduce(function(obj, item) {
            obj[item.name] = item.value;
            return obj;
        }, {});
        var returnData={
            "id" : formData.id,
            "habit" : formData.habit,
            "adviceTranslations" : [
            ]

        }
        for (var key in formData) {
            if (key.startsWith("content")) {
                var lang = key.split("content").pop();
                // console.log(lang + " -> " + formData["content"+lang]);
                returnData.factOfTheDayTranslations.push(
                    {
                        "content" : formData["content"+lang],
                        "code": lang
                    }
                );
            }
        }
        //запит save у модальній формі update
        $.ajax({
            url: '/management/factoftheday/',
            type: 'put',
            dataType: 'json',
            contentType: 'application/json',
            success: function (data) {
                if(Array.isArray(data.errors) && data.errors.length){
                    data.errors.forEach(function(el){
                        $(document.getElementById('errorModalUpdate'+el.fieldName)).text(el.fieldError);
                    })
                }
                else{
                    location.reload();
                }
            },
            data: JSON.stringify(returnData)
        });
    })
});

