<div id="accomplishmentsToolbar" style="margin-top:10px;"></div>

<script>

    $(document).ready(function () {
        $("#accomplishmentsToolbar").kendoToolBar({
            items: [
                {
                    type: "button", icon: "excel", text: 'Export to Excel', click: function () {
                        $("#filterForm").attr('action', '${createLink(controller: 'export', action: 'tasks')}');
                        $('#submit').click();
                        $("#filterForm").attr('action', '${createLink(controller: 'worklog', action: 'report')}');
                    }
                }
            ]
        });
    });
</script>