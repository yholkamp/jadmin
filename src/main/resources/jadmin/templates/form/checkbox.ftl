<div class="form-group">
    <div class="col-sm-offset-2 col-sm-10">
        <div class="checkbox">
            <label>
                <input type="checkbox" id="input-${input.name}" name="${input.name}" value="true" <#if object.properties[input.name]?? && object.properties[input.name]>checked</#if>> ${input.name}
                <input type="hidden" id="input-${input.name}" name="${input.name}" value="false">
            </label>
        </div>
    </div>
</div>