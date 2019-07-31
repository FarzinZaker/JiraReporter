<%@ page import="jirareporter.Configuration" %>
<div class="field">
    <label for="status">Status:</label>
    <input id="status" name="status" type="text" value="${params.status}">
</div>
<script>
    $(function () {
        $('#status').selectize({
            plugins: ['remove_button'],
            valueField: 'value',
            labelField: 'text',
            searchField: ['text', 'value'],
            create: false,
            options: [
                <g:each in="${Configuration.statusList}" var="status">
                {text: '${status.name}', value: '${status.name}'},
                </g:each>
            ]
        });
    });
</script>