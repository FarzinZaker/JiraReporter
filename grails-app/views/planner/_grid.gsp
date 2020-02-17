<%@ page import="jirareporter.GanttColumn; jirareporter.Team; jirareporter.Roles; jirareporter.Priority; jirareporter.JiraUser; jirareporter.Configuration" %>

<g:render template="toolbar"/>

<div id="gantt_here" style='width:100%; height:80vh'></div>

<script language="JavaScript">
    <sec:ifNotGranted roles="${[Roles.ADMIN, Roles.MANAGER].join(',')}">
    var readonly = true;
    gantt.config.drag_links = false;
    gantt.config.drag_move = false;
    gantt.config.drag_project = false;
    gantt.config.drag_resize = false;
    gantt.config.drag_timeline = false;
    gantt.config.click_drag = false;
    </sec:ifNotGranted>
    <sec:ifAnyGranted roles="${[Roles.ADMIN, Roles.MANAGER].join(',')}">
    var readonly = false;
    </sec:ifAnyGranted>

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


    gantt.attachEvent("onBeforeTaskUpdate", function (id, task) {
        var newOwnerId = parseInt(task.owner_id);
        if (newOwnerId !== task.owner.resource_id) {
            task.owner.resource_id = newOwnerId
        }
    });

    gantt.attachEvent("onAfterTaskUpdate", function (id, task) {
        updateIssue(id, task);
    });

    function updateIssue(id, task){
        var startDate = start_date = new Date(Date.UTC(task.start_date.getFullYear(), task.start_date.getMonth(), task.start_date.getDate(), 0, 0, 0));
        var endDate = new Date(Date.UTC(task.end_date.getFullYear(), task.end_date.getMonth(), task.end_date.getDate(), 0, 0, 0));

        // console.log(startDate)
        // console.log(endDate)

        var end_date = new Date(task.end_date.getTime());
        // end_date.setDate(task.end_date.getDate() - 1);
        var diffDays = workingDaysBetweenDates(task.start_date, end_date);
        var estimateHours = getDurationSeconds(task.originalEstimate) / 3600;
        var newValue = Math.round(estimateHours / diffDays);
        var oldOwnerValue = task.owner.value;
        if (oldOwnerValue !== newValue) {
            var tsk = gantt.getTask(id);
            tsk.owner.value = newValue;
            tsk.durationDays = diffDays;
            gantt.updateTask(id);
        } else {
            task.updateTime = new Date();
            $.ajax({
                url: '${createLink(action: 'updateIssue')}',
                dataType: 'json',
                type: 'post',
                data: {data: JSON.stringify({task: task, startDate: startDate, endDate: endDate, time: new Date()})},
                success: function (data, textStatus, jQxhr) {
                },
                error: function (jqXhr, textStatus, errorThrown) {
                    console.log(errorThrown);
                }
            });
        }
    }

    gantt.attachEvent("onTaskClick", function(id, e){
        console.log(id);
        console.log($(e.target).attr('data-action'));
        var button = e.target.closest("[data-action]");
        if(button){
            var action = button.getAttribute("data-action");
            switch (action) {
                case "forceUpdate":
                    var task = gantt.getTask(id);
                    console.log(task);
                    kendo.confirm("Are you sure about updating <b>" + task.key + "</b>?").then(function () {
                        updateIssue(id, task);
                    }, function () {
                    });
                    $('.k-confirm .k-window-title.k-dialog-title').text('Confirmation');
                    break;
            }
            return false;

        }
        return true;
    });

    function forceUpdateIssue(sender) {
        console.log('clicked');
        var key = $(sender).attr('data-task');
        console.log(key);
    }


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

    gantt.attachEvent("onGanttRender", function () {
        $.ajax({
            url: '${createLink(action: 'saveColumns')}',
            dataType: 'json',
            type: 'post',
            data: {columns: JSON.stringify(gantt.getGridColumns())},
            success: function (data, textStatus, jQxhr) {
            },
            error: function (jqXhr, textStatus, errorThrown) {
                console.log(errorThrown);
            }
        });
    });

    var managedUsers = [
        <g:each in="${managedUsers}" var="user">
        {
            key: ${user.id},
            label: "${user.displayName}",
            name: "${user.displayName}"
        },
        </g:each>
    ];


    var resources = [
        <g:each in="${Team.list()}" var="team" status="i">
        {id: ${10000000 + i}, text: "${team.name}", parent: null},
        <g:each in="${JiraUser.findAllByTeam(team)}" var="user">
        {
            id: ${user.id},
            text: "${user.displayName}",
            parent: ${10000000 + i},
            avatar: '${user.avatar}',
            key: ${user.id},
            label: "${user.displayName}",
            name: "${user.displayName}"
        },
        </g:each>
        </g:each>
        {id: ${20000000}, text: "Other Teams", parent: null},
        <g:each in="${JiraUser.findAllByTeamIsNull()}" var="user">
        {
            id: ${user.id},
            text: "${user.displayName}",
            parent: ${20000000},
            avatar: '${user.avatar}',
            key: ${user.id},
            label: "${user.displayName}",
            name: "${user.displayName}"
        },
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
        return result;
    }

    gantt.locale.labels.column_priority = gantt.locale.labels.section_priority = "Priority";

</script>

<asset:javascript src="gantt_fullscreen.js"/>
<asset:javascript src="gantt_helper.js"/>
<asset:javascript src="gantt_config.js"/>

<script>

    gantt.config.columns = [
        <g:set var="column" value="${GanttColumn.findByUserAndName(user, 'wbs')}"/>
        {name: "wbs", label: "#", width: 60, align: "left", hide: ${!column?.visible}, template: gantt.getWBSCode},
        <g:set var="column" value="${GanttColumn.findByUserAndName(user, 'text')}"/>
        {
            name: "text", width: 250, tree: true, label: "Summary", resize: true, template: function (task) {
                if (!task.taskType || task.taskType === 'project' || task.taskType === 'client')
                    return '<b>' + task.text + '</b>';
                else
                    return '<img src="' + task.issueTypeIcon + '" /> ' + task.text;
            }
        },
        <g:set var="column" value="${GanttColumn.findByUserAndName(user, 'key')}"/>
        {
            name: "key", width: 100, label: "Key", resize: true, hide: ${!column?.visible}, template: function (task) {
                if (!task.taskType || task.taskType === 'project' || task.taskType === 'client')
                    return '';
                else
                    return '<a class="gantt-task-link" href="https://jira.devfactory.com/browse/' + task.key + '" target="_blank"> ' + task.key + '</a>';
            }
        },
        <g:set var="column" value="${GanttColumn.findByUserAndName(user, 'owner')}"/>
        {
            name: "owner",
            align: "left",
            width: 100,
            label: "Assignee",
            editor: assigneeEditor,
            hide: ${!column?.visible},
            template: function (task) {
                if (!task.taskType || task.taskType === 'project' || task.taskType === 'client') {
                    return "";
                }

                var store = gantt.getDatastore("resource");
                var assignments = task[gantt.config.resource_property] || [];

                // if (!assignments || !assignments.length) {
                //     return "Unassigned";
                // }

                if (assignments.length == 1) {
                    return store.getItem(assignments[0].resource_id).text;
                }

                var result = "";
                // assignments.forEach(function (assignment) {
                var owner = store.getItem(assignments.resource_id);
                if (!owner) {
                    return "Unassigned";
                }
                // result += "<div class='owner-label' title='" + owner.text + "'>" + owner.text.substr(0, 1) + "</div>";

                result += '<img class="gantt-avatar" src="' + owner.avatar + '" /> ' + owner.text.split(' ')[0];

                // });

                return result;
            },
            resize: true
        },
        <g:set var="column" value="${GanttColumn.findByUserAndName(user, 'status')}"/>
        {
            name: "status",
            width: 80,
            label: "Status",
            resize: true,
            hide: ${!column?.visible},
            template: function (task) {
                if (!task.taskType || task.taskType === 'project' || task.taskType === 'client') {
                    return "";
                }
                return task.status.name;
            }
        },
        <g:set var="column" value="${GanttColumn.findByUserAndName(user, 'start_date')}"/>
        {
            name: "start_date",
            width: 80,
            label: "Start Date",
            resize: true,
            hide: ${!column?.visible},
            editor: startDateEditor,
            template: function (task) {
                if (!task.taskType || task.taskType === 'project' || task.taskType === 'client') {
                    return "";
                }
                return task.start_date ? task.start_date : '';
            }
        },
        <g:set var="column" value="${GanttColumn.findByUserAndName(user, 'end_date')}"/>
        {
            name: "end_date",
            width: 80,
            label: "Due Date",
            resize: true,
            hide: ${!column?.visible},
            editor: endDateEditor,
            template: function (task) {
                if (!task.taskType || task.taskType === 'project' || task.taskType === 'client') {
                    return "";
                }
                return task.end_date ? task.end_date : '-';
            }
        },
        <g:set var="column" value="${GanttColumn.findByUserAndName(user, 'originalEstimate')}"/>
        {
            name: "originalEstimate",
            width: 70,
            label: "Est.",
            resize: true,
            hide: ${!column?.visible},
            editor: durationEditor,
            template: function (task) {
                if (!task.taskType || task.taskType === 'project' || task.taskType === 'client') {
                    return "";
                }
                return task.originalEstimate;
            }
        },
        <g:set var="column" value="${GanttColumn.findByUserAndName(user, 'remainingEstimate')}"/>
        {
            name: "remainingEstimate",
            width: 90,
            label: "Rem. Est.",
            resize: true,
            hide: ${!column?.visible},
            template: function (task) {
                if (!task.taskType || task.taskType === 'project' || task.taskType === 'client') {
                    return "";
                }
                return task.remainingEstimate;
            }
        },
        <g:set var="column" value="${GanttColumn.findByUserAndName(user, 'timeSpent')}"/>
        {
            name: "timeSpent",
            width: 90,
            label: "Time Spent",
            resize: true,
            hide: ${!column?.visible},
            template: function (task) {
                if (!task.taskType || task.taskType === 'project' || task.taskType === 'client') {
                    return "";
                }
                return task.timeSpent;
            }
        },
        <g:set var="column" value="${GanttColumn.findByUserAndName(user, 'priority')}"/>
        {
            name: "priority",
            width: 43,
            label: "P",
            resize: true,
            hide: ${!column?.visible},
            editor: priorityEditor,
            template: function (task) {
                if (!task.taskType || task.taskType === 'project' || task.taskType === 'client') {
                    return "";
                }
                // return task.priority;
                return '<div class="priority-icon-container"><img class="priority-icon" src="' + priorityIcons['p' + task.priority] + '"/></div>';
            }
        },
        <g:set var="column" value="${GanttColumn.findByUserAndName(user, 'predecessors')}"/>
        {
            name: "predecessors", label: "Pred.", width: 100, align: "center", hide: ${!column?.visible},
            editor: predecessorsEditor, resize: true, template: function (task) {
                var links = task.$target;
                var labels = [];
                for (var i = 0; i < links.length; i++) {
                    var link = gantt.getLink(links[i]);
                    labels.push(linksFormatter.format(link));
                }
                return labels.join(", ")
            }
        },
        <g:set var="column" value="${GanttColumn.findByUserAndName(user, 'update')}"/>
        {
            name: "text", width: 43, tree: false, label: " ", resize: true, template: function (task) {
                if (!task.taskType || task.taskType === 'project' || task.taskType === 'client')
                    return ' ';
                else
                    return '<div class="update-button"><span class="k-icon k-i-upload" data-task="' + task.key + '" data-action="forceUpdate"></span></div>';
            }
        },
        <g:set var="column" value="${GanttColumn.findByUserAndName(user, 'add')}"/>
        {
            name: "add", hide: ${!column?.visible}
        }
    ];

    gantt.init("gantt_here");
    reloadPlanner();
    %{--gantt.load("${createLink(action: 'issues')}", function () {--}%
    %{--    resourcesStore.parse(filterResources());--}%
    %{--    gantt.refreshData();--}%
    %{--    showToday();--}%
    %{--    gantt.sort('start_date', false);--}%
    %{--});--}%
</script>

<asset:javascript src="gantt_tooltip.js"/>
<asset:javascript src="gantt_critical_path.js"/>
<asset:javascript src="gantt_marker.js"/>
<asset:javascript src="gantt_manage_columns.js"/>
<asset:javascript src="gantt_multiselect.js"/>
<asset:javascript src="gantt_grouping.js"/>
<asset:javascript src="gantt_zoom.js"/>