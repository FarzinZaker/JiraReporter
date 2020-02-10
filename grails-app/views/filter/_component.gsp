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
            labelField: 'text',
            searchField: 'text',
            create: false,
            options: [
                <g:each in="${components?.findAll{it}?.sort{it.fullName}}" var="component">
                {
                    value: '<format:html value="${component.name}"/>',
                    text: '<format:html value="${component.fullName}"/>'
                },
                </g:each>
            ]
        });
    });
</script>