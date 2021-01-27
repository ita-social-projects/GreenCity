function clearAllErrorsSpan() {
    $('.errorSpan').text('');
}

function applyFilters() {
    document.getElementById("searchForm").action = "/management/eco-news";
    document.getElementById("searchForm").submit();
}
function toggle(source) {
    var checkboxes = document.querySelectorAll('input[type="checkbox"]');
    for (var i = 0; i < checkboxes.length; i++) {
        if (checkboxes[i] != source)
            checkboxes[i].checked = source.checked;
    }
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
        let url = "/management/eco-news?query=";
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
    $('.delete.eDelBtn').on('click', function (event) {
        event.preventDefault();
        $('#deleteEcoNewsModal').modal();
        var href = $(this).attr('href');
        $('#deleteOneSubmit').attr('href', href);
    });

    //delete button in deleteAllSelectedModal
    $('#deleteAllSubmit').on('click', function (event) {
        event.preventDefault();
        var checkbox = $('input[type="checkbox"]');
        var payload = [];
        checkbox.each(function () {
            if (this.checked) {
                payload.push(this.value);
            }
        });
        var href = '/management/eco-news/deleteAll';
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

    //submit button in addEcoNewsModal
    $('#submitAddBtn').on('click', function (event) {
        event.preventDefault();
        clearAllErrorsSpan();
        var formData = $('#addEcoNewsForm').serializeArray().reduce(function (obj, item) {
            obj[item.name] = item.value;
            return obj;
        }, {});
        var payload = {
            "id": formData.id,
            "title": formData.title,
            "text": formData.text,
            "image": formData.image,
            "source": formData.source,
            "tags": []
        };
        for (var key in formData) {

            if (key.startsWith("tags") && formData["tags"].trim().length !== 0) {
                var tag = formData["tags"].toString().split(/[\s,]+/);

                for (var i = 0; i < tag.length; i++) {
                    payload.tags.push(tag[i]);
                }
            }
        }
        var result = new FormData();
        result.append("addEcoNewsDtoRequest", new Blob([JSON.stringify(payload)], {type: "application/json"}));
        var file = document.getElementById("creationFile").files[0];
        result.append("file", file);
        //save request in addEcoNewsModal
        $.ajax({
            url: '/management/eco-news/',
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
    $('.edit.eBtn').on('click', function (event) {
        event.preventDefault();
        $("#editEcoNewsModal").each(function () {
            $(this).find('input.eEdit').val("");
        });
        clearAllErrorsSpan();
        $('#editEcoNewsModal').modal();
        var href = $(this).attr('href');
        $.get(href, function (econews, status) {
            $('#id').val(econews.id);
            $('#title').val(econews.title);
            $('#text').val(econews.text);
            $('#imagePath').val(econews.imagePath);
            $('#source').val(econews.source);
            $('#tags').val(econews.tags);
            $('#file').val(econews.file);
        });
    });
    //submit button in editEcoNewsModal
    $('#submitEditBtn').on('click', function (event) {
        event.preventDefault();
        clearAllErrorsSpan();
        var formData = $('#editEcoNewsForm').serializeArray().reduce(function (obj, item) {
            obj[item.name] = item.value;
            return obj;
        }, {});
        var returnData = {
            "id": formData.id,
            "title": formData.title,
            "text": formData.text,
            "imagePath": formData.imagePath,
            "source": formData.source,
            "tags": []
        };
        for (var key in formData) {
            if (key.startsWith("tags") && $("#" + key).val().trim().length !== 0) {

                var tag = $("#" + key).val().split(/[\s,]+/);

                for (var i = 0; i < tag.length; i++) {
                    returnData.tags.push(tag[i]);
                }
            }
        }
        var result = new FormData();
        result.append("ecoNewsDtoManagement", new Blob([JSON.stringify(returnData)], {type: "application/json"}));
        var file = document.getElementById("file").files[0];
        result.append("file", file);
        //save request in editEcoNewsModal
        $.ajax({
            url: '/management/eco-news/',
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

    //Include Date Range Picker
    $('.input-daterange input').each(function () {
        $(this).datepicker({
            format: 'yyyy-mm-dd',
            todayHighlight: true,
            autoclose: true,
            orientation: 'top'
        });
    });
});

function markCurentPageOnNav(){
    document.getElementById("eco-news-nav").classList.add("eco-news-active-link");
}

function orderByNameField(nameField){
    var allParam = window.location.search;
    var urlSearch = new URLSearchParams(allParam);
    var sort = urlSearch.get("sort");
    var page = urlSearch.get("page");
    if (page!==null){
        urlSearch.set("page","0");
    }
    if (sort==null){
        urlSearch.set("sort",nameField+",ASC");
    }
    else if (sort.includes(nameField)) {
        sort = sort.toUpperCase();
        if (sort.includes("ASC")){
            urlSearch.set("sort",nameField+",DESC");
        }
        else if(sort.includes("DESC")) {
            urlSearch.set("sort",nameField+',ASC');
        }
    }else {
        urlSearch.set("sort",nameField+",ASC");
    }

    let url = "/management/eco-news?";
    $.ajax({
        url: url + urlSearch.toString(),
        type: 'GET',
        success: function(res) {
            window.location.href= url + urlSearch.toString();
            console.log("RESULT")
        }
    });
}
// mark order
function markOrder(){
    var allParam = window.location.search;
    var urlSearch = new URLSearchParams(allParam);
    var sort = urlSearch.get("sort");
    if (sort!==null){
        if(sort.includes('id')){
            if (sort.includes('ASC')){
                document.getElementById("id-icon").className='fas fa-chevron-up';
            }else {
                document.getElementById("id-icon").className="fas fa-chevron-down";
            }
        }else if(sort.includes('author.name')){
            if (sort.includes('ASC')){
                document.getElementById("author-icon").className='fas fa-chevron-up';
            }else {
                document.getElementById("author-icon").className="fas fa-chevron-down";
            }
        }else if(sort.includes('title')){
            if (sort.includes('ASC')){
                document.getElementById("title-icon").className='fas fa-chevron-up';
            }else {
                document.getElementById("title-icon").className="fas fa-chevron-down";
            }
        }else if (sort.includes('text')){
            if (sort.includes('ASC')){
                document.getElementById("text-icon").className='fas fa-chevron-up';
            }else {
                document.getElementById("text-icon").className="fas fa-chevron-down";
            }
        }else if(sort.includes('creationDate')){
            if (sort.includes('ASC')){
                document.getElementById("date-icon").className='fas fa-chevron-up';
            }else {
                document.getElementById("date-icon").className="fas fa-chevron-down";
            }
        }else if(sort.includes('tags')){
            if (sort.includes('ASC')){
                document.getElementById("tags-icon").className='fas fa-chevron-up';
            }else {
                document.getElementById("tags-icon").className="fas fa-chevron-down";
            }
        }else if(sort.includes('likes')){
            if (sort.includes('ASC')){
                document.getElementById("likes-icon").className='fas fa-chevron-up';
            }else {
                document.getElementById("likes-icon").className="fas fa-chevron-down";
            }
        }
    }
}

//sidebar
function openNav() {
    document.getElementById("mySidepanel").style.width = "250px";
    document.getElementById("openbtnId").hidden = true;
    document.getElementById("tab-content").style.marginLeft="15%";
}

function closeNav() {
    document.getElementById("mySidepanel").style.width = "0";
    document.getElementById("openbtnId").hidden = false;
    document.getElementById("tab-content").style.marginLeft="0";
}
