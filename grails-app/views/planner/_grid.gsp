<style>
html,
body {
    height: 100%;
    padding: 0px;
    margin: 0px;
}

.gantt_grid_scale .gantt_grid_head_cell,
.gantt_task .gantt_task_scale .gantt_scale_cell {
    font-weight: bold;
    font-size: 14px;
    color: rgba(0, 0, 0, 0.7);
}

.resource_marker {
    text-align: center;
}

.resource_marker div {
    width: 28px;
    height: 28px;
    line-height: 29px;
    display: inline-block;
    border-radius: 15px;
    color: #FFF;
    margin: 3px;
}

.resource_marker.workday_ok div {
    background: #51c185;
}

.resource_marker.workday_over div {
    background: #ff8686;
}



.owner-label {
    width: 20px;
    height: 20px;
    line-height: 20px;
    font-size: 12px;
    display: inline-block;
    border: 1px solid #cccccc;
    border-radius: 25px;
    background: #e6e6e6;
    color: #6f6f6f;
    margin: 0 3px;
    font-weight: bold;
}

.gantt_tooltip {
    font-size: 13px;
    line-height: 16px;
}
</style>
<div class="gantt_control">
    <button onclick="updateCriticalPath(this)">Show Critical Path</button>
</div>
<div id="gantt_here" style='width:100%; height:100vh;'></div>

<script>

    function updateCriticalPath(toggle) {
        toggle.enabled = !toggle.enabled;
        if (toggle.enabled) {
            toggle.innerHTML = "Hide Critical Path";
            gantt.config.highlight_critical_path = true;
        } else {
            toggle.innerHTML = "Show Critical Path";
            gantt.config.highlight_critical_path = false;
        }
        gantt.render();
    }

    function linkTypeToString(linkType) {
        switch (linkType) {
            case gantt.config.links.start_to_start:
                return "Start to start";
            case gantt.config.links.start_to_finish:
                return "Start to finish";
            case gantt.config.links.finish_to_start:
                return "Finish to start";
            case gantt.config.links.finish_to_finish:
                return "Finish to finish";
            default:
                return ""
        }
    }

    var date_to_str = gantt.date.date_to_str(gantt.config.task_date);
    var today = new Date(2018, 3, 5);
    gantt.addMarker({
        start_date: today,
        css: "today",
        text: "Today",
        title: "Today: " + date_to_str(today)
    });

    gantt.templates.tooltip_date_format = gantt.date.date_to_str("%F %j, %Y");
    gantt.attachEvent("onGanttReady", function () {
        var tooltips = gantt.ext.tooltips;

        gantt.templates.tooltip_text = function (start, end, task) {
            var store = gantt.getDatastore("resource");
            var assignments = task[gantt.config.resource_property] || [];

            var owners = [];
            assignments.forEach(function (assignment) {
                var owner = store.getItem(assignment.resource_id)
                owners.push(owner.text);
            });
            return "<b>Task:</b> " + task.text + "<br/>" +
                "<b>Owner:</b>" + owners.join(",") + "<br/>" +
                "<b>Start date:</b> " +
                gantt.templates.tooltip_date_format(start) +
                "<br/><b>End date:</b> " + gantt.templates.tooltip_date_format(end);
        };

        tooltips.tooltipFor({
            selector: ".gantt_task_link",
            html: function (event, node) {

                var linkId = node.getAttribute(gantt.config.link_attribute);
                if (linkId) {
                    var link = gantt.getLink(linkId);
                    var from = gantt.getTask(link.source);
                    var to = gantt.getTask(link.target);

                    return [
                        "<b>Link:</b> " + linkTypeToString(link.type),
                        "<b>From: </b> " + from.text,
                        "<b>To: </b> " + to.text
                    ].join("<br>");
                }
            }
        });

        tooltips.tooltipFor({
            selector: ".gantt_row[resource_id]",
            html: function (event, node) {

                var resourceId = node.getAttribute("resource_id");
                var store = gantt.getDatastore(gantt.config.resource_store);
                var resource = store.getItem(resourceId);
                var assignments = getResourceAssignments(resource, store)

                var totalDuration = 0;
                for (var i = 0; i < assignments.length; i++) {
                    var task = gantt.getTask(assignments[i].task_id);
                    totalDuration += task.duration * assignments[i].value;
                }

                return [
                    "<b>Resource:</b> " + resource.text,
                    "<b>Tasks assigned:</b> " + assignments.length,
                    "<b>Total load: </b>" + (totalDuration || 0) + "h"
                ].join("<br>");

            }
        });


        tooltips.tooltipFor({
            selector: ".gantt_scale_cell",
            html: function (event, node) {
                var relativePosition = gantt.utils.dom.getRelativeEventPosition(event, gantt.$task_scale);
                return gantt.templates.tooltip_date_format(gantt.dateFromPos(relativePosition.x));
            }
        });

        tooltips.tooltipFor({
            selector: ".gantt_resource_marker",
            html: function (event, node) {
                var dataElement = node.querySelector("[data-recource-tasks]");
                var ids = JSON.parse(dataElement.getAttribute("data-recource-tasks"));

                var date = gantt.templates.xml_date(dataElement.getAttribute("data-cell-date"));
                var resourceId = dataElement.getAttribute("data-resource-id");

                var relativePosition = gantt.utils.dom.getRelativeEventPosition(event, gantt.$task_scale);

                var store = gantt.getDatastore("resource");

                var html = [
                    "<b>" + store.getItem(resourceId).text + "</b>" + ", " + gantt.templates.tooltip_date_format(date),
                    "",
                    ids.map(function (id, index) {
                        var task = gantt.getTask(id);
                        var assignenment = gantt.getResourceAssignments(resourceId, task.id);
                        var amount = "";
                        var taskIndex = (index + 1);
                        if (assignenment[0]) {
                            amount = " (" + assignenment[0].value + "h) ";
                        }
                        return "Task #" + taskIndex + ": " + amount + task.text;
                    }).join("<br>")
                ].join("<br>");

                return html;
            }
        });
    });


    gantt.config.columns = [
        { name: "text", tree: true, width: 200, resize: true },
        { name: "start_date", align: "center", width: 80, resize: true },
        {
            name: "owner", align: "center", width: 75, label: "Owner", template: function (task) {
                if (task.type == gantt.config.types.project) {
                    return "";
                }

                var store = gantt.getDatastore("resource");
                var assignments = task[gantt.config.resource_property] || [];

                if (!assignments || !assignments.length) {
                    return "Unassigned";
                }

                if (assignments.length == 1) {
                    return store.getItem(assignments[0].resource_id).text;
                }

                var result = "";
                assignments.forEach(function (assignment) {
                    var owner = store.getItem(assignment.resource_id);
                    if (!owner)
                        return;
                    result += "<div class='owner-label' title='" + owner.text + "'>" + owner.text.substr(0, 1) + "</div>";

                });

                return result;
            }, resize: true
        },
        { name: "duration", width: 60, align: "center" },
        { name: "add", width: 44 }
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

                    var assignments = getResourceAssignments(resource, store)

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
        { name: "description", height: 38, map_to: "text", type: "textarea", focus: true },
        {
            name: "resources", type: "resources", map_to: "owner", options: gantt.serverList("people"), default_value: 8
        },

        { name: "time", type: "duration", map_to: "auto" }
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
                    { view: "grid", group: "grids", scrollY: "scrollVer" },
                    { resizer: true, width: 1 },
                    { view: "timeline", scrollX: "scrollHor", scrollY: "scrollVer" },
                    { view: "scrollbar", id: "scrollVer", group: "vertical" }
                ],
                gravity: 2
            },
            { resizer: true, width: 1 },
            {
                config: resourceConfig,
                cols: [
                    { view: "resourceGrid", group: "grids", width: 435, scrollY: "resourceVScroll" },
                    { resizer: true, width: 1 },
                    { view: "resourceTimeline", scrollX: "scrollHor", scrollY: "resourceVScroll" },
                    { view: "scrollbar", id: "resourceVScroll", group: "vertical" }
                ],
                gravity: 1
            },
            { view: "scrollbar", id: "scrollHor" }
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

    gantt.attachEvent("onTaskCreated", function(task){
        task[gantt.config.resource_property] = [];
        return true;
    });

    gantt.init("gantt_here");
    gantt.load("../assets/resource_project_assignments.json");

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

    resourcesStore.parse([
        { id: 1, text: "QA", parent: null },
        { id: 2, text: "Development", parent: null },
        { id: 3, text: "Sales", parent: null },
        { id: 4, text: "Other", parent: null },
        { id: 5, text: "Unassigned", parent: 4 },
        { id: 6, text: "John", parent: 1, unit: "hours/day" },
        { id: 7, text: "Mike", parent: 2, unit: "hours/day" },
        { id: 8, text: "Anna", parent: 2, unit: "hours/day" },
        { id: 9, text: "Bill", parent: 3, unit: "hours/day" },
        { id: 10, text: "Floe", parent: 3, unit: "hours/day" }
    ]);
</script>