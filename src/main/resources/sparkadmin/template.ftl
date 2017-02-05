<#macro template jsIncludes=[]>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <title>Spark Admin</title>

    <meta name="description" content="">
    <meta name="author" content="NextPulse">

    <link rel="stylesheet" href="/css/bootstrap.css">
    <link rel="stylesheet" href="/css/bootstrap-theme.css">
    <link rel="stylesheet" href="/css/style.css">
</head>
<body>

<nav class="navbar navbar-default navbar-static-top">
    <div class="container-fluid">
        <div class="navbar-header">
            <a class="navbar-brand" href="#">
                Spark Admin
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
    <footer class="pull-left col-md-12">
        <hr class="divider">
        <p>
            Powered by <a href="https://github.com/yholkamp/JAdmin">Spark Admin</a>
        </p>
    </footer>
</div>

<script src="/js/lib/jquery-3.1.0.min.js"></script>
<script src="/js/lib/bootstrap.js"></script>
    <#list jsIncludes as include>
    <script src="/js/${include}"></script>
    </#list>
</body>
</html>
</#macro>