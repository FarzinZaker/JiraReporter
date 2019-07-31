<div class="field">
    <label for="worklogTypes">Worklog Type:</label>
    <input id="worklogTypes" name="worklogTypes" type="text" value="${params.worklogTypes}">
</div>
<script>
    $(function () {
        $('#worklogTypes').selectize({
            plugins: ['remove_button'],
            valueField: 'value',
            labelField: 'text',
            searchField: ['text', 'value'],
            create: false,
            options: [
                {text: 'Billable', value: 'billable'},
                {text: 'Non-Billable', value: 'non-billable'}
            ]
        });
    });
</script>