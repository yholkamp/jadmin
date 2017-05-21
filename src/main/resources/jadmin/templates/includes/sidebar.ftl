<ul class="nav nav-pills nav-stacked">
  <li><a href="${templateObject.prefix}">Home</a></li>
<#list templateObject.tables as table>
  <li><a href="${templateObject.prefix}/${table}">${ii("resources.${table}")}</a></li>
</#list>
</ul>