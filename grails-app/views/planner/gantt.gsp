<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 11/26/2019
  Time: 8:47 PM
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="kendo"/>
    <title>Planner</title>
</head>

<body>
<div id="content" role="main" class="container-fluid">
    <section class="row colset-2-its">
        <div class="col-lg-2">
            <g:render template="/filter/filter" model="${[hideTime: true]}"/>
        </div>

        <div class="col-lg-10">
            <div id="gantt"></div>
        </div>
    </section>
</div>

<script>
    $(document).ready(function () {
        var serviceRoot = "https://demos.telerik.com/kendo-ui/service";
        var tasksDataSource = new kendo.data.GanttDataSource({
            transport: {
                read: {
                    url: serviceRoot + "/GanttTasks",
                    dataType: "jsonp"
                },
                update: {
                    url: serviceRoot + "/GanttTasks/Update",
                    dataType: "jsonp"
                },
                destroy: {
                    url: serviceRoot + "/GanttTasks/Destroy",
                    dataType: "jsonp"
                },
                create: {
                    url: serviceRoot + "/GanttTasks/Create",
                    dataType: "jsonp"
                },
                parameterMap: function (options, operation) {
                    if (operation !== "read") {
                        return {models: kendo.stringify(options.models || [options])};
                    }
                }
            },
            schema: {
                model: {
                    id: "id",
                    fields: {
                        id: {from: "ID", type: "number"},
                        orderId: {from: "OrderID", type: "number", validation: {required: true}},
                        parentId: {from: "ParentID", type: "number", defaultValue: null, validation: {required: true}},
                        start: {from: "Start", type: "date"},
                        end: {from: "End", type: "date"},
                        title: {from: "Title", defaultValue: "", type: "string"},
                        percentComplete: {from: "PercentComplete", type: "number"},
                        summary: {from: "Summary", type: "boolean"},
                        expanded: {from: "Expanded", type: "boolean", defaultValue: true}
                    }
                }
            }
        });

        var dependenciesDataSource = new kendo.data.GanttDependencyDataSource({
            transport: {
                read: {
                    url: serviceRoot + "/GanttDependencies",
                    dataType: "jsonp"
                },
                update: {
                    url: serviceRoot + "/GanttDependencies/Update",
                    dataType: "jsonp"
                },
                destroy: {
                    url: serviceRoot + "/GanttDependencies/Destroy",
                    dataType: "jsonp"
                },
                create: {
                    url: serviceRoot + "/GanttDependencies/Create",
                    dataType: "jsonp"
                },
                parameterMap: function (options, operation) {
                    if (operation !== "read") {
                        return {models: kendo.stringify(options.models || [options])};
                    }
                }
            },
            schema: {
                model: {
                    id: "id",
                    fields: {
                        id: {from: "ID", type: "string"},
                        predecessorId: {from: "PredecessorID", type: "number"},
                        successorId: {from: "SuccessorID", type: "number"},
                        type: {from: "Type", type: "number"}
                    }
                }
            }
        });

        var gantt = $("#gantt").kendoGantt({
            dataSource: tasksDataSource,
            dependencies: dependenciesDataSource,
            resources: {
                field: "resources",
                dataColorField: "Color",
                dataTextField: "Name",
                dataSource: {
                    transport: {
                        read: {
                            url: serviceRoot + "/GanttResources",
                            dataType: "jsonp"
                        }
                    },
                    schema: {
                        model: {
                            id: "id",
                            fields: {
                                id: {from: "ID", type: "number"}
                            }
                        }
                    }
                }
            },
            assignments: {
                dataTaskIdField: "TaskID",
                dataResourceIdField: "ResourceID",
                dataValueField: "Units",
                dataSource: {
                    transport: {
                        read: {
                            url: serviceRoot + "/GanttResourceAssignments",
                            dataType: "jsonp"
                        },
                        update: {
                            url: serviceRoot + "/GanttResourceAssignments/Update",
                            dataType: "jsonp"
                        },
                        destroy: {
                            url: serviceRoot + "/GanttResourceAssignments/Destroy",
                            dataType: "jsonp"
                        },
                        create: {
                            url: serviceRoot + "/GanttResourceAssignments/Create",
                            dataType: "jsonp"
                        },
                        parameterMap: function (options, operation) {
                            if (operation !== "read") {
                                return {models: kendo.stringify(options.models || [options])};
                            }
                        }
                    },
                    schema: {
                        model: {
                            id: "ID",
                            fields: {
                                ID: {type: "number"},
                                ResourceID: {type: "number"},
                                Units: {type: "number"},
                                TaskID: {type: "number"}
                            }
                        }
                    }
                }
            },
            views: [
                "day",
                {type: "week", selected: true},
                "month"
            ],
            columns: [
                {field: "id", title: "ID", width: 60},
                {field: "title", title: "Summary", editable: true, sortable: true, width:500},
                {
                    field: "start",
                    title: "Start Date",
                    format: "{0:MM/dd/yyyy}",
                    width: 110,
                    editable: true,
                    sortable: true
                },
                {field: "end", title: "Due Date", format: "{0:MM/dd/yyyy}", width: 110, editable: true, sortable: true},
                {field: "resources", title: "Assigned Resources", editable: true, width: 200},
                {field: "priority", title: "Priority", width: 120},
                {field: "components", title: "Components", width: 120},
                {field: "originalTimeEstimated", title: "Original Time Estimated", width: 120},
                {field: "remainingTimeEstimated", title: "Remaining Time Estimated", width: 120},
                {field: "timeSpent", title: "Time Spent", width: 120},
                {field: "issueType", title: "Issue Type", width: 120},
                {field: "reporter", title: "Reporter", width: 120},
                {field: "creationDate", title: "Creation Date", format: "{0:MM/dd/yyyy}", width: 110, editable: true, sortable: true},
                {field: "dateUpdated", title: "Date Updated", format: "{0:MM/dd/yyyy}", width: 110, editable: true, sortable: true},
            ],
            height: $(window).height() - $('.header').height() - 82,

            showWorkHours: false,
            showWorkDays: false,
            resizable: true,
            snap: false
        }).data("kendoGantt");

        $(window).resize(function () {
            window.location.reload();
        });

    });
</script>
</body>
</html>