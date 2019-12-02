<%@ page import="jirareporter.Configuration" %>
<div class="field">
    <label for="client">Client:</label>
    <input id="client" name="client" type="text" value="${params.client}">
</div>
<script>
    $(function () {
        $('#client').selectize({
            plugins: ['remove_button'],
            valueField: 'value',
            labelField: 'text',
            searchField: 'text',
            create: false,
            options: [
                <g:each in="${clients?.findAll{it}?.sort{it.name}}" var="client">
                {
                    value: '${client.name}',
                    text: '${client.name}'
                },
                </g:each>
            ]
        });
    });
</script>