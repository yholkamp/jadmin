<#list element.inputs as input>
    <#include input.templateName>
<#else>
<p>No editable fields available.</p>
</#list>