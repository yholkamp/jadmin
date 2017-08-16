<div class="form-group">
    <label for="input-${input.name}" class="control-label col-sm-2">${ii("resources.${resource.tableName}.${input.name}")}</label>
    <div class="col-sm-10">
        <select id="input-${input.name}" name="${input.name}" class="form-control">
        <#list input.options as option>
            <#assign inputName = object.properties[input.name]??>
            <#if inputName?is_number>
                <option value="${option.left!""}" <#if inputName?? && option.left?? && inputName?c == option.left!"">selected</#if>>${option.right}</option>
            <#else>
            <option value="${option.left!""}" <#if inputName?? && option.left?? && inputName!"" == option.left!"">selected</#if>>${option.right}</option>
        </#if>
        </#list>
        </select>
    </div>
</div>
