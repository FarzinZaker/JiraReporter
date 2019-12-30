<%@ page import="jirareporter.Configuration" %>
<div class="field">
    <label for="issueType">Issue Type:</label>
    <input id="issueType" name="issueType" type="text" value="${params.issueType}">
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
                {value: '<format:html value="${issueType}"/>'},
                </g:each>
            ]
        });
    });
</script>