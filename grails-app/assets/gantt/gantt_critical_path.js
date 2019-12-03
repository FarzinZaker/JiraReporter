function updateCriticalPath(toggle) {
    toggle.enabled = !toggle.enabled;
    if (toggle.enabled) {
        toggle.value = "Hide Critical Path";
        gantt.config.highlight_critical_path = true;
    } else {
        toggle.value = "Show Critical Path";
        gantt.config.highlight_critical_path = false;
    }
    gantt.render();
}