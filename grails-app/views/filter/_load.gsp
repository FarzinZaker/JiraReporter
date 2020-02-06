<%@ page import="jirareporter.JiraUser; jirareporter.FilterUser; jirareporter.User; jirareporter.Filter" %>

<g:set var="user"
       value="${User.findByUsernameOrDisplayName(sec.username()?.toString(), sec.username()?.toString())}"/>
<div class="filterMenuContainer">
    <ul id="filterMenu" style="display: none;">
        <li>
            <span class="k-icon k-i-filter"></span> My Filters
            <ul>
                <g:each in="${Filter.findAllByOwner(user)}" var="filter">
                    <li>
                        <a href="${createLink(controller: 'filter', action: 'load', params: [c: params.controller, a: params.action, id: filter.id])}">${filter.name}</a>
                        <ul>
                            <li>
                                <a href="${createLink(controller: 'filter', action: 'load', params: [c: params.controller, a: params.action, id: filter.id])}"><span
                                        class="k-icon k-i-download"></span> Load</a>
                            </li>
                            <li>
                                <a href="javascript:shareForm(${filter.id})"><span
                                        class="k-icon k-i-share"></span> Share</a>
                            </li>
                            <li class="k-separator"></li>
                            <li>
                                <a href="javascript:renameForm(${filter.id})"><span
                                        class="k-icon k-i-textbox"></span> Rename</a>
                            </li>
                            <li>
                                <a href="javascript:updateFilter(${filter.id}, '${filter.name}')"><span
                                        class="k-icon k-i-check"></span> Update</a>
                            </li>
                            <li>
                                <a href="javascript:copyForm(${filter.id})"><span
                                        class="k-icon k-i-copy"></span> Copy</a>
                            </li>
                            <li class="k-separator"></li>
                            <li>
                                <a href="javascript:deleteFilter(${filter.id}, '${filter.name}')"><span
                                        class="k-icon k-i-delete"></span> Delete</a>
                            </li>
                        </ul>
                    </li>
                </g:each>
            </ul>
        </li>
        <li>
            <span class="k-icon k-i-share"></span> Shared With Me
            <ul>
                <g:each in="${FilterUser.findAllByUser(user).collect { it.filter }}" var="filter">
                    <li>
                        <a href="${createLink(controller: 'filter', action: 'load', params: [c: params.controller, a: params.action, id: filter.id])}">${filter.name}</a>
                        <ul>
                            <li>
                                <a href="${createLink(controller: 'filter', action: 'load', params: [c: params.controller, a: params.action, id: filter.id])}"><span
                                        class="k-icon k-i-download"></span> Load</a>
                            </li>
                            <li>
                                <a href="javascript:copyForm(${filter.id})"><span
                                        class="k-icon k-i-copy"></span> Copy</a>
                            </li>
                        </ul>
                    </li>
                </g:each>
            </ul>
        </li>
    </ul>
</div>
<g:if test="${params.id}">
    <g:set var="currentFilter" value="${Filter.get(params.id)}"/>
    <g:if test="${currentFilter}">
        <div class="currentFilterLabel">Current Filter:</div>

        <h2 class="currentFilter">
            <span class="k-icon k-i-filter"></span> ${currentFilter.name}
        </h2>
    </g:if>
</g:if>

<div id="shareWindow"></div>
<script language="JavaScript" type="text/javascript">
    var shareWindow;
    $(document).ready(function () {

        $("#filterMenu").show().kendoMenu().slideDown();

        shareWindow = $("#shareWindow")
            .kendoWindow({
                title: "Share Filter",
                modal: true,
                visible: false,
                resizable: false,
                width: 400
            }).data("kendoWindow");
    });


    function createForm(id) {
        filterWindow.refresh({
            url: '${createLink(controller: 'filter', action:'createForm', params: [c: controllerName, a: actionName])}/',
            data: $('#filterForm').serialize()
        });
        filterWindow.center().open().title('Save Filter');
    }


    function shareForm(id) {
        shareWindow.refresh({
            url: '${createLink(controller: 'filter', action:'shareForm')}/' + id
        });
        shareWindow.center().open().title('Share Filter');
    }

    function renameForm(id) {
        filterWindow.refresh({
            url: '${createLink(controller: 'filter', action:'renameForm')}/' + id + '?c=${controllerName}&a=${actionName}'
        });
        filterWindow.center().open().title('Rename Filter');
    }

    function updateFilter(id, name) {
        kendo.confirm("Are you sure about updating <b>" + name + "</b> with the current filters? <br/><br/>You cannot undo this action after the update.").then(function () {
            $.ajax({
                url: '${createLink(controller: 'filter', action:'update')}/' + id,
                data: $('#filterForm').serialize()
            }).done(function (response) {
                window.location.href = '${createLink(controller: 'filter', action: 'load')}/' + id + '?c=${controllerName}&a=${actionName}';
            });
        }, function () {
        });
        $('.k-confirm .k-window-title.k-dialog-title').text('Confirmation');
    }

    function copyForm(id) {
        filterWindow.refresh({
            url: '${createLink(controller: 'filter', action:'copyForm')}/' + id + '?c=${controllerName}&a=${actionName}'
        });
        filterWindow.center().open().title('Copy Filter');
    }

    function deleteFilter(id, name) {
        kendo.confirm("Are you sure about deleting <b>" + name + "</b>? <br/><br/>You cannot undo this action after deletion.").then(function () {
            $.ajax({
                url: '${createLink(controller: 'filter', action:'delete')}/' + id
            }).done(function (response) {
                location.reload(true);
            });
        }, function () {
        });
        $('.k-confirm .k-window-title.k-dialog-title').text('Confirmation');
    }
</script>