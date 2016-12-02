<@root.template jsIncludes=[]>
<h1>${resource.tableName}</h1>
<div class="row">
    <table class="table">
        <thead>
        <tr>
					<#list headers as header>
              <th>${header}</th>
					</#list>
        </tr>
        </thead>
        <tbody>
					<#list rows as row>
          <tr>
						<#list headers as header>
                <td>${row[header]!}</td>
						</#list>
              <td>
                  <a href="${templateObject.prefix}/${resource.tableName}/${row['id']}" class="btn btn-info">Edit</a>
              </td>
          </tr>
					</#list>
        </tbody>
    </table>
</div>
</@root.template>