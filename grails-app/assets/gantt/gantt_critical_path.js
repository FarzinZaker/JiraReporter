// function updateCriticalPath(toggle) {
//     toggle.enabled = !toggle.enabled;
//     if (toggle.enabled) {
//         toggle.value = "Hide Critical Path";
//         gantt.config.highlight_critical_path = true;
//     } else {
//         toggle.value = "Show Critical Path";
//         gantt.config.highlight_critical_path = false;
//     }
//     gantt.render();
// }

//also possible
// gantt.templates.task_class = function (start, end, task) {
//     if (gantt.isCriticalTask(task))
//         return "critical_task";
//     return "";
// };
//
// gantt.templates.link_class = function (link) {
//     if (gantt.isCriticalLink(link))
//         return "critical_link";
//     return "";
// };