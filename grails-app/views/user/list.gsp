<%--
  Created by IntelliJ IDEA.
  User: root
  Date: 8/14/14
  Time: 4:48 PM
--%>

<%@ page import="jirareporter.Roles" contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main"/>
    <title><g:message code="${params.id}.list"/></title>
</head>

<body>
<div id="content" role="main" class="list-container">
    <h1><g:message code="${params.id}.list"/></h1>

    <div id="grid"></div>

    <div id="roles"></div>
</div>

<script>
    var wnd, rolesTemplate, grid, dataSource;
    $(document).ready(function () {
        dataSource = new kendo.data.DataSource({
            transport: {
                read: {
                    url: '${createLink(action:'listJSON', id:params.id)}',
                    dataType: "json"
                },
                update: {
                    url: '${createLink(action:'save')}',
                    dataType: "json"
                },
                destroy: {
                    url: '${createLink(action:'delete')}',
                    dataType: "json"
                },
                create: {
                    url: '${createLink(action:'save', id: params.id)}',
                    dataType: "json"
                },
                parameterMap: function (options, operation) {
                    if (operation !== "read" && options.models) {
                        return {models: kendo.stringify(options.models)};
                    }
                }
            },
            batch: true,
            pageSize: 20,
            schema: {
                data: 'data',
                total: 'total',
                model: {
                    id: "id",
                    fields: {
                        displayName: {type: "string", validation: {required: true}},
                        username: {type: "string", validation: {required: true}},
                        password: {type: "string"},
                        slackId: {type: "string"},
                        enabled: {type: "boolean"},
                        accountExpired: {type: "boolean"},
                        accountLocked: {type: "boolean"},
                        passwordExpired: {type: "boolean"}
                    }
                }
            }
        });

        grid = $("#grid").kendoGrid({
            dataSource: dataSource,
            pageable: true,
            toolbar: [{name: "create", text: "New User"}],
            columns: [
                {field: "displayName", title: "Display Name"},
                {field: "username", title: "Username"},
                {field: "password", title: "Password", hidden: true},
                {field: "slackId", title: "Slack ID"},
                {field: "enabled", title: "Enabled", editor: enabledEditor, template: enabledTemplate},
                {field: "accountExpired", title: "Account Expired", hidden: true, editor: accountExpiredEditor},
                {field: "accountLocked", title: "Account Locked", hidden: true, editor: accountLockedEditor},
                {field: "passwordExpired", title: "Password Expired", hidden: true, editor: passwordExpiredEditor},
                {command: ["edit", "destroy"], title: "&nbsp;", width: "230px"},
                {command: {text: "Roles", click: manageRoles}, title: " ", width: "120px"},
                <g:if test="${params.id == Roles.MANAGER}">
                {command: {text: "Teams", click: manageTeams}, title: " ", width: "120px"}
                </g:if>
            ],
            editable: "popup",
            edit: function (e) {
                $(e.container).find('input[type="checkbox"]').addClass('k-checkbox');
            }
        });

        wnd = $("#roles")
            .kendoWindow({
                title: "User Roles",
                modal: true,
                visible: false,
                resizable: false,
                width: 400
            }).data("kendoWindow");

    });

    function enabledTemplate(dataItem) {
        return dataItem.enabled ? '<i class="k-icon k-i-check-outline">check</i>' : '<i class="k-icon k-i-close-outline">close</i>'
    }

    function enabledEditor(container, options) {
        var guid = kendo.guid();
        $('<input class="k-checkbox no-margin" id="' + guid + '" type="checkbox" name="enabled" data-type="boolean" data-bind="checked:enabled">').appendTo(container);
        $('<label class="k-checkbox-label no-margin" for="' + guid + '">&#8203;</label>').appendTo(container);
    }

    function accountExpiredEditor(container, options) {
        var guid = kendo.guid();
        $('<input class="k-checkbox no-margin" id="' + guid + '" type="checkbox" name="accountExpired" data-type="boolean" data-bind="checked:accountExpired">').appendTo(container);
        $('<label class="k-checkbox-label no-margin" for="' + guid + '">&#8203;</label>').appendTo(container);
    }

    function accountLockedEditor(container, options) {
        var guid = kendo.guid();
        $('<input class="k-checkbox no-margin" id="' + guid + '" type="checkbox" name="accountLocked" data-type="boolean" data-bind="checked:accountLocked">').appendTo(container);
        $('<label class="k-checkbox-label no-margin" for="' + guid + '">&#8203;</label>').appendTo(container);
    }

    function passwordExpiredEditor(container, options) {
        var guid = kendo.guid();
        $('<input class="k-checkbox no-margin" id="' + guid + '" type="checkbox" name="passwordExpired" data-type="boolean" data-bind="checked:passwordExpired">').appendTo(container);
        $('<label class="k-checkbox-label no-margin" for="' + guid + '">&#8203;</label>').appendTo(container);
    }

    function manageRoles(e) {
        e.preventDefault();

        var dataItem = this.dataItem($(e.currentTarget).closest("tr"));
        wnd.refresh({
            url: '${createLink(action:'roles')}',
            data: {id: dataItem.id}
        });
        wnd.center().open();
    }

    function manageTeams(e) {
        e.preventDefault();

        var dataItem = this.dataItem($(e.currentTarget).closest("tr"));
        wnd.refresh({
            url: '${createLink(action:'teams')}',
            data: {id: dataItem.id}
        });
        wnd.center().open();
    }
</script>
</body>
</html>