<%@ page import="jirareporter.Team; jirareporter.Configuration" %>
<div class="field">
    <label for="team">Team:</label>
    <input id="team" name="team" type="text" value="${params.team}">
</div>
<script>
    $(function () {
        $('#team').selectize({
            plugins: ['remove_button'],
            valueField: 'value',
            labelField: 'text',
            searchField: 'text',
            create: false,
            options: [
                <g:each in="${Team.list()}" var="team">
                {value: ${team.id}, text: '${team.name}'},
                </g:each>
            ]
        });
    });
</script>