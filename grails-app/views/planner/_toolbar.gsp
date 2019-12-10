<div id="toolbar"></div>

<script>

    var groupByPriority = false;
    var zoomed = false;

    $(document).ready(function () {
        $("#toolbar").kendoToolBar({
            items: [
                {
                    type: "button", icon: "undo", click: function () {
                        gantt.performAction('undo');
                    }
                },
                {
                    type: "button", icon: "redo", click: function () {
                        gantt.performAction('redo');
                    }
                },
                // {type: "separator"},
                // {type: "button",icon: "indent-decrease"},
                // {type: "button",icon: "indent-increase"},
                {type: "separator"},
                {
                    type: "button", icon: "arrow-left", click: function () {
                        gantt.performAction('outdent');
                    }
                },
                {
                    type: "button", icon: "arrow-right", click: function () {
                        gantt.performAction('indent');
                    }
                },
                {type: "separator"},
                {type: "button", icon: "minus"},
                {type: "button", icon: "plus"},
                {type: "separator"},
                {
                    type: "button", text: "Group by Priority", togglable: true, icon: 'layout', toggle: function () {
                        groupByPriority = !groupByPriority;
                        if (groupByPriority)
                            showGroups('priority');
                        else
                            showGroups();
                    }
                },
                {
                    type: "button",
                    text: "Critical Path",
                    togglable: true,
                    icon: "rule-horizontal",
                    toggle: function (e) {
                        gantt.config.highlight_critical_path = !gantt.config.highlight_critical_path;
                        gantt.render();
                    }
                },
                {type: "separator"},
                {
                    type: "button", text: "Zoom to Fit", togglable: true, icon: "zoom", toggle: function () {
                        zoomed = !zoomed;
                        if (zoomed) {
                            saveConfig();
                            zoomToFit();
                        } else {
                            restoreConfig();
                            gantt.render();
                        }
                    }
                },
                {
                    type: "button", text: "Today", icon: "calendar-date", click: function () {
                        showToday();
                    }
                }
            ]
        });
    });
</script>