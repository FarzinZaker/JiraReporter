<%@ page import="jirareporter.Priority; jirareporter.JiraUser; jirareporter.Configuration" %>

<div class="gantt_control">
    <input class="action" value="Show Critical Path" type="button" onclick="updateCriticalPath(this)"
           style="background: #E91E63;">
    <input class="action" name="undo" value="Undo" type="button" style="background: #2196F3">
    <input class="action" name="redo" value="Redo" type="button" style="background: #2196F3">
    <input class="action" name="indent" value="Indent" type="button" style="background: #4CAF50">
    <input class="action" name="outdent" value="Outdent" type="button" style="background: #4CAF50">
    <input class="action" name="moveForward" value="Move Forward" type="button" style="background: #4CAF50">
    <input class="action" name="moveBackward" value="Move Backward" type="button" style="background: #4CAF50">
    <input type='button' id='default' onclick="showGroups()" value="Tree" style="background: #9C27B0">
    <input type='button' id='priority' onclick="showGroups('priority')" value="Group by priority"
           style="background: #9C27B0">
    %{--    <input type='button' id='user' onclick="showGroups('userGroups')" value="Group by owner">--}%
    <input type='button' id='expandAll' onclick="expandAll()" value="Expand All" style="background: #FF9800">
    <input type='button' id='collapseAll' onclick="collapseAll()" value="Collapse All" style="background: #FF9800">
    <input type="button" onclick="toggleMode(this)" value="Zoom to Fit" style="background: #FF9800"/>
    <input type="button" onclick="showToday()" value="Today" style="background: #FF9800"/>

    <g:render template="syncStatus"/>

</div>

<div id="gantt_here" style='width:100%; height:80vh'></div>

<asset:javascript src="gantt_config.js"/>
<script>

    gantt.init("gantt_here");
    gantt.load("${createLink(action: 'issues')}", function () {
        resourcesStore.parse(filterResources());
        gantt.refreshData();
        showToday();
    });

    var resources = [
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
    ];

    function filterResources() {
        var idList = [];
        gantt.eachTask(function (task) {
            if (task.owner && !idList.includes(task.owner.resource_id))
                idList.push(task.owner.resource_id);
        });
        var result = $.grep(resources, function(r) {
            return idList.includes(r.id);
        });
        $.each(result, function(index, id){
            $.each($.grep(resources, function(r){
                return r.id >= 10000000;
            }), function(i, r){
                // console.log(r);
                if(id.parent === r.id)
                    idList.push(r.id);
            });
        });
        result = $.grep(resources, function(r) {
            return idList.includes(r.id);
        });
        $.each(result, function(index, id){
            $.each($.grep(resources, function(r){
                return r.id >= 10000000;
            }), function(i, r){
                if(id.parent === r.id)
                    idList.push(r.id);
            });
        });
        result = $.grep(resources, function(r) {
            return idList.includes(r.id);
        });
        // console.log(result);
        return result;
    }

    gantt.locale.labels.column_priority = gantt.locale.labels.section_priority = "Priority";

    gantt.serverList("priority", [
        <g:each in="${Priority.list().sort{it.id}}" var="priority">
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
<asset:javascript src="gantt_zoom.js"/>