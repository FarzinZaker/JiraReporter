<%@ page import="org.ocpsoft.prettytime.PrettyTime; java.text.SimpleDateFormat; jirareporter.SyncJobConfig" %>
<!doctype html>
<html lang="en" class="no-js">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta http-equiv="X-UA-Compatible" content="IE=edge"/>
    <title>
        Jobs
    </title>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
    <asset:link rel="icon" href="favicon.ico" type="image/x-ico"/>

    <asset:stylesheet src="application.css"/>
    <asset:stylesheet src="custom.css"/>

    <asset:javascript src="jquery-2.2.0.min.js"/>

    <asset:stylesheet src="datatables.min.css"/>
    <asset:javascript src="datatables.min.js"/>

    <style>
    table {
        width: 100%
    }

    .dataTables_filter, .dt-buttons {
        display: none;
    }
    </style>
</head>

<body>
<g:set var="jobs" value="${SyncJobConfig.list().sort { it.name }}"/>

<g:if test="${jobs?.size()}">
    <table class="pivot" id="Client">
        <thead>
        <tr>
            <td class="string">
                Job
            </td>
            <td class="date">
                Last Execution
            </td>
            <td class="number">
                Duration
            </td>
            <td class="string">
                Error Message
            </td>
            <td class="date">
                Start Date
            </td>
            <td class="date">
                End Date
            </td>
            <td class="number">
                Last Record
            </td>
        </tr>
        </thead>
        <tbody>
        </tbody>
    </table>

    <script language="JavaScript" type="text/javascript">
        $(document).ready(function () {
            $('#Client').DataTable({
                scrollY: false,
                scrollX: true,
                scrollCollapse: true,
                paging: false,
                fixedHeader: true,
                fixedColumns: {
                    leftColumns: 1
                },
                dom: 'Bfrtip',
                buttons: [],
                colReorder: true,
                rowReorder: true,
                // dataSrc: '',
                data: [
                    <g:each in="${jobs}" var="job">
                    [
                        {text: '${job.name}', value: '${job.name}'},
                        {
                            text: '${job.lastExecutionDate ? new PrettyTime().format(job.lastExecutionDate): '-'}',
                            value: ${job.lastExecutionDate?.time ?: 0}
                        },
                        {
                            text: '${job.executionTime ?: '-'}',
                            value: ${job.executionTime ?: 0}
                        },
                        {
                            text: '${job.lastErrorMessage ?: '-'}',
                            value: '${job.lastErrorMessage ?: '-'}'
                        },
                        {
                            text: '${job.startDate ? new SimpleDateFormat('dd MMM, yyyy HH:mm').format(job.startDate): '-'}',
                            value: ${job.startDate?.time ?: 0}
                        },
                        {
                            text: '${job.endDate ? new SimpleDateFormat('dd MMM, yyyy HH:mm').format(job.startDate): '-'}',
                            value: ${job.endDate?.time ?: 0}
                        },
                        {
                            text: '${job.lastRecord ?: '-'}',
                            value: ${job.lastRecord ?: 0}
                        }
                    ],
                    </g:each>
                ],
                "columnDefs": [
                    {
                        render: {
                            _: 'text',
                            sort: 'value'
                        },
                        targets: 'string',
                        type: 'string'
                    },
                    {
                        render: {
                            _: 'text',
                            sort: 'value'
                        },
                        targets: 'number',
                        type: 'num'
                    },
                    {
                        render: {
                            _: 'text',
                            sort: 'value'
                        },
                        targets: 'date',
                        type: 'num'
                    }
                ],
                // 'rowCallback': function (row, data, index) {
                //     console.log(data[1].value);
                //     console.log(new Date().getTime());
                //     console.log(new Date().getTime() - data[1].value)
                //     // if (data[3] > 11.7) {
                //     //     $(row).find('td:eq(3)').css('color', 'red');
                //     // }
                //     // if (data[2].toUpperCase() == 'EE') {
                //     //     $(row).find('td:eq(2)').css('color', 'blue');
                //     // }
                // }
            });
        });
    </script>
</g:if>
<g:else>
    <div class="info" style="margin-top:65px;">
        No records to display
    </div>
</g:else>

</body>
</html>
