<%
    config.require("formFieldName")
    config.require("options")
%>

<p>
    <label for="${ config.id }-field">
        ${ config.label ?: '' } <% if (config.mandatory) { %>*<% } %>
    </label>
    <select id="${ config.id }-field" name="${ config.formFieldName}" <% if(config.maximumSize) { %> size="${ [config.maximumSize, config.options.size()].min() }" <% } %>/>
        <% if(!config.hideEmptyLabel) { %>
            <option value="">${ config.emptyOptionLabel ?: ''}</option>
        <% } %>
        <% config.options.each {
            def selected = it.selected || it.value == config.initialValue
        %>
            <option value="${ it.value }"  <% if (selected) { %>selected<% } %>/>${ it.label }</option>
        <% } %>
    </select>

    ${ ui.includeFragment("emr", "fieldErrors", [ fieldName: config.formFieldName ]) }
</p>
