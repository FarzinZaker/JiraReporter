<%--
  Created by IntelliJ IDEA.
  User: root
  Date: 8/14/14
  Time: 4:48 PM
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main"/>
    <title><g:message code="reminder.list"/></title>

    <style>
    div.k-edit-form-container {
        width: auto;
        height: auto;
    }

    .k-edit-field, .k-edit-form-container .editor-field{
        width: auto;
    }
    </style>
</head>

<body>
<div id="content" role="main" class="list-container">
    <h1><g:message code="reminder.list"/></h1>

    <div id="grid"></div>
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
                        name: {type: "string", validation: {required: true}},
                        template: {type: "string", validation: {required: true}},
                        query: {type: "string", validation: {required: true}},
                        emptyMessage: {type: "string", validation: {required: true}}
                    }
                }
            }
        });

        grid = $("#grid").kendoGrid({
            dataSource: dataSource,
            pageable: true,
            toolbar: [{name: "create", text: "New Reminder"}],
            columns: [
                {field: "name", title: "Name", editor: textEditor},
                {field: "template", title: "Template", editor: textEditor},
                {field: "query", title: "Query", editor: textEditor},
                {field: "emptyMessage", title: "Empty Message", editor: textEditor},
                {command: ["edit", "destroy"], title: "&nbsp;", width: "230px"}
            ],
            editable: "popup",
            edit: function (e) {
                $(e.container).find('input[type="checkbox"]').addClass('k-checkbox');
            }
        });
    });


    var textEditor = function (container, options) {
        $('<textarea data-bind="value: ' + options.field + '"></textarea>').appendTo(container);
    };
</script>
</body>
</html>