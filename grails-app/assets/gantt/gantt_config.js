gantt.config.xml_date = "%d-%m-%Y";
gantt.config.grid_resize = true;

gantt.config.auto_scheduling = true;
gantt.config.auto_scheduling_strict = true;
gantt.config.auto_scheduling_compatibility = true;

gantt.templates.grid_row_class = function (start_date, end_date, item) {
    // console.log(item.overdue);
    if (item.overdue) return "red";
    // else return "ok";
};

gantt.templates.task_row_class = function (start_date, end_date, item) {
    if (item.overdue) return "red";
    // else return "ok";
};

gantt.templates.task_class = function (start, end, task) {
    return 'bar-' + task.taskType
};

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
        name: "text", tree: true, width: 400, label: "Summary", resize: true, editor: textEditor
        // , template: function (task) {
        //     if (task.type === gantt.config.types.project)
        //         return '<b>' + task.text + '</b>';
        //     else
        //         return '<a class="gantt-task-link" href="https://jira.devfactory.com/browse/' + task.key + '" target="_blank"><img src="' + task.issueTypeIcon + '" /> ' + task.key + '</a> ' + task.text;
        // }
    },
    // {name: "start_date", align: "center", width: 80, resize: true},
    // {
    //     name: "owner", align: "left", width: 100, label: "Assignee", template: function (task) {
    //         if (task.type === gantt.config.types.project) {
    //             return "";
    //         }
    //
    //         var store = gantt.getDatastore("resource");
    //         var assignments = task[gantt.config.resource_property] || [];
    //
    //         // if (!assignments || !assignments.length) {
    //         //     return "Unassigned";
    //         // }
    //
    //         if (assignments.length == 1) {
    //             return store.getItem(assignments[0].resource_id).text;
    //         }
    //
    //         var result = "";
    //         // assignments.forEach(function (assignment) {
    //         var owner = store.getItem(assignments.resource_id);
    //         if (!owner) {
    //             return "Unassigned";
    //         }
    //         // result += "<div class='owner-label' title='" + owner.text + "'>" + owner.text.substr(0, 1) + "</div>";
    //
    //         result += '<img class="gantt-avatar" src="' + owner.avatar + '" /> ' + owner.text.split(' ')[0];
    //
    //         // });
    //
    //         return result;
    //     }, resize: true
    // },
    // {
    //     name: "status", width: 80, label: "Status", resize: true, template: function (task) {
    //         if (task.type === gantt.config.types.project) {
    //             return "";
    //         }
    //         return task.status.name;
    //     }
    // },
    // {
    //     name: "start_date", width: 80, label: "Start Date", resize: true, hide: false, template: function (task) {
    //         if (task.type === gantt.config.types.project) {
    //             return "";
    //         }
    //         return task.start_date ? task.start_date : '-';
    //     }
    // },
    // {
    //     name: "dueDate", width: 80, label: "Due Date", resize: true, hide: false, template: function (task) {
    //         if (task.type === gantt.config.types.project) {
    //             return "";
    //         }
    //         return task.dueDate ? task.dueDate : '-';
    //     }
    // },
    // {
    //     name: "originalEstimate",
    //     width: 90,
    //     label: "Orig. Est.",
    //     resize: true,
    //     hide: false,
    //     editor: durationEditor,
    //     template: function (task) {
    //         if (task.type === gantt.config.types.project) {
    //             return "";
    //         }
    //         return task.originalEstimate.formatted;
    //     }
    // },
    // {
    //     name: "remainingEstimate", width: 90, label: "Rem. Est.", resize: true, hide: true, template: function (task) {
    //         if (task.type === gantt.config.types.project) {
    //             return "";
    //         }
    //         return task.remainingEstimate.formatted;
    //     }
    // },
    // {
    //     name: "timeSpent", width: 90, label: "Time Spent", resize: true, hide: true, template: function (task) {
    //         if (task.type === gantt.config.types.project) {
    //             return "";
    //         }
    //         return task.timeSpent.formatted;
    //     }
    // },
    // {
    //     name: "priority", width: 32, label: "P", resize: true, template: function (task) {
    //         if (task.type === gantt.config.types.project) {
    //             return "";
    //         }
    //         return '<img class="priority-icon" alt="' + task.priorityName + '" src="' + task.priorityIcon + '"/>';
    //     }
    // }
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
    if (task.type === gantt.config.types.project) {
        return task.text;
    }

    var store = gantt.getDatastore("resource");
    var assignments = task[gantt.config.resource_property] || [];
    var owner = store.getItem(assignments.resource_id);

    return "<div class='task-info'><span class='client'>" + task.client + "</span>" +
        "<span class='priority'>" + task.priorityName + " <img class=\"priority-icon\" alt=\"" + task.priorityName + "\" src=\"" + task.priorityIcon + "\"/></span>" +
        "<h2><a class=\"gantt-task-link\" href=\"https://jira.devfactory.com/browse/" + task.key + "\" target=\"_blank\"><img src=\"" + task.issueTypeIcon + "\" /> " + task.key + "</a> " + task.text + "</h2>" +
        "<span class='assignee'><img class=\"gantt-avatar\" src=\"" + owner.avatar + "\" /> " + owner.text + "</span>" +
        "<span class='status'>" + task.status.name + "</span><hr/>"
};

gantt.templates.quick_info_date = function (start, end, task) {

    if (task.type === gantt.config.types.project) {
        return "<span>" + gantt.templates.tooltip_date_format(start) + '</span> - <span>' + gantt.templates.tooltip_date_format(end) + '</span>';
    }

    return "<span>" + gantt.templates.tooltip_date_format(start) + '</span> - <span>' + gantt.templates.tooltip_date_format(end) + '</span><br/>' +
        'Due Date: <span>' + (task.dueDate ? task.dueDate : '-') + '</span><br/>' +
        "<div class='estimates'><span>Original Estimate:</span> " + task.originalEstimate.formatted + "<br/>" +
        "<span>Remaining Estimate:</span> " + task.remainingEstimate.formatted + "<br/>" +
        "<span>Time Spent:</span> " + task.timeSpent.formatted + "</div>";
};

gantt.templates.quick_info_content = function (start, end, task) {
    if (task.type === gantt.config.types.project) {
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

gantt.config.scales = [
    {unit: "month", step: 1, format: "%F, %Y"},
    {unit: "day", step: 1, format: "%j, %D"}
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

var textEditor = {type: "text", map_to: "text"};
var dateEditor = {
    type: "date", map_to: "start_date", min: new Date(2018, 0, 1),
    max: new Date(2022, 0, 1)
};
var durationEditor = {type: "text", map_to: "originalEstimate"};


// gantt.attachEvent("onBeforeTaskDrag", function(id, mode, e){
//     //any custom logic here
//     return false;
// });
