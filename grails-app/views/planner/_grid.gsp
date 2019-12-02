<%@ page import="jirareporter.Priority; jirareporter.JiraUser; jirareporter.Configuration" %>

<div class="gantt_control">
    <input class="action" value="Show Critical Path" type="button" onclick="updateCriticalPath(this)">
    <input class="action" name="undo" value="Undo" type="button">
    <input class="action" name="redo" value="Redo" type="button">
    <input class="action" name="indent" value="Indent" type="button">
    <input class="action" name="outdent" value="Outdent" type="button">
    <input class="action" name="del" value="Delete" type="button">
    <input class="action" name="moveForward" value="Move Forward" type="button">
    <input class="action" name="moveBackward" value="Move Backward" type="button">
    <input type='button' id='default' onclick="showGroups()" value="Tree">
    <input type='button' id='priority' onclick="showGroups('priority')" value="Group by priority">
    <input type='button' id='user' onclick="showGroups('userGroups')" value="Group by owner">
    <input type='button' id='stage' onclick="showGroups('stage')" value="Group by stage">
</div>

<div id="gantt_here" style='width:100%; height:80vh'></div>

<asset:javascript src="gantt_config.js"/>
<script>

    gantt.init("gantt_here");
    gantt.load("${createLink(action: 'issues')}");

    resourcesStore.parse([
        <g:each in="${Configuration.crossOverTeams}" var="team" status="i">
        {id: ${10000000 + i}, text: "${team.name}", parent: null},
        <g:each in="${JiraUser.findAllByTeamName(team.name)}" var="user">
        {id: ${user.id}, text: "${user.displayName}", parent: ${10000000 + i}, avatar: '${user.avatar}'},
        </g:each>
        </g:each>
        {id: ${20000000}, text: "Other Teams", parent: null},
        <g:each in="${JiraUser.findAllByTeamNameIsNull()}" var="user">
        {id: ${user.id}, text: "${user.displayName}", parent: ${20000000}, avatar: '${user.avatar}'},
        </g:each>
    ]);

    gantt.locale.labels.column_priority = gantt.locale.labels.section_priority = "Priority";
    gantt.locale.labels.column_owner =    gantt.locale.labels.section_owner    = "Owner";
    gantt.locale.labels.column_stage =    gantt.locale.labels.section_stage    = "Stage";
    gantt.serverList("Project-Client-Team-Assignee", [
        {key: 1, "label": "Planning"},
        {key: 2, "label": "Dev"},
        {key: 3, "label": "Testing"}
    ]);

    gantt.serverList("user", [
        {key: 0, label: "N/A"},
        {key: 1, label: "John"},
        {key: 2, label: "Mike"},
        {key: 3, label: "Anna"}
    ]);

    gantt.serverList("userGroups", [
        {key: 0, label: "N/A", "user": 6},
        {key: 1, label: "John", "user": 4},
        {key: 2, label: "Mike", "user": 5},
        {key: 3, label: "Anna", "user": 4},
        //multi level groups
        {key: 4, label: "Dev"},
        {key: 5, label: "QA"},
        {key: 6, label: "Other"}
    ]);

    gantt.serverList("priority", [
        <g:each in="${Priority.list()}" var="priority">
        {key: ${priority.id}, label: "${priority.name}"},
        </g:each>
    ]);
</script>

<asset:javascript src="gantt_tooltip.js"/>
<asset:javascript src="gantt_critical_path.js"/>
<asset:javascript src="gantt_marker.js"/>
<asset:javascript src="gantt_manage_columns.js"/>
<asset:javascript src="gantt_multiselect.js"/>
<asset:javascript src="gantt_grouping.js"/>