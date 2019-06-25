<div class="field">
    <label for="from">From:</label>
    <input type='text' class="datepicker" name="from" id="from" readonly value="${params.from}"/>
</div>

<div class="field">
    <label for="from">To:</label>
    <input type='text' class="datepicker" name="to" id="to" readonly value="${params.to}"/>
</div>
<script type="text/javascript">
    $(function () {
        $('.datepicker').datepicker();
    });
</script>