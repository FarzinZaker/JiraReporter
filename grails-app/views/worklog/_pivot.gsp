<g:set var="users" value="${data.keySet()}"/>
<g:set var="others" value="${new HashSet()}"/>
<g:each in="${data}" var="user">
    <g:each in="${user.value.others}" var="other">
        <g:set var="others" value="${others + other.key}"/>
    </g:each>
</g:each>
<h2 class="tableHeader">${label}s</h2>
<g:if test="${data?.size()}">
    <table class="pivot" id="${label.replace(' ', '_')}">
        <thead>
        <tr>
            <td rowspan="2">
                ${label}
            </td>
            <g:each in="${users}" var="user">
                <td class="user center" colspan="2">
                    <g:render template="user" model="${[user: data[user].data]}"/>
                </td>
            </g:each>
        </tr>
        <tr>
            <g:each in="${users}" var="user">
                <td class="user center">
                    Time Spent
                </td>
                <td class="user center">
                    Tasks
                </td>
            </g:each>
        </tr>
        </thead>
        <tbody>
        %{--    <g:each in="${others}" var="other">--}%
        %{--        <tr>--}%
        %{--            <td class="other">--}%
        %{--                ${other}--}%
        %{--            </td>--}%
        %{--            <g:each in="${users}" var="user">--}%
        %{--                <td class="timeSpent center">--}%
        %{--                    ${data[user].others[other]?.timeSpendSeconds ?: '-'}--}%
        %{--                </td>--}%
        %{--            </g:each>--}%
        %{--        </tr>--}%
        %{--    </g:each>--}%
        </tbody>
    </table>

    <script language="JavaScript" type="text/javascript">
        $(document).ready(function () {
            $('#${label.replace(' ', '_')}').DataTable({
                scrollY: "400px",
                scrollX: true,
                scrollCollapse: true,
                paging: false,
                fixedHeader: true,
                fixedColumns: {
                    leftColumns: 1
                },
                dom: 'Bfrtip',
                buttons: [
                    'copy', 'csv', 'excel'
                ],
                colReorder: true,
                rowReorder: true,
                // dataSrc: '',
                data: [
                    <g:each in="${others}" var="other">
                    [
                        {text: '${other}', value: '${other}'},
                        <g:each in="${users}" var="user" status="i">
                        {
                            text: '${data[user].others[other]?.timeSpent ?: '-'}',
                            value: '${data[user].others[other]?.timeSpendSeconds ?: '0'}'
                        },
                        {
                            text: '${data[user].others[other]?.tasksCount ?: '-'}',
                            value: '${data[user].others[other]?.tasksCount ?: '0'}'
                        },
                        </g:each>
                    ],
                    </g:each>
                ],
                "columnDefs": [
                    {
                        render: {
                            _: 'text',
                            sort: 'value'
                        },
                        targets: 'user',
                        type: 'num'
                    },
                    {
                        render: {
                            _: 'text',
                            sort: 'value'
                        },
                        targets: 0,
                        type: 'string'
                    }
                ]
            });
        });
    </script>
</g:if>
<g:else>
    <div class="info" style="margin-top:65px;">
        No records to display
    </div>
</g:else>