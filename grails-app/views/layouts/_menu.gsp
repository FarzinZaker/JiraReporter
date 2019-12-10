<ul id="menu">
    <li>
        Reports
        <ul>
            <li>
                <a href="${createLink(controller: 'worklog', action: 'report')}#summary">Summary</a>
            </li>
            <li>
                <a href="${createLink(controller: 'worklog', action: 'report')}#users">Users</a>
            </li>
            <li>
                <a href="${createLink(controller: 'worklog', action: 'report')}#crossOver">CrossOver</a>
            </li>
            <li>
                <a href="${createLink(controller: 'worklog', action: 'report')}#accomplishments">Tasks</a>
            </li>
            <li>
                <a href="${createLink(controller: 'worklog', action: 'report')}#details">Worklogs</a>
            </li>
        </ul>
    </li>
    <li>
        Planning
        <ul>
            <li>
                <a href="${createLink(controller: 'planner', action: 'gantt')}">Gantt Chart</a>
            </li>
        </ul>
    </li>
    <li>
        Validation
        <ul>
            <li>
                <a href="${createLink(controller: 'validation', action: 'estimate')}">No Original Estimate</a>
            </li>
        </ul>
    </li>
    <li disabled="disabled">
        Teams
    </li>
    <li disabled="disabled">
        Users
    </li>
</ul>
<script>
    $(document).ready(function () {
        $("#menu").kendoMenu();
    });
</script>