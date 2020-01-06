<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 11/26/2019
  Time: 8:47 PM
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="gantt"/>
    <title>Planner</title>
</head>

<body>
<div id="content" role="main">
    <table class="main-body fullscreen">
        <tr>
            <td class="filter-column">
                <g:render template="filter"/>
            </td>

            <td class="expander-cell">
                <g:render template="/filter/expander"/>
            </td>

            <td class="body-column">
                <g:render template="editor"/>
                <g:render template="grid"/>
            </td>
        </tr>
    </table>
</div>
<script>
    window.onbeforeunload = function() { return "You are leaving this page. Do you want to proceed?"; };
</script>

</body>
</html>