$(function () {
    // set up the data table
    var dataTable = $('#listResource').DataTable({
        "processing": true,
        "serverSide": true,
        "ajax": jsonUrl,
        "columns": columns,
        "dom": //"<'row'<'col-sm-6'l><'col-sm-6'f>>" +
        "<'row'<'col-sm-12'tr>>" +
        "<'row'<'col-sm-5'i><'col-sm-7'p>>",
        "language": dataTableLanguage,
        "pageLength": count
    });
    
    // bind to clicks on the delete button
    $("#listResource tbody").on("click", "a", function (event) {
        event.preventDefault();
        
        console.log(this);
        var $this = $(this);
        var id = $this.closest("tr").attr("id");
        if ($this.hasClass("edit-btn")) {
            window.location.replace(window.location.pathname + "/" + id);
        } else if ($this.hasClass("delete-btn")) {
            handleDelete(id);
        }
    });
    
    function handleDelete(id) {
        if (confirm(confirmationMessage)) {
            $.ajax({
                type: "DELETE",
                url: window.location.pathname + "/" + id,
                success: function (data) {
                    if (data.success) {
                        // reload the datatable
                        dataTable.ajax.reload();
                    } else {
                        alert(data.errorMessage);
                    }
                }
            });
        }
    }
});