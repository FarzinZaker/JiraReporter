<%@ page import="jirareporter.TeamManager; jirareporter.User; jirareporter.Roles" %>
<g:set var="user" value="${User.findByUsernameOrDisplayName(sec.username(), sec.username())}"/>
<sec:ifAnyGranted roles="${[Roles.ADMIN, Roles.MANAGER].join(',')}">
    <g:set var="teams" value="${TeamManager.findAllByManager(user).collect { it.team }}"/>
    <g:if test="${teams.size()}">
        <div class="field">
            <label for="user">User:</label>
            <input id="user" name="user" type="text" value="${params.user}">
        </div>
        <script>
            $(function () {
                $('#user').selectize({
                    plugins: ['remove_button'],
                    valueField: 'value',
                    labelField: 'value',
                    searchField: 'value',
                    options: [],
                    create: false,
                    load: function (query, callback) {
                        if (!query.length) return callback();
                        $.ajax({
                            url: '${createLink(controller: 'user', action: 'search')}',
                            type: 'GET',
                            data: {
                                id: query
                            },
                            error: function () {
                                callback();
                            },
                            success: function (res) {
                                callback(res);
                            }
                        });
                    }
                });
            });
        </script>
    </g:if>
</sec:ifAnyGranted>