<form id="filterForm" class="filterPanel">
    <g:render template="/filter/project"/>
    <g:render template="/filter/issueType"/>
    <g:render template="/filter/priority"/>
    <g:render template="/filter/status"/>
    <g:render template="/filter/component"/>
    <g:render template="/filter/client"/>
    <g:render template="/filter/team"/>
    <g:render template="/filter/user"/>
    <input type="button" onclick="reloadGrid()" value="FILTER" class="k-button k-primary"/>
</form>
<script language="JavaScript" type="text/javascript">
    function reloadGrid() {
        $("#grid").data("kendoGrid").dataSource.read();
    }
</script>