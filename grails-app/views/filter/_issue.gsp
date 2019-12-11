
<div class="field">
    <label for="issue">Issue:</label>
    <input id="issue" name="issue" type="text" value="${params.issue}">
</div>
<script>
    $(function() {
        $('#issue').selectize({
            plugins: ['remove_button'],
            valueField: 'value',
            labelField: 'value',
            searchField: 'value',
            options: [],
            create: false,
            load: function(query, callback) {
                if (!query.length) return callback();
                $.ajax({
                    url: '${createLink(controller: 'issue', action: 'search')}',
                    type: 'GET',
                    data: {
                        id: query
                    },
                    error: function() {
                        callback();
                    },
                    success: function(res) {
                        callback(res);
                    }
                });
            }
        });
    });
</script>