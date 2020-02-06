<%--
  Created by IntelliJ IDEA.
  User: root
  Date: 7/1/14
  Time: 3:12 PM
--%>

<%@ page import="jirareporter.Configuration; jirareporter.Component; jirareporter.Project" contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>Recurring Task Settings</title>
    <script language="javascript" src="${resource(dir: 'form-validator', file: 'security.js')}"></script>
</head>

<body>
<div id="content" role="main">
    <div class="small-container">
        <h1>Recurring Task Settings</h1>

        <g:if test="${flash.message}">
            <div class="info">Your new password has been saved successfully.</div>
        </g:if>
        <g:form action="save">
            <input type="hidden" name="user" value="${user.id}"/>
            <g:each in="${Project.findAllByNameInList(Configuration.projects.collect { it.name })}"
                    var="project">
                <div class="check-box-row">
                    <div class="k-edit-label k-roles-label">
                        <label for="project_${project.id}">${project.name} (${project.key})</label>
                    </div>

                    <div data-container-for="enabled" class="k-edit-field">
                        <input type="checkbox" class="projects"
                               name="enabled_${project.id}" ${settings?.find {
                            it.project.id == project.id
                        }?.enabled ? 'checked="checked"' : ''}/>
                    </div>

                    <p style="float:left;">
                        <label for="estmiate_${project.id}">Original Estimate:</label>
                        <input type="text" name="estmiate_${project.id}" id="estmiate_${project.id}" class="k-textbox"
                               value="${settings?.find { it.project.id == project.id }?.originalEstimate}"/> / Day
                    </p>

                    <div class="k-edit-field" style="float:left;">
                        <label for="component_${project.id}">Component/s:</label>
                        <input id="component_${project.id}" name="component_${project.id}" type="text"
                               value="${settings?.find { it.project.id == project.id }?.components?.collect {
                                   it.name
                               }?.join(',') ?: ''}" style="width:400px;">
                    </div>
                    <script>
                        $(function () {
                            $('#component_${project.id}').selectize({
                                plugins: ['remove_button'],
                                valueField: 'value',
                                labelField: 'text',
                                searchField: 'text',
                                create: false,
                                options: [
                                    <g:each in="${ Component.findAllByProject(project)?.sort{it.fullName}}" var="component">
                                    {
                                        value: '${component.name}',
                                        text: '${component.name}'
                                    },
                                    </g:each>
                                ]
                            });
                        });
                    </script>

                    <div class="clearfix"></div>
                </div>
            </g:each>

            <p>
                <input type="submit" id="submit" class="k-button" value="Save Settings"/>
            </p>
        </g:form>
    </div>
</div>
<script>
    (function () {
        $(".projects").kendoSwitch({
            messages: {
                checked: "YES",
                unchecked: "NO"
            }
        });
    })();
</script>
</body>
</html>