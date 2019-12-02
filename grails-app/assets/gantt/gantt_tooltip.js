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

gantt.templates.tooltip_date_format = gantt.date.date_to_str("%F %j, %Y");
gantt.attachEvent("onGanttReady", function () {
    var tooltips = gantt.ext.tooltips;

    gantt.templates.tooltip_text = function (start, end, task) {
        var store = gantt.getDatastore("resource");
        var assignments = task[gantt.config.resource_property] || [];

        var owners = [];
        // assignments.forEach(function (assignment) {
        var owner = store.getItem(assignments.resource_id);
        if (owner)
            owners.push(owner.text);
        // });
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