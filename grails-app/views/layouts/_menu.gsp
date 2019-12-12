<%@ page import="jirareporter.Roles; jirareporter.User" %>
<sec:ifLoggedIn>
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
        <sec:ifAnyGranted roles="${[Roles.ADMIN].join(',')}">
            <li disabled="disabled">
                Teams
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
        </sec:ifAnyGranted>
        <li>
            ${User.findByUsername(sec.username()?.toString())}
            <ul>
                <li>
                    <a href="${createLink(controller: 'user', action: 'changePassword')}">Change Password</a>
                </li>
                <li>
                    <a href="${createLink(controller: 'logout')}">Logout</a>
                </li>
            </ul>
        </li>
    </ul>
    <script>
        $(document).ready(function () {
            $("#menu").kendoMenu();
        });
    </script>
</sec:ifLoggedIn>