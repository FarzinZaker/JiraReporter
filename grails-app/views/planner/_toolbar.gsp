<div id="toolbar"></div>

<script>

    $(document).ready(function () {
        $("#toolbar").kendoToolBar({
            items: [
                {
                    type: "button", icon: "refresh", click: function () {
                        reloadPlanner(true);
                    }
                },
                {type: "separator"},
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
                // {type: "separator"},
                // {
                //     type: "button", icon: "arrow-left", click: function () {
                //         gantt.performAction('outdent');
                //     }
                // },
                // {
                //     type: "button", icon: "arrow-right", click: function () {
                //         gantt.performAction('indent');
                //     }
                // },
                {type: "separator"},
                {
                    type: "button", icon: "minus", click: function () {
                        expandAll();
                    }
                },
                {
                    type: "button", icon: "plus", click: function () {
                        collapseAll();
                    }
                },
                {type: "separator"},
                {
                    type: "button", text: "Group by Priority", togglable: true, icon: 'layout', toggle: function (e) {
                        if (e.checked)
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
                    type: "button",
                    text: "Zoom to Fit",
                    togglable: true,
                    icon: "zoom",
                    id: "zoomToFit",
                    toggle: function (e) {
                        if (e.checked) {
                            restoreConfig();
                            $("#toolbar").data("kendoToolBar").toggle("#weeklyView", false);
                            saveConfig();
                            zoomToFit();
                        } else {
                            restoreConfig();
                            gantt.render();
                        }
                    }
                },
                {
                    type: "button",
                    text: "Weekly View",
                    togglable: true,
                    icon: "calendar",
                    id: "weeklyView",
                    toggle: function (e) {
                        if (e.checked) {
                            restoreConfig();
                            $("#toolbar").data("kendoToolBar").toggle("#zoomToFit", false);
                            saveConfig();
                            switchToWeeklyView();
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
                },
                {type: "separator"},
                {
                    template: "<input type='text' id='syncKey' class='k-textbox' style='width:120px;text-align:center;' placeholder='ISSUE KEY'/>"
                },
                {
                    type: "button", text: "", icon: "download", click: function () {
                        var issueKey = $('#syncKey').val();
                        if (!issueKey || issueKey.trim() === '') {
                            kendo.alert('Please enter the Issue Key.');
                            $('.k-alert .k-window-title.k-dialog-title').text('Planner');
                            return;
                        }

                        issueKey = issueKey.trim();

                        $.ajax({
                            url: '${createLink(action: 'addToDownloadQueue')}/' + issueKey,
                            dataType: 'json',
                            type: 'get',
                            success: function (data, textStatus, jQxhr) {
                                $('#syncKey').val('');
                                kendo.alert('We added ' + issueKey + ' to the download queue.');
                                $('.k-alert .k-window-title.k-dialog-title').text('Planner');
                            },
                            error: function (jqXhr, textStatus, errorThrown) {
                                console.log(errorThrown);
                            }
                        });
                    }
                }
            ]
        });
    });
</script>