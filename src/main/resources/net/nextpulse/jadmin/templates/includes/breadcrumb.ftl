<ol class="breadcrumb">
    <li class="breadcrumb-item <#if !resource?? && breadcrumb=="">active</#if>"><a href="${templateObject.prefix}">${i("view.page_title")}</a></li>
    <#if resource??>
        <li class="breadcrumb-item <#if breadcrumb=="">active</#if>"><a href="${templateObject.prefix}/${resource.tableName?url}">${ii("resources.${resource.tableName}")}</a></li>
    </#if>
    <#if breadcrumb!="">
        <li class="breadcrumb-item active">${breadcrumb}</li>
    </#if>
</ol>