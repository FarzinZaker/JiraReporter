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
// gantt.attachEvent("onGanttReady", function () {
var tooltips = gantt.ext.tooltips;

gantt.templates.tooltip_text = function (start, end, task) {
    // if (task.type == 'project')
        return;
    // var store = gantt.getDatastore("resource");
    // var assignments = task[gantt.config.resource_property] || [];
    // var owner = store.getItem(assignments.resource_id);

    // return "<div class='task-info'><span class='client'>" + task.client + "</span><br/>" +
    //     "<h2><a class=\"gantt-task-link\" href=\"https://jira.devfactory.com/browse/" + task.key + "\" target=\"_blank\"><img src=\"" + task.issueTypeIcon + "\" /> " + task.key + "</a> " + task.text + "</h2><br/>" +
    //     "<span class='assignee'><img class=\"gantt-avatar\" src=\"" + owner.avatar + "\" />" + owner.text + "</span><br/>" +
    //     "<hr/>" +
    //     "<span class='priority'><img class=\"priority-icon\" alt=\"" + task.priority.name + "\" src=\"" + task.priority.icon + "\"/>" + task.priority.name + "</span><br/>" +
    //     "<span class='status'>" + task.status.name + "</span><br/>" +
    //     "<hr/>" +
    //     "<span>" + gantt.templates.tooltip_date_format(start) + '</span> - <span>' + gantt.templates.tooltip_date_format(end) + '</span></div>';// +
        // "<hr/>" +
        // "<span>Original Estimate</span> " + task.originalEstimate.formatted + "<br/>" +
        // "<span>Remaining Estimate</span> " + task.remainingEstimate.formatted + "<br/>" +
        // "<span>Time Spent</span> " + task.timeSpent.formatted + "<br/>" +
        // "<hr/>";
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
                "<b>" + linkTypeToString(link.type) + "</b>",
                from.text + " <span>THEN</span> " + to.text
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
        var assignments = getResourceAssignments(resource, store);

        var totalDuration = 0;
        for (var i = 0; i < assignments.length; i++) {
            var task = gantt.getTask(assignments[i].task_id);
            totalDuration += task.duration * assignments[i].value;
        }

        return [
            "<b>" + resource.text + "</b><hr/><span>Tasks assigned:</span> <b>" + assignments.length + "</b>",
            "<span>Total load:</span> <b>" + (totalDuration || 0) + "h</b>"
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
            "<b>" + store.getItem(resourceId).text + "</b>" + "<br/>" + gantt.templates.tooltip_date_format(date) +
            "<hr/>" +
            ids.map(function (id, index) {
                var task = gantt.getTask(id);
                var assignenment = gantt.getResourceAssignments(resourceId, task.id);
                var amount = "";
                var taskIndex = (index + 1);
                if (assignenment[0]) {
                    amount = " <b>(" + assignenment[0].value + "h)</b> ";
                }
                return '<span>' + task.key + "</span> " + amount + ': ' + task.text;
            }).join("<br>")
        ].join("<br>").replace('<br><br>', '<br>');

        return html;
    }
});
// });