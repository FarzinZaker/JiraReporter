<%@ page import="jirareporter.Configuration" %>
<div class="field">
    <label for="component">Component:</label>
    <input id="component" name="component" type="text" value="${params.component}">
</div>
<script>
    $(function () {
        $('#component').selectize({
            plugins: ['remove_button'],
            valueField: 'value',
            labelField: 'value',
            searchField: 'value',
            create: false,
            options: [
                <g:each in="${components}" var="component">
                {value: '${component.name}'},
                </g:each>
            ]
        });
    });
</script>