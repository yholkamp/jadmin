<ul class="nav nav-pills nav-stacked">
  <li <#if !resource??>class="active"</#if>><a href="${templateObject.prefix}">Home</a></li>
<#list templateObject.tables as table>
  <li <#if resource?? && resource.tableName == table>class="active"</#if>><a href="${templateObject.prefix}/${table}">${ii("resources.${table}")}</a></li>
</#list>
</ul>