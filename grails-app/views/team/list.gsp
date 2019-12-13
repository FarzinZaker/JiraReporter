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
    <title><g:message code="team.list"/></title>
</head>

<body>
<div id="content" role="main" class="list-container">
    <h1><g:message code="team.list"/></h1>

    <div id="grid"></div>
</div>

<script>
    var wnd, grid, dataSource;
    $(document).ready(function () {
        dataSource = new kendo.data.DataSource({
            transport: {
                read: {
                    url: '${createLink(action:'listJSON')}',
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
                        xoName: {type: "string", validation: {required: true}},
                        xoKey: {type: "string", validation: {required: true}},
                        xoManagerId: {type: "integer"}
                    }
                }
            }
        });

        grid = $("#grid").kendoGrid({
            dataSource: dataSource,
            pageable: true,
            toolbar: [{name: "create", text: "New Team"}],
            columns: [
                {field: "name", title: "Name"},
                {field: "xoName", title: "CrossOver Name"},
                {field: "xoKey", title: "CrossOver Id"},
                {field: "xoManagerId", title: "CrossOver Manager Id"},
                {command: ["edit", "destroy"], title: "&nbsp;", width: "230px"}
            ],
            editable: "popup"
        });

    });
</script>
</body>
</html>