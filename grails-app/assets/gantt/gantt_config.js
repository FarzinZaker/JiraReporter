gantt.config.xml_date = "%d-%m-%Y";
gantt.config.grid_resize = true;
gantt.config.fit_tasks = true;
gantt.config.sort = true;

gantt.config.auto_scheduling = true;
gantt.config.auto_scheduling_strict = false;
gantt.config.auto_scheduling_compatibility = true;
gantt.config.keyboard_navigation_cells = true;

gantt.config.server_utc = false;


var formatter = gantt.ext.formatters.durationFormatter({
    enter: "day",
    store: "day",
    format: "auto"
});
var linksFormatter = gantt.ext.formatters.linkFormatter({durationFormatter: formatter});

// gantt.attachEvent("onAfterTaskAutoSchedule", function (task, start, link, predecessor) {
//     gantt.sort('start_date', false);
// });

gantt.templates.grid_row_class = function (start_date, end_date, item) {
    // console.log(item);

    if (item.type != 'project' && !item.completed && end_date < new Date()) {
        // console.log(end_date);
        // $('[task_id=' + item.id + ']').addClass('red');
        return "red";
    } else {
        // $('[task_id=' + item.id + ']').removeClass('red');
        return "ok";
    }
};

gantt.templates.task_row_class = function (start_date, end_date, item) {

    if (item.type != 'project' && !item.completed && end_date < new Date()) {
        // console.log(end_date);
        // $('[task_id=' + item.id + ']').addClass('red');
        return "red";
    } else {
        // $('[task_id=' + item.id + ']').removeClass('red');
        return "ok";
    }
};

gantt.templates.task_class = function (start, end, task) {
    return 'bar-' + task.taskType
};

gantt.attachEvent("onTaskDrag", function (id, mode, task, original) {
    // console.log('drag')
});

gantt.templates.scale_cell_class = function (date) {
    if (!gantt.isWorkTime(date)) {
        return "weekend";
    }
};
gantt.templates.timeline_cell_class = function (item, date) {
    if (!gantt.isWorkTime(date)) {
        return "weekend";
    }
};

gantt.config.work_time = true;

gantt.config.drag_timeline = {
    ignore: ".gantt_task_line, .gantt_task_link",
    useKey: false
};

var textEditor = {type: "text", map_to: "text"};
var startDateEditor = {
    type: "date", map_to: "start_date"
};
var endDateEditor = {
    type: "date", map_to: "end_date"
};
var durationEditor = {type: "originalEstimateEditor", map_to: "auto"};
var storyPointsEditor = {type: "storyPointsEditor", map_to: "auto"};
var predecessorsEditor = {type: "predecessor", map_to: "auto", formatter: linksFormatter};
var priorityEditor = {type: "select", map_to: "priority", options: gantt.serverList("priority")};
var assigneeEditor = {
    type: "select", map_to: "owner_id", options: managedUsers
};

gantt.config.editor_types.storyPointsEditor = {
    show: function (id, column, config, placeholder) {
        placeholder.innerHTML = "<div><input type='text' name='" + column.name + "'></div>";
    },
    hide: function () {
        // called when input is hidden
        // destroy any complex editors or detach event listeners from here
    },

    set_value: function (value, id, column, node) {
        node.querySelector('input[name="' + column.name + '"]').value = value.storyPoints ? value.storyPoints : 0;
    },

    get_value: function (id, column, node) {
        return node.querySelector('input[name="' + column.name + '"]').value;
    },

    is_changed: function (value, id, column, node) {
        return node.querySelector('input[name="' + column.name + '"]').value !== value.storyPoints
    },

    is_valid: function (value, id, column, node) {
        var validChars = '0123456789.';
        for(var i = 0; i < value.length; i++) {
            if(validChars.indexOf(value.charAt(i)) === -1)
                return false;
        }
        return true;
    },

    save: function (id, column, node) {
        var task = gantt.getTask(id);
        task.storyPoints = parseFloat(node.querySelector('input[name="' + column.name + '"]').value);
        gantt.updateTask(id);
    },
    focus: function (node) {
    }
};

gantt.config.editor_types.originalEstimateEditor = {
    show: function (id, column, config, placeholder) {
        placeholder.innerHTML = "<div><input type='text' name='" + column.name + "'></div>";
    },
    hide: function () {
        // called when input is hidden
        // destroy any complex editors or detach event listeners from here
    },

    set_value: function (value, id, column, node) {
        node.querySelector('input[name="' + column.name + '"]').value = value.originalEstimate;
    },

    get_value: function (id, column, node) {
        return node.querySelector('input[name="' + column.name + '"]').value
    },

    is_changed: function (value, id, column, node) {
        return node.querySelector('input[name="' + column.name + '"]').value !== value.originalEstimate
    },

    is_valid: function (value, id, column, node) {
        return validateDuration(value);
    },

    save: function (id, column, node) {
        var task = gantt.getTask(id);
        task.originalEstimate = reformatDuration(node.querySelector('input[name="' + column.name + '"]').value);
        gantt.updateTask(id);
        //console.log(task);
    },
    focus: function (node) {
    }
};

function getResourceAssignments(resource, store) {
    var assignments = [];
    if (store.hasChild(resource.id)) {
        store.eachItem(function (res) {
            assignments = assignments.concat(gantt.getResourceAssignments(res.id));
        }, resource.id)
    } else {
        assignments = gantt.getResourceAssignments(resource.id)
    }
    return assignments;
}

var resourceConfig = {
    columns: [
        {
            name: "name", label: "Name", tree: true, template: function (resource) {
                return resource.text;
            }
        },
        {
            name: "workload", label: "Workload", template: function (resource) {
                var store = gantt.getDatastore(gantt.config.resource_store);

                var assignments = getResourceAssignments(resource, store);

                var totalDuration = 0;
                for (var i = 0; i < assignments.length; i++) {
                    var task = gantt.getTask(assignments[i].task_id);
                    totalDuration += task.duration * assignments[i].value;
                }

                return (totalDuration || 0) + "h";
            }
        }
    ]
};

function getTasksLoad(tasks, resourceId, start_date, end_date) {
    var totalLoad = 0;
    // console.log(tasks);
    // console.log('--------------------');
    tasks.forEach(function (task) {
        if (task.owner_id === resourceId && task.estimateHours && task.durationDays && task.owner.value) {
            var sd = start_date;
            if (task.start_date.getTime() > sd.getTime())
                sd = task.start_date;
            var ed = end_date;
            if (task.end_date.getTime() < end_date.getTime())
                ed = task.end_date;
            // var taskDuration = workingDaysBetweenDates(task.start_date, task.end_date);
            // var periodDuration = workingDaysBetweenDates(start_date, end_date);
            var duration = workingDaysBetweenDates(sd, ed);
            // var assignments = gantt.getResourceAssignments(resourceId, task.id);

            // console.log(task.key);
            // console.log(task.start_date);
            // console.log(task.end_date);
            // console.log(start_date);
            // console.log(end_date);
            // console.log(sd);
            // console.log(ed);
            // console.log(task.end_date.getTime());
            // console.log(end_date.getTime());
            // console.log(task.estimateHours);
            // console.log(task.durationDays);
            // console.log(task.estimateHours / task.durationDays);
            // console.log(duration);
            // console.log(task.estimateHours / task.durationDays * duration);
            // console.log('--------------------');
            totalLoad += task.estimateHours / task.durationDays * duration;//assignments[0].value * duration;
        }
    });
    // console.log(totalLoad);
    // console.log('--------------------');

    return Math.round(totalLoad * 10) / 10;
}

gantt.templates.resource_cell_class = function (start_date, end_date, resource, tasks) {


    var totalLoad = getTasksLoad(tasks, resource.id, start_date, end_date);
    var diffDays = workingDaysBetweenDates(start_date, end_date);
    var css = [];
    css.push("resource_marker");
    if (diffDays > 0)
        if (totalLoad / diffDays <= 8) {
            css.push("workday_ok");
        } else {
            css.push("workday_over");
        }
    else
        css.push("workday_weekend");
    return css.join(" ");
};

gantt.templates.resource_cell_value = function (start_date, end_date, resource, tasks) {

    var totalLoad = getTasksLoad(tasks, resource.id, start_date, end_date);

    var tasksIds = "data-recource-tasks='" + JSON.stringify(tasks.map(function (task) {
        return task.id
    })) + "'";

    var resourceId = "data-resource-id='" + resource.id + "'";

    var dateAttr = "data-cell-date='" + gantt.templates.xml_format(start_date) + "'";

    return "<div " + tasksIds + " " + resourceId + " " + dateAttr + ">" + totalLoad + "</div>";
};

gantt.locale.labels.section_resources = "Owners";
gantt.showLightbox = showEditor;
gantt.config.lightbox.sections = [
    {
        name: "text", label: 'Summary', height: 38, map_to: "text", type: "textarea", focus: true
    },
    {
        name: 'owner', label: 'Assignee', height: 38, type: "select", map_to: "owner_id", options: managedUsers
    },
    {
        name: "originalEstimate",
        label: 'Original Estimate',
        height: 38,
        map_to: "originalEstimate",
        type: "textarea",
        focus: true
    },
    {
        name: "time", height: 72, map_to: "auto", type: "time"
    },
    {
        name: 'priority',
        label: 'Priority',
        height: 38,
        type: "select",
        map_to: "priority",
        options: gantt.serverList("priority")
    },
    {
        name: 'originalEstimateEditor',
        label: 'Original Estimate',
        height: 38,
        type: "originalEstimateEditor",
        map_to: "auto"
    }
];

gantt.config.resource_store = "resource";
gantt.config.resource_property = "owner";
gantt.config.order_branch = true;
gantt.config.open_tree_initially = true;
gantt.config.layout = {
    css: "gantt_container",
    rows: [
        {
            cols: [
                {view: "grid", group: "grids", scrollY: "scrollVer"},
                {resizer: true, width: 1},
                {view: "timeline", scrollX: "scrollHor", scrollY: "scrollVer"},
                {view: "scrollbar", id: "scrollVer", group: "vertical"}
            ],
            gravity: 2
        },
        {resizer: true, width: 1},
        {
            height: 170,
            config: resourceConfig,
            cols: [
                {view: "resourceGrid", group: "grids", width: 435, scrollY: "resourceVScroll"},
                {resizer: true, width: 1},
                {view: "resourceTimeline", scrollX: "scrollHor", scrollY: "resourceVScroll"},
                {view: "scrollbar", id: "resourceVScroll", group: "vertical"}
            ],
            gravity: 1
        },
        {view: "scrollbar", id: "scrollHor"}
    ]
};

var resourcesStore = gantt.createDatastore({
    name: gantt.config.resource_store,
    type: "treeDatastore",
    initItem: function (item) {
        item.parent = item.parent || gantt.config.root_id;
        item[gantt.config.resource_property] = item.parent;
        item.open = true;
        return item;
    }
});

gantt.attachEvent("onTaskCreated", function (task) {
    task[gantt.config.resource_property] = [];
    return true;
});

resourcesStore.attachEvent("onParse", function () {
    var people = [];
    resourcesStore.eachItem(function (res) {
        if (!resourcesStore.hasChild(res.id)) {
            var copy = gantt.copy(res);
            copy.key = res.id;
            copy.label = res.text;
            people.push(copy);
        }
    });
    gantt.updateCollection("people", people);
});

gantt.config.quickinfo_buttons = ["close_button"];
gantt.locale.labels["open_in_jira"] = "Open in Jira";
gantt.locale.labels["close_button"] = "Close";
gantt.$click.buttons.open_in_jira = function (id) {
    return false;
};
gantt.$click.buttons.close_button = function (id) {
    gantt.hideQuickInfo(id);
    return false;
};

gantt.templates.quick_info_title = function (start, end, task) {
    if (!task.taskType || task.taskType === 'project' || task.taskType === 'client') {
        return task.text;
    }

    var store = gantt.getDatastore("resource");
    var assignments = task[gantt.config.resource_property] || [];
    var owner = store.getItem(assignments.resource_id);

    return "<div class='task-info'><span class='client'>" + task.client + "</span>" +
        "<span class='priority'>" + task.priorityName + " <img class=\"priority-icon\" alt=\"" + task.priorityName + "\" src=\"" + task.priorityIcon + "\"/></span>" +
        "<h2><img src=\"" + task.issueTypeIcon + "\" /> <a class=\"gantt-task-link\" href=\"https://jira.devfactory.com/browse/" + task.key + "\" target=\"_blank\">" + task.key + "</a> " + task.text + "</h2>" +
        "<span class='assignee'><img class=\"gantt-avatar\" src=\"" + owner.avatar + "\" /> " + owner.text + "</span>" +
        "<span class='status'>" + task.status.name + "</span><hr/>"
};

gantt.templates.quick_info_date = function (start, end, task) {

    if (!task.taskType || task.taskType === 'project' || task.taskType === 'client') {
        return "<span>" + gantt.templates.tooltip_date_format(start) + '</span> - <span>' + gantt.templates.tooltip_date_format(end) + '</span>';
    }
    var content = "<span>" + gantt.templates.tooltip_date_format(start) + "</span> - <span>" + gantt.templates.tooltip_date_format(end) + "</span><br/>" +
        "<div class='estimates'><span>Original Estimate:</span> " + task.originalEstimate + "<br/>" +
        "<span>Remaining Estimate:</span> " + (task.remainingEstimate ? task.remainingEstimate : '-') + "<br/>" +
        "<span>Time Spent:</span> " + (task.timeSpent ? task.timeSpent : '-') + "</div>" +
        "<div class='meta-dates'>Sync: <span>" + (task.lastSync ? task.lastSync : '-') + '</span><br/>' +
        "Update: <span>" + (task.updated ? task.updated : '-') + '</span></div>';
    return content;
};

gantt.templates.quick_info_content = function (start, end, task) {
    if (!task.taskType || task.taskType === 'project' || task.taskType === 'client') {
        return '';
    }

    return task.description ? '<div class="task-description">' + task.description + '</div>' : '';
};

function expandAll() {
    gantt.eachTask(function (task) {
        task.$open = true;
    });
    gantt.render();
}

function collapseAll() {
    gantt.eachTask(function (task) {
        task.$open = false;
    });
    gantt.render();
}

gantt.config.scale_height = 60;
gantt.config.scales = [
    {unit: "month", step: 1, format: "%F, %Y"},
    {unit: "day", step: 1, format: "%j, %D"},
    // {unit: "hour", step: 1, format: "%H"}
];

gantt.templates.progress_text = function (start, end, task) {
    return "<span>" + Math.round(task.progress * 100) + "% </span>";
};

gantt.config.duration_unit = "day";//an hour
gantt.config.duration_step = 1;
gantt.config.drag_move = true;
gantt.config.drag_progress = false;
gantt.config.drag_project = false;
gantt.config.order_branch = false;


// gantt.attachEvent("onBeforeTaskDrag", function(id, mode, e){
//     //any custom logic here
//     return false;
// });


gantt.attachEvent("onBeforeLightbox", function (id) {
    var task = gantt.getTask(id);
    return task.$new;
});

var inlineEditors = gantt.ext.inlineEditors;

inlineEditors.attachEvent("onBeforeEditStart", function (state) {
    // console.log(state);
    if (state.id.startsWith('p') || readonly)
        return false;
    // -> {id: itemId, columnName: columnName};
    return true;
});

gantt.attachEvent("onBeforeLinkAdd", function (id, link) {
    console.log(link);
    if (link.source.startsWith('p') || link.target.startsWith('p'))
        return false;
    return true;
});

gantt.attachEvent("onBeforeTaskDrag", function (id, parent, tindex) {
    if (readonly)
        return false;

    return true;
});

inlineEditors.attachEvent("onSave", function (state) {
    // console.log(stateete);
    // -> { id: itemId,
    //      columnName: columnName,
    //      oldValue: value,
    //      newValue: value
    //    };
});



