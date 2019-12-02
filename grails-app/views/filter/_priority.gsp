<%@ page import="jirareporter.Priority; jirareporter.Configuration" %>
<div class="field">
    <label for="priority">Priority:</label>
    <input id="priority" name="priority" type="text" value="${params.priority}">
</div>
<script>
    $(function () {
        $('#priority').selectize({
            plugins: ['remove_button'],
            valueField: 'value',
            labelField: 'value',
            searchField: 'value',
            create: false,
            options: [
                <g:each in="${Priority.list()}" var="priority">
                {value: '${priority?.name}'},
                </g:each>
            ]
        });
    });
</script>