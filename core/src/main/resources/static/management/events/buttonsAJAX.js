function clearAllErrorsSpan() {
    $('.errorSpan').text('');
}
function clearAllTagsInTagList() {
    document.getElementById("tagsEdit").innerHTML='';
}

function getEventTagList() {

}

function sendDataFromSearchForm() {
    let url = "/management/events?";
    let id = document.getElementById("idSearchForm").value;
    let author = document.getElementById("authorSearchForm").value;
    let title = document.getElementById("titleSearchForm").value;
    let text = document.getElementById("textSearchForm").value;
    let tag = document.getElementById("tagSearchForm").value;
    if (id!==""){
        url = url.concat("id="+id+"&");
    }
    if (author!==""){
        url = url.concat("author="+author+"&");
    }
    if (title!==""){
        url = url.concat("title="+title+"&");
    }
    if (text!==""){
        url = url.concat("description="+text+"&");
    }
    if (tag!==""){
        url = url.concat("tags="+tag);
    } else {
        url = url.slice(0, -1);
    }
    $.ajax({
        url: url,
        type: 'GET',
        success: function() {
            window.location.href= url;
        }
    });
}