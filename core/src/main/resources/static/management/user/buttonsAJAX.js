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
        $("#editUserModal").each(function(){
            $(this).find('input.eEdit').val("");
        });
        clearAllErrorsSpan();
        $('#editUserModal').modal();
        var href = $(this).attr('href');
        $.get(href, function (user,status){
            $('#id').val(user.id);
            $('#name').val(user.name);
            $('#email').val(user.email);
            $('#userCredo').val(user.userCredo);
            $('#userstatus').val(user.userStatus);

        });
    });
    //Кнопка addUser зверху таблиці
    $('#addUserModal').on('click',function(event){
        clearAllErrorsSpan();
    });
    //Кнопка delete справа в таблиці
    $('td .delete.eDelBtn').on('click',function(event){
        event.preventDefault();
        $('#deactivateUserModal').modal();
        var href = $(this).attr('href');
        $('#deactivateOneSubmit').attr('href',href);
    });
    //Кнопка delete в deleteFactOfTheDayModal
    $('#deactivateOneSubmit').on('click',function(event){
        event.preventDefault();
        var href = $(this).attr('href');
        $.ajax({
            url: href,
            type: 'post',
            dataType: 'json',
            contentType: 'application/json',
            success: function (data) {
                location.reload();
            },
        });
    });
    //Кнопка delete в deleteAllSelectedModal
    $('#deactivateAllSubmit').on('click',function(event){
        event.preventDefault();
        var checkbox = $('table tbody input[type="checkbox"]');
        var payload=[];
        checkbox.each(function (){
            if(this.checked){
                payload.push(this.value);
            }
        })
        var href = '/management/users/deactivateAll';
        $.ajax({
            url: href,
            type: 'post',
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
        var formData = $('#addUserForm').serializeArray().reduce(function(obj, item) {
            obj[item.name] = item.value;
            return obj;
        }, {});
        var payload = {
            "id" : formData.id,
            "name" : formData.name,
            "email" : formData.email,
            "userCredo" : formData.userCredo,
            "userStatus" : formData.userStatus
        }

        //запит save у модальній формі add
        $.ajax({
            url: '/management/users/update',
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
        var formData = $('#editUserForm').serializeArray().reduce(function(obj, item) {
            obj[item.name] = item.value;
            return obj;
        }, {});
        var returnData={
            "id" : formData.id,
            "name" : formData.name,
            "email" : formData.email,
            "userCredo" : formData.userCredo,
            "userStatus" : formData.userStatus
        }
        //запит save у модальній формі update
        $.ajax({
            url: '/management/users/',
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
