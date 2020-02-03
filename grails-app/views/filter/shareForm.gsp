<%@ page import="jirareporter.TeamManager; jirareporter.User; jirareporter.Roles" %>
<div class="k-edit-form-container manual-dialog">
    <form id="share">
        <input type="hidden" name="id" value="${params.id}"/>

        <div class="field" style="margin-bottom: 150px;">
            <label for="share_user">Users:</label>
            <input id="share_user" name="users" type="text"
                   value="${users?.collect { it.id }?.join(',') ?: ''}">
        </div>
    </form>

    <div class="k-edit-buttons k-state-default">
        <a role="button" class="k-button k-button-icontext k-primary k-grid-update" href="javascript:shareFilter()">
            <span class="k-icon k-i-share"></span>Share
        </a>
        <a role="button" class="k-button k-button-icontext k-grid-cancel" href="javascript:shareWindow.close()">
            <span class="k-icon k-i-cancel"></span>Cancel
        </a>
    </div>
</div>
<script>
    $(function () {
        $('#share_user').selectize({
            plugins: ['remove_button'],
            valueField: 'id',
            labelField: 'name',
            searchField: 'name',
            create: false,
            options: [
                <g:each in="${User.list()}" var="${user}">
                {name: '${user.displayName}', id: '${user.id}'},
                </g:each>
            ]
        });
    });

    function shareFilter() {
        $.ajax({
            url: '${createLink(controller: 'filter', action:'share')}',
            data: $('#share').serialize()
        }).done(function (response) {
            if (response.toString() === '1') {
                shareWindow.close()
            }
        });
    }
</script>
