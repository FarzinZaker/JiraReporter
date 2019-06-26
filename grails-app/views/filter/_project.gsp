<div class="field">
    <label for="project">Project:</label>
    <input id="project" name="project" style="width: 400px;" type="text" value="${params.project}">
</div>
<script>
    $(function () {
        $('#project').selectize({
            plugins: ['remove_button'],
            valueField: 'value',
            labelField: 'text',
            searchField: ['text','value'],
            create: false,
            options: [
                <g:each in=""
                {text: 'Platinum-TAKE', value: 'PLTAKE'},
                {text: 'Platinum-Beckon', value: 'PLBECK'},
                {text: 'Platinum-NorthPlains', value: 'PLNP'},
                {text: 'Platinum-SMS', value: 'PLSMS'}
            ]
        });
    });
</script>