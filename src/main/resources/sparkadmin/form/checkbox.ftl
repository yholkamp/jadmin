<div class="form-group">
    <div class="col-sm-offset-2 col-sm-10">
        <div class="checkbox">
            <label>
                <input type="hidden" id="input-${input.name}" name="${input.name}" value="0">
                <input type="checkbox" id="input-${input.name}" name="${input.name}" value="${object[input.name]?? && object[input.name]?then("1", "")!""}"> ${input.name}
            </label>
        </div>
    </div>
</div>