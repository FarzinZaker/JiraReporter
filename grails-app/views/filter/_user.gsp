
<div class="field">
    <label for="user">User:</label>
    <input id="user" name="user" type="text" value="${params.user}">
</div>
<script>
    $(function() {
        $('#user').selectize({
            plugins: ['remove_button'],
            valueField: 'value',
            labelField: 'value',
            searchField: 'value',
            options: [],
            create: false,
            load: function(query, callback) {
                if (!query.length) return callback();
                $.ajax({
                    url: '${createLink(controller: 'user', action: 'search')}',
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