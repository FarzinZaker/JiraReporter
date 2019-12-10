<%@ page import="jirareporter.Priority; jirareporter.JiraUser; jirareporter.Configuration" %>

<g:render template="toolbar"/>

<g:render template="syncStatus"/>

<div id="gantt_here" style='width:100%; height:80vh'></div>

<script language="JavaScript">
    gantt.serverList("priority", [
        <g:each in="${[
        Priority.findByName('Showstopper'),
        Priority.findByName('High'),
        Priority.findByName('Medium'),
        Priority.findByName('Minor'),
        Priority.findByName('Low'),
        ]}" var="priority">
        {key: ${priority.id}, label: "${priority.name}", name: "${priority.name}"},
        </g:each>
    ]);

    var priorityIcons = {};
    <g:each in="${Priority.list()}" var="priority">
    priorityIcons.p${priority.id} = '${priority.icon}';
    </g:each>
</script>
<asset:javascript src="gantt_fullscreen.js"/>
<asset:javascript src="gantt_helper.js"/>
<asset:javascript src="gantt_config.js"/>
<script language="JavaScript">
    gantt.attachEvent("onAfterTaskUpdate", function (id, task) {

        // console.log(task.owner.value);
        var end_date = new Date();
        end_date.setDate(task.end_date.getDate() - 1);
        var diffDays = workingDaysBetweenDates(task.start_date, end_date);
        var estimateHours = getDurationSeconds(task.originalEstimate) / 3600;
        // console.log(diffDays);
        var newValue = Math.round(estimateHours * 10 / diffDays) / 10;
        var oldOwnerValue = task.owner.value;
        if (oldOwnerValue !== newValue) {
            gantt.getTask(id).owner.value = newValue;
            gantt.updateTask(id);
            // console.log(getTask(id).owner.value);
        } else {
            console.log('UPDATE TASK:');
            console.log(task);
            $.ajax({
                url: '${createLink(action: 'updateIssue')}',
                dataType: 'json',
                type: 'post',
                data: {issue: JSON.stringify(task)},
                success: function (data, textStatus, jQxhr) {
                },
                error: function (jqXhr, textStatus, errorThrown) {
                    console.log(errorThrown);
                }
            });
        }
    });


    gantt.attachEvent("onAfterLinkAdd", function (id, link) {
        $.ajax({
            url: '${createLink(action: 'addLink')}',
            dataType: 'json',
            type: 'post',
            data: link,
            success: function (data, textStatus, jQxhr) {
            },
            error: function (jqXhr, textStatus, errorThrown) {
                console.log(errorThrown);
            }
        });
    });

    gantt.attachEvent("onAfterLinkDelete", function (id, link) {
        $.ajax({
            url: '${createLink(action: 'deleteLink')}',
            dataType: 'json',
            type: 'post',
            data: link,
            success: function (data, textStatus, jQxhr) {
            },
            error: function (jqXhr, textStatus, errorThrown) {
                console.log(errorThrown);
            }
        });
    });
</script>
<script>

    gantt.init("gantt_here");
    reloadPlanner();
    %{--gantt.load("${createLink(action: 'issues')}", function () {--}%
    %{--    resourcesStore.parse(filterResources());--}%
    %{--    gantt.refreshData();--}%
    %{--    showToday();--}%
    %{--    gantt.sort('start_date', false);--}%
    %{--});--}%

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
        var result = $.grep(resources, function (r) {
            return idList.includes(r.id);
        });
        $.each(result, function (index, id) {
            $.each($.grep(resources, function (r) {
                return r.id >= 10000000;
            }), function (i, r) {
                // console.log(r);
                if (id.parent === r.id)
                    idList.push(r.id);
            });
        });
        result = $.grep(resources, function (r) {
            return idList.includes(r.id);
        });
        $.each(result, function (index, id) {
            $.each($.grep(resources, function (r) {
                return r.id >= 10000000;
            }), function (i, r) {
                if (id.parent === r.id)
                    idList.push(r.id);
            });
        });
        result = $.grep(resources, function (r) {
            return idList.includes(r.id);
        });
        // console.log(result);
        return result;
    }

    gantt.locale.labels.column_priority = gantt.locale.labels.section_priority = "Priority";

    %{--gantt.serverList("priority", [--}%
    %{--    <g:each in="${Priority.list().sort{it.id}}" var="priority">--}%
    %{--    {key: ${priority.id}, label: "${priority.name}"},--}%
    %{--    </g:each>--}%
    %{--]);--}%
</script>

<asset:javascript src="gantt_tooltip.js"/>
<asset:javascript src="gantt_critical_path.js"/>
<asset:javascript src="gantt_marker.js"/>
<asset:javascript src="gantt_manage_columns.js"/>
<asset:javascript src="gantt_multiselect.js"/>
<asset:javascript src="gantt_grouping.js"/>
<asset:javascript src="gantt_zoom.js"/>