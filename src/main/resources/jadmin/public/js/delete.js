$(function () {
    $(".deletebtn").on("click", function (event) {
        event.preventDefault();

        if(confirm(confirmationMessage)) {
            console.log("confirmed!");
            $.ajax({
                type: "DELETE",
                url: $(this).attr("href"),
                success: function(data) {
                    if(data.success) {
                        location.reload();
                    } else {
                        alert(data.errorMessage);
                    }
                }
            });
        }
    });
});