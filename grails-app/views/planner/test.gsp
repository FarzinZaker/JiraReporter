
<!DOCTYPE html>
<head>
    <meta name="layout" content="gantt"/>
    <meta http-equiv="Content-type" content="text/html; charset=utf-8">
    <title>Inline editing</title>

    <style>
    html, body {
        height: 100%;
        padding: 0px;
        margin: 0px;
        overflow: hidden;
    }


    </style>
</head>

<body>
<div id="gantt_here" style='width:100%; height:100%;'></div>
<script>
    gantt.serverList("priority", [
        {key: "1", label: "Low"},
        {key: "2", label: "Normal"},
        {key: "3", label: "High"}
    ]);

    var textEditor = {type: "text", map_to: "text"};
    var dateEditor = {type: "date", map_to: "start_date", min: new Date(2018, 0, 1), max: new Date(2019, 0, 1)};
    var durationEditor = {type: "number", map_to: "duration", min:0, max: 100};
    var priority = {type: "select", map_to: "priority", options:gantt.serverList("priority")};

    function priorityLabel(task){
        var value = task.priority;
        var list = gantt.serverList("priority");
        for(var i = 0; i < list.length; i++){
            if(list[i].key == value){
                return list[i].label;
            }
        }
        return "";
    }

    gantt.config.columns = [
        {name: "text", tree: true, width: 200, resize: true, editor: textEditor},
        {name: "start_date", align: "center", width: 90, resize: true, editor: dateEditor},
        {name: "duration", align: "center", width: 90, resize: true, editor: durationEditor},
        {name: "priority", label: "Priority", width:80, align: "center", resize: true, editor: priority, template: priorityLabel},
        {name: "add", width: 44}
    ];

    gantt.init("gantt_here");
    gantt.parse(demo_tasks);

</script>
</body>