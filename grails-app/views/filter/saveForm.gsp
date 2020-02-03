<%@ page import="grails.converters.JSON; jirareporter.Roles" %>
<g:set var="filterName" value="${filter?.name ?: ''}"/>
<g:if test="${actionName == 'copyForm'}">
    <g:set var="filterName" value="Copy of ${filterName}"/>
</g:if>
<div class="k-edit-form-container manual-dialog">
    <form id="filter">
        <input type="hidden" name="data" value="${parameters as JSON}"/>

        <div class="field">
            <label for="name">Name:</label>
            <input id="name" name="name" type="text" class="k-textbox" value="${filterName}">
        </div>
    </form>

    <div class="k-edit-buttons k-state-default">
        <g:if test="${actionName == 'renameForm'}">
            <a role="button" class="k-button k-button-icontext k-primary k-grid-update"
               href="javascript:saveFilter(${id})">
                <span class="k-icon k-i-textbox"></span>Rename
            </a>
        </g:if>
        <g:if test="${actionName == 'copyForm'}">
            <a role="button" class="k-button k-button-icontext k-primary k-grid-update"
               href="javascript:saveFilter()">
                <span class="k-icon k-i-copy"></span>Copy
            </a>
        </g:if>
        <g:if test="${actionName == 'createForm'}">
            <a role="button" class="k-button k-button-icontext k-primary k-grid-update"
               href="javascript:saveFilter()">
                <span class="k-icon k-i-save"></span>Save
            </a>
        </g:if>
        <a role="button" class="k-button k-button-icontext k-grid-cancel" href="javascript:close()">
            <span class="k-icon k-i-cancel"></span>Cancel
        </a>
    </div>
</div>
<script>

    function close() {
        filterWindow.close()
    }

    function saveFilter(id) {
        $.ajax({
            url: '${createLink(controller: 'filter', action:'save')}/' + (id ? id : ''),
            data: $('#filter').serialize()
        }).done(function (response) {
            if (response.toString() != '0') {
                close();
                window.location.href = '${createLink(controller: 'filter', action: 'load')}/' + response + '?c=${params.c}&a=${params.a}';
            }
        });
    }
</script>

