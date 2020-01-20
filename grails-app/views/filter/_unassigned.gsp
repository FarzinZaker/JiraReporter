<div class="field">
    <label for="unassigned" class="switch-label">Unassigned Issues:</label>
    <input type="checkbox" id="unassigned" style="float: right"
           name="unassigned" ${params.unassigned ? 'checked="checked"' : ''}/></div>
<script>
    $(function () {
        $('#unassigned').kendoSwitch({
            messages: {
                checked: "YES",
                unchecked: "NO"
            }
        });
    });
</script>