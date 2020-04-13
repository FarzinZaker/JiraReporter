<%@ page import="jirareporter.Company;" %>

<div class="field">
    <label for="company">Company:</label>
    <input id="company" name="company" type="text" value="${params.company}">
</div>
<script>
    $(function () {
        $('#company').selectize({
            plugins: ['remove_button'],
            valueField: 'value',
            labelField: 'text',
            searchField: 'text',
            create: false,
            options: [
                <g:each in="${Company.list()}" var="company">
                {value: ${company.id}, text: '<format:html value='${company.name}'/>'},
                </g:each>
            ]
        });
    });
</script>