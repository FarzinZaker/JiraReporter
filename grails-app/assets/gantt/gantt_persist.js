gantt.attachEvent("onBeforeTaskUpdate", function (id, task) {
    console.log(task);
});

gantt.attachEvent("onAfterTaskUpdate", function (id, task) {
    console.log(task);
});

gantt.attachEvent("onAfterLinkAdd", function (id, link) {
    console.log(link)
});

gantt.attachEvent("onBeforeLinkUpdate", function (id, link) {
    console.log(link)
});

gantt.attachEvent("onAfterLinkUpdate", function (id, link) {
    console.log(link)
});

gantt.attachEvent("onAfterLinkDelete", function (id, link) {
    console.log(link)
});