<%@ page import="jirareporter.Roles" %>
<div class="k-edit-form-container manual-dialog">
    <form id="roles_${params.id}">
        <g:each in="${Roles.ALL}" var="role">
            <div class="k-edit-label k-roles-label">
                <label for="${role}"><g:message code="${role}"/></label>
            </div>

            <div data-container-for="enabled" class="k-edit-field">
                <input type="checkbox" class="roles" name="${role}" ${roles.contains(role) ? 'checked="checked"' : ''}/>
            </div>
        </g:each>
    </form>

    <div class="k-edit-buttons k-state-default">
        <a role="button" class="k-button k-button-icontext k-primary k-grid-update" href="javascript:save()">
            <span class="k-icon k-i-check"></span>Update
        </a>
        <a role="button" class="k-button k-button-icontext k-grid-cancel" href="javascript:close()">
            <span class="k-icon k-i-cancel"></span>Cancel
        </a>
    </div>
</div>
<script>
    (function () {
        $(".roles").kendoSwitch({
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
            url: '${createLink(action:'saveRoles', id:params.id)}',
            data: $('#roles_${params.id}').serialize()
        }).done(function () {
            close();
            dataSource.read();
        });
    }
</script>

