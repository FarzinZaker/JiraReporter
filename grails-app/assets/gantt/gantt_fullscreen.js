gantt.attachEvent("onTemplatesReady", function () {
    var toggle = document.createElement("i");
    toggle.className = "fa fa-expand gantt-fullscreen";
    gantt.toggleIcon = toggle;
    gantt.$container.appendChild(toggle);
    toggle.onclick = function() {
        gantt.ext.fullscreen.toggle();
    };
});
gantt.attachEvent("onExpand", function () {
    var icon = gantt.toggleIcon;
    if (icon) {
        icon.className = icon.className.replace("fa-expand", "fa-compress");
    }

});
gantt.attachEvent("onCollapse", function () {
    var icon = gantt.toggleIcon;
    if (icon) {
        icon.className = icon.className.replace("fa-compress", "fa-expand");
    }
});