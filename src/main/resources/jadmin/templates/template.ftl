<#macro template jsIncludes=[] cssIncludes=[]>
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
        <#nested>
    </div>
    <#include "includes/footer.ftl"/>
</div>

    <#include "includes/scripts.ftl"/>
</body>
</html>
</#macro>