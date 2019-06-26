<div class="field">
    <label for="issueType">Issue Type:</label>
    <input id="issueType" name="issueType" style="width: 400px;" type="text" value="${params.issueType}">
</div>
<script>
    $(function () {
        $('#issueType').selectize({
            plugins: ['remove_button'],
            valueField: 'value',
            labelField: 'value',
            searchField: 'value',
            create: false,
            options: [
                {value:'Bugfix'},
                {value:'Defect'},
                {value:'Development'},
                {value:'Documentation'},
                {value:'Pairing'},
                {value:'R&D'},
                {value:'Story'},
                {value:'Task'},
                {value:'Test'},
                {value:'Bugfix Sub-Task'},
                {value:'Development Sub-Task'},
                {value:'Documentation Sub-Task'},
                {value:'Pairing Sub-Task'},
                {value:'R&D Sub-Task'},
                {value:'Sub-task'},
                {value:'Test Sub-Task'}
            ]
        });
    });
</script>