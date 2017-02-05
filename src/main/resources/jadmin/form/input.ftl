<div class="form-group">
    <label for="input-${input.name}" class="control-label col-sm-2">${input.name}</label>
    <div class="col-sm-10">
        <input type="text" class="form-control" id="input-${input.name}" name="${input.name}"
               value="${object.properties[input.name]!""}">
    </div>
</div>
