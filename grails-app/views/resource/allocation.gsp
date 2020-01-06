<!DOCTYPE html>
<!--pro-->
<html>
<head>
    <meta name="layout" content="grid"/>
    <title>Resource Allocation</title>
    <asset:javascript src="gantt_helper.js"/>
    <asset:javascript src="js/jszip.min.js"/>
    <asset:javascript src="js/pako_deflate.min.js"/>
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
<script type="x/kendo-template" id="page-template">
<div class="page-template">
    <div class="header">
        <div style="float: right">Page #: pageNum # of #: totalPages #</div>
        Resource Allocation
    </div>
    <div class="watermark">Aclate</div>
    <div class="footer">
        Page #: pageNum # of #: totalPages #
    </div>
</div>
</script>
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
                        url: "${createLink(action:'allocationJson')}",
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
                            project: {type: "string"},
                            client: {type: "string"},
                            assignee: {type: "string"},
                            originalEstimateSeconds: {type: "number"},
                            remainingEstimateSeconds: {type: "number"},
                            timeSpentSeconds: {type: "number"},
                            key: {type: "string"},
                            summary: {type: "string"}
                        }
                    }
                },
                // pageSize: 7,
                group: [{
                    field: "project", aggregates: [
                        {field: "originalEstimateSeconds", aggregate: "sum"},
                        {field: "remainingEstimateSeconds", aggregate: "sum"},
                        {field: "timeSpentSeconds", aggregate: "sum"}
                    ]
                }, {
                    field: "client", aggregates: [
                        {field: "originalEstimateSeconds", aggregate: "sum"},
                        {field: "remainingEstimateSeconds", aggregate: "sum"},
                        {field: "timeSpentSeconds", aggregate: "sum"}
                    ]
                }, {
                    field: "assignee", aggregates: [
                        {field: "originalEstimateSeconds", aggregate: "sum"},
                        {field: "remainingEstimateSeconds", aggregate: "sum"},
                        {field: "timeSpentSeconds", aggregate: "sum"}
                    ]
                }],
                aggregate: [
                    {field: "originalEstimateSeconds", aggregate: "sum"},
                    {field: "remainingEstimateSeconds", aggregate: "sum"},
                    {field: "timeSpentSeconds", aggregate: "sum"}
                ]
            },
            sortable: true,
            scrollable: false,
            groupable: true,
            // pageable: true,
            toolbar: ["excel"],
            excel: {
                fileName: "Resource Allocation.xlsx",
                proxyURL: "https://demos.telerik.com/kendo-ui/service/export",
                filterable: true
            },
            pdf: {
                allPages: true,
                avoidLinks: true,
                paperSize: "A4",
                margin: { top: "2cm", left: "1cm", right: "1cm", bottom: "1cm" },
                landscape: true,
                repeatHeaders: true,
                template: $("#page-template").html(),
                scale: 0.8
            },
            columns: [
                {
                    field: "summary", title: "Summary", template: '<img src="#: taskIcon #" /> #: summary #'
                },
                {
                    field: "key",
                    title: "Key",
                    template: "<a href='https://jira.devfactory.com/browse/#: key #' target='_blank' class='link'>#: key #</a>",
                },
                {
                    field: "assignee",
                    title: "Assignee",
                    template: '<img class="gantt-avatar" src="#: userIcon #" /> #:assignee#'
                },
                {
                    field: "client",
                    title: "Client",
                    template: "#: client #"
                },
                {
                    field: "project", title: "Project", template: '#: project #'
                },
                {
                    field: "originalEstimateSeconds",
                    title: "Original Estimate",
                    template: '#:formatDuration(originalEstimateSeconds)#',
                    aggregates: ["sum"], groupHeaderColumnTemplate: "#=formatDuration(sum)#"
                },
                {
                    field: "remainingEstimateSeconds",
                    title: "Remaining Estimate",
                    template: '#:formatDuration(remainingEstimateSeconds)#',
                    aggregates: ["sum"], groupHeaderColumnTemplate: "#=formatDuration(sum)#"
                },
                {
                    field: "timeSpentSeconds",
                    title: "Time Spent",
                    template: '#:formatDuration(timeSpentSeconds)#',
                    aggregates: ["sum"], groupHeaderColumnTemplate: "#=formatDuration(sum)#"
                }
            ],
            dataBound: function (e) {
                var grid = this;

                var g = $("#grid").data("kendoGrid");
                for (var i = 0; i < g.columns.length; i++) {
                    g.showColumn(i);
                }
                $("div.k-group-indicator").each(function (i, v) {
                    g.hideColumn($(v).data("field"));
                });

                $(".k-grouping-row").each(function (e) {
                    if ($(this).text().trim().startsWith('Assignee:'))
                        grid.collapseGroup(this);
                });
            }
        });
    });
</script>
</body>
</html>