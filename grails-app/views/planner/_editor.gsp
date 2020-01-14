<%@ page import="jirareporter.IssueType; jirareporter.Priority; jirareporter.Configuration" %>
<div id="editorWindow">
    <div class="error" id="editError" style="display: none;">

    </div>

    <form id="editForm">
        <table style="width:100%;table-layout: fixed;">
            <tr>
                <td>
                    <div class="field">
                        <label for="edit_project">Project:</label>
                        <input id="edit_project" name="project" type="text">
                    </div>
                </td>
                <td colspan="2">
                    <div class="field">
                        <label for="edit_parent">Parent:</label>

                        <input id="edit_parent" name="issue" type="text">
                    </div>
                </td>
            </tr>
            <tr>
                <td>
                    <div class="field">
                        <label for="edit_client">Client:</label>
                        <input id="edit_client" name="client" type="text">
                    </div>
                </td>
                <td>
                    <div class="field">
                        <label for="edit_issueType">Issue Type:</label>
                        <input id="edit_issueType" name="issueType" type="text">
                    </div>
                </td>
                <td>
                    <div class="field">
                        <label for="edit_priority">Priority:</label>
                        <input id="edit_priority" name="priority" type="text">
                    </div>
                </td>
            </tr>
            <tr>
                <td colspan="3">
                    <div class="field">
                        <label for="edit_summary">Summary:</label>
                        <input id="edit_summary" name="summary" type="text" autocomplete="off">
                    </div>
                </td>
            </tr>
            <tr>
                <td colspan="3">
                    <div class="field">
                        <label for="edit_description">Description:</label>
                        <textarea id="edit_description" name="description" type="text" autocomplete="off"></textarea>
                    </div>
                </td>
            </tr>
            <tr>
                <td colspan="2">
                    <div class="field">
                        <label for="edit_component">Component/s:</label>
                        <input id="edit_component" name="component" type="text">
                    </div>
                </td>
                <td>
                    <div class="field">
                        <label for="edit_user">Assignee:</label>
                        <input id="edit_user" name="user" type="text">
                    </div>
                </td>
            </tr>
            <tr>
                <td>
                    <div class="field">
                        <label for="edit_originalEstimate">Original Estimate:</label>
                        <input id="edit_originalEstimate" name="originalEstimate" type="text" autocomplete="off">
                    </div>
                </td>
                <td>
                    <div class="field">
                        <label for="edit_startDate">Start Date:</label>
                        <input type='text' class="datepicker" name="startDate" id="edit_startDate" autocomplete="off"/>
                    </div>
                </td>
                <td>
                    <div class="field">
                        <label for="edit_endDate">Due Date:</label>
                        <input type='text' class="datepicker" name="dueDate" id="edit_endDate" autocomplete="off"/>
                    </div>
                </td>
            </tr>
        </table>

        <div class="toolbar">
            <input type="button" id="edit_save" class="k-button k-primary right" value="Create"/>
            <input type="button" id="edit_cancel" class="k-button left" value="Cancel"/>

            <div class="clearfix"></div>
        </div>
    </form>
</div>
<script>
    var currentTaskId;

    function showEditor(id) {
        currentTaskId = id;
        var task = id > 0 ? gantt.getTask(id) : null;
        if (!task || task.$new) {
            var parentTask = (task && task.parent != 0) ? gantt.getTask(task.parent) : null;
            clearEditValues(!!(parentTask && parentTask.key) && !(parentTask && parentTask.isSubTask));
            if (task && parentTask)
                setEditValues(task, parentTask);
            createEditFormElements(parentTask && parentTask.isSubTask);
            var window = $("#editorWindow").data('kendoWindow');
            if (parentTask)
                window.title('[' + parentTask.projectName + '] Create New Task');
            else
                window.title('Create New Task');
            window.center().open();
        }
    }

    function setEditValues(task, parentTask) {
        $('#edit_project').val(parentTask.projectKey);
        if (parentTask.key)
            $('#edit_parent').val(parentTask.key + ': ' + parentTask.text);
        $('#edit_client').val(parentTask.client);
        $('#edit_component').val(parentTask.components);
    }

    function clearEditValues(subtask) {
        $('#edit_project').val('');
        $('#edit_parent').val('');
        $('#edit_client').val('');
        $('#edit_issueType').val(subtask ? 'Sub-task' : 'Task');
        $('#edit_priority').val('Medium');
        $('#edit_summary').val('');
        $('#edit_description').val('');
        $('#edit_component').val('');
        $('#edit_user').val('');
        $('#edit_originalEstimate').val('1h');
        $('#edit_startDate').val(formatDate(new Date()));
        var endDate = new Date();
        endDate.setDate(endDate.getDate() + 1);
        $('#edit_endDate').val(formatDate(endDate));
    }

    function formatDate(date) {

        var day = date.getDate();
        var monthIndex = date.getMonth();
        var year = date.getFullYear();

        return (monthIndex + 1) + '/' + day + '/' + year;
    }

    function onClose() {
        gantt.deleteTask(currentTaskId);
    }

    function createEditFormElements(noSubTasks) {
        $('#edit_priority').selectize()[0].selectize.destroy();
        $('#edit_priority').selectize({
            plugins: ['remove_button'],
            valueField: 'value',
            labelField: 'value',
            searchField: 'value',
            maxItems: 1,
            create: false,
            options: [
                <g:each in="${Priority.list()}" var="priority">
                {value: '${priority?.name}'},
                </g:each>
            ]
        });
        if (noSubTasks)
            $('#edit_issueType').selectize({
                plugins: ['remove_button'],
                valueField: 'value',
                labelField: 'value',
                searchField: 'value',
                maxItems: 1,
                create: false,
                options: [
                    <g:each in="${IssueType.createCriteria().list {
            projections {
                property('name')
            }
        }.sort().findAll{!it.contains('Sub-')}}" var="issueType">
                    {value: '<format:html value="${issueType}"/>'},
                    </g:each>
                ]
            });
        else
            $('#edit_issueType').selectize({
                plugins: ['remove_button'],
                valueField: 'value',
                labelField: 'value',
                searchField: 'value',
                maxItems: 1,
                create: false,
                options: [
                    <g:each in="${IssueType.createCriteria().list {
            projections {
                property('name')
            }
        }.sort()}" var="issueType">
                    {value: '<format:html value="${issueType}"/>'},
                    </g:each>
                ]
            });
        $('#edit_project').selectize()[0].selectize.destroy();
        $('#edit_project').selectize({
            plugins: ['remove_button'],
            valueField: 'value',
            labelField: 'text',
            searchField: ['text', 'value'],
            maxItems: 1,
            create: false,
            options: [
                <g:each in="${Configuration.projects}" var="${project}">
                {text: '${project.name}', value: '${project.key}'},
                </g:each>
            ]
        });
        $('#edit_client').selectize()[0].selectize.destroy();
        $('#edit_client').selectize({
            plugins: ['remove_button'],
            valueField: 'value',
            labelField: 'text',
            searchField: 'text',
            maxItems: 1,
            create: false,
            options: [
                <g:each in="${clients?.findAll{it}?.sort{it.name}}" var="client">
                {
                    value: '${client.name}',
                    text: '${client.name}'
                },
                </g:each>
            ]
        });
        $('#edit_component').selectize()[0].selectize.destroy();
        $('#edit_component').selectize({
            plugins: ['remove_button'],
            valueField: 'value',
            labelField: 'text',
            searchField: 'text',
            create: false,
            options: [
                <g:each in="${components?.findAll{it}?.sort{it.fullName}}" var="component">
                {
                    value: '${component.name}',
                    text: '${component.fullName}'
                },
                </g:each>
            ]
        });
        $('#edit_user').selectize()[0].selectize.destroy();
        $('#edit_user').selectize({
            plugins: ['remove_button'],
            valueField: 'value',
            labelField: 'value',
            searchField: 'value',
            maxItems: 1,
            options: [],
            create: false,
            load: function (query, callback) {
                if (!query.length) return callback();
                $.ajax({
                    url: '${createLink(controller: 'user', action: 'search')}',
                    type: 'GET',
                    data: {
                        id: query
                    },
                    error: function () {
                        callback();
                    },
                    success: function (res) {
                        callback(res);
                    }
                });
            }
        });
        $('#edit_parent').selectize()[0].selectize.destroy();
        $('#edit_parent').selectize({
            plugins: ['remove_button'],
            valueField: 'value',
            labelField: 'value',
            searchField: 'value',
            maxItems: 1,
            options: [],
            create: false,
            load: function (query, callback) {
                if (!query.length) return callback();
                $.ajax({
                    url: '${createLink(controller: 'issue', action: 'search')}',
                    type: 'GET',
                    data: {
                        id: query
                    },
                    error: function () {
                        callback();
                    },
                    success: function (res) {
                        callback(res);
                    }
                });
            }
        });
        $('.datepicker').datepicker();
    }

    $(document).ready(function () {

        $("#editorWindow").kendoWindow({
            width: "800px",
            title: "Create New Task",
            visible: false,
            modal: true,
            actions: [
                "Close"
            ],
            close: onClose
        });

        $('#edit_cancel').click(function () {
            $("#editorWindow").data('kendoWindow').close();
        });

        $('#edit_save').click(function () {
            $('#editError').slideUp();
            kendo.ui.progress($("#editorWindow").data('kendoWindow').element, true);
            $.ajax({
                url: '${createLink(action: 'createIssue')}',
                dataType: 'json',
                type: 'post',
                data: $('#editForm').serialize(),
                success: function (data, textStatus, jQxhr) {
                    if (data.succeed) {
                        $("#editorWindow").data('kendoWindow').close();
                        console.log(data.key);
                        reloadPlanner(true);
                    } else {
                        $('#editError').html(data.error).slideDown();
                    }
                    kendo.ui.progress($("#editorWindow").data('kendoWindow').element, false);
                },
                error: function (jqXhr, textStatus, errorThrown) {
                    console.log(errorThrown);
                    kendo.ui.progress($("#editorWindow").data('kendoWindow').element, false);
                }
            });
        });
    });
</script>