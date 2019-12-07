<!DOCTYPE html>
<!--pro-->
<html>
<head>
    <meta name="layout" content="grid"/>
    <title>Missing Estimates</title>
</head>

<body>

<div id="content" role="main">
    <table class="main-body">
        <tr>
            <td class="filter-column">
                <g:render template="filter"/>
            </td>

            <td class="expander-cell">
                <g:render template="/filter/expander"/>
            </td>

            <td class="body-column">

                <div id="grid"></div>
            </td>
        </tr>
    </table>
</div>
<script>
    function objectifyForm(formArray) {//serialize data function

        var returnArray = {};
        for (var i = 0; i < formArray.length; i++) {
            returnArray[formArray[i]['name']] = formArray[i]['value'];
        }
        return returnArray;
    }

    $(document).ready(function () {
        $("#grid").kendoGrid({
            dataSource: {
                transport: {
                    read: {
                        url: "${createLink(action:'estimateJson')}",
                        data: function () {
                            console.log($('#filterForm').serializeArray());
                            return objectifyForm($('#filterForm').serializeArray());
                        }
                    },
                    dataType: "json"
                },
                schema: {
                    model: {
                        fields: {
                            key: {type: "string"},
                            summary: {type: "string"},
                            assignee: {type: "string"}
                        }
                    }
                },
                // pageSize: 7,
                group: {
                    field: "assignee", aggregates: [
                        {field: "key", aggregate: "count"}
                    ]
                },
                aggregate: [{field: "key", aggregate: "count"}]
            },
            sortable: true,
            scrollable: false,
            // pageable: true,
            columns: [
                {
                    field: "summary", title: "Summary", template: '<img src="#: tastIcon #" /> #: summary #'
                },
                {
                    field: "key",
                    title: "Key",
                    template: "<a href='https://jira.devfactory.com/browse/#: key #' target='_blank' class='link'>#: key #</a>",
                    aggregates: ["count"], groupHeaderColumnTemplate: "#=count# Tasks"
                },
                {
                    field: "assignee",
                    title: "Assignee",
                    template: '<img class="gantt-avatar" src="#: userIcon #" /> #:assignee#'
                }
                // {field: "assignee", title: "Assignee", aggregates: ["count"], groupHeaderColumnTemplate: "Tasks: #=count#"}
            ],
            dataBound: function (e) {
                var grid = this;
                $(".k-grouping-row").each(function (e) {
                    grid.collapseGroup(this);
                });
            }
        });
    });
</script>
</body>
</html>