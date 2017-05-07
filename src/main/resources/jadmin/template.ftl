<#macro template jsIncludes=[]>
<!DOCTYPE html>
<html lang="en">
<head>
    <#include "includes/head.ftl"/>
</head>
<body>

<nav class="navbar navbar-default navbar-static-top">
    <div class="container-fluid">
        <div class="navbar-header">
            <a class="navbar-brand" href="#">
                JAdmin
            </a>
        </div>
    </div>
</nav>
<div class="container-fluid">
    <div class="col-md-2">
        <ul class="nav nav-pills nav-stacked">
            <li><a href="${templateObject.prefix}">Home</a></li>
            <#assign tables = templateObject.tables/>
            <#list tables as table>
                <li><a href="${templateObject.prefix}/${table}">${table}</a></li>
            </#list>
        </ul>
    </div>
    <div class="col-md-10 content">
        <#nested>
    </div>
    <#include "includes/footer.ftl"/>
</div>

    <#include "includes/scripts.ftl"/>
</body>
</html>
</#macro>