<%@ page import="jirareporter.Configuration" %>
<div class="field">
    <label for="labels">Labels:</label>
    <input id="labels" name="labels" type="text" value="${params.labels}">
</div>
<script>
    $(function () {
        $('#labels').selectize({
            plugins: ['remove_button'],
            valueField: 'value',
            labelField: 'text',
            searchField: 'text',
            create: false,
            options: [
                <g:each in="${labels?.findAll{it}?.sort{it.name}}" var="label">
                {
                    value: '${label.name}',
                    text: '${label.name}'
                },
                </g:each>
            ]
        });
    });
</script>