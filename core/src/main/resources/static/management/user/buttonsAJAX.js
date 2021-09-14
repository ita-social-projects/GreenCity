function chageIcons() {
    var allParam = window.location.search;
    var urlSearch = new URLSearchParams(allParam);
    var sort = urlSearch.get("sort");
    if (sort !== null) {
        if (sort.includes('id')) {
            if (sort.includes('ASC')) {
                document.getElementById("id-icon").className = 'fas fa-chevron-up';
            } else {
                document.getElementById("id-icon").className = "fas fa-chevron-down";
            }
        } else if (sort.includes('name')) {
            if (sort.includes('ASC')) {
                document.getElementById("user-icon").className = 'fas fa-chevron-up';
            } else {
                document.getElementById("user-icon").className = "fas fa-chevron-down";
            }
        } else if (sort.includes('email')) {
            if (sort.includes('ASC')) {
                document.getElementById("email-icon").className = 'fas fa-chevron-up';
            } else {
                document.getElementById("email-icon").className = "fas fa-chevron-down";
            }
        } else if (sort.includes('userCredo')) {
            if (sort.includes('ASC')) {
                document.getElementById("credo-icon").className = 'fas fa-chevron-up';
            } else {
                document.getElementById("credo-icon").className = "fas fa-chevron-down";
            }
        } else if (sort.includes('role')) {
            if (sort.includes('ASC')) {
                document.getElementById("role-icon").className = 'fas fa-chevron-up';
            } else {
                document.getElementById("role-icon").className = "fas fa-chevron-down";
            }
        } else if (sort.includes('userStatus')) {
            if (sort.includes('ASC')) {
                document.getElementById("tags-icon").className = 'fas fa-chevron-up';
            } else {
                document.getElementById("tags-icon").className = "fas fa-chevron-down";
            }
        }

    }
}

let filter=document.querySelectorAll('.filter');
for (i=0;i<filter.length;i++){
    let subMenu=filter[i].nextElementSibling;
    let thisFilter=filter[i];
    filter[i].addEventListener('click', function () {
        subMenu.classList.toggle('open');
        thisFilter.classList.toggle('active');
    });
}
function sortByFieldName(nameField){
    var allParam = window.location.search;
    var urlSearch = new URLSearchParams(allParam);
    var sort = urlSearch.get("sort");
    var page = urlSearch.get("page");
    if (page !== null) {
        urlSearch.set("page", "0");
    }
    if(sort==nameField+",ASC"){
        urlSearch.set("sort", nameField + ",DESC");
        localStorage.setItem("sort", nameField + ",DESC");
    }else{
        urlSearch.set("sort", nameField + ",ASC");
        localStorage.setItem("sort", nameField + ",ASC");
    }
   let url = "/management/users?";
    $.ajax({
        url: url + urlSearch.toString(),
        type: 'GET',
        success: function (res) {
            window.location.href = url + urlSearch.toString();
        }
    });
}
function saveItemsOnPage(itemsOnPage){
    var allParam = window.location.search;
    var urlSearch = new URLSearchParams(allParam);
    localStorage.setItem("size", itemsOnPage);
    let url = "/management/users?";
    console.log(url);
    urlSearch.set("size",itemsOnPage);
    $.ajax({
        url: url + urlSearch.toString(),
        type: 'GET',
        success: function (res) {
            window.location.href = url + urlSearch.toString();
        }
    });
}

function otherCheck() {
    let other = document.getElementById('other');
    if (other.checked == true) {
        document.getElementById("othertext").disabled = false;
    } else {
        document.getElementById("othertext").disabled = true;
    }
}

function clearAllErrorsSpan() {
    $('.errorSpan').text('');
}

let checkedCh = 0;

function updateCheckBoxCount(chInt) {
    let chBox = $('#checkbox' + chInt);
    let deactivateButton = $("#btnDeactivate");
    chBox.is(":checked") ? checkedCh++ : checkedCh--;
    console.log(checkedCh)
    if (checkedCh === 0) {
        deactivateButton.addClass("disabled");
    } else deactivateButton.removeClass("disabled");
}

let checkedChAdvices = 0;
function updateCheckBoxCountAdvices(chInt) {
    let chBox = $('#advicescheckbox' + chInt);
    chBox.is(":checked") ? checkedChAdvices++ : checkedChAdvices--;
    console.log('advices '+checkedChAdvices)

    let deactivateButton = $('#unlinktable2');
    if (checkedChAdvices === 0) {
        deactivateButton.addClass("disabled");
    } else deactivateButton.removeClass("disabled");
}

$(document).ready(function () {
    let deactivateButton = $("#btnDeactivate");

    // Activate tooltip
    $('[data-toggle="tooltip"]').tooltip();

    // Select/Deselect checkboxes
    var checkbox = $('table tbody input[type="checkbox"]');
    $("#selectAll").click(function () {
        if (this.checked) {
            checkedCh = 0;
            checkbox.each(function () {
                this.checked = true;
                checkedCh++;
            });
            deactivateButton.removeClass("disabled");
        } else {
            checkbox.each(function () {
                checkedCh--;
                this.checked = false;
            });
            deactivateButton.addClass("disabled");
        }
    });
    checkbox.click(function () {
        if (!this.checked) {
            $("#selectAll").prop("checked", false);
        }
    });

    $('#btnSearchImage').click(function () {
        let url = "/management/users?query=";
        let query = $('#inputSearch').val();
        $.ajax({
            url: url + query,
            type: 'GET',
            success: function (res) {
                window.location.href = url + query;
            }
        });
    });


    // Add user button (popup)
    $('#addUserModalBtn').on('click', function (event) {
        clearAllErrorsSpan();
    });

    // Submit button in addUserModal
    $('#submitAddBtn').on('click', function (event) {
        event.preventDefault();
        clearAllErrorsSpan();
        var formData = $('#addUserForm').serializeArray().reduce(function (obj, item) {
            obj[item.name] = item.value;
            return obj;
        }, {});
        var payload = {
            "id": formData.id,
            "name": formData.name,
            "email": formData.email,
            "role": formData.role,
            "userStatus": formData.userStatus
        }

        // Ajax request
        $.ajax({
            url: '/management/users/register',
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
    })

    // Edit user button (popup)
    $('td .edit.eBtn').on('click', function (event) {
        event.preventDefault();
        $("#editUserModal").each(function () {
            $(this).find('input.eEdit').val("");
        });
        clearAllErrorsSpan();
        $('#editUserModal').modal();
        var href = $(this).attr('href');
        $.get(href, function (user, status) {
            $('#id').val(user.id);
            $('#name').val(user.name);
            $('#email').val(user.email);
            $('#role').val(user.role);
            $('#userCredo').val(user.userCredo);
            $('#userStatus').val(user.userStatus);
        });
    });
    // Save editing button in editUserModal
    $('#submitEditBtn').on('click', function (event) {
        event.preventDefault();
        clearAllErrorsSpan();
        var formData = $('#editUserForm').serializeArray().reduce(function (obj, item) {
            obj[item.name] = item.value;
            return obj;
        }, {});
        var returnData = {
            "id": formData.id,
            "name": formData.name,
            "email": formData.email,
            "role": formData.role,
            "userCredo": formData.userCredo,
            "userStatus": formData.userStatus
        }
        // put request when 'Save' button in editUserModal clicked
        $.ajax({
            url: '/management/users/',
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

    // Deactivate user button (popup)
    $('td .deactivate-user.eDeactBtn').on('click', function (event) {
        event.preventDefault();
        var href = $(this).attr('href');
        $('#deactivateUserModal').modal();
        $.get(href, function (lang, status) {
            let userLang = document.createElement('div');
            window.globalVariable=lang;
            userLang.classList.add('user-lang');
            userLang.innerHTML = `<div class="user-lang"> ${lang}</div>`;
            document.querySelector('#user-lang').appendChild(userLang);

        });
        $('#deactivateOneSubmit').attr('href', href);
    });
    // Confirm deactivation button in deactivateUserModal

    $('#deactivateOneSubmit').on('click', function (event) {
        event.preventDefault();
        let firstClick = document.getElementById("first-click");
        let secondClick = document.getElementById("second-click");
        let otherClick = document.getElementById("other");
        let listReasons = [];
        if (firstClick.checked === true) {
            listReasons.push("inappropriate credo content in profile {en}");
            listReasons.push("неприйнятний вміст кредо у профілі {ua}");
        }
        if (secondClick.checked === true) {
            listReasons.push("inappropriate behavior in news publications {en}");
            listReasons.push("неадекватна поведінка в публікаціях новин {ua}");
        }
        if (otherClick.checked === true) {
            listReasons.push($("input#othertext").val() + "{" + globalVariable + "}");
        }
        let f= globalVariable;

        var href = $(this).attr('href');
        let newUrl = href.toString().replace("lang", "deactivate")
        $.ajax({
            url: newUrl,
            type: 'post',
            data: JSON.stringify(listReasons),
            contentType: 'application/json',
            success: function (data) {
                location.reload();
            },
        });
    });

    // Aactivate user button (popup)
    $('td .activate-user.eActBtn').on('click', function (event) {
        event.preventDefault();
        let localStorage = window.localStorage;
        let currentLang = localStorage.getItem("language");
        if (currentLang === null) {
            currentLang = "en";
        }
        var href = $(this).attr('href');
        let updateHref = href + "&admin=" + currentLang;
        $('#activateUserModal').modal();
        $('#activateOneSubmit').attr('href', href);
        $.get(updateHref, function (allReasons, status) {
            allReasons.forEach(item => {
                let reason = document.createElement('div');
                reason.classList.add('reason');

                reason.innerHTML = `<div class="reason"> ${item.toString()}</div>
                `;
                document.querySelector('#reasons').appendChild(reason);
            });
        });
    });
    // Confirm deactivation button in activateUserModal
    $('#activateOneSubmit').on('click', function (event) {
        event.preventDefault();
        var href = $(this).attr('href');
        let newUrl = href.toString().replace("reasons", "activate")
        $.ajax({
            url: newUrl,
            type: 'post',
            success: function (data) {
                location.reload();
            },
        });
    });

    // Deactivate marked users button (popup)
    $('#deactivateAllSubmit').on('click', function (event) {
        event.preventDefault();
        var checkbox = $('table tbody input[type="checkbox"]');
        var payload = [];
        checkbox.each(function () {
            if (this.checked) {
                payload.push(this.value);
            }
        })
        var href = '/management/users/deactivateAll';
        // post request when 'Deactivate marked' button in deactivateAllSelectedModal clicked
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

    /*$(".clickable-row").click(function () {
        $('#userFriendsModal').modal();
        $('.modal-content').scrollTop(0);
        const href = $(this).attr("href");
        const body = $('#friendsTable').find('tbody');
        body.html("");
        $.get(href, function (users) {
            for (const user of users) {
                body.append($('<tr>')
                    .append($('<td>').text(user.id))
                    .append($('<td>').text(user.name))
                    .append($('<td>').text(user.email))
                    .append($('<td>').text(user.userCredo))
                    .append($('<td>').text(user.role))
                    .append($('<td>').text(user.userStatus)));
            }
        })
    });*/
});

//sidebar
function openNav() {
    document.getElementById("mySidepanel").style.width = "250px";
    document.getElementById("openbtnId").hidden = true;
    document.getElementById("tab-content").style.marginLeft="15%";
    // document.getElementById("eco-news-content").style.marginRight="15%";
}

function closeNav() {
    document.getElementById("mySidepanel").style.width = "0";
    document.getElementById("openbtnId").hidden = false;
    document.getElementById("tab-content").style.marginLeft="0";
    // document.getElementById("eco-news-content").style.marginRight="0";
}

function hideTable(clickedId) {
    var clickedButton = document.getElementById(clickedId);
    var hideTable = document.getElementById('table' + clickedId.match(/\d+/));
    var state = clickedButton.value;
    if (state == 'hide') {
        hideTable.style.display = "none";
        clickedButton.value = 'show';
        clickedButton.getElementsByTagName('span')[0].innerHTML = 'show';
        clickedButton.getElementsByTagName('img')[0].src = "/img/arrow-down.svg";
    }
    else if (state == 'show') {
        hideTable.style.display = "table-row-group";
        clickedButton.value = 'hide';
        clickedButton.getElementsByTagName('span')[0].innerHTML = 'hide';
        clickedButton.getElementsByTagName('img')[0].src = "/img/arrow-up.svg";
    }
}

function showTooltip(e) {
    var tooltip = e.target.classList.contains("tooltip")
        ? e.target
        : e.target.querySelector(":scope .tooltip");
    tooltip.style.left =
        (e.pageX + tooltip.clientWidth + 10 < document.body.clientWidth)
            ? (e.pageX + 10 + "px")
            : (document.body.clientWidth + 5 - tooltip.clientWidth + "px");
    tooltip.style.top =
        (e.pageY + tooltip.clientHeight + 10 < document.body.clientHeight)
            ? (e.pageY + 10 + "px")
            : (document.body.clientHeight + 5 - tooltip.clientHeight + "px");
}

var tooltips = document.querySelectorAll('.hoverable');
for(var i = 0; i < tooltips.length; i++) {
    tooltips[i].addEventListener('mousemove', showTooltip);
    console.log("some");
}

$('.popover-dismiss').popover({
    trigger: 'focus'
})