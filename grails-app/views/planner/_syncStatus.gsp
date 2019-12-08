<div class="sync-status">
    Jira: <span id="upload-queue">0</span>
    Planner: <span id="download-queue">0</span>
</div>

<script>
    function updateQueueStatus() {
        $.getJSON("${createLink(action: 'syncStatus')}", function (data) {
            $('#upload-queue').html(data.upload);
            $('#download-queue').html(data.download);
        });
        setTimeout(updateQueueStatus, 10000)
    }

    $(document).ready(function () {
        updateQueueStatus();
    })
</script>