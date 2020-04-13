<%@ page import="jirareporter.Product;" %>

<div class="field">
    <label for="product">Product:</label>
    <input id="product" name="product" type="text" value="${params.product}">
</div>
<script>
    $(function () {
        $('#product').selectize({
            plugins: ['remove_button'],
            valueField: 'value',
            labelField: 'text',
            searchField: 'text',
            create: false,
            options: [
                <g:each in="${Product.list()}" var="product">
                {value: ${product.id}, text: '<format:html value='${product.company?.name}: ${product.name}'/>'},
                </g:each>
            ]
        });
    });
</script>