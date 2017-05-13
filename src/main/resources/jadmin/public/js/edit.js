$(function () {
    $("form").on("submit", function (event) {
        event.preventDefault();
        var $form = $(this);
        var data = $form.serialize();
        var currentPath = window.location.pathname;
        $.ajax({
            type: "POST",
            url: currentPath,
            data: data,
            success: function(data) {
                if(data.success) {
                    window.location = window.location.pathname.split("/").slice(0,-1).join("/");
                } else {
                    alert(data.errorMessage);
                }
            }
        });
    });
});