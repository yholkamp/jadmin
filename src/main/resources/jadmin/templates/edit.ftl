<@root.template jsIncludes=["edit.js"]>
<div class="row">
    <div class="panel panel-default">
        <div class="panel-heading">
            ${i("view.edit_resource", ii("resources.${resource.tableName}"))}
        </div>
        <div class="panel-body">
            <form class="form-horizontal" method="post">
                <#list resource.primaryKeys as key>
                    <#if object.properties[key]??>
                        <input type="hidden" name="${key}" value="${object.properties[key]}">
                    </#if>
                </#list>
                <#list resource.formPage as element>
                    <#include "form/${element.templateName}">
                </#list>
            </form>
        </div>
    </div>
</div>
</@root.template>