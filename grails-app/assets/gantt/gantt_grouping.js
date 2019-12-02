

gantt.templates.grid_row_class = gantt.templates.task_row_class = function (start, end, task) {
    if (task.$virtual)
        return "summary-row"
};

gantt.templates.task_class = function (start, end, task) {
    if (task.$virtual)
        return "summary-bar";
};

function showGroups(listname) {
    if (listname) {
        var relation = listname == 'userGroups' ? 'user' : listname;
        gantt.groupBy({
            groups: gantt.serverList(listname),
            relation_property: relation,
            group_id: "key",
            group_text: "label"
        });
    } else {
        gantt.groupBy(false);

    }
}