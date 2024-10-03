$(document).on('click', '.edit', function (event) {
    event.preventDefault();

    const row = $(this).closest('tr');
    const id = row.find('td').eq(0).text();
    const name = row.find('td').eq(1).text();
    const points = row.find('td').eq(2).text();
    const status = row.find('td').eq(3).text();

    $('#editId').val(id);
    $('#editName').val(name);
    $('#editPoints').val(points);
    $('#editStatus').val(status);

    $('#editModal').modal('show');
});

// Handle the edit form submission
$(document).on('click','#editModalSubmit', function (event) {
    event.preventDefault();

    const id = $('#editId').val();
    const name = $('#editName').val();
    const points = $('#editPoints').val();
    const status = $('#editStatus').val();

    $.ajax({
        url: '/management/rating/calculation',
        type: 'PUT',
        dataType: 'json',
        contentType: 'application/json',
        data: JSON.stringify({
            id: id,
            name: name,
            points: points,
            status: status
        }),
        success: function (response) {
            location.reload();
        },
        error: function (error) {
            alert('Error updating rating points: ' + error.responseText);
        }
    });
});


// Function to handle the delete button click
$(document).on('click', '.delete', function (event) {
    event.preventDefault();

    const row = $(this).closest('tr');
    const id = row.find('td').eq(0).text();

    $.ajax({
        url: `/management/rating/calculation/${id}`,
        type: 'DELETE',
        success: function(result) {
            location.reload();
        },
        error: function(xhr, status, error) {
            alert("Error deleting rating points: " + xhr.responseText);
        }
    });
});

$(document).on('click', '.restore', function (event) {
    event.preventDefault();

    const row = $(this).closest('tr');
    const id = row.find('td').eq(0).text();

    $.ajax({
        url: `/management/rating/calculation/restore/${id}`,
        type: 'PUT',
        success: function(result) {
            location.reload();
        },
        error: function(xhr, status, error) {
            alert("Error deleting rating points: " + xhr.responseText);
        }
    });
});