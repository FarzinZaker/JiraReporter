gantt.config.xml_date = "%d-%m-%Y";
gantt.config.grid_resize = true;

gantt.config.auto_scheduling = true;
gantt.config.auto_scheduling_strict = true;
gantt.config.auto_scheduling_compatibility = true;

gantt.attachEvent("onTaskDrag", function (id, mode, task, original) {
    console.log('drag')
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

gantt.config.columns = [
    {
        name: "text", tree: true, width: 400, label: "Summary", resize: true, template: function (task) {
            if (task.type == 'project')
                return '<b>' + task.text + '</b>';
            else
                return '<a class="gantt-task-link" href="https://jira.devfactory.com/browse/' + task.key + '" target="_blank"><img src="' + task.issueTypeIcon + '" /> ' + task.key + '</a> ' + task.text;
        }
    },
    // {name: "start_date", align: "center", width: 80, resize: true},
    {
        name: "owner", align: "left", width: 100, label: "Assignee", template: function (task) {
            if (task.type == gantt.config.types.project) {
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
        }, resize: true
    },
    {
        name: "status", width: 80, label: "Status", resize: true, template: function (task) {
            if (task.type == gantt.config.types.project) {
                return "";
            }
            return task.status.name;
        }
    },
    {
        name: "priority", width: 32, label: "P", resize: true, template: function (task) {
            if (task.type == gantt.config.types.project) {
                return "";
            }
            return '<img class="priority-icon" src="' + task.priority.icon + '"/>';
        }
    }
    // {name: "duration", width: 60, align: "center"},
    // {name: "add", width: 44}
];

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

function getTasksLoad(tasks, resourceId) {
    var totalLoad = 0;
    tasks.forEach(function (task) {
        var assignments = gantt.getResourceAssignments(resourceId, task.id);
        totalLoad += assignments[0].value;
    });
    return totalLoad;
}

gantt.templates.resource_cell_class = function (start_date, end_date, resource, tasks) {

    var totalLoad = getTasksLoad(tasks, resource.id);
    var css = [];
    css.push("resource_marker");
    if (totalLoad <= 8) {
        css.push("workday_ok");
    } else {
        css.push("workday_over");
    }
    return css.join(" ");
};

gantt.templates.resource_cell_value = function (start_date, end_date, resource, tasks) {

    var totalLoad = getTasksLoad(tasks, resource.id);

    var tasksIds = "data-recource-tasks='" + JSON.stringify(tasks.map(function (task) {
        return task.id
    })) + "'";

    var resourceId = "data-resource-id='" + resource.id + "'";

    var dateAttr = "data-cell-date='" + gantt.templates.xml_format(start_date) + "'";

    return "<div " + tasksIds + " " + resourceId + " " + dateAttr + ">" + totalLoad + "</div>";
};

gantt.locale.labels.section_resources = "Owners";
gantt.config.lightbox.sections = [
    {name: "description", height: 38, map_to: "text", type: "textarea", focus: true},
    {
        name: "resources", type: "resources", map_to: "owner", options: gantt.serverList("people"), default_value: 8
    },

    {name: "time", type: "duration", map_to: "auto"}
];

gantt.config.resource_store = "resource";
gantt.config.resource_property = "owner";
gantt.config.order_branch = true;
gantt.config.open_tree_initially = false;
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