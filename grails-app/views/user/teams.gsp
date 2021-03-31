<%@ page import="jirareporter.Team" %>
<div class="k-edit-form-container manual-dialog">
    <form id="teams_${params.id}">
        <g:each in="${Team.findAllByDeleted(false)}" var="team">
            <div class="k-edit-label k-teams-label">
                <label for="team_${team.id}">${team.name}</label>
            </div>

            <div data-container-for="enabled" class="k-edit-field">
                <input type="checkbox" class="teams" id="team_${team.id}" name="team_${team.id}" ${teams.collect {
                    it.id
                }.contains(team.id) ? 'checked="checked"' : ''}/>
            </div>
        </g:each>
    </form>

    <div class="k-edit-buttons k-state-default">
        <a team="button" class="k-button k-button-icontext k-primary k-grid-update" href="javascript:save()">
            <span class="k-icon k-i-check"></span>Update
        </a>
        <a team="button" class="k-button k-button-icontext k-grid-cancel" href="javascript:close()">
            <span class="k-icon k-i-cancel"></span>Cancel
        </a>
    </div>
</div>
<script>
    (function () {
        $(".teams").kendoSwitch({
            messages: {
                checked: "YES",
                unchecked: "NO"
            }
        });
    })();

    function close() {
        wnd.close()
    }

    function save() {
        $.ajax({
            url: '${createLink(action:'saveTeams', id:params.id)}',
            data: $('#teams_${params.id}').serialize()
        }).done(function () {
            close();
            dataSource.read();
        });
    }
</script>

