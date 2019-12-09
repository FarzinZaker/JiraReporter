// var taskUpdateList = {};
// gantt.attachEvent("onBeforeTaskUpdate", function (id, task) {
//     taskUpdateList[id] = task;
// });

gantt.attachEvent("onAfterTaskUpdate", function (id, task) {
    // console.log(taskUpdateList[id]);
    console.log(task);
    // taskUpdateList[id] = null;
});


gantt.attachEvent("onAfterLinkAdd", function (id, link) {
    console.log(link)
});

gantt.attachEvent("onAfterLinkDelete", function (id, link) {
    console.log(link)
});