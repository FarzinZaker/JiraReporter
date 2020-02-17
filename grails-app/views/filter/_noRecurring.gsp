<div class="field">
    <label for="noRecurring" class="switch-label">Exclude Recurring Issues:</label>
    <input type="checkbox" id="noRecurring" style="float: right"
           name="noRecurring" ${params.noRecurring ? 'checked="checked"' : ''}/></div>
<script>
    $(function () {
        $('#noRecurring').kendoSwitch({
            noRecurring: {
                checked: "YES",
                unchecked: "NO"
            }
        });
    });
</script>