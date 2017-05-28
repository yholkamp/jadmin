<#macro template jsIncludes=[] cssIncludes=[] breadcrumb="">
<!DOCTYPE html>
<html lang="en">
<head>
    <#include "includes/head.ftl"/>
</head>
<body>

    <#include "includes/navbar.ftl"/>
<div class="container-fluid">
    <div class="col-md-2 sidebar">
       <#include "includes/sidebar.ftl">
    </div>
    <div class="col-md-10 content">
        <ol class="breadcrumb">
            <li class="breadcrumb-item <#if !resource?? && breadcrumb=="">active</#if>"><a href="${templateObject.prefix}">${i("view.page_title")}</a></li>
            <#if resource??>
            <li class="breadcrumb-item <#if breadcrumb=="">active</#if>"><a href="${templateObject.prefix}/${resource.tableName?url}">${ii("resources.${resource.tableName}")}</a></li>
            </#if>
            <#if breadcrumb!="">
                <li class="breadcrumb-item active">${breadcrumb}</li>
            </#if>
        </ol>
        
        <#nested>
    </div>
    <#include "includes/footer.ftl"/>
</div>

    <#include "includes/scripts.ftl"/>
</body>
</html>
</#macro>