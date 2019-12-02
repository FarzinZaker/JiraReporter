var date_to_str = gantt.date.date_to_str(gantt.config.task_date);
var today = new Date(2018, 3, 5);
gantt.addMarker({
    start_date: today,
    css: "today",
    text: "Today",
    title: "Today: " + date_to_str(today)
});