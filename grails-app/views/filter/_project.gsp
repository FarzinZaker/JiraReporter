<%@ page import="jirareporter.Configuration" %>
<div class="field">
    <label for="project">Project:</label>
    <input id="project" name="project" type="text" value="${params.project}">
</div>
<script>
    $(function () {
        $('#project').selectize({
            plugins: ['remove_button'],
            valueField: 'value',
            labelField: 'text',
            searchField: ['text', 'value'],
            create: false,
            options: [
                <g:each in="${Configuration.projects}" var="${project}">
                {text: '${project.name}', value: '${project.key}'},
                </g:each>
            ]
        });
    });
</script>