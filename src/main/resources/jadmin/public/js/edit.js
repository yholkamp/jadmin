$(function () {
    $("form").on("submit", function (event) {
        event.preventDefault();
        var $form = $(this);
        var passwordField =$("input[type='password']");
        if(passwordField && passwordField.val() === "") {
            passwordField.attr("disabled", "disabled");
        }
        var data = $form.serialize();

        var currentPath = window.location.pathname;
        $.ajax({
            type: "POST",
            url: currentPath,
            data: data,
            success: function(data) {
                if(data.success) {
                    if(passwordField.isDisabled) {passwordField.removeAttribute("disabled")}

                    window.location = window.location.pathname.split("/").slice(0,-1).join("/");
                } else {
                    alert(data.errorMessage);
                }
            }
        });
    });
});