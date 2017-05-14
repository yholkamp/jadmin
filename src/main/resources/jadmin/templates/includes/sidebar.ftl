<ul class="nav nav-pills nav-stacked">
  <li><a href="${templateObject.prefix}">Home</a></li>
<#assign tables = templateObject.tables/>
<#list tables as table>
  <li><a href="${templateObject.prefix}/${table}">${table}</a></li>
</#list>
</ul>