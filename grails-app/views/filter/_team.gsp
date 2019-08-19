<%@ page import="jirareporter.Configuration" %>
<div class="field">
    <label for="team">Team:</label>
    <input id="team" name="team" type="text" value="${params.team}">
</div>
<script>
    $(function () {
        $('#team').selectize({
            plugins: ['remove_button'],
            valueField: 'value',
            labelField: 'value',
            searchField: 'value',
            create: false,
            options: [
                <g:each in="${Configuration.crossOverTeams}" var="team">
                {value: '${team.name}'},
                </g:each>
            ]
        });
    });
</script>