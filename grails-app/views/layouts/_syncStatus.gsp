<div class="sync-status">
    Upload: <span id="upload-queue">0</span>
    Download: <span id="download-queue">0</span>
</div>

<div id="sync-status-window"></div>

<script>
    function updateQueueStatus() {
        $.getJSON("${createLink(controller: 'planner', action: 'syncStatus')}", function (data) {
            $('#upload-queue').html(data.upload);
            $('#download-queue').html(data.download);
        });
        setTimeout(updateQueueStatus, 10000)
    }

    var statusWindow;
    $(document).ready(function () {
        updateQueueStatus();

        statusWindow = $("#sync-status-window")
            .kendoWindow({
                title: "Jobs Status",
                modal: true,
                visible: false,
                resizable: true,
                width: 1200,
                height: 650,
                close: function () {
                    statusWindowIsOpen = false;
                }
            }).data("kendoWindow");

        $('.sync-status').click(function () {
            statusWindowIsOpen = true;
            refreshStatusWindow();
            statusWindow.center().open();
        });

        var statusWindowIsOpen = false;

        function refreshStatusWindow() {
            if (statusWindowIsOpen)
                statusWindow.refresh({
                    url: '${createLink(controller: 'monitoring', action:'jobs')}'
                });

            setTimeout(function () {
                refreshStatusWindow();
            }, 30000)
        }
    })
</script>