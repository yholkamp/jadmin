<@root.template jsIncludes=["lib/jquery-3.1.0.min.js", "lib/datatables.min.js", "list.js"] cssIncludes=["datatables.min.css"]>
<h1>${ii("resources.${resource.tableName}")}</h1>
<a href="${templateObject.prefix}/${resource.tableName}/new" class="btn btn-success">${i("view.button.add")}</a>
<div class="row">
    <table class="table" id="listResource">
    </table>
</div>
<script>
    var confirmationMessage = "${i("delete.confirmation", ii("resource.${resource.tableName}"))?json_string}";
    var jsonUrl = "${templateObject.prefix}/${resource.tableName}/json";
    var columns = [<#list headers as header>
    {"title": "${ii("resources.${resource.tableName}.${header}")?json_string}", "data": "${header?json_string}"},
    </#list>
        {"title":"", "defaultContent": "${
        '<a href="#" class="btn btn-info btn-sm edit-btn">${i("view.button.edit")}</a>&nbsp;<a href="#" class="btn btn-danger btn-sm delete-btn">${i("view.button.delete")}</a>'?json_string
        }"}
    ];
    var dataTableLanguage = {
        "infoFiltered": "",
        "info": "${i("view.table.info")?json_string}",
        "infoEmpty": "${i("view.table.info_empty")?json_string}",
        "paginate.first": "${i("view.table.info_empty")?json_string}",
        "paginate.last": "${i("view.table.paginate.last")?json_string}",
        "paginate.next": "${i("view.table.paginate.next")?json_string}",
        "paginate.previous": "${i("view.table.paginate.previous")?json_string}",
        "processing": "${i("view.table.processing")?json_string}",
        "zeroRecords": "${i("view.table.zeroRecords")?json_string}"
    };
    <#-- TODO: make the number of entries configurable -->
    var count = 20;
</script>
</@root.template>