<form id="filterForm" class="filterPanel">
    <g:render template="/filter/project"/>
    <g:render template="/filter/issueType"/>
    <g:render template="/filter/priority"/>
    <g:render template="/filter/status"/>
    <g:render template="/filter/component"/>
    <g:render template="/filter/client"/>
    <g:render template="/filter/team"/>
    <g:render template="/filter/user"/>
    <input type="button" onclick="reloadPlanner()" value="FILTER"/>
</form>
<script language="JavaScript" type="text/javascript">
    function reloadPlanner() {
        gantt.clearAll();
        gantt.load('${createLink(controller: 'planner', action: 'issues')}?' + $('#filterForm').serialize(), function () {
            resourcesStore.parse(filterResources());
            gantt.refreshData();
            showToday();
            gantt.sort('start_date', false);
        });
    }
</script>