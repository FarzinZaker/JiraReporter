<%@ page import="jirareporter.JiraUser; jirareporter.Roles; jirareporter.User" %>
<sec:ifLoggedIn>
    <ul id="menu" style="display: none;">
        <li>
            Worklogs
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
        <a href="${createLink(controller: 'worklog', action: 'report')}#details">Details</a>
    </li>
    </ul>
</li>
    <li>
        Planning
        <ul>
            <li>
                <a href="${createLink(controller: 'planner', action: 'gantt')}">Gantt Chart</a>
            </li>
            <li>
                <a href="${createLink(controller: 'resource', action: 'allocation')}">Resource Allocation</a>
            </li>
        </ul>
    <li>
        Source Codes
        <ul>
            <li>
                <a href="${createLink(controller: 'gitHub', action: 'report')}#languages">Coding Languages</a>
            </li>
            <li>
                <a href="${createLink(controller: 'gitHub', action: 'report')}#heatMap">HeatMap</a>
            </li>
        </ul>
    </li>
    </li>
    <li>
        Validation
        <ul>
            <li>
                <a href="${createLink(controller: 'validation', action: 'estimate')}">No Original Estimate</a>
            </li>
        </ul>
    </li>
    <sec:ifAnyGranted roles="${[Roles.ADMIN].join(',')}">
        <li>
            <a href="${createLink(controller: 'team', action: 'list')}">Teams</a>
        </li>
        <li>
            Users
            <ul>
                <g:each in="${Roles.ALL}" var="role">
                    <li>
                        <a href="${createLink(controller: 'user', action: 'list', id: role)}">
                            <g:message code="${role}.list"/>
                        </a>
                    </li>
                </g:each>
            </ul>
        </li>
        <li>
            <a href="${createLink(controller: 'reminder', action: 'list')}">Reminders</a>
        </li>
    </sec:ifAnyGranted>
    <li>
        ${User.findByUsername(sec.username()?.toString()) ?: sec.username()}
        <ul>
            <g:set var="jiraUser"
                   value="${JiraUser.findByName(sec.username()?.toString()) ?: JiraUser.findByDisplayName(sec.username()?.toString())}"/>
            <g:if test="${jiraUser}">
                <li>
                    <a href="${createLink(controller: 'recurringTaskSetting', action: 'edit')}">Recurring Task Settings</a>
                </li>
            </g:if>
            <sec:ifAnyGranted roles="${[Roles.ADMIN].join(',')}">
                <li>
                    <a href="${createLink(controller: 'user', action: 'changePassword')}">Change Password</a>
                </li>
            </sec:ifAnyGranted>
            <li>
                <a href="${createLink(controller: 'logout')}">Logout</a>
            </li>
        </ul>
    </li>
    </ul>
    <script>
        $(document).ready(function () {
            $("#menu").show().kendoMenu();
        });
    </script>
</sec:ifLoggedIn>