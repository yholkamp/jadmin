<@root.template jsIncludes=[]>
<h1>${ii("resources.${resource.tableName}")}</h1>
<a href="${templateObject.prefix}/${resource.tableName}/new" class="btn btn-success">${i("view.button.add")}</a>
<div class="row">
    <table class="table">
        <thead>
        <tr>
            <#list headers as header>
                <th>${ii("resources.${resource.tableName}.${header}")}</th>
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
                       class="btn btn-info">${i("view.button.edit")}</a>
                </td>
            </tr>
            </#list>
        </tbody>
    </table>
    
    <ul class="pagination pagination-sm">
        <#list 1..numberOfPages as page>
            <li <#if page == currentPage>class="active"</#if>><a href="${templateObject.prefix}/${resource.tableName}?page=${page}">${page}</a></li>
        </#list>
    </ul>
</div>
</@root.template>