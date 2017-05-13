<@root.template jsIncludes=[]>
<h1>${resource.tableName}</h1>
<a href="${templateObject.prefix}/${resource.tableName}/new" class="btn btn-success">Add</a>
<div class="row">
    <table class="table">
        <thead>
        <tr>
            <#list headers as header>
                <th>${header}</th>
            </#list>
        </tr>
        </thead>
        <tbody>
            <#list rows as row>
            <tr>
                <#list headers as header>
                    <td>${row.properties[header]!}</td>
                </#list>
                <td>
                    <a href="${templateObject.prefix}/${resource.tableName}/${row.properties['id']}"
                       class="btn btn-info">Edit</a>
                </td>
            </tr>
            </#list>
        </tbody>
    </table>
</div>
</@root.template>