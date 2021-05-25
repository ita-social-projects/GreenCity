var languages;
$.get('/factoftheday/languages',function (data){
    languages=data;
});

function clearAllErrorsSpan(){
    $('.errorSpan').text('');
}

$(document).ready(function(){
    // Activate tooltip
    $('[data-toggle="tooltip"]').tooltip();

    // Select/Deselect checkboxes
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
    //Кнопка edit справа в таблиці
    $('td .edit.eBtn').on('click',function(event){
        event.preventDefault();
        $("#editFactOfTheDayModal").each(function(){
            $(this).find('input.eEdit').val("");
        });
        clearAllErrorsSpan();
        $('#editFactOfTheDayModal').modal();
        var href = $(this).attr('href');
        $.get(href, function (factoftheday,status){
            $('#id').val(factoftheday.id);
            $('#name').val(factoftheday.name);
            factoftheday.factOfTheDayTranslations.forEach(function (translation,index){
                $('#content'+translation.language.code).val(translation.content);
            })
        });
    });
    //Кнопка addFactOftheDay зверху таблиці
    $('#addFactOfTheDayModalBtn').on('click',function(event){
        clearAllErrorsSpan();
    });
    //Кнопка delete справа в таблиці
    $('td .delete.eDelBtn').on('click',function(event){
        event.preventDefault();
        $('#deleteFactOfTheDayModal').modal();
        var href = $(this).attr('href');
        $('#deleteOneSubmit').attr('href',href);
    });
    //Кнопка delete в deleteFactOfTheDayModal
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
    //Кнопка delete в deleteAllSelectedModal
    $('#deleteAllSubmit').on('click',function(event){
        event.preventDefault();
        var payload=[];
        checkbox.each(function (){
            if(this.checked){
                payload.push(this.value);
            }
        })
        var href = '/management/factoftheday/deleteAll';
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
    $('#submitAddBtn').on('click',function(event){
        event.preventDefault();
        clearAllErrorsSpan();
        var formData = $('#addFactOfTheDayForm').serializeArray().reduce(function(obj, item) {
            obj[item.name] = item.value;
            return obj;
        }, {});
        var payload={
            "id" : formData.id,
            "name" : formData.name,
            "factOfTheDayTranslations" : [
            ]

        }
        for (var key in formData) {
            if (key.startsWith("content")) {
                var lang = key.split("content").pop();
                payload.factOfTheDayTranslations.push(
                    {
                        "content" : formData["content"+lang],
                        "languageCode": lang
                    }
                );
            }
        }
        //запит save у модальній формі add
        $.ajax({
            url: '/management/factoftheday/',
            type: 'post',
            dataType: 'json',
            contentType: 'application/json',
            success: function (data) {
                if(Array.isArray(data.errors) && data.errors.length){
                    data.errors.forEach(function(el){
                        $(document.getElementById('errorModalSave'+el.fieldName)).text(el.fieldError);
                    })
                }
                else{
                    location.reload();
                }
            },
            data: JSON.stringify(payload)
        });
    })

    //Кнопка submit в модальній формі Edit
    $('#submitEditBtn').on('click',function(event){
        event.preventDefault();
        clearAllErrorsSpan();
        var formData = $('#editFactOfTheDayForm').serializeArray().reduce(function(obj, item) {
            obj[item.name] = item.value;
            return obj;
        }, {});
        var returnData={
            "id" : formData.id,
            "name" : formData.name,
            "factOfTheDayTranslations" : [
            ]

        }
        for (var key in formData) {
            if (key.startsWith("content")) {
                var lang = key.split("content").pop();
                // console.log(lang + " -> " + formData["content"+lang]);
                returnData.factOfTheDayTranslations.push(
                    {
                        "content" : formData["content"+lang],
                        "languageCode": lang
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
