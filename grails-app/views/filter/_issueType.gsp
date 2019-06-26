<%@ page import="jirareporter.Configuration" %>
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
                <g:each in="${Configuration.issueTypes}" var="issueType">
                {value: '${issueType}'},
                </g:each>
            ]
        });
    });
</script>