<%@ page import="jirareporter.Roles; jirareporter.TeamManager; jirareporter.User; jirareporter.Team; jirareporter.Configuration" %>

<g:set var="user" value="${User.findByUsernameOrDisplayName(sec.username(), sec.username())}"/>
<sec:ifAnyGranted roles="${[Roles.ADMIN, Roles.MANAGER].join(',')}">
    <g:set var="teams" value="${TeamManager.findAllByManager(user).collect { it.team }}"/>
    <g:if test="${teams.size()}">
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
                        <g:each in="${teams}" var="team">
                        {value: ${team.id}, text: '${team.name}'},
                        </g:each>
                    ]
                });
            });
        </script>
    </g:if>
</sec:ifAnyGranted>