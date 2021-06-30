<%@ page import="jirareporter.IssueProduct;" %>

<div class="field">
    <label for="issueProduct">Product:</label>
    <input id="issueProduct" name="issueProduct" type="text" value="${params.product}">
</div>
<script>
    $(function () {
        $('#issueProduct').selectize({
            plugins: ['remove_button'],
            valueField: 'value',
            labelField: 'text',
            searchField: 'text',
            create: false,
            options: [
                <g:each in="${IssueProduct.list()?.sort{it.name}}" var="product">
                {value: ${product.id}, text: '${product.name}'},
                </g:each>
            ]
        });
    });
</script>