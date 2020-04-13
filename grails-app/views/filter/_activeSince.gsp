<div class="field">
    <label for="activeSince">Active Since:</label>
    <input type='text' class="datepicker" name="activeSince" id="activeSince" autocomplete="off" value="${params.activeSince}"/>
</div>

<script type="text/javascript">
    $(function () {
        $('.datepicker').kendoDatePicker({
        });
    });
</script>