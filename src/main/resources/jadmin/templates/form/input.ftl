<div class="form-group">
    <label for="input-${input.name}" class="control-label col-sm-2">${ii("resources.${resource.tableName}.${input.name}")}</label>
    <div class="col-sm-10">
        <input type="${input.inputType}" class="form-control" id="input-${input.name}" name="${input.name}"
               <#if input.inputType != 'password'>value="${object.properties[input.name]!""}"</#if>>
    </div>
</div>
