<div class="form-group">
    <label for="input-${input.name}" class="control-label col-sm-2">${input.name}</label>
    <div class="col-sm-10">
        <select id="input-${input.name}" name="${input.name}" class="form-control">
        <#list input.options as option>
            <option value="${option.left!""}" <#if object.properties[input.name]??>selected</#if>>${option.right}</option>
        </#list>
        </select>
    </div>
</div>
