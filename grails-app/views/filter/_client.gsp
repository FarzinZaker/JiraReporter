<%@ page import="jirareporter.Configuration" %>
<div class="field">
    <label for="client">Client:</label>
    <input id="client" name="client" type="text" value="${params.client}">
</div>
<script>
    $(function () {
        $('#client').selectize({
            plugins: ['remove_button'],
            create: true,
            options: []
        });
    });
</script>